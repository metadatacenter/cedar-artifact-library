package org.metadatacenter.artifacts.model.reader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.metadatacenter.artifacts.model.core.AttributeValueField;
import org.metadatacenter.artifacts.model.core.ControlledTermField;
import org.metadatacenter.artifacts.model.core.ElementSchemaArtifact;
import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifacts.model.core.NumericField;
import org.metadatacenter.artifacts.model.core.SectionBreakField;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.artifacts.model.core.TemporalField;
import org.metadatacenter.artifacts.model.core.TextField;
import org.metadatacenter.artifacts.model.core.fields.TemporalGranularity;
import org.metadatacenter.artifacts.model.core.fields.XsdNumericDatatype;
import org.metadatacenter.artifacts.model.core.fields.XsdTemporalDatatype;
import org.metadatacenter.artifacts.model.core.fields.constraints.ValueConstraintsActionType;
import org.metadatacenter.artifacts.model.core.fields.constraints.ValueType;
import org.metadatacenter.artifacts.model.renderer.JsonArtifactRenderer;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.metadatacenter.model.ModelNodeNames.BIBO_STATUS;
import static org.metadatacenter.model.ModelNodeNames.ELEMENT_SCHEMA_ARTIFACT_TYPE_IRI;
import static org.metadatacenter.model.ModelNodeNames.FIELD_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS;
import static org.metadatacenter.model.ModelNodeNames.FIELD_SCHEMA_ARTIFACT_TYPE_IRI;
import static org.metadatacenter.model.ModelNodeNames.JSON_LD_CONTEXT;
import static org.metadatacenter.model.ModelNodeNames.JSON_LD_TYPE;
import static org.metadatacenter.model.ModelNodeNames.JSON_LD_VALUE;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_ADDITIONAL_PROPERTIES;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_DESCRIPTION;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_OBJECT;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_PROPERTIES;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_SCHEMA;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_SCHEMA_IRI;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_TITLE;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_TYPE;
import static org.metadatacenter.model.ModelNodeNames.MODEL_VERSION;
import static org.metadatacenter.model.ModelNodeNames.PARENT_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS;
import static org.metadatacenter.model.ModelNodeNames.PAV_VERSION;
import static org.metadatacenter.model.ModelNodeNames.SCHEMA_ORG_DESCRIPTION;
import static org.metadatacenter.model.ModelNodeNames.SCHEMA_ORG_NAME;
import static org.metadatacenter.model.ModelNodeNames.SCHEMA_ORG_SCHEMA_VERSION;
import static org.metadatacenter.model.ModelNodeNames.SCHEMA_IS_BASED_ON;
import static org.metadatacenter.model.ModelNodeNames.TEMPLATE_SCHEMA_ARTIFACT_TYPE_IRI;
import static org.metadatacenter.model.ModelNodeNames.UI;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_ACTION;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_NUMBER_TYPE;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_TEMPORAL_TYPE;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_TYPE;

/**
 * Negative-path tests for JsonArtifactReader: malformed input must fail fast with a useful
 * exception rather than producing a silently-wrong artifact.
 */
public class JsonArtifactReaderNegativePathsTest
{
  private JsonArtifactReader reader;
  private ObjectMapper mapper;

  @BeforeEach public void setup()
  {
    reader = new JsonArtifactReader();
    mapper = new ObjectMapper();
  }

  // ---- @type checks ----

  @Test public void testReadTemplateThrowsOnMissingJsonLdType()
  {
    ObjectNode node = baseTemplate("T", "d");
    node.remove(JSON_LD_TYPE);

    assertThrows(ArtifactParseException.class, () -> reader.readTemplateSchemaArtifact(node));
  }

  @Test public void testReadTemplateThrowsOnWrongJsonLdType()
  {
    // A template document tagged with the element IRI must be rejected, not silently coerced.
    ObjectNode node = baseTemplate("T", "d");
    node.put(JSON_LD_TYPE, ELEMENT_SCHEMA_ARTIFACT_TYPE_IRI);

    ArtifactParseException ex = assertThrows(ArtifactParseException.class,
      () -> reader.readTemplateSchemaArtifact(node));
    assertTrue(ex.getMessage().contains("template"));
  }

  @Test public void testReadFieldThrowsOnMissingSchemaName()
  {
    ObjectNode node = baseField("Test", "d");
    assertDoesNotThrow(() -> reader.readFieldSchemaArtifact(node));
    node.remove(SCHEMA_ORG_NAME);

    ArtifactParseException ex = assertThrows(ArtifactParseException.class,
      () -> reader.readFieldSchemaArtifact(node));
    assertEquals(SCHEMA_ORG_NAME, ex.getFieldKey());
    assertEquals("/", ex.getPath());
  }

