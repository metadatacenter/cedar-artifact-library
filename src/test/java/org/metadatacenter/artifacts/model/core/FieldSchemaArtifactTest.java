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

import static org.junit.Assert.*;

public class FieldSchemaArtifactTest
{

  @Test public void testCreateFieldSchemaArtifact()
  {
    Map<String, URI> jsonLdContext = new HashMap<>();
    jsonLdContext.put("key", URI.create("http://example.com/context"));
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

    FieldSchemaArtifact fieldSchemaArtifact = new FieldSchemaArtifact(jsonLdContext, jsonLdTypes, Optional.of(jsonLdId),
      Optional.of(createdBy), Optional.of(modifiedBy), Optional.of(createdOn), Optional.of(lastUpdatedOn),
      jsonSchemaSchemaUri, jsonSchemaType, jsonSchemaTitle, jsonSchemaDescription, schemaOrgName, schemaOrgDescription,
      schemaOrgIdentifier, modelVersion, artifactVersion, artifactVersionStatus, previousVersion, derivedFrom,
      FieldUI.builder().withInputType(FieldInputType.TEXTFIELD).build(),
      Optional.empty(), Optional.empty(), Collections.emptyList(),
      false, Optional.empty(), Optional.empty(), propertyURI);

    Assert.assertEquals(jsonLdTypes, fieldSchemaArtifact.getJsonLdTypes());
    Assert.assertEquals(jsonLdId, fieldSchemaArtifact.getJsonLdId().get());
    Assert.assertEquals(jsonLdContext, fieldSchemaArtifact.getJsonLdContext());
    Assert.assertEquals(createdBy, fieldSchemaArtifact.getCreatedBy().get());
    Assert.assertEquals(modifiedBy, fieldSchemaArtifact.getModifiedBy().get());
    Assert.assertEquals(createdOn, fieldSchemaArtifact.getCreatedOn().get());
    Assert.assertEquals(lastUpdatedOn, fieldSchemaArtifact.getLastUpdatedOn().get());
    Assert.assertEquals(jsonSchemaSchemaUri, fieldSchemaArtifact.getJsonSchemaSchemaUri());
    Assert.assertEquals(jsonSchemaType, fieldSchemaArtifact.getJsonSchemaType());
    Assert.assertEquals(jsonSchemaTitle, fieldSchemaArtifact.getJsonSchemaTitle());
    Assert.assertEquals(jsonSchemaDescription, fieldSchemaArtifact.getJsonSchemaDescription());
    Assert.assertEquals(schemaOrgName, fieldSchemaArtifact.getName());
    Assert.assertEquals(schemaOrgDescription, fieldSchemaArtifact.getDescription());
    Assert.assertEquals(schemaOrgIdentifier, fieldSchemaArtifact.getIdentifier());
    Assert.assertEquals(modelVersion, fieldSchemaArtifact.getModelVersion());
    Assert.assertEquals(artifactVersion, fieldSchemaArtifact.getVersion());
    Assert.assertEquals(artifactVersionStatus, fieldSchemaArtifact.getStatus());
    Assert.assertEquals(previousVersion, fieldSchemaArtifact.getPreviousVersion());
    Assert.assertEquals(derivedFrom, fieldSchemaArtifact.getDerivedFrom());
    Assert.assertEquals(propertyURI, fieldSchemaArtifact.getPropertyURI());
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
    Version artifactVersion = new Version(2, 0, 0);
    Optional<Status> artifactVersionStatus = Optional.of(Status.DRAFT);
    URI previousVersion = URI.create("https://repo.metadatacenter.org/fields/3232");
    URI derivedFrom = URI.create("https://repo.metadatacenter.org/fields/7666");
    Optional<URI> propertyURI = Optional.of(URI.create("https://schema.metadatacenter.org/properties/854"));

    FieldSchemaArtifact fieldSchemaArtifact = FieldSchemaArtifact.builder().
      withJsonLdId(jsonLdId).
      withName(schemaOrgName).
      withDescription(schemaOrgDescription).
      withSchemaOrgIdentifier(schemaOrgIdentifier).
      withFieldUI(FieldUI.builder().withInputType(FieldInputType.TEXTFIELD).build()).
      withCreatedBy(createdBy).withCreatedOn(createdOn).withModifiedBy(modifiedBy).withLastUpdatedOn(lastUpdatedOn).
      withArtifactVersion(artifactVersion).
      withPreviousVersion(previousVersion).
      withDerivedFrom(derivedFrom).
      withPropertyURI(propertyURI.get()).
      build();

    Assert.assertEquals(jsonLdTypes, fieldSchemaArtifact.getJsonLdTypes());
    Assert.assertEquals(jsonLdId, fieldSchemaArtifact.getJsonLdId().get());
    Assert.assertEquals(createdBy, fieldSchemaArtifact.getCreatedBy().get());
    Assert.assertEquals(modifiedBy, fieldSchemaArtifact.getModifiedBy().get());
    Assert.assertEquals(createdOn, fieldSchemaArtifact.getCreatedOn().get());
    Assert.assertEquals(lastUpdatedOn, fieldSchemaArtifact.getLastUpdatedOn().get());
    Assert.assertEquals(jsonSchemaSchemaUri, fieldSchemaArtifact.getJsonSchemaSchemaUri());
    Assert.assertEquals(jsonSchemaType, fieldSchemaArtifact.getJsonSchemaType());
    Assert.assertEquals(schemaOrgName, fieldSchemaArtifact.getName());
    Assert.assertEquals(schemaOrgDescription, fieldSchemaArtifact.getDescription());
    Assert.assertEquals(schemaOrgIdentifier, fieldSchemaArtifact.getIdentifier().get());
    Assert.assertEquals(modelVersion, fieldSchemaArtifact.getModelVersion());
    Assert.assertEquals(artifactVersion, fieldSchemaArtifact.getVersion().get());
    Assert.assertEquals(artifactVersionStatus, fieldSchemaArtifact.getStatus());
    Assert.assertEquals(previousVersion, fieldSchemaArtifact.getPreviousVersion().get());
    Assert.assertEquals(derivedFrom, fieldSchemaArtifact.getDerivedFrom().get());
    Assert.assertEquals(propertyURI, fieldSchemaArtifact.getPropertyURI());

  }

}