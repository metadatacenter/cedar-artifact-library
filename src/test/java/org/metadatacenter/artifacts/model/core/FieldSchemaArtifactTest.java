package org.metadatacenter.artifacts.model.core;

import org.junit.Assert;
import org.junit.Test;
import org.metadatacenter.model.ModelNodeNames;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class FieldSchemaArtifactTest
{

  @Test public void testCreateFieldSchemaArtifact()
  {
    Map<String, URI> jsonLdContext = ModelNodeNames.SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS;
    List<URI> jsonLdTypes = Collections.singletonList(URI.create("https://schema.metadatacenter.org/core/TemplateField"));
    URI jsonLdId = URI.create("https://repo.metadatacenter.org/fields/4455");
    URI createdBy = URI.create("http://example.com/user");
    URI modifiedBy = URI.create("http://example.com/user");
    OffsetDateTime createdOn = OffsetDateTime.now();
    OffsetDateTime lastUpdatedOn = OffsetDateTime.now();

    URI jsonSchemaSchemaUri = URI.create(ModelNodeNames.JSON_SCHEMA_SCHEMA_IRI);
    String jsonSchemaType = "object";
    String jsonSchemaTitle = "title";
    String jsonSchemaDescription = "description";
    String schemaOrgName = "Schema Org name";
    String schemaOrgDescription = "Schema Org description";
    Optional<String> schemaOrgIdentifier = Optional.of("Schema Org identifier");
    Version modelVersion = new Version(1, 6, 0);
    Optional<Version> artifactVersion = Optional.of(new Version(2, 0, 0));
    Optional<Status> artifactVersionStatus = Optional.of(Status.DRAFT);
    Optional<URI> previousVersion = Optional.of(URI.create("https://repo.metadatacenter.org/template-fields/3232"));
    Optional<URI> derivedFrom = Optional.of(URI.create("https://repo.metadatacenter.org/fields/7666"));
    Optional<URI> propertyURI = Optional.of(URI.create("https://schema.metadatacenter.org/properties/854"));

    FieldSchemaArtifact fieldSchemaArtifact = FieldSchemaArtifact.create(jsonLdContext, jsonLdTypes, Optional.of(jsonLdId),
      Optional.of(createdBy), Optional.of(modifiedBy), Optional.of(createdOn), Optional.of(lastUpdatedOn),
      jsonSchemaSchemaUri, jsonSchemaType, jsonSchemaTitle, jsonSchemaDescription, schemaOrgName, schemaOrgDescription,
      schemaOrgIdentifier, modelVersion, artifactVersion, artifactVersionStatus, previousVersion, derivedFrom,
      FieldUi.builder().withInputType(FieldInputType.TEXTFIELD).build(),
      Optional.empty(), Optional.empty(), Collections.emptyList(),
      false, Optional.empty(), Optional.empty(), propertyURI);

    Assert.assertEquals(jsonLdTypes, fieldSchemaArtifact.jsonLdTypes());
    Assert.assertEquals(jsonLdId, fieldSchemaArtifact.jsonLdId().get());
    Assert.assertEquals(jsonLdContext, fieldSchemaArtifact.jsonLdContext());
    Assert.assertEquals(createdBy, fieldSchemaArtifact.createdBy().get());
    Assert.assertEquals(modifiedBy, fieldSchemaArtifact.modifiedBy().get());
    Assert.assertEquals(createdOn, fieldSchemaArtifact.createdOn().get());
    Assert.assertEquals(lastUpdatedOn, fieldSchemaArtifact.lastUpdatedOn().get());
    Assert.assertEquals(jsonSchemaSchemaUri, fieldSchemaArtifact.jsonSchemaSchemaUri());
    Assert.assertEquals(jsonSchemaType, fieldSchemaArtifact.jsonSchemaType());
    Assert.assertEquals(jsonSchemaTitle, fieldSchemaArtifact.jsonSchemaTitle());
    Assert.assertEquals(jsonSchemaDescription, fieldSchemaArtifact.jsonSchemaDescription());
    Assert.assertEquals(schemaOrgName, fieldSchemaArtifact.name());
    Assert.assertEquals(schemaOrgDescription, fieldSchemaArtifact.description());
    Assert.assertEquals(schemaOrgIdentifier, fieldSchemaArtifact.identifier());
    Assert.assertEquals(modelVersion, fieldSchemaArtifact.modelVersion());
    Assert.assertEquals(artifactVersion, fieldSchemaArtifact.version());
    Assert.assertEquals(artifactVersionStatus, fieldSchemaArtifact.status());
    Assert.assertEquals(previousVersion, fieldSchemaArtifact.previousVersion());
    Assert.assertEquals(derivedFrom, fieldSchemaArtifact.derivedFrom());
    Assert.assertEquals(propertyURI, fieldSchemaArtifact.propertyUri());
  }

  @Test public void testCreateFieldSchemaWithBuilder()
  {
    List<URI> jsonLdTypes = Collections.singletonList(URI.create("https://schema.metadatacenter.org/core/TemplateField"));
    URI jsonLdId = URI.create("https://repo.metadatacenter.org/template-fields/4455");
    URI createdBy = URI.create("http://example.com/user/1");
    URI modifiedBy = URI.create("http://example.com/user/2");
    OffsetDateTime createdOn = OffsetDateTime.now();
    OffsetDateTime lastUpdatedOn = OffsetDateTime.now();

    URI jsonSchemaSchemaUri = URI.create(ModelNodeNames.JSON_SCHEMA_SCHEMA_IRI);
    String jsonSchemaType = "object";
    String schemaOrgName = "My Field";
    String schemaOrgDescription = "My Field description";
    String schemaOrgIdentifier = "id3443";
    Version modelVersion = new Version(1, 6, 0);
    Version version = new Version(2, 0, 0);
    Optional<Status> artifactVersionStatus = Optional.of(Status.DRAFT);
    URI previousVersion = URI.create("https://repo.metadatacenter.org/fields/3232");
    URI derivedFrom = URI.create("https://repo.metadatacenter.org/fields/7666");
    Optional<URI> propertyURI = Optional.of(URI.create("https://schema.metadatacenter.org/properties/854"));

    FieldSchemaArtifact fieldSchemaArtifact = FieldSchemaArtifact.builder().
      withJsonLdId(jsonLdId).
      withName(schemaOrgName).
      withDescription(schemaOrgDescription).withIdentifier(schemaOrgIdentifier).withFieldUi(FieldUi.builder().withInputType(FieldInputType.TEXTFIELD).build()).
      withCreatedBy(createdBy).withCreatedOn(createdOn).withModifiedBy(modifiedBy).withLastUpdatedOn(lastUpdatedOn).
      withVersion(version).
      withPreviousVersion(previousVersion).
      withDerivedFrom(derivedFrom).
      withPropertyURI(propertyURI.get()).
      build();

    Assert.assertEquals(jsonLdTypes, fieldSchemaArtifact.jsonLdTypes());
    Assert.assertEquals(jsonLdId, fieldSchemaArtifact.jsonLdId().get());
    Assert.assertEquals(createdBy, fieldSchemaArtifact.createdBy().get());
    Assert.assertEquals(modifiedBy, fieldSchemaArtifact.modifiedBy().get());
    Assert.assertEquals(createdOn, fieldSchemaArtifact.createdOn().get());
    Assert.assertEquals(lastUpdatedOn, fieldSchemaArtifact.lastUpdatedOn().get());
    Assert.assertEquals(jsonSchemaSchemaUri, fieldSchemaArtifact.jsonSchemaSchemaUri());
    Assert.assertEquals(jsonSchemaType, fieldSchemaArtifact.jsonSchemaType());
    Assert.assertEquals(schemaOrgName, fieldSchemaArtifact.name());
    Assert.assertEquals(schemaOrgDescription, fieldSchemaArtifact.description());
    Assert.assertEquals(schemaOrgIdentifier, fieldSchemaArtifact.identifier().get());
    Assert.assertEquals(modelVersion, fieldSchemaArtifact.modelVersion());
    Assert.assertEquals(version, fieldSchemaArtifact.version().get());
    Assert.assertEquals(artifactVersionStatus, fieldSchemaArtifact.status());
    Assert.assertEquals(previousVersion, fieldSchemaArtifact.previousVersion().get());
    Assert.assertEquals(derivedFrom, fieldSchemaArtifact.derivedFrom().get());
    Assert.assertEquals(propertyURI, fieldSchemaArtifact.propertyUri());

  }

}