  // ---- An empty schema:name is refused by the reader, naming the key and the path ----

  private static final JsonArtifactRenderer RENDERER = new JsonArtifactRenderer();
  private static final TextField FIELD = TextField.builder().withName("C").build();
  private static final TextField MULTIPLE_FIELD = TextField.builder().withName("M").withIsMultiple(true).build();
  private static final SectionBreakField STATIC_FIELD = SectionBreakField.builder().withName("S").build();
  private static final AttributeValueField ATTRIBUTE_VALUE_FIELD = AttributeValueField.builder().withName("AV").build();
  private static final ElementSchemaArtifact ELEMENT = ElementSchemaArtifact.builder().withName("E")
    .withFieldSchema("C", FIELD).build();
  private static final ElementSchemaArtifact MULTIPLE_ELEMENT = ElementSchemaArtifact.builder().withName("ME")
    .withFieldSchema("C", FIELD).withIsMultiple(true).build();
  private static final TemplateSchemaArtifact TEMPLATE = TemplateSchemaArtifact.builder().withName("T")
    .withFieldSchema("C", FIELD).withFieldSchema("M", MULTIPLE_FIELD).withFieldSchema("S", STATIC_FIELD)
    .withFieldSchema("AV", ATTRIBUTE_VALUE_FIELD).withElementSchema("E", ELEMENT)
    .withElementSchema("ME", MULTIPLE_ELEMENT).build();

  /** A standalone multi-instance field, as the reader accepts it: the field wrapped in an array schema. */
  private static ObjectNode wrappedMultipleField()
  {
    ObjectNode wrapper = new ObjectMapper().createObjectNode();
    wrapper.put(JSON_SCHEMA_TYPE, "array");
    wrapper.put("minItems", 1);
    wrapper.set("items", RENDERER.renderFieldSchemaArtifact("M", MULTIPLE_FIELD));
    return wrapper;
  }

  static Stream<Arguments> namedPositions()
  {
    Function<ObjectNode, ?> template = node -> new JsonArtifactReader().readTemplateSchemaArtifact(node);
    Function<ObjectNode, ?> element = node -> new JsonArtifactReader().readElementSchemaArtifact(node);
    Function<ObjectNode, ?> field = node -> new JsonArtifactReader().readFieldSchemaArtifact(node);
    Supplier<ObjectNode> templateJson = () -> RENDERER.renderTemplateSchemaArtifact(TEMPLATE);
    Supplier<ObjectNode> elementJson = () -> RENDERER.renderElementSchemaArtifact("E", ELEMENT);
    // position, the document, where its name is, how it is read, the path the refusal names
    return Stream.of(
      Arguments.of("template", templateJson, "", template, ""),
      Arguments.of("element", elementJson, "", element, "/"),
      Arguments.of("field", (Supplier<ObjectNode>) () -> RENDERER.renderFieldSchemaArtifact("C", FIELD), "", field, "/"),
      Arguments.of("static field", (Supplier<ObjectNode>) () -> RENDERER.renderFieldSchemaArtifact("S", STATIC_FIELD), "",
        field, "/"),
      Arguments.of("multi-instance field", (Supplier<ObjectNode>) JsonArtifactReaderNegativePathsTest::wrappedMultipleField,
        "/items", field, "/"),
      Arguments.of("template's field", templateJson, "/properties/C", template, "/properties/C"),
      Arguments.of("template's multi-instance field", templateJson, "/properties/M/items", template,
        "/properties/M/items"),
      Arguments.of("template's static field", templateJson, "/properties/S", template, "/properties/S"),
      Arguments.of("template's attribute-value field", templateJson, "/properties/AV/items", template,
        "/properties/AV/items"),
      Arguments.of("template's element", templateJson, "/properties/E", template, "/properties/E"),
      Arguments.of("template's multi-instance element", templateJson, "/properties/ME/items", template,
        "/properties/ME/items"),
      Arguments.of("template's element's field", templateJson, "/properties/E/properties/C", template,
        "/properties/E/properties/C"),
      Arguments.of("element's field", elementJson, "/properties/C", element, "/properties/C"));
  }

