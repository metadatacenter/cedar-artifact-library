package org.metadatacenter.artifacts.model.core;

import org.junit.Assert;
import org.junit.Test;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.metadatacenter.model.ModelNodeNames.ELEMENT_SCHEMA_ARTIFACT_TYPE_IRI;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_SCHEMA_IRI;

public class ElementSchemaArtifactTest
{

  @Test public void testConstructor()
  {
    URI jsonLdId = URI.create("http://example.com/artifact");
    Map<String, URI> jsonLdContext = new HashMap<>();
    jsonLdContext.put("key", URI.create("http://example.com/context"));
    URI createdBy = URI.create("http://example.com/user");
    URI modifiedBy = URI.create("http://example.com/user");
    OffsetDateTime createdOn = OffsetDateTime.now();
    OffsetDateTime lastUpdatedOn = OffsetDateTime.now();
    List<URI> jsonLdTypes = Collections.singletonList(URI.create(ELEMENT_SCHEMA_ARTIFACT_TYPE_IRI));
    URI jsonSchemaSchemaUri = URI.create(JSON_SCHEMA_SCHEMA_IRI);
    String jsonSchemaType = "type";
    String jsonSchemaTitle = "title";
    String jsonSchemaDescription = "description";
    String name = "Schema Org name";
    String description = "Schema Org description";
    Optional<String> identifier = Optional.of("Schema Org identifier");
    Version modelVersion = new Version(1, 6, 0);
    Optional<Version> version = Optional.of(new Version(2, 0, 0));
    Optional<Status> status = Optional.of(Status.DRAFT);
    Optional<URI> previousVersion = Optional.of(URI.create("https://repo.metadatacenter.org/templates/3232"));
    Optional<URI> derivedFrom = Optional.of(URI.create("http://example.com/derived"));
    Optional<Integer> minItems = Optional.of(1);
    Optional<Integer> maxItems = Optional.of(3);
    Optional<URI> propertyURI = Optional.of(URI.create("https://schema.metadatacenter.org/properties/434"));

    ElementSchemaArtifact elementSchemaArtifact = ElementSchemaArtifact.create(jsonLdContext, jsonLdTypes, Optional.of(jsonLdId),
      Optional.of(createdBy), Optional.of(modifiedBy), Optional.of(createdOn), Optional.of(lastUpdatedOn), jsonSchemaSchemaUri, jsonSchemaType, jsonSchemaTitle,
      jsonSchemaDescription, name, description, identifier, modelVersion, version, status, previousVersion, derivedFrom,
      Collections.emptyMap(), Collections.emptyMap(), ElementUi.builder().build(), false, minItems, maxItems,
      propertyURI);

    Assert.assertEquals(jsonLdTypes, elementSchemaArtifact.jsonLdTypes());
    Assert.assertEquals(jsonLdId, elementSchemaArtifact.jsonLdId().get());
    Assert.assertEquals(jsonLdContext, elementSchemaArtifact.jsonLdContext());
    Assert.assertEquals(createdBy, elementSchemaArtifact.createdBy().get());
    Assert.assertEquals(modifiedBy, elementSchemaArtifact.modifiedBy().get());
    Assert.assertEquals(createdOn, elementSchemaArtifact.createdOn().get());
    Assert.assertEquals(lastUpdatedOn, elementSchemaArtifact.lastUpdatedOn().get());
    Assert.assertEquals(jsonSchemaSchemaUri, elementSchemaArtifact.jsonSchemaSchemaUri());
    Assert.assertEquals(jsonSchemaType, elementSchemaArtifact.jsonSchemaType());
    Assert.assertEquals(jsonSchemaTitle, elementSchemaArtifact.jsonSchemaTitle());
    Assert.assertEquals(jsonSchemaDescription, elementSchemaArtifact.jsonSchemaDescription());
    Assert.assertEquals(name, elementSchemaArtifact.name());
    Assert.assertEquals(description, elementSchemaArtifact.description());
    Assert.assertEquals(identifier, elementSchemaArtifact.identifier());
    Assert.assertEquals(modelVersion, elementSchemaArtifact.modelVersion());
    Assert.assertEquals(version, elementSchemaArtifact.version());
    Assert.assertEquals(status, elementSchemaArtifact.status());
    Assert.assertEquals(previousVersion, elementSchemaArtifact.previousVersion());
    Assert.assertEquals(derivedFrom, elementSchemaArtifact.derivedFrom());
    Assert.assertEquals(propertyURI, elementSchemaArtifact.propertyUri());
  }
}