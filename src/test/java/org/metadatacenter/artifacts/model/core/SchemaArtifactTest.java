package org.metadatacenter.artifacts.model.core;


import org.junit.Assert;
import org.junit.Test;
import org.metadatacenter.model.ModelNodeNames;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.*;

public class SchemaArtifactTest {

  @Test
  public void testConstructorWithArtifact() {
    List<URI> jsonLdTypes = Collections.singletonList(URI.create("http://example.com/type"));
    URI jsonLdId = URI.create("http://example.com/artifact");
    Map<String, URI> jsonLdContext = new HashMap<>();
    jsonLdContext.put("key", URI.create("http://example.com/context"));
    URI createdBy = URI.create("http://example.com/user");
    URI modifiedBy = URI.create("http://example.com/user");
    OffsetDateTime createdOn = OffsetDateTime.now();
    OffsetDateTime lastUpdatedOn = OffsetDateTime.now();
    Artifact artifact = new Artifact(jsonLdTypes, Optional.of(jsonLdId), jsonLdContext,
      Optional.of(createdBy), Optional.of(modifiedBy), Optional.of(createdOn), Optional.of(lastUpdatedOn));

    URI jsonSchemaSchemaUri = URI.create(ModelNodeNames.JSON_SCHEMA_SCHEMA_IRI);
    String jsonSchemaType = "type";
    String jsonSchemaTitle = "title";
    String jsonSchemaDescription = "description";
    String schemaOrgName = "schemaOrgName";
    String schemaOrgDescription = "schemaOrgDescription";
    Version modelVersion = new Version(1, 0, 0);
    Optional<Version> artifactVersion = Optional.of(new Version(2, 0, 0));
    Optional<Status> artifactVersionStatus = Optional.of(Status.DRAFT);
    Optional<URI> previousVersion = Optional.of(URI.create("https://repo.metadatacenter.org/templates/3232"));
    Optional<URI> derivedFrom = Optional.of(URI.create("http://example.com/derived"));

    SchemaArtifact schemaArtifact = new SchemaArtifact(artifact, jsonSchemaSchemaUri, jsonSchemaType,
      jsonSchemaTitle, jsonSchemaDescription, schemaOrgName, schemaOrgDescription,
      modelVersion, artifactVersion, artifactVersionStatus, previousVersion, derivedFrom);

    Assert.assertEquals(jsonLdTypes, schemaArtifact.getJsonLdTypes());
    Assert.assertEquals(jsonLdId, schemaArtifact.getJsonLdId().get());
    Assert.assertEquals(jsonLdContext, schemaArtifact.getJsonLdContext());
    Assert.assertEquals(createdBy, schemaArtifact.getCreatedBy().get());
    Assert.assertEquals(modifiedBy, schemaArtifact.getModifiedBy().get());
    Assert.assertEquals(createdOn, schemaArtifact.getCreatedOn().get());
    Assert.assertEquals(lastUpdatedOn, schemaArtifact.getLastUpdatedOn().get());
    Assert.assertEquals(jsonSchemaSchemaUri, schemaArtifact.getJsonSchemaSchemaUri());
    Assert.assertEquals(jsonSchemaType, schemaArtifact.getJsonSchemaType());
    Assert.assertEquals(jsonSchemaTitle, schemaArtifact.getJsonSchemaTitle());
    Assert.assertEquals(jsonSchemaDescription, schemaArtifact.getJsonSchemaDescription());
    Assert.assertEquals(schemaOrgName, schemaArtifact.getName());
    Assert.assertEquals(schemaOrgDescription, schemaArtifact.getDescription());
    Assert.assertEquals(modelVersion, schemaArtifact.getModelVersion());
    Assert.assertEquals(artifactVersion, schemaArtifact.getVersion());
    Assert.assertEquals(artifactVersionStatus, schemaArtifact.getStatus());
    Assert.assertEquals(previousVersion, schemaArtifact.getPreviousVersion());
    Assert.assertEquals(derivedFrom, schemaArtifact.getDerivedFrom());
  }

  @Test
  public void testConstructorWithoutArtifact() {
    URI jsonLdId = URI.create("http://example.com/artifact");
    Map<String, URI> jsonLdContext = new HashMap<>();
    jsonLdContext.put("key", URI.create("http://example.com/context"));
    URI createdBy = URI.create("http://example.com/user");
    URI modifiedBy = URI.create("http://example.com/user");
    OffsetDateTime createdOn = OffsetDateTime.now();
    OffsetDateTime lastUpdatedOn = OffsetDateTime.now();

    List<URI> jsonLdTypes = Collections.singletonList(URI.create("http://example.com/type"));
    URI jsonSchemaSchemaUri = URI.create(ModelNodeNames.JSON_SCHEMA_SCHEMA_IRI);
    String jsonSchemaType = "type";
    String jsonSchemaTitle = "title";
    String jsonSchemaDescription = "description";
    String schemaOrgName = "schemaOrgName";
    String schemaOrgDescription = "schemaOrgDescription";
    Version modelVersion = new Version(1, 0, 0);
    Optional<Version> artifactVersion = Optional.of(new Version(2, 0, 0));
    Optional<Status> artifactVersionStatus = Optional.of(Status.DRAFT);
    Optional<URI> previousVersion = Optional.of(URI.create("https://repo.metadatacenter.org/templates/3232"));
    Optional<URI> derivedFrom = Optional.of(URI.create("http://example.com/derived"));

    SchemaArtifact schemaArtifact = new SchemaArtifact(jsonLdTypes, Optional.of(jsonLdId), jsonLdContext,
      Optional.of(createdBy), Optional.of(modifiedBy), Optional.of(createdOn), Optional.of(lastUpdatedOn),
      jsonSchemaSchemaUri, jsonSchemaType, jsonSchemaTitle, jsonSchemaDescription,
      schemaOrgName, schemaOrgDescription, modelVersion, artifactVersion, artifactVersionStatus,
      previousVersion, derivedFrom);

    Assert.assertEquals(jsonLdTypes, schemaArtifact.getJsonLdTypes());
    Assert.assertEquals(jsonLdId, schemaArtifact.getJsonLdId().get());
    Assert.assertEquals(jsonLdContext, schemaArtifact.getJsonLdContext());
    Assert.assertEquals(createdBy, schemaArtifact.getCreatedBy().get());
    Assert.assertEquals(modifiedBy, schemaArtifact.getModifiedBy().get());
    Assert.assertEquals(createdOn, schemaArtifact.getCreatedOn().get());
    Assert.assertEquals(lastUpdatedOn, schemaArtifact.getLastUpdatedOn().get());
    Assert.assertEquals(jsonSchemaSchemaUri, schemaArtifact.getJsonSchemaSchemaUri());
    Assert.assertEquals(jsonSchemaType, schemaArtifact.getJsonSchemaType());
    Assert.assertEquals(jsonSchemaTitle, schemaArtifact.getJsonSchemaTitle());
    Assert.assertEquals(jsonSchemaDescription, schemaArtifact.getJsonSchemaDescription());
    Assert.assertEquals(schemaOrgName, schemaArtifact.getName());
    Assert.assertEquals(schemaOrgDescription, schemaArtifact.getDescription());
    Assert.assertEquals(modelVersion, schemaArtifact.getModelVersion());
    Assert.assertEquals(artifactVersion, schemaArtifact.getVersion());
    Assert.assertEquals(artifactVersionStatus, schemaArtifact.getStatus());
    Assert.assertEquals(previousVersion, schemaArtifact.getPreviousVersion());
    Assert.assertEquals(derivedFrom, schemaArtifact.getDerivedFrom());
  }
}