  @ParameterizedTest(name = "{0}") @MethodSource("namedPositions")
  public void anEmptySchemaNameIsRefusedWithItsKeyAndPath(String position, Supplier<ObjectNode> document,
    String pointer, Function<ObjectNode, ?> read, String path)
  {
    ObjectNode node = document.get();
    assertDoesNotThrow(() -> read.apply(node));
    ObjectNode named = pointer.isEmpty() ? node : (ObjectNode) node.at(pointer);
    assertTrue(named.has(SCHEMA_ORG_NAME), pointer);
    named.put(SCHEMA_ORG_NAME, "");

    ArtifactParseException ex = assertThrows(ArtifactParseException.class, () -> read.apply(node));
    assertEquals("Expecting non-empty string value", ex.getParseErrorMessage());
    assertEquals(SCHEMA_ORG_NAME, ex.getFieldKey());
    assertEquals(path, ex.getPath());
  }

  // ---- A controlled term's type and action are refused by the reader, naming the key and the path ----

  private static final ControlledTermField TERM_FIELD = ControlledTermField.builder().withName("D")
    .withClassValueConstraint(URI.create("http://purl.obolibrary.org/obo/DOID_530"), "DOID", "eyelid disease",
      "eyelid disease", ValueType.ONTOLOGY_CLASS)
    .withValueConstraintsAction(URI.create("http://purl.obolibrary.org/obo/DOID_162"), "DOID", ValueType.ONTOLOGY_CLASS,
      ValueConstraintsActionType.DELETE, URI.create("https://data.bioontology.org/ontologies/DOID"), 0)
    .build();

  static Stream<Arguments> termPositions()
  {
    return Stream.of(Arguments.of("standalone", false), Arguments.of("child", true));
  }

  /** Reads the term field's JSON, standalone or as a template's child, after {@code key} at {@code entry} is set. */
  private void readTermField(boolean child, String entry, String key, String spelling)
  {
    ObjectNode node = child
      ? RENDERER.renderTemplateSchemaArtifact(TemplateSchemaArtifact.builder().withName("T")
          .withFieldSchema("D", TERM_FIELD).build())
      : RENDERER.renderFieldSchemaArtifact("D", TERM_FIELD);
    ObjectNode target = (ObjectNode) node.at((child ? "/properties/D" : "") + "/" + VALUE_CONSTRAINTS + entry);
    assertTrue(target.has(key), entry + " " + key);
    target.put(key, spelling);
    if (child) reader.readTemplateSchemaArtifact(node); else reader.readFieldSchemaArtifact(node);
  }

  @ParameterizedTest(name = "{0}") @MethodSource("termPositions")
  public void aTermTypeIsReadIgnoringCaseAndAnActionExactly(String position, boolean child)
  {
    for (String type : List.of("OntologyClass", "Value", "ontologyclass", "VALUE")) {
      assertDoesNotThrow(() -> readTermField(child, "/classes/0", VALUE_CONSTRAINTS_TYPE, type), type);
      assertDoesNotThrow(() -> readTermField(child, "/actions/0", VALUE_CONSTRAINTS_TYPE, type), type);
    }
    for (String action : List.of("delete", "move"))
      assertDoesNotThrow(() -> readTermField(child, "/actions/0", VALUE_CONSTRAINTS_ACTION, action), action);
  }

  @ParameterizedTest(name = "{0}") @MethodSource("termPositions")
  public void anUnknownTermTypeOrActionIsRefusedWithItsKeyAndPath(String position, boolean child)
  {
    String constraints = (child ? "/properties/D" : "") + "/" + VALUE_CONSTRAINTS;
    for (String type : List.of("Class", "class", "term", ""))
      for (String entry : List.of("classes", "actions")) {
        ArtifactParseException ex = assertThrows(ArtifactParseException.class,
          () -> readTermField(child, "/" + entry + "/0", VALUE_CONSTRAINTS_TYPE, type), entry + " " + type);
        assertEquals("Invalid term type \"" + type + "\"; should be OntologyClass or Value", ex.getParseErrorMessage());
        assertEquals(VALUE_CONSTRAINTS_TYPE, ex.getFieldKey());
        assertEquals(constraints + "/" + entry, ex.getPath());
      }
    for (String action : List.of("Delete", "MOVE", "remove", "")) {
      ArtifactParseException ex = assertThrows(ArtifactParseException.class,
        () -> readTermField(child, "/actions/0", VALUE_CONSTRAINTS_ACTION, action), action);
      assertEquals("Invalid action \"" + action + "\"; should be delete or move", ex.getParseErrorMessage());
      assertEquals(VALUE_CONSTRAINTS_ACTION, ex.getFieldKey());
      assertEquals(constraints + "/actions", ex.getPath());
    }
  }

