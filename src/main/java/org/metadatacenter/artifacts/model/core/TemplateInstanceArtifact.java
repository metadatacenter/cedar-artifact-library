package org.metadatacenter.artifacts.model.core;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class TemplateInstanceArtifact extends InstanceArtifact implements ParentInstanceArtifact
{
  private final URI isBasedOn;
  private final Map<String, List<ElementInstanceArtifact>> elementInstances;
  private final Map<String, List<FieldInstanceArtifact>> fieldInstances;

  public TemplateInstanceArtifact(InstanceArtifact instanceArtifact, URI isBasedOn,
    Map<String, List<ElementInstanceArtifact>> elementInstances,
    Map<String, List<FieldInstanceArtifact>> fieldInstances)
  {
    super(instanceArtifact);
    this.isBasedOn = isBasedOn;
    this.elementInstances = Collections.unmodifiableMap(elementInstances);
    this.fieldInstances = Collections.unmodifiableMap(fieldInstances);
  }

  public TemplateInstanceArtifact(List<URI> jsonLdTypes, Optional<URI> jsonLdId, Map<String, URI> jsonLdContext,
    Optional<URI> createdBy, Optional<URI> modifiedBy,
    Optional<OffsetDateTime> createdOn, Optional<OffsetDateTime> lastUpdatedOn,
    URI isBasedOn,
    Map<String, List<ElementInstanceArtifact>> elementInstances,
    Map<String, List<FieldInstanceArtifact>> fieldInstances)
  {
    super(jsonLdTypes, jsonLdId, jsonLdContext, createdBy, modifiedBy, createdOn, lastUpdatedOn);
    this.isBasedOn = isBasedOn;
    this.elementInstances = Collections.unmodifiableMap(elementInstances);
    this.fieldInstances = Collections.unmodifiableMap(fieldInstances);
  }

  public TemplateInstanceArtifact(TemplateInstanceArtifact templateInstanceArtifact)
  {
    super(templateInstanceArtifact);
    this.isBasedOn = templateInstanceArtifact.isBasedOn;
    this.elementInstances = templateInstanceArtifact.elementInstances;
    this.fieldInstances = templateInstanceArtifact.fieldInstances;
  }

  private TemplateInstanceArtifact(Builder builder) {
    super(builder.jsonLdTypes, builder.jsonLdId, builder.jsonLdContext, builder.createdBy, builder.modifiedBy, builder.createdOn, builder.lastUpdatedOn);
    this.isBasedOn = builder.isBasedOn;
    this.elementInstances = Collections.unmodifiableMap(builder.elementInstances);
    this.fieldInstances = Collections.unmodifiableMap(builder.fieldInstances);
  }

  public URI getIsBasedOn()
  {
    return isBasedOn;
  }

  @Override public Map<String, List<ElementInstanceArtifact>> getElementInstances()
  {
    return elementInstances;
  }

  @Override public Map<String, List<FieldInstanceArtifact>> getFieldInstances()
  {
    return fieldInstances;
  }

  @Override public String toString()
  {
    return super.toString() + "\n TemplateInstanceArtifact{" + "isBasedOn='" + isBasedOn + '\'' + ", elementInstances=" + elementInstances
      + ", fieldInstances=" + fieldInstances + '}';
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private List<URI> jsonLdTypes = Collections.emptyList();
    private Optional<URI> jsonLdId = Optional.empty();
    private Map<String, URI> jsonLdContext = Collections.emptyMap();
    private Optional<URI> createdBy = Optional.empty();
    private Optional<URI> modifiedBy = Optional.empty();
    private Optional<OffsetDateTime> createdOn = Optional.empty();
    private Optional<OffsetDateTime> lastUpdatedOn = Optional.empty();
    private URI isBasedOn;
    private Map<String, List<ElementInstanceArtifact>> elementInstances = Collections.emptyMap();
    private Map<String, List<FieldInstanceArtifact>> fieldInstances = Collections.emptyMap();

    private Builder() {
    }

    public Builder withJsonLdType(URI jsonLdType) {
      this.jsonLdTypes.add(jsonLdType);
      return this;
    }

    public Builder withJsonLdId(Optional<URI> jsonLdId) {
      this.jsonLdId = jsonLdId;
      return this;
    }

    public Builder withJsonLdContext(Map<String, URI> jsonLdContext) {
      this.jsonLdContext = jsonLdContext;
      return this;
    }

    public Builder withCreatedBy(URI createdBy) {
      this.createdBy = Optional.of(createdBy);
      return this;
    }

    public Builder withModifiedBy(URI modifiedBy) {
      this.modifiedBy = Optional.of(modifiedBy);
      return this;
    }

    public Builder withCreatedOn(OffsetDateTime createdOn) {
      this.createdOn = Optional.of(createdOn);
      return this;
    }

    public Builder withLastUpdatedOn(OffsetDateTime lastUpdatedOn) {
      this.lastUpdatedOn = Optional.of(lastUpdatedOn);
      return this;
    }

    public Builder withIsBasedOn(URI isBasedOn) {
      this.isBasedOn = isBasedOn;
      return this;
    }

    public Builder withElementInstances(Map<String, List<ElementInstanceArtifact>> elementInstances) {
      this.elementInstances = elementInstances;
      return this;
    }

    public Builder withFieldInstances(Map<String, List<FieldInstanceArtifact>> fieldInstances) {
      this.fieldInstances = fieldInstances;
      return this;
    }

    public TemplateInstanceArtifact build() {
      return new TemplateInstanceArtifact(this);
    }
  }
}
