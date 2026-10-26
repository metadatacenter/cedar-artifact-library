package org.metadatacenter.artifacts.model.reader;

import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.core.ElementInstanceArtifact;
import org.metadatacenter.artifacts.model.core.TemplateInstanceArtifact;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.metadatacenter.model.ModelNodeNames.MODEL_VERSION;

/**
 * A key the YAML form does not have is refused with its path, where it used to be dropped.
 *
 * <p>The same mistake in JSON is refused by the validator, so a misspelled {@code descripton:}
 * was the one edit that could reach the repository without its value and without a word. The
 * keys a document chooses itself stay open: children, annotations, and the attribute names of an
 * attribute-value group. A key holding a mapping beside an instance's {@code children:} is read
 * as an attribute-value group, because only the template can say whether it is one.
 */
public class YamlUnknownKeyTest
{
  private final YamlArtifactReader reader = new YamlArtifactReader();

  private static final String TEMPLATE = """
    type: template
    name: "T"
    modelVersion: %s
    children:
      - key: "Title"
        type: text-field
        name: "Title"
        modelVersion: %s
        configuration:
          required: true
      - key: "Block"
        type: element
        name: "Block"
        modelVersion: %s
        children:
          - key: "Inner"
            type: text-field
            name: "Inner"
            modelVersion: %s
    """.formatted(MODEL_VERSION, MODEL_VERSION, MODEL_VERSION, MODEL_VERSION);

  private static final String INSTANCE = """
    type: instance
    name: "Instance"
    isBasedOn: "https://repo.metadatacenter.org/templates/t1"
    children:
      Title:
        value: "A title"
      Block:
        id: "https://repo.metadatacenter.org/template-element-instances/e1"
        children:
          Inner:
            value: "x"
        More:
          Weight:
            value: "3"
    Extra:
      Colour:
        value: "red"
    """;

  private TemplateSchemaArtifact template(String yaml) throws IOException
  {
    return reader.readTemplateSchemaArtifact(ArtifactSourceParser.yaml(yaml));
  }

  private TemplateInstanceArtifact instance(String yaml) throws IOException
  {
    return reader.readTemplateInstanceArtifact(ArtifactSourceParser.yaml(yaml));
  }

  private static void assertRefused(String key, String path, Executable read)
  {
    ArtifactParseException refused = assertThrows(ArtifactParseException.class, read::run);
    assertTrue(refused.getParseErrorMessage().startsWith("unknown key"), refused.getMessage());
    assertEquals(key, refused.getFieldKey(), refused.getMessage());
    assertEquals(path, refused.getPath(), refused.getMessage());
  }

  private interface Executable
  {
    void run() throws Exception;
  }

  @Test public void theDocumentsAsWrittenAreRead() throws Exception
  {
    TemplateSchemaArtifact template = template(TEMPLATE);
    assertTrue(template.getFieldSchemaArtifact("Title").requiredValue());

    TemplateInstanceArtifact instance = instance(INSTANCE);
    assertEquals("red",
      instance.attributeValueFieldInstanceGroups().get("Extra").get("Colour").jsonLdValue().orElseThrow());
    ElementInstanceArtifact block = instance.singleInstanceElementInstances().get("Block");
    assertEquals("3", block.attributeValueFieldInstanceGroups().get("More").get("Weight").jsonLdValue().orElseThrow());
  }

  @Test public void aMisspelledKeyOfATemplateIsRefusedWhereItIs() throws Exception
  {
    assertRefused("descripton", "/", () -> template(TEMPLATE.replace("name: \"T\"\n",
      "name: \"T\"\ndescripton: \"lost\"\n")));
  }

  @Test public void aMisspelledKeyOfAChildsConfigurationIsRefusedWhereItIs() throws Exception
  {
    assertRefused("requird", "/Title/configuration", () -> template(TEMPLATE.replace("required: true",
      "requird: true")));
  }

  @Test public void aMisspelledKeyOfAChildInsideAnElementIsRefusedWhereItIs() throws Exception
  {
    assertRefused("labl", "/Block/Inner", () -> template(TEMPLATE.replace("name: \"Inner\"\n",
      "name: \"Inner\"\n        labl: \"x\"\n")));
  }

