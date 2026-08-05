package org.metadatacenter.artifacts.model.core;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ArtifactImmutabilityTest
{
  private static final URI BASED_ON = URI.create("https://repo.metadatacenter.org/templates/example");
  private static final URI TYPE = URI.create("https://example.org/Type");
  private static final URI PROPERTY = URI.create("https://example.org/property");

  @Test void fieldSchemasDefensivelyStoreListsAndReturnDetachedContexts()
  {
    List<String> callerLabels = new ArrayList<>(List.of("original"));
    TextField field = TextField.builder()
      .withName("Field")
      .withAlternateLabels(callerLabels)
      .build();
    int originalHashCode = field.hashCode();

    callerLabels.add("caller mutation");
    field.jsonLdContext().clear();

    assertEquals(List.of("original"), field.alternateLabels());
    assertThrows(UnsupportedOperationException.class,
      () -> field.alternateLabels().add("accessor mutation"));
    assertFalse(field.jsonLdContext().isEmpty());
    assertEquals(originalHashCode, field.hashCode());
  }

  @Test void parentSchemasAndTheirUiReturnDetachedMaps()
  {
    TextField field = TextField.builder().withName("Field").build();
    ElementSchemaArtifact element = ElementSchemaArtifact.builder()
      .withName("Element")
      .withJsonLdType(TYPE)
      .withAlternateLabels(List.of("alternate"))
      .withFieldSchema("nested", field, "Nested label", "Nested description")
      .build();
    TemplateSchemaArtifact template = TemplateSchemaArtifact.builder()
      .withName("Template")
      .withFieldSchema("field", field, "Field label", "Field description")
      .withElementSchema("element", element)
      .build();
    int elementHashCode = element.hashCode();
    int templateHashCode = template.hashCode();
    int elementUiHashCode = element.elementUi().hashCode();
    int templateUiHashCode = template.templateUi().hashCode();

    element.jsonLdContext().clear();
    element.fieldSchemas().clear();
    template.jsonLdContext().clear();
    template.fieldSchemas().clear();
    template.elementSchemas().clear();
    element.elementUi().propertyLabels().clear();
    element.elementUi().propertyDescriptions().clear();
    template.templateUi().propertyLabels().clear();
    template.templateUi().propertyDescriptions().clear();

    assertThrows(UnsupportedOperationException.class, () -> element.jsonLdTypes().add(TYPE));
    assertThrows(UnsupportedOperationException.class, () -> element.alternateLabels().add("mutation"));
    assertTrue(element.fieldSchemas().containsKey("nested"));
    assertTrue(template.fieldSchemas().containsKey("field"));
    assertTrue(template.elementSchemas().containsKey("element"));
    assertTrue(element.elementUi().propertyLabels().containsKey("nested"));
    assertTrue(element.elementUi().propertyDescriptions().containsKey("nested"));
    assertTrue(template.templateUi().propertyLabels().containsKey("field"));
    assertTrue(template.templateUi().propertyDescriptions().containsKey("field"));
    assertEquals(elementHashCode, element.hashCode());
    assertEquals(templateHashCode, template.hashCode());
    assertEquals(elementUiHashCode, element.elementUi().hashCode());
    assertEquals(templateUiHashCode, template.templateUi().hashCode());
  }

  @Test void parentInstancesDefensivelyStoreAndReturnNestedCollections()
  {
    FieldInstanceArtifact value = TextFieldInstance.builder().withValue("value").build();
    List<FieldInstanceArtifact> callerValues = new ArrayList<>(List.of(value));
    LinkedHashMap<String, FieldInstanceArtifact> callerAttributes = new LinkedHashMap<>();
    callerAttributes.put("custom", value);

    TemplateInstanceArtifact templateInstance = TemplateInstanceArtifact.builder()
      .withIsBasedOn(BASED_ON)
      .withJsonLdContextEntry("field", PROPERTY)
      .withSingleInstanceFieldInstance("single", value)
      .withMultiInstanceFieldInstances("many", callerValues)
      .withAttributeValueFieldGroup("attributes", callerAttributes)
      .build();
    ElementInstanceArtifact elementInstance = ElementInstanceArtifact.builder()
      .withJsonLdContextEntry("field", PROPERTY)
      .withSingleInstanceFieldInstance("single", value)
      .withMultiInstanceFieldInstances("many", callerValues)
      .withAttributeValueFieldGroup("attributes", callerAttributes)
      .build();

    callerValues.clear();
    callerAttributes.clear();

    assertParentInstanceIsImmutable(templateInstance);
    assertParentInstanceIsImmutable(elementInstance);
  }

  @Test void fieldInstancesDefensivelyStoreJsonLdTypes()
  {
    List<URI> callerTypes = new ArrayList<>(List.of(TYPE));
    TextFieldInstance typed = TextFieldInstance.create(callerTypes, Optional.of("value"), Optional.empty());
    FieldInstanceArtifact generic = FieldInstanceArtifact.create(callerTypes, Optional.empty(), Optional.of("value"),
      Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
    int typedHashCode = typed.hashCode();
    int genericHashCode = generic.hashCode();

    callerTypes.clear();

    assertEquals(List.of(TYPE), typed.jsonLdTypes());
    assertEquals(List.of(TYPE), generic.jsonLdTypes());
    assertThrows(UnsupportedOperationException.class, () -> typed.jsonLdTypes().add(TYPE));
    assertThrows(UnsupportedOperationException.class, () -> generic.jsonLdTypes().add(TYPE));
    assertEquals(typedHashCode, typed.hashCode());
    assertEquals(genericHashCode, generic.hashCode());
  }

  @Test void annotationsDefensivelyStoreAndReturnTheirMap()
  {
    LinkedHashMap<String, AnnotationValue> callerAnnotations = new LinkedHashMap<>();
    callerAnnotations.put("one", new LiteralAnnotationValue("value"));
    Annotations annotations = new Annotations(callerAnnotations);
    int originalHashCode = annotations.hashCode();

    callerAnnotations.put("two", new LiteralAnnotationValue("caller mutation"));
    annotations.annotations().put("three", new LiteralAnnotationValue("accessor mutation"));

    assertEquals(List.of("one"), new ArrayList<>(annotations.annotations().keySet()));
    assertEquals(originalHashCode, annotations.hashCode());
  }

  @Test void booleanConstraintLabelsAreUnmodifiable()
  {
    BooleanField field = BooleanField.builder()
      .withName("Boolean")
      .withTrueLabel("Yes")
      .withFalseLabel("No")
      .build();
    Map<String, String> labels = field.valueConstraints().orElseThrow()
      .asBooleanValueConstraints().labels();

    assertThrows(UnsupportedOperationException.class, () -> labels.put("true", "mutation"));
  }

  private static void assertParentInstanceIsImmutable(ParentInstanceArtifact instance)
  {
    int originalHashCode = instance.hashCode();

    instance.jsonLdContext().clear();
    instance.singleInstanceFieldInstances().clear();
    instance.multiInstanceFieldInstances().clear();
    instance.singleInstanceElementInstances().clear();
    instance.multiInstanceElementInstances().clear();
    instance.attributeValueFieldInstanceGroups().clear();

    assertThrows(UnsupportedOperationException.class,
      () -> instance.multiInstanceFieldInstances().get("many").clear());
    assertThrows(UnsupportedOperationException.class,
      () -> instance.attributeValueFieldInstanceGroups().get("attributes").clear());
    assertThrows(UnsupportedOperationException.class, () -> instance.childKeys().add("mutation"));
    assertTrue(instance.jsonLdContext().containsKey("field"));
    assertTrue(instance.singleInstanceFieldInstances().containsKey("single"));
    assertEquals(1, instance.multiInstanceFieldInstances().get("many").size());
    assertTrue(instance.attributeValueFieldInstanceGroups().get("attributes").containsKey("custom"));
    assertEquals(originalHashCode, instance.hashCode());
  }
}
