package org.metadatacenter.artifacts.model.core;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.core.ui.ElementUi;
import org.metadatacenter.artifacts.model.renderer.JsonArtifactRenderer;
import org.metadatacenter.model.validation.CedarValidator;
import org.metadatacenter.model.validation.report.ValidationReport;

import java.io.IOException;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.UnaryOperator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.metadatacenter.model.ModelNodeNames.ELEMENT_SCHEMA_ARTIFACT_TYPE_IRI;
import static org.metadatacenter.model.ModelNodeNames.PARENT_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS;
import static org.metadatacenter.model.ModelNodeNames.SCHEMA_ORG_NAME;

public class ElementSchemaArtifactTest
{

  @Test public void testConstructor()
  {
    LinkedHashMap<String, URI> jsonLdContext = new LinkedHashMap<>(PARENT_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS);
    List<URI> jsonLdTypes = Collections.singletonList(URI.create(ELEMENT_SCHEMA_ARTIFACT_TYPE_IRI));
    URI jsonLdId = URI.create("http://example.com/artifact");
    URI instanceJsonLdType = URI.create("http://example.com/Study");
    URI createdBy = URI.create("http://example.com/user");
    URI modifiedBy = URI.create("http://example.com/user");
    OffsetDateTime createdOn = OffsetDateTime.now();
    OffsetDateTime lastUpdatedOn = OffsetDateTime.now();
    String internalName = "title";
    String internalDescription = "description";
    String name = "Schema Org name";
    String description = "Schema Org description";
    Optional<String> identifier = Optional.of("Schema Org identifier");
    Optional<Version> version = Optional.of(new Version(2, 0, 0));
    Optional<Status> status = Optional.of(Status.DRAFT);
    Optional<URI> previousVersion = Optional.of(URI.create("https://repo.metadatacenter.org/templates/3232"));
    Optional<URI> derivedFrom = Optional.of(URI.create("http://example.com/derived"));
    Optional<Integer> minItems = Optional.of(1);
    Optional<Integer> maxItems = Optional.of(3);
    Optional<URI> propertyUri = Optional.of(URI.create("https://schema.metadatacenter.org/properties/434"));
    Optional<String> language = Optional.of("en");

    ElementSchemaArtifact elementSchemaArtifact = ElementSchemaArtifact.create(internalName, internalDescription,
      jsonLdContext, jsonLdTypes, Optional.of(jsonLdId), Optional.of(instanceJsonLdType), name, description, identifier,
      version, status, previousVersion, derivedFrom, Optional.of(createdBy), Optional.of(modifiedBy),
      Optional.of(createdOn), Optional.of(lastUpdatedOn), new LinkedHashMap<>(), new LinkedHashMap<>(), false, minItems,
      maxItems, propertyUri, language, ElementUi.builder().build(), Optional.empty());

    Assertions.assertEquals(jsonLdTypes, elementSchemaArtifact.jsonLdTypes());
    Assertions.assertEquals(jsonLdId, elementSchemaArtifact.jsonLdId().get());
    Assertions.assertEquals(instanceJsonLdType, elementSchemaArtifact.instanceJsonLdType().get());
    Assertions.assertEquals(jsonLdContext, elementSchemaArtifact.jsonLdContext());
    Assertions.assertEquals(createdBy, elementSchemaArtifact.createdBy().get());
    Assertions.assertEquals(modifiedBy, elementSchemaArtifact.modifiedBy().get());
    Assertions.assertEquals(createdOn, elementSchemaArtifact.createdOn().get());
    Assertions.assertEquals(lastUpdatedOn, elementSchemaArtifact.lastUpdatedOn().get());
    Assertions.assertEquals(internalName, elementSchemaArtifact.internalName());
    Assertions.assertEquals(internalDescription, elementSchemaArtifact.internalDescription());
    Assertions.assertEquals(name, elementSchemaArtifact.name());
    Assertions.assertEquals(description, elementSchemaArtifact.description());
    Assertions.assertEquals(identifier, elementSchemaArtifact.identifier());
    Assertions.assertEquals(version, elementSchemaArtifact.version());
    Assertions.assertEquals(status, elementSchemaArtifact.status());
    Assertions.assertEquals(previousVersion, elementSchemaArtifact.previousVersion());
    Assertions.assertEquals(derivedFrom, elementSchemaArtifact.derivedFrom());
    Assertions.assertEquals(propertyUri, elementSchemaArtifact.propertyUri());
    Assertions.assertEquals(language, elementSchemaArtifact.language());
  }

  @Test public void testCreateElementSchemaArtifactWithChildren()
  {
    String elementName = "Element 1";
    String textFieldName1 = "Text Field 1";
    String textFieldName2 = "Text Field 2";
    String textField2Label = "text field 2 label";
    String textField2Description = "text field 2 description";

    TextField textField1 = TextField.builder().withName(textFieldName1).build();
    TextField textField2 = TextField.builder().withName(textFieldName2).build();

    ElementSchemaArtifact elementSchemaArtifact = ElementSchemaArtifact.builder().withName(elementName)
      .withFieldSchema(textField1).withFieldSchema(textField2, textField2Label, textField2Description).build();

    assertEquals(elementSchemaArtifact.name(), elementName);
    assertTrue(elementSchemaArtifact.hasFields());
    assertEquals(elementSchemaArtifact.getFieldSchemaArtifact(textFieldName1), textField1);
    assertEquals(elementSchemaArtifact.getFieldSchemaArtifact(textFieldName2), textField2);
    assertEquals(elementSchemaArtifact.elementUi().order().size(), 2);
    assertEquals(elementSchemaArtifact.elementUi().order().get(0), textFieldName1);
    assertEquals(elementSchemaArtifact.elementUi().order().get(1), textFieldName2);
    assertEquals(elementSchemaArtifact.elementUi().propertyLabels().size(), 2);
    assertEquals(elementSchemaArtifact.elementUi().propertyLabels().get(textFieldName2), textField2Label);
    assertEquals(elementSchemaArtifact.elementUi().propertyDescriptions().size(), 2);
    assertEquals(elementSchemaArtifact.elementUi().propertyDescriptions().get(textFieldName2), textField2Description);
  }

