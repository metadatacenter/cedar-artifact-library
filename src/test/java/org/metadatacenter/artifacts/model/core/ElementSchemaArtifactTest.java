package org.metadatacenter.artifacts.model.core;

import org.junit.Assert;
import org.junit.Test;
import org.metadatacenter.artifacts.model.core.ui.ElementUi;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.metadatacenter.model.ModelNodeNames.ELEMENT_SCHEMA_ARTIFACT_TYPE_IRI;
import static org.metadatacenter.model.ModelNodeNames.PARENT_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS;

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
    Optional<String> preferredLabel = Optional.of("A preferred label");
    Optional<String> language = Optional.of("en");

    ElementSchemaArtifact elementSchemaArtifact = ElementSchemaArtifact.create(internalName, internalDescription,
      jsonLdContext, jsonLdTypes, Optional.of(jsonLdId), Optional.of(instanceJsonLdType), name, description, identifier,
      version, status, previousVersion, derivedFrom, Optional.of(createdBy), Optional.of(modifiedBy),
      Optional.of(createdOn), Optional.of(lastUpdatedOn), new LinkedHashMap<>(), new LinkedHashMap<>(), false, minItems,
      maxItems, propertyUri, preferredLabel, language, ElementUi.builder().build(), Optional.empty());

    Assert.assertEquals(jsonLdTypes, elementSchemaArtifact.jsonLdTypes());
    Assert.assertEquals(jsonLdId, elementSchemaArtifact.jsonLdId().get());
    Assert.assertEquals(instanceJsonLdType, elementSchemaArtifact.instanceJsonLdType().get());
    Assert.assertEquals(jsonLdContext, elementSchemaArtifact.jsonLdContext());
    Assert.assertEquals(createdBy, elementSchemaArtifact.createdBy().get());
    Assert.assertEquals(modifiedBy, elementSchemaArtifact.modifiedBy().get());
    Assert.assertEquals(createdOn, elementSchemaArtifact.createdOn().get());
    Assert.assertEquals(lastUpdatedOn, elementSchemaArtifact.lastUpdatedOn().get());
    Assert.assertEquals(internalName, elementSchemaArtifact.internalName());
    Assert.assertEquals(internalDescription, elementSchemaArtifact.internalDescription());
    Assert.assertEquals(name, elementSchemaArtifact.name());
    Assert.assertEquals(description, elementSchemaArtifact.description());
    Assert.assertEquals(identifier, elementSchemaArtifact.identifier());
    Assert.assertEquals(version, elementSchemaArtifact.version());
    Assert.assertEquals(status, elementSchemaArtifact.status());
    Assert.assertEquals(previousVersion, elementSchemaArtifact.previousVersion());
    Assert.assertEquals(derivedFrom, elementSchemaArtifact.derivedFrom());
    Assert.assertEquals(propertyUri, elementSchemaArtifact.propertyUri());
    Assert.assertEquals(preferredLabel, elementSchemaArtifact.preferredLabel());
    Assert.assertEquals(language, elementSchemaArtifact.language());
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

  @Test(expected = IllegalStateException.class) public void testMissingName()
  {
    ElementSchemaArtifact elementSchemaArtifact = ElementSchemaArtifact.builder().build();
  }

}