  // ---- A status, a number type and a temporal type are refused by the reader, naming the key and the path ----

  static Stream<Arguments> statusPositions()
  {
    Function<ObjectNode, ?> template = node -> new JsonArtifactReader().readTemplateSchemaArtifact(node);
    Function<ObjectNode, ?> element = node -> new JsonArtifactReader().readElementSchemaArtifact(node);
    Function<ObjectNode, ?> field = node -> new JsonArtifactReader().readFieldSchemaArtifact(node);
    Supplier<ObjectNode> templateJson = () -> RENDERER.renderTemplateSchemaArtifact(TEMPLATE);
    // position, the document, where its status is, how it is read, the path the refusal names
    return Stream.of(
      Arguments.of("template", templateJson, "", template, ""),
      Arguments.of("element", (Supplier<ObjectNode>) () -> RENDERER.renderElementSchemaArtifact("E", ELEMENT), "",
        element, ""),
      Arguments.of("field", (Supplier<ObjectNode>) () -> RENDERER.renderFieldSchemaArtifact("C", FIELD), "", field, ""),
      Arguments.of("template's element", templateJson, "/properties/E", template, "/properties/E"),
      Arguments.of("template's field", templateJson, "/properties/C", template, "/properties/C"),
      Arguments.of("template's element's field", templateJson, "/properties/E/properties/C", template,
        "/properties/E/properties/C"));
  }

  /** The document with {@code bibo:status} at {@code pointer} set to {@code status}. */
  private static ObjectNode withStatus(Supplier<ObjectNode> document, String pointer, String status)
  {
    ObjectNode node = document.get();
    (pointer.isEmpty() ? node : (ObjectNode) node.at(pointer)).put(BIBO_STATUS, status);
    return node;
  }

  @ParameterizedTest(name = "{0}") @MethodSource("statusPositions")
  public void aStatusIsReadIgnoringCase(String position, Supplier<ObjectNode> document, String pointer,
    Function<ObjectNode, ?> read, String path)
  {
    for (String status : List.of("bibo:draft", "bibo:published", "BIBO:DRAFT", "Bibo:Published")) {
      ObjectNode node = withStatus(document, pointer, status);
      assertDoesNotThrow(() -> read.apply(node), status);
    }
  }

  @ParameterizedTest(name = "{0}") @MethodSource("statusPositions")
  public void anUnknownStatusIsRefusedWithItsKeyAndPath(String position, Supplier<ObjectNode> document, String pointer,
    Function<ObjectNode, ?> read, String path)
  {
    for (String status : List.of("draft", "published", "bibo:limbo", "")) {
      ObjectNode node = withStatus(document, pointer, status);
      ArtifactParseException ex = assertThrows(ArtifactParseException.class, () -> read.apply(node), status);
      assertEquals("Invalid status \"" + status + "\"; should be bibo:draft or bibo:published",
        ex.getParseErrorMessage());
      assertEquals(BIBO_STATUS, ex.getFieldKey());
      assertEquals(path, ex.getPath());
    }
  }

  private static final NumericField NUMERIC_FIELD = NumericField.builder().withName("N")
    .withNumericType(XsdNumericDatatype.INT).build();
  private static final TemporalField TEMPORAL_FIELD = TemporalField.builder().withName("W")
    .withTemporalType(XsdTemporalDatatype.DATE).withTemporalGranularity(TemporalGranularity.DAY).build();

  static Stream<Arguments> datatypePositions()
  {
    return Stream.of(Arguments.of("standalone", false), Arguments.of("child", true));
  }

  /**
   * Reads the numeric field N or the temporal field W, standalone or as a template's child, after
   * {@code key} in its value constraints is set to {@code spelling}.
   */
  private void readTypedField(boolean child, String name, String key, String spelling)
  {
    FieldSchemaArtifact field = name.equals("N") ? NUMERIC_FIELD : TEMPORAL_FIELD;
    ObjectNode node = child
      ? RENDERER.renderTemplateSchemaArtifact(TemplateSchemaArtifact.builder().withName("T")
          .withFieldSchema(name, field).build())
      : RENDERER.renderFieldSchemaArtifact(name, field);
    ObjectNode constraints = (ObjectNode) node.at((child ? "/properties/" + name : "") + "/" + VALUE_CONSTRAINTS);
    assertTrue(constraints.has(key), name + " " + key);
    constraints.put(key, spelling);
    if (child) reader.readTemplateSchemaArtifact(node); else reader.readFieldSchemaArtifact(node);
  }

