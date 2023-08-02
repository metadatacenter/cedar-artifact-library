package org.metadatacenter.artifacts.model.core;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * While element instances may not necessarily have JSON-LD identifiers or provenance fields (name, description,
 * createdBy, modifiedBy, createdOn, lastUpdatedOn), the model allows them.
 */
public final class ElementInstanceArtifact extends InstanceArtifact implements ParentInstanceArtifact
{
  private final Map<String, List<FieldInstanceArtifact>> fieldInstances;
  private final Map<String, List<ElementInstanceArtifact>> elementInstances;

  public ElementInstanceArtifact(InstanceArtifact instanceArtifact,
    Map<String, List<FieldInstanceArtifact>> fieldInstances,
    Map<String, List<ElementInstanceArtifact>> elementInstances)
  {
    super(instanceArtifact);
    this.fieldInstances = Collections.unmodifiableMap(fieldInstances);
    this.elementInstances = Collections.unmodifiableMap(elementInstances);
  }

  public ElementInstanceArtifact(List<URI> jsonLdTypes, Optional<URI> jsonLdId, Map<String, URI> jsonLdContext, Optional<URI> createdBy, Optional<URI> modifiedBy,
    Optional<OffsetDateTime> createdOn, Optional<OffsetDateTime> lastUpdatedOn,
    Map<String, List<FieldInstanceArtifact>> fieldInstances,
    Map<String, List<ElementInstanceArtifact>> elementInstances)
  {
    super(jsonLdTypes, jsonLdId, jsonLdContext, createdBy, modifiedBy, createdOn, lastUpdatedOn);
    this.fieldInstances = Collections.unmodifiableMap(fieldInstances);
    this.elementInstances = Collections.unmodifiableMap(elementInstances);
  }

  public ElementInstanceArtifact(ElementInstanceArtifact elementInstanceArtifact)
  {
    super(elementInstanceArtifact);
    this.fieldInstances = elementInstanceArtifact.fieldInstances;
    this.elementInstances = elementInstanceArtifact.elementInstances;
  }

  private ElementInstanceArtifact(Builder builder)
  {
    super(builder.jsonLdTypes, builder.jsonLdId, builder.jsonLdContext, builder.createdBy, builder.modifiedBy, builder.createdOn, builder.lastUpdatedOn);
    this.fieldInstances = Collections.unmodifiableMap(builder.fieldInstances);
    this.elementInstances = Collections.unmodifiableMap(builder.elementInstances);
  }

  @Override public Map<String, List<FieldInstanceArtifact>> getFieldInstances()
  {
    return fieldInstances;
  }

  @Override public Map<String, List<ElementInstanceArtifact>> getElementInstances()
  {
    return elementInstances;
  }

  @Override public String toString()
  {
    return super.toString() + "\n ElementInstanceArtifact{" + "fieldInstances=" + fieldInstances + ", elementInstances=" + elementInstances
      + '}';
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private List<URI> jsonLdTypes = Collections.emptyList();
    private Optional<URI> jsonLdId = Optional.empty();
    private Map<String, URI> jsonLdContext = new HashMap<>();
    private Optional<URI> createdBy = Optional.empty();
    private Optional<URI> modifiedBy = Optional.empty();
    private Optional<OffsetDateTime> createdOn = Optional.empty();
    private Optional<OffsetDateTime> lastUpdatedOn = Optional.empty();
    private Map<String, List<FieldInstanceArtifact>> fieldInstances;
    private Map<String, List<ElementInstanceArtifact>> elementInstances;

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

    public Builder withCreatedBy(Optional<URI> createdBy) {
      this.createdBy = createdBy;
      return this;
    }

    public Builder withModifiedBy(Optional<URI> modifiedBy) {
      this.modifiedBy = modifiedBy;
      return this;
    }

    public Builder withCreatedOn(Optional<OffsetDateTime> createdOn) {
      this.createdOn = createdOn;
      return this;
    }

    public Builder withLastUpdatedOn(Optional<OffsetDateTime> lastUpdatedOn) {
      this.lastUpdatedOn = lastUpdatedOn;
      return this;
    }

    public Builder withFieldInstances(Map<String, List<FieldInstanceArtifact>> fieldInstances) {
      this.fieldInstances = fieldInstances;
      return this;
    }

    public Builder withElementInstances(Map<String, List<ElementInstanceArtifact>> elementInstances) {
      this.elementInstances = elementInstances;
      return this;
    }

    public ElementInstanceArtifact build() {
      return new ElementInstanceArtifact(this);
    }
  }
}
