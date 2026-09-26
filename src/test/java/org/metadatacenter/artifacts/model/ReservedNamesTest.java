package org.metadatacenter.artifacts.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.core.AttributeValueField;
import org.metadatacenter.artifacts.model.core.ElementSchemaArtifact;
import org.metadatacenter.artifacts.model.core.ReservedNames;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.artifacts.model.core.TextField;
import org.metadatacenter.artifacts.model.reader.JsonArtifactReader;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.metadatacenter.artifacts.model.core.ReservedNames.AttributeValueFieldParent.ELEMENT;
import static org.metadatacenter.artifacts.model.core.ReservedNames.AttributeValueFieldParent.TEMPLATE;

class ReservedNamesTest
{
  @Test void reservesInstancePropertiesKeywordsAndObjectInternals()
  {
    for (String name : List.of("@id", "@anything", "schema:name", "schema:isBasedOn", "pav:derivedFrom",
        "oslc:modifiedBy", "rdfs:label", "skos:altLabel", "_annotations", "__proto__", "constructor", "prototype"))
      assertTrue(ReservedNames.isReservedName(name), name);
    for (String name : List.of("type", "name", "children", "Channel type", "label"))
      assertFalse(ReservedNames.isReservedName(name), name);
  }

  @Test void reservesAParentsYamlKeysForAnAttributeValueField()
  {
    for (String name : ReservedNames.TEMPLATE_INSTANCE_YAML_KEYS)
      assertTrue(ReservedNames.isReservedAttributeValueFieldName(name, TEMPLATE), name);
    for (String name : List.of("type", "id", "children"))
      assertTrue(ReservedNames.isReservedAttributeValueFieldName(name, ELEMENT), name);
    // An element used inside its parent writes only type, id and children beside its fields.
    for (String name : List.of("name", "description", "isBasedOn"))
      assertFalse(ReservedNames.isReservedAttributeValueFieldName(name, ELEMENT), name);
    assertFalse(ReservedNames.isReservedAttributeValueFieldName("Channel type", TEMPLATE));
    assertTrue(ReservedNames.isReservedAttributeValueFieldName("schema:name", ELEMENT));
  }

  private static AttributeValueField attributeValueField(String name)
  {
    return AttributeValueField.builder().withName(name).build();
  }

  @Test void aTemplateRefusesAnAttributeValueFieldNamedAfterItsYamlKeys()
  {
    Exception e = assertThrows(IllegalStateException.class, () -> TemplateSchemaArtifact.builder()
        .withName("T").withFieldSchema(attributeValueField("type")).build());
    assertTrue(e.getMessage().contains("reserved for CEDAR instance metadata"), e.getMessage());
    assertThrows(IllegalStateException.class, () -> TemplateSchemaArtifact.builder()
        .withName("T").withFieldSchema(attributeValueField("name")).build());
  }

  @Test void anOrdinaryFieldMayTakeAYamlKeyName()
  {
    assertDoesNotThrow(() -> TemplateSchemaArtifact.builder()
        .withName("T").withFieldSchema(TextField.builder().withName("type").build()).build());
  }

  @Test void anElementReservesOnlyTheNestedYamlKeys()
  {
    assertThrows(IllegalStateException.class, () -> ElementSchemaArtifact.builder()
        .withName("E").withFieldSchema(attributeValueField("type")).build());
    assertDoesNotThrow(() -> ElementSchemaArtifact.builder()
        .withName("E").withFieldSchema(attributeValueField("name")).build());
  }

  @Test void noChildMayTakeAReservedName()
  {
    assertThrows(IllegalStateException.class, () -> TemplateSchemaArtifact.builder()
        .withName("T").withFieldSchema(TextField.builder().withName("constructor").build()).build());
  }

  @Test void theJsonReaderRefusesAnInstanceWhoseAttributeValueGroupIsNamedType() throws Exception
  {
    ObjectNode source = (ObjectNode)new ObjectMapper().readTree("{\"schema:name\":\"Example\","
        + "\"schema:isBasedOn\":\"https://example.org/t\",\"type\":[\"qeeq\"],\"qeeq\":{\"@value\":\"x\"}}");

    Exception e = assertThrows(RuntimeException.class,
        () -> new JsonArtifactReader().readTemplateInstanceArtifact(source));
    assertTrue(e.getMessage().contains("reserved for CEDAR instance metadata"), e.getMessage());
  }
}