  @ParameterizedTest(name = "{0}") @MethodSource("datatypePositions")
  public void aNumberTypeAndATemporalTypeAreReadIgnoringCase(String position, boolean child)
  {
    for (String type : List.of("xsd:int", "XSD:INT", "Xsd:Int"))
      assertDoesNotThrow(() -> readTypedField(child, "N", VALUE_CONSTRAINTS_NUMBER_TYPE, type), type);
    for (String type : List.of("xsd:date", "XSD:DATE", "Xsd:Date"))
      assertDoesNotThrow(() -> readTypedField(child, "W", VALUE_CONSTRAINTS_TEMPORAL_TYPE, type), type);
  }

  @ParameterizedTest(name = "{0}") @MethodSource("datatypePositions")
  public void anUnknownNumberTypeOrTemporalTypeIsRefusedWithItsKeyAndPath(String position, boolean child)
  {
    for (String type : List.of("xsd:integer", "int", "xsd:date", "")) {
      ArtifactParseException ex = assertThrows(ArtifactParseException.class,
        () -> readTypedField(child, "N", VALUE_CONSTRAINTS_NUMBER_TYPE, type), type);
      assertEquals("Invalid number type \"" + type + "\"; should be xsd:decimal, xsd:long, xsd:byte, xsd:short, "
        + "xsd:int, xsd:float or xsd:double", ex.getParseErrorMessage());
      assertEquals(VALUE_CONSTRAINTS_NUMBER_TYPE, ex.getFieldKey());
      assertEquals((child ? "/properties/N" : "") + "/" + VALUE_CONSTRAINTS, ex.getPath());
    }
    for (String type : List.of("xsd:dateTimeStamp", "date", "xsd:int", "")) {
      ArtifactParseException ex = assertThrows(ArtifactParseException.class,
        () -> readTypedField(child, "W", VALUE_CONSTRAINTS_TEMPORAL_TYPE, type), type);
      assertEquals("Invalid temporal type \"" + type + "\"; should be xsd:date, xsd:dateTime or xsd:time",
        ex.getParseErrorMessage());
      assertEquals(VALUE_CONSTRAINTS_TEMPORAL_TYPE, ex.getFieldKey());
      assertEquals((child ? "/properties/W" : "") + "/" + VALUE_CONSTRAINTS, ex.getPath());
    }
  }

  @Test public void testReadFieldThrowsOnMalformedVersion()
  {
    ObjectNode node = baseField("Test", "d");
    node.put(PAV_VERSION, "not-a-version");

    ArtifactParseException ex = assertThrows(ArtifactParseException.class,
      () -> reader.readFieldSchemaArtifact(node));
    assertTrue(ex.getMessage().contains("not-a-version"));
  }

  @Test public void testReadTemplateInstanceThrowsOnMissingIsBasedOn()
  {
    // Without `isBasedOn` there's no template to validate the instance against.
    ObjectNode node = mapper.createObjectNode();
    node.put(JSON_LD_TYPE, mapper.createArrayNode());
    node.put(SCHEMA_ORG_NAME, "Inst");
    node.put(SCHEMA_ORG_DESCRIPTION, "d");

    assertThrows(Exception.class, () -> reader.readTemplateInstanceArtifact(node));
  }

  @Test public void testReadTemplateInstanceRejectsDuplicateAttributeValueName()
  {
    ObjectNode node = mapper.createObjectNode();
    node.put(SCHEMA_IS_BASED_ON, "https://repo.metadatacenter.org/templates/abc");
    node.putArray("attrs").add("colour").add("colour");
    node.putObject("colour").put(JSON_LD_VALUE, "blue");

    ArtifactParseException ex = assertThrows(ArtifactParseException.class,
      () -> reader.readTemplateInstanceArtifact(node));
    assertTrue(ex.getMessage().contains("Duplicate attribute-value"));
  }

  @Test public void testReadElementThrowsOnMissingProperties()
  {
    // Element schemas need a `properties` block for nested children.
    ObjectNode node = baseElement("E", "d");
    node.remove(JSON_SCHEMA_PROPERTIES);

    assertThrows(ArtifactParseException.class, () -> reader.readElementSchemaArtifact(node));
  }

  // ---- JsonNodeReaders helper rejections ----

  @Test public void testReadUriRejectsNonStringNode()
  {
    ObjectNode node = mapper.createObjectNode();
    node.put("uri", 42);

    assertThrows(Exception.class, () -> JsonNodeReaders.readUri(node, "/", "uri"));
  }

