package org.metadatacenter.artifacts.model.core;

import org.junit.Assert;
import org.junit.Test;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ArtifactTest {

  @Test
  public void testGetJsonLdId() {
    URI jsonLdId = URI.create("http://example.com/artifact");
    Artifact artifact = new Artifact(Collections.emptyList(), Optional.of(jsonLdId), Collections.emptyMap(),
      Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
    Assert.assertEquals(Optional.of(jsonLdId), artifact.getJsonLdId());
  }

  @Test
  public void testGetJsonLdContext() {
    Map<String, URI> jsonLdContext = new HashMap<>();
    jsonLdContext.put("key", URI.create("http://example.com/context"));
    Artifact artifact = new Artifact(Collections.emptyList(), Optional.empty(), jsonLdContext,
      Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
    Assert.assertEquals(jsonLdContext, artifact.getJsonLdContext());
  }

  @Test
  public void testGetCreatedBy() {
    URI createdBy = URI.create("http://example.com/user");
    Artifact artifact = new Artifact(Collections.emptyList(), Optional.empty(), Collections.emptyMap(),
      Optional.of(createdBy), Optional.empty(), Optional.empty(), Optional.empty());
    Assert.assertEquals(Optional.of(createdBy), artifact.getCreatedBy());
  }

  @Test
  public void testGetModifiedBy() {
    URI modifiedBy = URI.create("http://example.com/user");
    Artifact artifact = new Artifact(Collections.emptyList(), Optional.empty(), Collections.emptyMap(),
      Optional.empty(), Optional.of(modifiedBy), Optional.empty(), Optional.empty());
    Assert.assertEquals(Optional.of(modifiedBy), artifact.getModifiedBy());
  }

  @Test
  public void testGetCreatedOn() {
    OffsetDateTime createdOn = OffsetDateTime.now();
    Artifact artifact = new Artifact(Collections.emptyList(), Optional.empty(), Collections.emptyMap(),
      Optional.empty(), Optional.empty(), Optional.of(createdOn), Optional.empty());
    Assert.assertEquals(Optional.of(createdOn), artifact.getCreatedOn());
  }

  @Test
  public void testGetLastUpdatedOn() {
    OffsetDateTime lastUpdatedOn = OffsetDateTime.now();
    Artifact artifact = new Artifact(Collections.emptyList(), Optional.empty(), Collections.emptyMap(),
      Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(lastUpdatedOn));
    Assert.assertEquals(Optional.of(lastUpdatedOn), artifact.getLastUpdatedOn());
  }
}