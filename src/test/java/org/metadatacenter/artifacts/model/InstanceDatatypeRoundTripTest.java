package org.metadatacenter.artifacts.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.metadatacenter.artifacts.model.core.FieldInstanceArtifact;
import org.metadatacenter.artifacts.model.core.TemplateInstanceArtifact;
import org.metadatacenter.artifacts.model.reader.ArtifactSourceParser;
import org.metadatacenter.artifacts.model.reader.JsonArtifactReader;
import org.metadatacenter.artifacts.model.reader.YamlArtifactReader;
import org.metadatacenter.artifacts.model.renderer.JsonArtifactRenderer;
import org.metadatacenter.artifacts.model.tools.YamlSerializer;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
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
 *
 * <p>A value can also name an XSD datatype the library has no constant for, such as
 * {@code xsd:integer}. The renderers prefixed only the datatypes the library has constants for, and the
 * YAML reader expands every {@code xsd:} name to its full IRI. So JSON {@code xsd:integer} came back
 * from YAML as {@code http://www.w3.org/2001/XMLSchema#integer}, and YAML {@code xsd:integer} was
 * written back as YAML in that form. Both renderers now write every IRI in the XSD namespace as
 * {@code xsd:} and its local name, and any other IRI as it is.
 *
 * <p>The YAML writer leaves a datatype unquoted only when the library has a constant for it. A name
 * without one is quoted, unless the writer minimizes quotes.
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

  /** XSD datatypes the library has no constant for, each with a value of that type. */
  private static final Map<String, String> OTHER_XSD_DATATYPES = new LinkedHashMap<>();

  static {
    OTHER_XSD_DATATYPES.put("xsd:integer", "42");
    OTHER_XSD_DATATYPES.put("xsd:boolean", "true");
    OTHER_XSD_DATATYPES.put("xsd:nonNegativeInteger", "3");
    OTHER_XSD_DATATYPES.put("xsd:gYear", "2026");
    OTHER_XSD_DATATYPES.put("xsd:duration", "P1DT2H");
  }

  /** An IRI that begins as the XSD namespace does but lies outside it, since the namespace ends at the #. */
  private static final String NOT_XSD = "http://www.w3.org/2001/XMLSchema-datatypes#integer";

  /** A field is a child of the instance; an attribute value is a member of the group A. */
  private static Stream<Arguments> datatypesAndPositions()
  {
    return DATATYPES.keySet().stream()
      .flatMap(datatype -> Stream.of(Arguments.of(datatype, "field"), Arguments.of(datatype, "attribute")));
  }

  /** Each datatype without a constant, spelled with the prefix and as its full IRI, in each position. */
  private static Stream<Arguments> otherDatatypesSpellingsAndPositions()
  {
    return OTHER_XSD_DATATYPES.keySet().stream()
      .flatMap(datatype -> Stream.of(datatype, XSD + datatype.substring("xsd:".length()))
        .flatMap(spelling -> Stream.of(Arguments.of(datatype, spelling, "field"),
          Arguments.of(datatype, spelling, "attribute"))));
  }

  /** Each datatype, with a constant or without, spelled with the prefix and as its full IRI, in each position. */
  private static Stream<Arguments> everyDatatypeSpellingAndPosition()
  {
    return Stream.concat(DATATYPES.keySet().stream(), OTHER_XSD_DATATYPES.keySet().stream())
      .flatMap(datatype -> Stream.of(datatype, XSD + datatype.substring("xsd:".length()))
        .flatMap(spelling -> Stream.of(Arguments.of(datatype, spelling, "field"),
          Arguments.of(datatype, spelling, "attribute"))));
  }

  private static Stream<String> datatypes()
  {
    return DATATYPES.keySet().stream();
  }

  private static Stream<String> otherDatatypes()
  {
    return OTHER_XSD_DATATYPES.keySet().stream();
  }

  private static String valueOf(String datatype)
  {
    return DATATYPES.containsKey(datatype) ? DATATYPES.get(datatype) : OTHER_XSD_DATATYPES.get(datatype);
  }

  private static ObjectNode jsonInstance(String datatype, String position) throws Exception
  {
    return jsonInstance(datatype, DATATYPES.get(datatype), position);
  }

  private static ObjectNode jsonInstance(String type, String value, String position) throws Exception
  {
    String fieldValue = "{\"@value\":\"" + value + "\",\"@type\":\"" + type + "\"}";
    String child = position.equals("field") ? "\"F\":" + fieldValue : "\"A\":[\"F\"],\"F\":" + fieldValue;
    return (ObjectNode) MAPPER.readTree("{\"schema:name\":\"Typed\",\"schema:isBasedOn\":"
      + "\"https://repo.metadatacenter.org/templates/t1\"," + child + "}");
  }

  private static TemplateInstanceArtifact fromYaml(String type, String value, String position) throws Exception
  {
    String yaml = "type: instance\nname: Typed\nisBasedOn: \"https://repo.metadatacenter.org/templates/t1\"\n"
      + parentKey(position) + ":\n  F:\n    datatype: \"" + type + "\"\n    value: \"" + value + "\"\n";
    return new YamlArtifactReader().readTemplateInstanceArtifact(ArtifactSourceParser.yaml(yaml));
  }

  private static TemplateInstanceArtifact throughYaml(TemplateInstanceArtifact instance) throws Exception
  {
    return readYaml(YamlSerializer.getYAML(instance, true, true));
  }

  private static TemplateInstanceArtifact readYaml(String yaml) throws Exception
  {
    return new YamlArtifactReader().readTemplateInstanceArtifact(ArtifactSourceParser.yaml(yaml));
  }

  /** The datatype a YAML document states for the value F. */
  private static Object datatypeIn(Map<?, ?> yaml, String position)
  {
    return ((Map<?, ?>) ((Map<?, ?>) yaml.get(parentKey(position))).get("F")).get("datatype");
  }

  /** The datatype a written YAML document states for the value F, as the writer spelled it. */
  private static String datatypeLine(String yaml)
  {
    List<String> lines = yaml.lines().map(String::trim).filter(line -> line.startsWith("datatype:")).toList();
    assertEquals(1, lines.size(), yaml);
    return lines.get(0);
  }

  /** Where YAML writes the value: under the instance's children, or under the attribute-value group. */
  private static String parentKey(String position)
  {
    return position.equals("field") ? "children" : "A";
  }

  /** The types the model holds for the value F. */
  private static List<URI> typesOf(TemplateInstanceArtifact instance, String position)
  {
    FieldInstanceArtifact value = position.equals("field")
      ? instance.singleInstanceFieldInstances().get("F")
      : instance.attributeValueFieldInstanceGroups().get("A").get("F");
    return value.jsonLdTypes();
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
    assertEquals(datatype, datatypeIn(yaml, position));
    TemplateInstanceArtifact restored = new YamlArtifactReader().readTemplateInstanceArtifact(yaml);

    assertEquals(datatype, typeOf(restored).asText());
  }

  @ParameterizedTest(name = "{0}, {1}")
  @MethodSource("datatypesAndPositions")
  public void aDatatypeStatedInAOneElementArrayReadsAsTheSameIriAsAString(String datatype, String position)
    throws Exception
  {
    // The JSON reader expanded a prefixed constant stated as a string, and kept one stated in an array as
    // written, so the model held the IRI xsd:int for one spelling and the full XSD IRI for the other.
    ObjectNode asString = jsonInstance(datatype, position);
    ObjectNode asArray = jsonInstance(datatype, position);
    ((ObjectNode) asArray.get("F")).putArray("@type").add(datatype);
    List<URI> expanded = List.of(URI.create(XSD + datatype.substring("xsd:".length())));

    assertEquals(expanded, typesOf(new JsonArtifactReader().readTemplateInstanceArtifact(asString), position));
    assertEquals(expanded, typesOf(new JsonArtifactReader().readTemplateInstanceArtifact(asArray), position));
  }

  @ParameterizedTest(name = "{0}, {1}")
  @MethodSource("datatypesAndPositions")
  public void aDatatypeStatedInAOneElementArrayIsWrittenAsYaml(String datatype, String position) throws Exception
  {
    // The YAML renderer once took the unexpanded IRI the JSON reader kept from an array for a known
    // datatype, then failed to look it up.
    ObjectNode source = jsonInstance(datatype, position);
    ((ObjectNode) source.get("F")).putArray("@type").add(datatype);
    TemplateInstanceArtifact instance = new JsonArtifactReader().readTemplateInstanceArtifact(source);

    LinkedHashMap<String, Object> yaml = ArtifactSourceParser.yaml(YamlSerializer.getYAML(instance, true, true));
    assertEquals(datatype, datatypeIn(yaml, position));

    assertEquals(datatype, typeOf(new YamlArtifactReader().readTemplateInstanceArtifact(yaml)).asText());
  }

  @ParameterizedTest(name = "{1}, {2}")
  @MethodSource("everyDatatypeSpellingAndPosition")
  public void aDatatypeReadFromYamlIsWrittenPrefixedAsYamlAndReadsBack(String datatype, String spelling,
    String position) throws Exception
  {
    TemplateInstanceArtifact instance = fromYaml(spelling, valueOf(datatype), position);

    for (boolean compact : new boolean[] { true, false }) {
      LinkedHashMap<String, Object> yaml = ArtifactSourceParser.yaml(YamlSerializer.getYAML(instance, compact, true));
      assertEquals(datatype, datatypeIn(yaml, position));
      TemplateInstanceArtifact restored = new YamlArtifactReader().readTemplateInstanceArtifact(yaml);

      assertEquals(datatype,
        datatypeIn(ArtifactSourceParser.yaml(YamlSerializer.getYAML(restored, compact, true)), position));
    }
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("datatypes")
  public void aDatatypeWithAConstantIsWrittenUnquoted(String datatype) throws Exception
  {
    TemplateInstanceArtifact instance = fromYaml(datatype, valueOf(datatype), "field");

    assertEquals("datatype: " + datatype, datatypeLine(YamlSerializer.getYAML(instance, true, true)));
    assertEquals("datatype: " + datatype, datatypeLine(YamlSerializer.getYAML(instance, true, false)));
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("otherDatatypes")
  public void anXsdDatatypeWithoutAConstantIsQuotedUnlessQuotesAreMinimizedAndReadsBack(String datatype)
    throws Exception
  {
    TemplateInstanceArtifact instance = fromYaml(datatype, valueOf(datatype), "field");

    String quoted = YamlSerializer.getYAML(instance, true, true);
    assertEquals("datatype: \"" + datatype + "\"", datatypeLine(quoted));
    assertEquals(datatype, typeOf(readYaml(quoted)).asText());
    assertEquals(datatypeLine(quoted), datatypeLine(YamlSerializer.getYAML(readYaml(quoted), true, true)));

    String minimal = YamlSerializer.getYAML(instance, true, false);
    assertEquals("datatype: " + datatype, datatypeLine(minimal));
    assertEquals(datatype, typeOf(readYaml(minimal)).asText());
    assertEquals(datatypeLine(minimal), datatypeLine(YamlSerializer.getYAML(readYaml(minimal), true, false)));
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
    // XSD names are case-sensitive, so XMLSchema#DateTime is not xsd:dateTime. The meta-schema allows
    // any IRI as a value's type, and this one is written prefixed, as every IRI in the namespace is.
    ObjectNode source = jsonInstance("xsd:dateTime", "field");
    ((ObjectNode) source.get("F")).put("@type", XSD + "DateTime");

    assertEquals("xsd:DateTime", typeOf(new JsonArtifactReader().readTemplateInstanceArtifact(source)).asText());
  }

  @ParameterizedTest(name = "{1}, {2}")
  @MethodSource("otherDatatypesSpellingsAndPositions")
  public void anXsdDatatypeWithoutAConstantIsWrittenPrefixedAndSurvivesYaml(String datatype, String spelling,
    String position) throws Exception
  {
    TemplateInstanceArtifact instance = new JsonArtifactReader().readTemplateInstanceArtifact(
      jsonInstance(spelling, OTHER_XSD_DATATYPES.get(datatype), position));
    assertEquals(datatype, typeOf(instance).asText());

    assertEquals(datatype, typeOf(throughYaml(instance)).asText());
  }

  @ParameterizedTest(name = "{1}, {2}")
  @MethodSource("otherDatatypesSpellingsAndPositions")
  public void anXsdDatatypeWithoutAConstantReadFromYamlIsWrittenPrefixed(String datatype, String spelling,
    String position) throws Exception
  {
    assertEquals(datatype, typeOf(fromYaml(spelling, OTHER_XSD_DATATYPES.get(datatype), position)).asText());
  }

  @ParameterizedTest(name = "{0}")
  @ValueSource(strings = { "field", "attribute" })
  public void aDatatypeOutsideTheXsdNamespaceIsWrittenAsItIs(String position) throws Exception
  {
    TemplateInstanceArtifact instance = new JsonArtifactReader().readTemplateInstanceArtifact(
      jsonInstance(NOT_XSD, "42", position));
    assertEquals(NOT_XSD, typeOf(instance).asText());

    assertEquals(NOT_XSD, typeOf(throughYaml(instance)).asText());
    assertEquals(NOT_XSD, typeOf(fromYaml(NOT_XSD, "42", position)).asText());
    assertEquals(NOT_XSD, datatypeIn(ArtifactSourceParser.yaml(
      YamlSerializer.getYAML(instance, true, true)), position));
    assertEquals(NOT_XSD, datatypeIn(ArtifactSourceParser.yaml(
      YamlSerializer.getYAML(fromYaml(NOT_XSD, "42", position), true, true)), position));
  }

  @Test public void theXsdNamespaceIriItselfIsWrittenAsItIs() throws Exception
  {
    // It has no local name, and a bare xsd: is not an IRI the JSON reader could read back.
    TemplateInstanceArtifact instance = new JsonArtifactReader().readTemplateInstanceArtifact(
      jsonInstance(XSD, "42", "field"));

    assertEquals(XSD, typeOf(instance).asText());
    assertEquals("datatype: \"" + XSD + "\"", datatypeLine(YamlSerializer.getYAML(instance, true, true)));
    // YAML expands a bare xsd: to the namespace IRI, which is then written as it is.
    for (String spelling : new String[] { XSD, "xsd:" })
      assertEquals("datatype: \"" + XSD + "\"",
        datatypeLine(YamlSerializer.getYAML(fromYaml(spelling, "42", "field"), true, true)));
  }
}
