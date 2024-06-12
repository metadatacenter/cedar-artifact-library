package org.metadatacenter.artifacts.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import org.metadatacenter.artifacts.model.core.fields.constraints.ControlledTermValueConstraints;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TerminologyServerClient
{
  private final String terminologyServerIntegratedSearchEndpoint;
  private final String terminologyServerApiKey;
  private final ObjectMapper mapper;
  private final ObjectWriter objectWriter;

  public static final String xsdDateTimeFormatterString = "uuuu-MM-dd'T'HH:mm:ssZZZZZ";
  public static final DateTimeFormatter xsdDateTimeFormatter =
    DateTimeFormatter.ofPattern(xsdDateTimeFormatterString).withZone(ZoneId.systemDefault());

  public TerminologyServerClient(String terminologyServerIntegratedSearchEndpoint, String terminologyServerApiKey)
  {
    this.terminologyServerIntegratedSearchEndpoint = terminologyServerIntegratedSearchEndpoint;
    this.terminologyServerApiKey = terminologyServerApiKey;

    this.mapper = new ObjectMapper();
    mapper.registerModule(new Jdk8Module());
    mapper.setSerializationInclusion(JsonInclude.Include.NON_ABSENT);

    this.objectWriter = mapper.writer().withDefaultPrettyPrinter();
  }

  // Return prefLabel->IRI
  public Map<String, String> getValuesFromTerminologyServer(ControlledTermValueConstraints controlledTermValueConstraints)
  {
    Map<String, String> values = new HashMap<>();

    try {
      String vc = controlledTermValueConstraints2Json(controlledTermValueConstraints);
      Map<String, Object> vcMap = mapper.readValue(vc, Map.class);

      List<Map<String, String>> valueDescriptions;
      // TODO Replace arbitrary 4000 BioPortal terms; show error if more
      Map<String, Object> searchResult = integratedSearch(vcMap, 1, 4000,
        terminologyServerIntegratedSearchEndpoint, terminologyServerApiKey);
      valueDescriptions = searchResult.containsKey("collection") ?
        (List<Map<String, String>>)searchResult.get("collection") :
        new ArrayList<>();
      if (valueDescriptions.size() > 0) {
        for (int valueDescriptionsIndex = 0; valueDescriptionsIndex < valueDescriptions.size(); valueDescriptionsIndex++) {
          String uri = valueDescriptions.get(valueDescriptionsIndex).get("@id");
          String prefLabel = valueDescriptions.get(valueDescriptionsIndex).get("prefLabel");
          values.put(prefLabel, uri);
        }
      }
    } catch (IOException | RuntimeException e) {
      throw new RuntimeException("Error retrieving values from terminology server " + e.getMessage());
    }
    return values;
  }

  private Map<String, Object> integratedSearch(java.util.Map<String, Object> valueConstraints,
    Integer page, Integer pageSize, String integratedSearchEndpoint, String apiKey) throws IOException, RuntimeException
  {
    HttpURLConnection connection = null;
    Map<String, Object> resultsMap;

    try {
      java.util.Map<String, Object> vcMap = new HashMap<>();
      vcMap.put("valueConstraints", valueConstraints);
      Map<String, Object> payloadMap = new HashMap<>();
      payloadMap.put("parameterObject", vcMap);
      payloadMap.put("page", page);
      payloadMap.put("pageSize", pageSize);
      String payload = mapper.writeValueAsString(payloadMap);
      connection = ConnectionUtil.createAndOpenConnection("POST", integratedSearchEndpoint, apiKey);
      OutputStream os = connection.getOutputStream();
      os.write(payload.getBytes());
      os.flush();
      int responseCode = connection.getResponseCode();
      if (responseCode >= HttpURLConnection.HTTP_BAD_REQUEST) {
        String message = "Error running integrated search. Response code: " + responseCode + "; Payload: " + payload;
        throw new RuntimeException(message);
      } else {
        String response = ConnectionUtil.readResponseMessage(connection.getInputStream());
        resultsMap = mapper.readValue(response, HashMap.class);
      }
    } finally {
      if (connection != null) {
        connection.disconnect();
      }
    }
    return resultsMap;
  }

  private java.util.Map<String, Object> integratedRetrieve(java.util.Map<String, Object> valueConstraints,
    Integer page, Integer pageSize, String integratedRetrieveEndpoint, String apiKey) throws IOException, RuntimeException
  {
    HttpURLConnection connection = null;
    java.util.Map<String, Object> resultsMap;
    try {
      java.util.Map<String, Object> vcMap = new HashMap<>();
      vcMap.put("valueConstraints", valueConstraints);
      Map<String, Object> payloadMap = new HashMap<>();
      payloadMap.put("parameterObject", vcMap);
      payloadMap.put("page", page);
      payloadMap.put("pageSize", pageSize);
      String payload = mapper.writeValueAsString(payloadMap);
      connection = ConnectionUtil.createAndOpenConnection("POST", integratedRetrieveEndpoint, apiKey);
      OutputStream os = connection.getOutputStream();
      os.write(payload.getBytes());
      os.flush();
      int responseCode = connection.getResponseCode();
      if (responseCode >= HttpURLConnection.HTTP_BAD_REQUEST) {
        String message = "Error running integrated retrieve. Response code: " + responseCode + "; Payload: " + payload;
        throw new RuntimeException(message);
      } else {
        String response = ConnectionUtil.readResponseMessage(connection.getInputStream());
        resultsMap = mapper.readValue(response, HashMap.class);
      }
    } finally {
      if (connection != null) {
        connection.disconnect();
      }
    }
    return resultsMap;
  }

  /**
   *
   * The terminology server is expecting a controlled term value constraints object that looks like the following:
   * <p>
   * public class ControlledTermValueConstraints
   *   private List<OntologyValueConstraint> ontologies;
   *   private List<BranchValueConstraint> branches;
   *   private List<ValueSetValueConstraint> valueSets;
   *   private List<ClassValueConstraint> classes;
   *   private List<Action> actions;
   * <p>
   * public class BranchValueConstraint
   *   private String termUri;
   *   private String acronym;
   * <p>
   * public class OntologyValueConstraint
   *   private String acronym;
   * <p>
   * public class ValueSetValueConstraint
   *   private String termUri;
   *   private String vsCollection;
   * <p>
   * public class ClassValueConstraint
   *   private String termUri;
   *   private String prefLabel;
   *   private String type;
   *   private String label; // Optional
   *   private String source;
   * <p>
   * public class Action
   *   private Integer to; // Optional
   *   private String action;
   *   private String label;
   *   private String type;
   *   private String source;
   *   private String sourceUri; // Optional
   *
   */
  private String controlledTermValueConstraints2Json(ControlledTermValueConstraints controlledTermValueConstraints)
  {
    // TODO Do a manual conversion of valueConstraints to JSON so we can do error checking
    try {
      return objectWriter.writeValueAsString(controlledTermValueConstraints);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Error generation value constraints object for terminology server " + e.getMessage());
    }
  }
}