  @Test public void aKeyOfAnotherFieldTypeIsRefusedWhereItIs() throws Exception
  {
    // A unit is a numeric field's. Read on a text field, it would be dropped.
    assertRefused("unit", "/Block/Inner", () -> template(TEMPLATE.replace("name: \"Inner\"\n",
      "name: \"Inner\"\n        unit: \"cm\"\n")));
  }

  @Test public void aChoiceOptionIsALabelAndASelection() throws Exception
  {
    String radio = TEMPLATE.replace("type: text-field\n    name: \"Title\"",
      "type: radio-field\n    name: \"Title\"\n    values:\n      - label: \"a\"");
    assertTrue(template(radio).getFieldSchemaArtifact("Title").valueConstraints().isPresent());
    assertRefused("type", "/Title/values[0]", () -> template(radio.replace("- label: \"a\"", "- label: \"a\"\n        type: class")));
  }

  @Test public void aFieldsSettingsStatedForAnElementAreReadPast() throws Exception
  {
    String stated = TEMPLATE.replace("    type: element\n    name: \"Block\"\n",
      "    type: element\n    name: \"Block\"\n    configuration:\n      continuePreviousLine: true\n"
        + "      valueRecommendation: true\n      hidden: true\n      required: true\n      recommended: true\n");
    assertEquals(template(TEMPLATE), template(stated));
  }

  @Test public void anAnnotationIsALiteralOrAnIri() throws Exception
  {
    String annotated = TEMPLATE + "annotations:\n  note:\n    value: \"A note\"\n";
    assertTrue(template(annotated).annotations().isPresent());
    assertRefused("id", "/annotations/note", () -> template(annotated + "    id: \"https://example.org/note\"\n"));
  }

  @Test public void aChildMayLeaveItsModelVersionToTheDocument() throws Exception
  {
    // A writer states every child's model version, and a hand-written document often states only its own.
    String unstated = TEMPLATE.replaceFirst("(key: \"Title\"[\\s\\S]*?)    modelVersion: \\S+\n", "$1");
    assertTrue(unstated.length() < TEMPLATE.length());
    assertEquals(template(TEMPLATE), template(unstated));
  }

  @Test public void aChildOfAnotherModelVersionIsRefused() throws Exception
  {
    ArtifactParseException refused = assertThrows(ArtifactParseException.class,
      () -> template(TEMPLATE.replaceFirst("(key: \"Title\"[\\s\\S]*?modelVersion: )\\S+", "$11.0.0")));
    assertEquals("/Title", refused.getPath(), refused.getMessage());
    assertTrue(refused.getParseErrorMessage().startsWith("Expecting model version"), refused.getMessage());
  }

  @Test public void aScalarBesideAnInstancesChildrenIsRefused() throws Exception
  {
    assertRefused("bogusKey", "/", () -> instance(INSTANCE + "bogusKey: 1\n"));
  }

  @Test public void aMisspelledKeyOfAFieldValueIsRefusedWhereItIs() throws Exception
  {
    assertRefused("labl", "/Title", () -> instance(INSTANCE.replace("value: \"A title\"\n",
      "value: \"A title\"\n    labl: \"x\"\n")));
  }

  @Test public void aMisspelledKeyInsideAnAttributeIsRefusedWhereItIs() throws Exception
  {
    assertRefused("valu", "/Extra/Colour", () -> instance(INSTANCE.replace("value: \"red\"",
      "valu: \"red\"")));
    assertRefused("valu", "/Block/More/Weight", () -> instance(INSTANCE.replace("value: \"3\"",
      "valu: \"3\"")));
  }

  @Test public void anAttributeNameIsTheDocumentsOwnChoice() throws Exception
  {
    TemplateInstanceArtifact instance = instance(INSTANCE.replace("Colour:", "descripton:"));
    assertTrue(instance.attributeValueFieldInstanceGroups().get("Extra").containsKey("descripton"));
  }

  @Test public void aScalarBesideAnElementsChildrenIsRefused() throws Exception
  {
    assertRefused("labl", "/Block", () -> instance(INSTANCE.replace("    More:\n",
      "    labl: \"x\"\n    More:\n")));
  }
}