  @Test public void testReadIntegerRejectsStringFormattedNumber()
  {
    ObjectNode node = mapper.createObjectNode();
    node.put("n", "42");

    ArtifactParseException ex = assertThrows(ArtifactParseException.class,
      () -> JsonNodeReaders.readInteger(node, "/", "n"));
    assertTrue(ex.getMessage().contains("integer"));
  }

  @Test public void testReadBooleanRejectsStringTrue()
  {
    ObjectNode node = mapper.createObjectNode();
    node.put("flag", "true");

    ArtifactParseException ex = assertThrows(ArtifactParseException.class,
      () -> JsonNodeReaders.readBoolean(node, "/", "flag", false));
    assertTrue(ex.getMessage().contains("boolean"));
  }

  @Test public void testReadOffsetDateTimeRejectsMalformedString()
  {
    ObjectNode node = mapper.createObjectNode();
    node.put("ts", "not-a-date");

    assertThrows(Exception.class, () -> JsonNodeReaders.readOffsetDateTime(node, "/", "ts"));
  }

  // ---- JSON shape robustness ----

  @Test public void testReadTemplateThrowsOnArrayProperties()
  {
    // JSON Schema `properties` must be an object; an array (or other non-object) is malformed.
    ObjectNode node = baseTemplate("T", "d");
    node.set(JSON_SCHEMA_PROPERTIES, mapper.createArrayNode());

    ArtifactParseException ex = assertThrows(ArtifactParseException.class,
      () -> reader.readTemplateSchemaArtifact(node));
    assertTrue(ex.getMessage().toLowerCase().contains("properties"));
  }

  @Test public void testReadFieldThrowsOnUnknownInputType()
  {
    ObjectNode node = baseField("Test", "d");
    node.with(UI).put("inputType", "not-a-real-input-type");

    ArtifactParseException ex = assertThrows(ArtifactParseException.class,
      () -> reader.readFieldSchemaArtifact(node));
    assertTrue(ex.getMessage().contains("not-a-real-input-type"));
  }

  @Test public void testReadFieldThrowsOnMissingUi()
  {
    ObjectNode node = baseField("Test", "d");
    assertDoesNotThrow(() -> reader.readFieldSchemaArtifact(node));
    node.remove(UI);

    ArtifactParseException ex = assertThrows(ArtifactParseException.class,
      () -> reader.readFieldSchemaArtifact(node));
    assertEquals(UI, ex.getFieldKey());
    assertEquals("", ex.getPath());
  }

  @Test public void testReadTemplateThrowsOnWrongJsonSchemaType()
  {
    // Top-level JSON Schema `type` must be `object`.
    ObjectNode node = baseTemplate("T", "d");
    node.put(JSON_SCHEMA_TYPE, "array");

    assertThrows(ArtifactParseException.class, () -> reader.readTemplateSchemaArtifact(node));
  }

  @Test public void reservedPropertiesCannotHideChildSchemas()
  {
    for (String key : java.util.List.of("@value", "@id", "schema:name", "rdfs:label")) {
      for (boolean array : java.util.List.of(false, true)) {
        ObjectNode parent = baseTemplate("T", "d");
        ObjectNode child = baseField("Child", "d");
        ObjectNode declaration = child;
        if (array) {
          declaration = mapper.createObjectNode().put("type", "array");
          declaration.set("items", child);
        }
        parent.withObject("/properties").set(key, declaration);
        ArtifactParseException ex = assertThrows(ArtifactParseException.class,
            () -> reader.readTemplateSchemaArtifact(parent));
        assertTrue(ex.getMessage().contains("reserved instance property name"), ex.getMessage());
        assertTrue(ex.getPath().endsWith("/properties/" + key), ex.getMessage());
      }
    }
  }

