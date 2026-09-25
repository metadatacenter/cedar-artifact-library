package org.metadatacenter.artifacts.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.core.FieldInstanceArtifact;
import org.metadatacenter.artifacts.model.reader.JsonArtifactReader;
import org.metadatacenter.artifacts.model.reader.YamlArtifactReader;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A field value carries at most one {@code @type}.
 *
 * Production holds four 2016–2017 instances whose values carry two ontology classes under
 * {@code @type}, on literals and on controlled terms alike. The YAML form has one slot for it, so
 * the renderer kept the first and dropped the rest. A list is now refused wherever a value is made.
 */
class MultipleInstanceTypeTest
{
  private static final String FIRST = "http://purl.bioontology.org/ontology/LNC/LA17713-1";
  private static final String SECOND = "http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl#C3671";

  private static ObjectNode instanceWithField(String field) throws Exception
  {
    return (ObjectNode)new ObjectMapper().readTree("{\"schema:name\":\"Example\","
        + "\"schema:isBasedOn\":\"https://example.org/t\",\"field\":" + field + "}");
  }

  @Test void jsonReaderRejectsTwoTypesOnALiteral() throws Exception
  {
    ObjectNode source = instanceWithField(
        "{\"@value\":\"Mark D Wilkinson\",\"@type\":[\"" + FIRST + "\",\"" + SECOND + "\"]}");

    Exception e = assertThrows(RuntimeException.class,
        () -> new JsonArtifactReader().readTemplateInstanceArtifact(source));
    assertTrue(e.getMessage().contains("at most one @type"), e.getMessage());
  }

  @Test void jsonReaderRejectsTwoTypesOnAControlledTerm() throws Exception
  {
    ObjectNode source = instanceWithField("{\"@id\":\"http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl#C100069\","
        + "\"rdfs:label\":\"Cardiac Valve Injury\",\"@type\":[\"" + FIRST + "\",\"" + SECOND + "\"]}");

    Exception e = assertThrows(RuntimeException.class,
        () -> new JsonArtifactReader().readTemplateInstanceArtifact(source));
    assertTrue(e.getMessage().contains("at most one @type"), e.getMessage());
  }

  @Test void jsonReaderAcceptsOneTypeWrittenEitherWay() throws Exception
  {
    String xsdString = "http://www.w3.org/2001/XMLSchema#string";
    for (String type : new String[] { "\"" + xsdString + "\"", "[\"" + xsdString + "\"]" }) {
      ObjectNode source = instanceWithField("{\"@value\":\"text\",\"@type\":" + type + "}");

      var instance = new JsonArtifactReader().readTemplateInstanceArtifact(source);

      assertEquals(List.of(URI.create(xsdString)),
          instance.singleInstanceFieldInstances().get("field").jsonLdTypes());
    }
  }

  @Test void yamlReaderRejectsAListOfDatatypes()
  {
    var field = new LinkedHashMap<String, Object>();
    field.put("datatype", List.of(FIRST, SECOND));
    field.put("value", "Mark D Wilkinson");
    var yaml = new LinkedHashMap<String, Object>();
    yaml.put("type", "instance");
    yaml.put("name", "Example");
    yaml.put("isBasedOn", "https://example.org/t");
    yaml.put("children", new LinkedHashMap<>(Map.of("field", field)));

    Exception e = assertThrows(RuntimeException.class,
        () -> new YamlArtifactReader().readTemplateInstanceArtifact(yaml));
    assertTrue(e.getMessage().contains("at most one datatype"), e.getMessage());
  }

  @Test void theModelRefusesTwoTypes()
  {
    Exception e = assertThrows(IllegalStateException.class,
        () -> FieldInstanceArtifact.create(List.of(URI.create(FIRST), URI.create(SECOND)), Optional.empty(),
            Optional.of("text"), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()));
    assertTrue(e.getMessage().contains("at most one @type"), e.getMessage());
  }
}
