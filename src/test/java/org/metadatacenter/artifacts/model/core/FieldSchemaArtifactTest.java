package org.metadatacenter.artifacts.model.core;

import org.junit.Assert;
import org.junit.Test;
import org.metadatacenter.artifacts.model.core.fields.FieldInputType;
import org.metadatacenter.artifacts.model.core.ui.FieldUi;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

import static org.metadatacenter.model.ModelNodeNames.FIELD_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS;

public class FieldSchemaArtifactTest
{

  @Test public void testCreateTextField()
  {
    LinkedHashMap<String, URI> jsonLdContext = new LinkedHashMap<>(FIELD_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS);
    List<URI> jsonLdTypes = Collections.singletonList(
      URI.create("https://schema.metadatacenter.org/core/TemplateField"));
    URI jsonLdId = URI.create("https://repo.metadatacenter.org/fields/4455");
    URI createdBy = URI.create("http://example.com/user");
    URI modifiedBy = URI.create("http://example.com/user");
    OffsetDateTime createdOn = OffsetDateTime.now();
    OffsetDateTime lastUpdatedOn = OffsetDateTime.now();

    String internalName = "title";
    String internalDescription = "description";
    String name = "Schema Org name";
    String description = "Schema Org description";
    Optional<String> identifier = Optional.of("Schema Org identifier");
    Optional<String> preferredLabel = Optional.of("My label");
    List<String> alternateLabels = Collections.emptyList();
    Optional<Version> version = Optional.of(new Version(2, 0, 0));
    Optional<Status> status = Optional.of(Status.DRAFT);
    Optional<URI> previousVersion = Optional.of(URI.create("https://repo.metadatacenter.org/template-fields/3232"));
    Optional<URI> derivedFrom = Optional.of(URI.create("https://repo.metadatacenter.org/fields/7666"));
    Optional<URI> propertyUri = Optional.of(URI.create("https://schema.metadatacenter.org/properties/854"));
    Optional<String> language = Optional.of("en");
    Optional<Integer> minItems = Optional.of(2);
    Optional<Integer> maxItems = Optional.of(4);

    FieldSchemaArtifact fieldSchemaArtifact = FieldSchemaArtifact.create(internalName, internalDescription,
      jsonLdContext, jsonLdTypes, Optional.of(jsonLdId), name, description, identifier, version, status,
      previousVersion, derivedFrom, false, minItems, maxItems, propertyUri, Optional.of(createdBy),
      Optional.of(modifiedBy), Optional.of(createdOn), Optional.of(lastUpdatedOn), preferredLabel, alternateLabels,
      language, FieldUi.builder().withInputType(FieldInputType.TEXTFIELD).build(), Optional.empty(), Optional.empty());

    Assert.assertEquals(internalName, fieldSchemaArtifact.internalName());
    Assert.assertEquals(internalDescription, fieldSchemaArtifact.internalDescription());
    Assert.assertEquals(jsonLdTypes, fieldSchemaArtifact.jsonLdTypes());
    Assert.assertEquals(jsonLdId, fieldSchemaArtifact.jsonLdId().get());
    Assert.assertEquals(jsonLdContext, fieldSchemaArtifact.jsonLdContext());
    Assert.assertEquals(createdBy, fieldSchemaArtifact.createdBy().get());
    Assert.assertEquals(modifiedBy, fieldSchemaArtifact.modifiedBy().get());
    Assert.assertEquals(createdOn, fieldSchemaArtifact.createdOn().get());
    Assert.assertEquals(lastUpdatedOn, fieldSchemaArtifact.lastUpdatedOn().get());
    Assert.assertEquals(name, fieldSchemaArtifact.name());
    Assert.assertEquals(description, fieldSchemaArtifact.description());
    Assert.assertEquals(identifier, fieldSchemaArtifact.identifier());
    Assert.assertEquals(preferredLabel, fieldSchemaArtifact.preferredLabel());
    Assert.assertEquals(alternateLabels, fieldSchemaArtifact.alternateLabels());
    Assert.assertEquals(version, fieldSchemaArtifact.version());
    Assert.assertEquals(status, fieldSchemaArtifact.status());
    Assert.assertEquals(previousVersion, fieldSchemaArtifact.previousVersion());
    Assert.assertEquals(derivedFrom, fieldSchemaArtifact.derivedFrom());
    Assert.assertEquals(propertyUri, fieldSchemaArtifact.propertyUri());
    Assert.assertEquals(language, fieldSchemaArtifact.language());
    Assert.assertEquals(minItems, fieldSchemaArtifact.minItems());
  }

  @Test public void testCreateTextFieldWithBuilder()
  {
    String name = "My Field";
    String description = "My Field description";
    String identifier = "id3443";
    String preferredLabel = "My label";
    List<String> alternateLabels = Collections.emptyList();
    Version version = new Version(2, 0, 0);
    Status status = Status.DRAFT;
    Boolean requiredValue = true;
    Integer minLength = 0;
    Integer maxLength = 10;
    URI jsonLdId = URI.create("https://repo.metadatacenter.org/template-fields/4455");
    URI createdBy = URI.create("http://example.com/user/1");
    URI modifiedBy = URI.create("http://example.com/user/2");
    OffsetDateTime createdOn = OffsetDateTime.now();
    OffsetDateTime lastUpdatedOn = OffsetDateTime.now();
    URI previousVersion = URI.create("https://repo.metadatacenter.org/fields/3232");
    URI derivedFrom = URI.create("https://repo.metadatacenter.org/fields/7666");
    URI propertyUri = URI.create("https://schema.metadatacenter.org/properties/854");

    TextField textField = TextField.builder().withJsonLdId(jsonLdId).withName(name).withDescription(description)
      .withIdentifier(identifier).withPreferredLabel(preferredLabel).withAlternateLabels(alternateLabels)
      .withVersion(version).withStatus(status).withRequiredValue(requiredValue).withMinLength(minLength)
      .withMaxLength(maxLength).withCreatedBy(createdBy).withCreatedOn(createdOn).withModifiedBy(modifiedBy)
      .withLastUpdatedOn(lastUpdatedOn).withPreviousVersion(previousVersion).withDerivedFrom(derivedFrom)
      .withPropertyUri(propertyUri).build();

    Assert.assertEquals(jsonLdId, textField.jsonLdId().get());
    Assert.assertEquals(createdBy, textField.createdBy().get());
    Assert.assertEquals(modifiedBy, textField.modifiedBy().get());
    Assert.assertEquals(createdOn, textField.createdOn().get());
    Assert.assertEquals(lastUpdatedOn, textField.lastUpdatedOn().get());
    Assert.assertEquals(name, textField.name());
    Assert.assertEquals(description, textField.description());
    Assert.assertEquals(identifier, textField.identifier().get());
    Assert.assertEquals(preferredLabel, textField.preferredLabel().get());
    Assert.assertEquals(alternateLabels, textField.alternateLabels());
    Assert.assertEquals(version, textField.version().get());
    Assert.assertEquals(status, textField.status().get());
    Assert.assertEquals(previousVersion, textField.previousVersion().get());
    Assert.assertEquals(derivedFrom, textField.derivedFrom().get());
    Assert.assertEquals(propertyUri, textField.propertyUri().get());
    Assert.assertEquals(requiredValue, textField.requiredValue());
    Assert.assertEquals(minLength, textField.minLength().get());
    Assert.assertEquals(maxLength, textField.maxLength().get());
  }

}