  @Test public void ordinaryMetadataPropertyConstraintsAreStillAccepted()
  {
    ObjectNode parent = baseTemplate("T", "d");
    parent.withObject("/properties").set("@id", mapper.createObjectNode().put("type", "string"));
    parent.withObject("/properties").set("schema:name", mapper.createObjectNode().put("type", "string"));
    org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> reader.readTemplateSchemaArtifact(parent));
  }

  // ---- Helpers ----

  private ObjectNode baseTemplate(String name, String description)
  {
    ObjectNode node = baseSchema(name, description);
    node.put(JSON_LD_CONTEXT, createContextMap(PARENT_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS));
    node.put(JSON_LD_TYPE, TEMPLATE_SCHEMA_ARTIFACT_TYPE_IRI);
    return node;
  }

  private ObjectNode baseElement(String name, String description)
  {
    ObjectNode node = baseSchema(name, description);
    node.put(JSON_LD_CONTEXT, createContextMap(PARENT_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS));
    node.put(JSON_LD_TYPE, ELEMENT_SCHEMA_ARTIFACT_TYPE_IRI);
    return node;
  }

  private ObjectNode baseField(String name, String description)
  {
    ObjectNode node = baseSchema(name, description);
    node.put(JSON_LD_CONTEXT, createContextMap(FIELD_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS));
    node.put(JSON_LD_TYPE, FIELD_SCHEMA_ARTIFACT_TYPE_IRI);
    node.withObject("/" + UI).put("inputType", "textfield");
    return node;
  }

  private ObjectNode baseSchema(String name, String description)
  {
    ObjectNode node = mapper.createObjectNode();
    node.put(SCHEMA_ORG_SCHEMA_VERSION, MODEL_VERSION);
    node.put(SCHEMA_ORG_NAME, name);
    node.put(SCHEMA_ORG_DESCRIPTION, description);
    node.put(JSON_SCHEMA_SCHEMA, JSON_SCHEMA_SCHEMA_IRI);
    node.put(JSON_SCHEMA_TYPE, JSON_SCHEMA_OBJECT);
    node.put(JSON_SCHEMA_TITLE, "title");
    node.put(JSON_SCHEMA_DESCRIPTION, "desc");
    node.put(JSON_SCHEMA_PROPERTIES, mapper.createObjectNode());
    node.put(JSON_SCHEMA_ADDITIONAL_PROPERTIES, false);
    node.put(UI, mapper.createObjectNode());
    return node;
  }

  private ObjectNode createContextMap(Map<String, java.net.URI> contextMap)
  {
    ObjectNode node = mapper.createObjectNode();
    for (var e : contextMap.entrySet())
      node.put(e.getKey(), e.getValue().toString());
    return node;
  }

  @Test public void anEmptyIdentifierIsRefusedRatherThanReadAsAbsent() throws Exception
  {
    // The reader production documents arrive through. It read "" as though the key were absent, so an
    // instance carrying one came back with null in its place and nothing said so.
    ObjectNode instance = (ObjectNode) mapper.readTree("""
        {
          "@id": "https://repo.metadatacenter.org/template-instances/i1",
          "@context": {},
          "schema:name": "I",
          "schema:description": "",
          "schema:isBasedOn": "https://repo.metadatacenter.org/templates/t1",
          "address": { "@context": {}, "@id": "", "street": { "@value": "x" } }
        }
        """);

    ArtifactParseException thrown = assertThrows(ArtifactParseException.class,
      () -> reader.readTemplateInstanceArtifact(instance));
    assertTrue(thrown.getMessage().contains("empty string is not a URI"), thrown.getMessage());
  }

  @Test public void aRepeatedChildMixingElementAndFieldEntriesIsRefused() throws Exception
  {
    // An entry carrying @context makes the list a repeated element, and every entry is then read as
    // an element occurrence. A controlled term read as one keeps its @id as the occurrence's and loses
    // its label. A key only a field value carries marks the entry as a field.
    String element = "{\"@context\":{\"f\":\"https://example.org/p\"},\"f\":{\"@value\":\"a\"}}";
    String term = "\"@id\":\"http://purl.obolibrary.org/obo/DOID_4\"";
    for (String field : new String[] { "{" + term + ",\"rdfs:label\":\"disease\"}",
        "{" + term + ",\"skos:notation\":\"D4\"}", "{" + term + ",\"skos:prefLabel\":\"Disease\"}",
        "{" + term + ",\"skos:altLabel\":\"Illness\"}", "{\"@value\":\"b\"}", "{\"@language\":\"en\"}" }) {
      for (boolean fieldFirst : new boolean[] { false, true }) {
        ObjectNode instance = instanceWithItems(
            "[" + (fieldFirst ? field + "," + element : element + "," + field) + "]");

        ArtifactParseException thrown = assertThrows(ArtifactParseException.class,
            () -> reader.readTemplateInstanceArtifact(instance), field);
        assertEquals("A repeated child mixes element and field entries", thrown.getParseErrorMessage(), field);
        assertEquals("items", thrown.getFieldKey(), field);
        assertEquals(fieldFirst ? "/items[0]" : "/items[1]", thrown.getPath(), field);
      }
    }

    // Empty and identifier-only entries carry nothing of a field's, and stay occurrences of the element.
    var instance = reader.readTemplateInstanceArtifact(
        instanceWithItems("[" + element + ",{\"@id\":\"https://example.org/occurrence\"},{}]"));
    assertEquals(3, instance.multiInstanceElementInstances().get("items").size());
  }

  @Test public void whatSitsBesideAFieldValueStaysReadable() throws Exception
  {
    // The instance's annotations, an attribute-value group and its members, an element's @context, and
    // the legacy _valueLabel each sit beside or in field values, and none of them is an element's child.
    ObjectNode instance = (ObjectNode) mapper.readTree("""
        {
          "schema:name": "X",
          "schema:isBasedOn": "https://example.org/t",
          "_annotations": { "https://example.org/note": { "@value": "reviewed" } },
          "Extra": ["Extra1"],
          "Extra1": { "@value": "v1" },
          "Term": { "@id": "http://purl.obolibrary.org/obo/DOID_4", "@type": "https://example.org/Disease",
                    "rdfs:label": "disease", "skos:notation": "DOID:4", "skos:prefLabel": "Disease",
                    "skos:altLabel": "Illness" },
          "Legacy": { "@value": "a", "_valueLabel": "A" },
          "Section": { "@context": { "f": "https://example.org/f" }, "f": { "@value": "a", "@language": "en" } }
        }
        """);

    var read = reader.readTemplateInstanceArtifact(instance);

    assertTrue(read.annotations().isPresent());
    assertEquals("v1",
        read.attributeValueFieldInstanceGroups().get("Extra").get("Extra1").jsonLdValue().orElseThrow());
    assertEquals("disease", read.singleInstanceFieldInstances().get("Term").label().orElseThrow());
    assertEquals("a", read.singleInstanceFieldInstances().get("Legacy").jsonLdValue().orElseThrow());
    assertEquals("a", read.singleInstanceElementInstances().get("Section").singleInstanceFieldInstances().get("f")
        .jsonLdValue().orElseThrow());
  }

  @Test public void anEmptyDatatypeIsRefusedRatherThanReadAsNone() throws Exception
  {
    // "" parses as a relative IRI reference, yet it names no datatype. Read as no @type at all, the
    // value lost what it stated and was written back without it.
    Map<String, String> pathsByItems = Map.of(
        "{\"@value\":\"b\",\"@type\":\"\"}", "/items",
        "[{\"@value\":\"b\",\"@type\":\"\"}]", "/items[0]",
        "{\"@value\":\"b\",\"@type\":[\"\"]}", "/items");
    for (var items : pathsByItems.entrySet()) {
      ArtifactParseException thrown = assertThrows(ArtifactParseException.class,
          () -> reader.readTemplateInstanceArtifact(instanceWithItems(items.getKey())), items.getKey());
      assertEquals("Invalid datatype \"\"", thrown.getParseErrorMessage(), items.getKey());
      assertEquals(JSON_LD_TYPE, thrown.getFieldKey(), items.getKey());
      assertEquals(items.getValue(), thrown.getPath(), items.getKey());
    }
  }

  private ObjectNode instanceWithItems(String items) throws Exception
  {
    return (ObjectNode) mapper.readTree("{\"schema:name\":\"X\",\"schema:isBasedOn\":\"https://example.org/t\","
        + "\"items\":" + items + "}");
  }

  @Test public void anEmptyDerivedFromIsRefusedToo() throws Exception
  {
    // pav:derivedFrom names the artifact this one was copied from, and it is optional: an artifact
    // derived from nothing leaves the key out. 289 schema artifacts in the shared corpus wrote "" for
    // it, against 41 naming a real IRI. The meta-schema types it as a string with format: uri and the
    // validator does assert that format — it rejects "not a uri at all" — but an empty relative
    // reference is a well-formed URI, so "" passes where the schema means it should not.
    ObjectNode field = (ObjectNode) mapper.readTree("""
        {
          "@id": "https://repo.metadatacenter.org/template-fields/f1",
          "@type": "https://schema.metadatacenter.org/core/TemplateField",
          "@context": {},
          "type": "object",
          "title": "F",
          "description": "d",
          "_ui": { "inputType": "textfield" },
          "_valueConstraints": { "requiredValue": false },
          "pav:derivedFrom": "",
          "schema:name": "F",
          "schema:description": "d"
        }
        """);

    ArtifactParseException thrown = assertThrows(ArtifactParseException.class,
      () -> reader.readFieldSchemaArtifact(field));
    assertTrue(thrown.getMessage().contains("empty string is not a URI"), thrown.getMessage());
  }
}