  @Test public void testMissingName()
  {
    Assertions.assertThrows(IllegalStateException.class, () -> ElementSchemaArtifact.builder().build());
  }

  @Test public void testEmptyNameIsAcceptedBySetterAndRefusedByBuild()
  {
    ElementSchemaArtifact.Builder builder = ElementSchemaArtifact.builder().withName("");

    IllegalStateException ex = assertThrows(IllegalStateException.class, builder::build);
    assertTrue(ex.getMessage().startsWith("field " + SCHEMA_ORG_NAME + " is empty in "), ex.getMessage());
  }

  @Test public void testBuilderRejectsWhitespaceOnlyName()
  {
    for (String name : BuilderValidationTest.WHITESPACE_ONLY_NAMES) {
      ElementSchemaArtifact.Builder builder = ElementSchemaArtifact.builder();

      IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> builder.withName(name));
      assertEquals("whitespace-only name passed to builder", ex.getMessage());

      // The refusal leaves the builder unchanged, so the internal name follows the next name given.
      ElementSchemaArtifact element = builder.withName("Address").build();
      assertEquals("Address", element.name());
      assertEquals("Address element schema", element.internalName());

      assertThrows(IllegalArgumentException.class, () -> ElementSchemaArtifact.builder(element).withName(name));
    }
  }

  @Test public void testBuilderAcceptsNameThatHasWhitespaceAroundIt()
  {
    assertEquals(" Address\n", ElementSchemaArtifact.builder().withName(" Address\n").build().name());
  }

  @Test public void testRemovingAChildRemovesItsUiEntriesAndLeavesAValidElement() throws IOException
  {
    ElementSchemaArtifact original = ElementSchemaArtifact.builder().withName("Address")
      .withFieldSchema("street", TextField.builder().withName("Street").build(), "Street label", "Street description")
      .withElementSchema("geo", ElementSchemaArtifact.builder().withName("Geo").build())
      .withFieldSchema("zip", TextField.builder().withName("ZIP").build()).build();

    ElementSchemaArtifact pruned = ElementSchemaArtifact.builder(original).withoutFieldSchema("street")
      .withoutElementSchema("geo").build();

    assertEquals(Set.of("zip"), pruned.fieldSchemas().keySet());
    assertTrue(pruned.elementSchemas().isEmpty());
    assertEquals(List.of("zip"), pruned.elementUi().order());
    assertEquals(Set.of("zip"), pruned.elementUi().propertyLabels().keySet());
    assertEquals(Set.of("zip"), pruned.elementUi().propertyDescriptions().keySet());
    ValidationReport report = new CedarValidator()
      .validateTemplateElement(new JsonArtifactRenderer().renderElementSchemaArtifact(pruned));
    assertEquals("true", report.getValidationStatus(), report.getErrors().toString());

    // A removed key is free again, which is how a child is replaced.
    ElementSchemaArtifact replaced = ElementSchemaArtifact.builder(pruned)
      .withFieldSchema("street", TextField.builder().withName("Street").build(), "New label").build();
    assertEquals(List.of("zip", "street"), replaced.elementUi().order());

    ElementSchemaArtifact.Builder builder = ElementSchemaArtifact.builder(original);
    assertThrows(IllegalArgumentException.class, () -> builder.withoutElementSchema("street"));
    assertThrows(IllegalArgumentException.class, () -> builder.withoutFieldSchema("geo"));
    assertThrows(IllegalArgumentException.class, () -> builder.withoutFieldSchema("missing"));
  }

  @Test public void testAKeyHoldingAChildRefusesAnotherChildThroughEveryOverload()
  {
    // Every overload refuses a key the element already uses, for a field and an element alike, with
    // the same message and before changing the builder, so the child already held survives.
    TextField field = TextField.builder().withName("x").build();
    TextField otherField = TextField.builder().withName("x").withDescription("other").build();
    ElementSchemaArtifact element = ElementSchemaArtifact.builder().withName("x").build();
    List<UnaryOperator<ElementSchemaArtifact.Builder>> addChild = List.of(
      b -> b.withElementSchema("x", element),
      b -> b.withElementSchema("x", element, "Label"),
      b -> b.withElementSchema("x", element, "Label", "Description"),
      b -> b.withFieldSchema("x", otherField),
      b -> b.withFieldSchema("x", otherField, "Label"),
      b -> b.withFieldSchema("x", otherField, "Label", "Description"));

    for (UnaryOperator<ElementSchemaArtifact.Builder> add : addChild) {
      for (UnaryOperator<ElementSchemaArtifact.Builder> existing : List.<UnaryOperator<ElementSchemaArtifact.Builder>>of(
        b -> b.withFieldSchema("x", field), b -> b.withElementSchema("x", element))) {
        ElementSchemaArtifact.Builder builder = existing.apply(ElementSchemaArtifact.builder().withName("Parent"));
        ElementSchemaArtifact before = builder.build();

        IllegalArgumentException refusal = assertThrows(IllegalArgumentException.class, () -> add.apply(builder));

        assertEquals("Element already has a child x", refusal.getMessage());
        assertEquals(before, builder.build());
      }
    }
  }
}
