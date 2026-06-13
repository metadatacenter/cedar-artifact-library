package org.metadatacenter.artifacts.model.core;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.core.fields.FieldInputType;
import org.metadatacenter.artifacts.model.core.ui.FieldUi;
import org.metadatacenter.artifacts.model.core.ui.StaticFieldUi;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

import static org.metadatacenter.model.ModelNodeNames.FIELD_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS;
import static org.metadatacenter.model.ModelNodeNames.STATIC_FIELD_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS;
import static org.metadatacenter.model.ModelNodeNames.STATIC_FIELD_SCHEMA_ARTIFACT_TYPE_IRI;

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

    Assertions.assertEquals(internalName, fieldSchemaArtifact.internalName());
    Assertions.assertEquals(internalDescription, fieldSchemaArtifact.internalDescription());
    Assertions.assertEquals(jsonLdTypes, fieldSchemaArtifact.jsonLdTypes());
    Assertions.assertEquals(jsonLdId, fieldSchemaArtifact.jsonLdId().get());
    Assertions.assertEquals(jsonLdContext, fieldSchemaArtifact.jsonLdContext());
    Assertions.assertEquals(createdBy, fieldSchemaArtifact.createdBy().get());
    Assertions.assertEquals(modifiedBy, fieldSchemaArtifact.modifiedBy().get());
    Assertions.assertEquals(createdOn, fieldSchemaArtifact.createdOn().get());
    Assertions.assertEquals(lastUpdatedOn, fieldSchemaArtifact.lastUpdatedOn().get());
    Assertions.assertEquals(name, fieldSchemaArtifact.name());
    Assertions.assertEquals(description, fieldSchemaArtifact.description());
    Assertions.assertEquals(identifier, fieldSchemaArtifact.identifier());
    Assertions.assertEquals(preferredLabel, fieldSchemaArtifact.preferredLabel());
    Assertions.assertEquals(alternateLabels, fieldSchemaArtifact.alternateLabels());
    Assertions.assertEquals(version, fieldSchemaArtifact.version());
    Assertions.assertEquals(status, fieldSchemaArtifact.status());
    Assertions.assertEquals(previousVersion, fieldSchemaArtifact.previousVersion());
    Assertions.assertEquals(derivedFrom, fieldSchemaArtifact.derivedFrom());
    Assertions.assertEquals(propertyUri, fieldSchemaArtifact.propertyUri());
    Assertions.assertEquals(language, fieldSchemaArtifact.language());
    Assertions.assertEquals(minItems, fieldSchemaArtifact.minItems());
  }

  @Test public void testCreateYouTubeFieldPreservesPreferredLabel()
  {
    // Regression test for a positional-argument bug in FieldSchemaArtifact.create's YOUTUBE arm:
    // the outer `language` variable was being passed into YouTubeField.create's preferredLabel slot,
    // silently discarding the outer preferredLabel for YouTube fields.
    LinkedHashMap<String, URI> jsonLdContext = new LinkedHashMap<>(STATIC_FIELD_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS);
    List<URI> jsonLdTypes = Collections.singletonList(URI.create(STATIC_FIELD_SCHEMA_ARTIFACT_TYPE_IRI));

    Optional<String> preferredLabel = Optional.of("Pref");
    Optional<String> language = Optional.of("en");

    FieldSchemaArtifact fieldSchemaArtifact = FieldSchemaArtifact.create("yt internal name", "yt internal description",
      jsonLdContext, jsonLdTypes, Optional.empty(), "yt name", "yt description", Optional.empty(), Optional.empty(),
      Optional.empty(), Optional.empty(), Optional.empty(), false, Optional.empty(), Optional.empty(), Optional.empty(),
      Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), preferredLabel, Collections.emptyList(),
      language, StaticFieldUi.youTubeFieldUiBuilder().build(), Optional.empty(), Optional.empty());

    Assertions.assertInstanceOf(YouTubeField.class, fieldSchemaArtifact);
    Assertions.assertEquals(preferredLabel, fieldSchemaArtifact.preferredLabel(),
      "outer preferredLabel must be preserved on YouTube field");
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

    Assertions.assertEquals(jsonLdId, textField.jsonLdId().get());
    Assertions.assertEquals(createdBy, textField.createdBy().get());
    Assertions.assertEquals(modifiedBy, textField.modifiedBy().get());
    Assertions.assertEquals(createdOn, textField.createdOn().get());
    Assertions.assertEquals(lastUpdatedOn, textField.lastUpdatedOn().get());
    Assertions.assertEquals(name, textField.name());
    Assertions.assertEquals(description, textField.description());
    Assertions.assertEquals(identifier, textField.identifier().get());
    Assertions.assertEquals(preferredLabel, textField.preferredLabel().get());
    Assertions.assertEquals(alternateLabels, textField.alternateLabels());
    Assertions.assertEquals(version, textField.version().get());
    Assertions.assertEquals(status, textField.status().get());
    Assertions.assertEquals(previousVersion, textField.previousVersion().get());
    Assertions.assertEquals(derivedFrom, textField.derivedFrom().get());
    Assertions.assertEquals(propertyUri, textField.propertyUri().get());
    Assertions.assertEquals(requiredValue, textField.requiredValue());
    Assertions.assertEquals(minLength, textField.minLength().get());
    Assertions.assertEquals(maxLength, textField.maxLength().get());
  }

}
