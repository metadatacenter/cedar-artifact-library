package org.metadatacenter.artifacts.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.metadatacenter.artifacts.model.core.TemplateInstanceArtifact;
import org.metadatacenter.artifacts.model.reader.ArtifactSourceParser;
import org.metadatacenter.artifacts.model.reader.JsonArtifactReader;
import org.metadatacenter.artifacts.model.reader.YamlArtifactReader;
import org.metadatacenter.artifacts.model.renderer.JsonArtifactRenderer;
import org.metadatacenter.artifacts.model.tools.YamlSerializer;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Every XSD datatype a value can name, read from YAML and from JSON and written as the other, in a
 * field and in an attribute value.
 *
 * <p>The meta-schema names eleven XSD datatypes for values: {@code xsd:string}, seven number types and
 * three temporal types. The library also knows {@code xsd:anyURI}, which it spelled {@code xsd:anyUri}.
 * It decided whether an IRI named a datatype without regard to case, then looked the datatype up by
 * its exact spelling and found none. So {@code datatype: xsd:anyURI} read from YAML could not be
 * written as JSON, and a JSON value typed with the full anyURI IRI could not be read.
 */
public class InstanceDatatypeRoundTripTest
{
  private static final ObjectMapper MAPPER = new ObjectMapper();
  private static final String XSD = "http://www.w3.org/2001/XMLSchema#";

  /** Each datatype, with a value of that type. */
  private static final Map<String, String> DATATYPES = new LinkedHashMap<>();

  static {
    DATATYPES.put("xsd:string", "Cardiology cohort");
    DATATYPES.put("xsd:anyURI", "https://example.org/cohort");
    DATATYPES.put("xsd:date", "2026-10-22");
    DATATYPES.put("xsd:dateTime", "2026-10-22T08:00:00-07:00");
    DATATYPES.put("xsd:time", "08:00:00");
    DATATYPES.put("xsd:decimal", "12.50");
    DATATYPES.put("xsd:long", "9000000000");
    DATATYPES.put("xsd:int", "42");
    DATATYPES.put("xsd:double", "1.5E3");
    DATATYPES.put("xsd:float", "1.5");
    DATATYPES.put("xsd:byte", "7");
    DATATYPES.put("xsd:short", "300");
  }

  /** A field is a child of the instance; an attribute value is a member of the group A. */
  private static Stream<Arguments> datatypesAndPositions()
  {
    return DATATYPES.keySet().stream()
      .flatMap(datatype -> Stream.of(Arguments.of(datatype, "field"), Arguments.of(datatype, "attribute")));
  }

  private static ObjectNode jsonInstance(String datatype, String position) throws Exception
  {
    String value = "{\"@value\":\"" + DATATYPES.get(datatype) + "\",\"@type\":\"" + datatype + "\"}";
    String child = position.equals("field") ? "\"F\":" + value : "\"A\":[\"F\"],\"F\":" + value;
    return (ObjectNode) MAPPER.readTree("{\"schema:name\":\"Typed\",\"schema:isBasedOn\":"
      + "\"https://repo.metadatacenter.org/templates/t1\"," + child + "}");
  }

  /** Where YAML writes the value: under the instance's children, or under the attribute-value group. */
  private static String parentKey(String position)
  {
    return position.equals("field") ? "children" : "A";
  }

  private static JsonNode typeOf(TemplateInstanceArtifact instance)
  {
    return new JsonArtifactRenderer().renderTemplateInstanceArtifact(instance).path("F").path("@type");
  }

  @ParameterizedTest(name = "{0}, {1}")
  @MethodSource("datatypesAndPositions")
  public void aDatatypeReadFromYamlIsWrittenAsJson(String datatype, String position) throws Exception
  {
    String yaml = "type: instance\nname: Typed\nisBasedOn: \"https://repo.metadatacenter.org/templates/t1\"\n"
      + parentKey(position) + ":\n  F:\n    datatype: " + datatype + "\n    value: \"" + DATATYPES.get(datatype) + "\"\n";

    TemplateInstanceArtifact instance = new YamlArtifactReader().readTemplateInstanceArtifact(
      ArtifactSourceParser.yaml(yaml));

    assertEquals(datatype, typeOf(instance).asText());
  }

  @ParameterizedTest(name = "{0}, {1}")
  @MethodSource("datatypesAndPositions")
  public void aDatatypeReadFromJsonSurvivesYaml(String datatype, String position) throws Exception
  {
    TemplateInstanceArtifact instance = new JsonArtifactReader().readTemplateInstanceArtifact(
      jsonInstance(datatype, position));
    assertEquals(datatype, typeOf(instance).asText());

    LinkedHashMap<String, Object> yaml = ArtifactSourceParser.yaml(YamlSerializer.getYAML(instance, true, true));
    assertEquals(datatype, ((Map<?, ?>) ((Map<?, ?>) yaml.get(parentKey(position))).get("F")).get("datatype"));
    TemplateInstanceArtifact restored = new YamlArtifactReader().readTemplateInstanceArtifact(yaml);

    assertEquals(datatype, typeOf(restored).asText());
  }

  @ParameterizedTest(name = "{0}, {1}")
  @MethodSource("datatypesAndPositions")
  public void aDatatypeStatedAsItsFullIriIsWrittenPrefixed(String datatype, String position) throws Exception
  {
    ObjectNode source = jsonInstance(datatype, position);
    ((ObjectNode) source.get("F")).put("@type", XSD + datatype.substring("xsd:".length()));

    assertEquals(datatype, typeOf(new JsonArtifactReader().readTemplateInstanceArtifact(source)).asText());
  }

  @Test public void anXsdIriInAnotherCaseIsAnIriLikeAnyOther() throws Exception
  {
    // XSD names are case-sensitive, so XMLSchema#DateTime names no XSD datatype. The meta-schema allows
    // any IRI as a value's type, and this one is kept as written.
    ObjectNode source = jsonInstance("xsd:dateTime", "field");
    ((ObjectNode) source.get("F")).put("@type", XSD + "DateTime");

    assertEquals(XSD + "DateTime", typeOf(new JsonArtifactReader().readTemplateInstanceArtifact(source)).asText());
  }
}
