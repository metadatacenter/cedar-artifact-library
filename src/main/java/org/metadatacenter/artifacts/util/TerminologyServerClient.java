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
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TerminologyServerClient
{
  private static final String INTEGRATED_SEARCH_PATH_SEGMENT = "integrated-search";
  private static final String INTEGRATED_RETRIEVE_PATH_SEGMENT = "integrated-retrieve";

  private static final int PAGE_SIZE = 1000;
  private static final int MAXIMUM_PAGES = 1000;

  private final String terminologyServerIntegratedRetrieveEndpoint;
  private final String terminologyServerApiKey;
  private final ObjectMapper mapper;
  private final ObjectWriter objectWriter;

  /**
   * @param terminologyServerEndpoint The integrated-retrieve endpoint, the integrated-search
   *                                  endpoint, or the base that the two share
   */
  public TerminologyServerClient(String terminologyServerEndpoint, String terminologyServerApiKey)
  {
    this.terminologyServerIntegratedRetrieveEndpoint = integratedRetrieveEndpoint(terminologyServerEndpoint);
    this.terminologyServerApiKey = terminologyServerApiKey;

    this.mapper = new ObjectMapper();
    mapper.registerModule(new Jdk8Module());
    mapper.setSerializationInclusion(JsonInclude.Include.NON_ABSENT);

    this.objectWriter = mapper.writer().withDefaultPrettyPrinter();
  }

  // TODO Think about sleep to avoid BioPortal limit of 15 calls per second limit

  /**
   * Retrieve every value in the value space described by the supplied constraints, in the order that
   * the terminology server returns them. Values are distinguished by URI, so classes that share a
   * preferred label are all retained; a URI seen more than once is retained once.
   */
  public List<TerminologyValue> getValuesFromTerminologyServer(
    ControlledTermValueConstraints controlledTermValueConstraints)
  {
    Map<URI, TerminologyValue> valuesByUri = new LinkedHashMap<>();

    try {
      String vc = controlledTermValueConstraints2Json(controlledTermValueConstraints);
      Map<String, Object> vcMap = mapper.readValue(vc, Map.class);

      int page = 1;
      int retrievedCount = 0;

      while (true) {
        Map<String, Object> pageOfResults = integratedRetrieve(vcMap, page, PAGE_SIZE,
          terminologyServerIntegratedRetrieveEndpoint, terminologyServerApiKey);

        List<Map<String, String>> valueDescriptions = valueDescriptions(pageOfResults);

        for (Map<String, String> valueDescription : valueDescriptions) {
          TerminologyValue value = terminologyValue(valueDescription);
          valuesByUri.putIfAbsent(value.uri(), value);
        }
        retrievedCount += valueDescriptions.size();

        if (valueDescriptions.size() < PAGE_SIZE)
          break;

        // The server reports no total when it has sorted values merged from several sources
        Integer totalCount = totalCount(pageOfResults);
        if (totalCount != null && retrievedCount >= totalCount)
          break;

        if (page == MAXIMUM_PAGES)
          throw new RuntimeException(
            "value space exceeds " + (MAXIMUM_PAGES * PAGE_SIZE) + " values, which is more than this client retrieves");

        page++;
      }
    } catch (IOException | RuntimeException e) {
      throw new RuntimeException("Error retrieving values from terminology server " + e.getMessage(), e);
    }

    return List.copyOf(valuesByUri.values());
  }

  private TerminologyValue terminologyValue(Map<String, String> valueDescription)
  {
    String uri = valueDescription.get("@id");
    String prefLabel = valueDescription.get("prefLabel");

    if (uri == null)
      throw new RuntimeException("value returned with no @id: " + valueDescription);

    if (prefLabel == null)
      throw new RuntimeException("value returned with no prefLabel: " + uri);

    return new TerminologyValue(URI.create(uri), prefLabel);
  }

  @SuppressWarnings("unchecked")
  private List<Map<String, String>> valueDescriptions(Map<String, Object> pageOfResults)
  {
    return pageOfResults.containsKey("collection") ?
      (List<Map<String, String>>)pageOfResults.get("collection") :
      new ArrayList<>();
  }

  private Integer totalCount(Map<String, Object> pageOfResults)
  {
    Object totalCount = pageOfResults.get("totalCount");

    return totalCount instanceof Number ? ((Number)totalCount).intValue() : null;
  }

  /**
   * The integrated-retrieve endpoint pages through each constrained source and concatenates the
   * results, so a request is bounded only by the page size it is given.
   */
  private static String integratedRetrieveEndpoint(String terminologyServerEndpoint)
  {
    String endpoint = terminologyServerEndpoint;

    while (endpoint.endsWith("/"))
      endpoint = endpoint.substring(0, endpoint.length() - 1);

    if (endpoint.endsWith("/" + INTEGRATED_SEARCH_PATH_SEGMENT) ||
      endpoint.endsWith("/" + INTEGRATED_RETRIEVE_PATH_SEGMENT))
      endpoint = endpoint.substring(0, endpoint.lastIndexOf('/'));

    return endpoint + "/" + INTEGRATED_RETRIEVE_PATH_SEGMENT;
  }

  private Map<String, Object> integratedRetrieve(Map<String, Object> valueConstraints,
    Integer page, Integer pageSize, String integratedRetrieveEndpoint, String apiKey) throws IOException, RuntimeException
  {
    HttpURLConnection connection = null;
    Map<String, Object> resultsMap;

    try {
      // integrated-retrieve takes the value constraints directly, where integrated-search nests
      // them in a parameterObject alongside the user's input text
      Map<String, Object> payloadMap = new HashMap<>();
      payloadMap.put("valueConstraints", valueConstraints);
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
