package org.metadatacenter.artifacts.model.core;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * While field instances may not necessarily have JSON-LD identifiers or provenance fields (name, description,
 * createdBy, modifiedBy, createdOn, lastUpdatedOn) the model allows them.
 */
public final class FieldInstanceArtifact extends InstanceArtifact
{
  private final String jsonLdValue;
  private final Optional<String> label;
  private final Optional<String> notation;
  private final Optional<String> prefLabel;

  public FieldInstanceArtifact(InstanceArtifact instanceArtifact, String jsonLdValue,
    Optional<String> label, Optional<String> notation, Optional<String> prefLabel)
  {
    super(instanceArtifact);
    this.jsonLdValue = jsonLdValue;
    this.label = label;
    this.notation = notation;
    this.prefLabel = prefLabel;
  }

  public FieldInstanceArtifact(List<URI> jsonLdTypes, Optional<URI> jsonLdId, Map<String, URI> jsonLdContext, Optional<URI> createdBy, Optional<URI> modifiedBy,
    Optional<OffsetDateTime> createdOn, Optional<OffsetDateTime> lastUpdatedOn, String jsonLdValue,
    Optional<String> label, Optional<String> notation, Optional<String> prefLabel)
  {
    super(jsonLdTypes, jsonLdId, jsonLdContext, createdBy, modifiedBy, createdOn, lastUpdatedOn);
    this.jsonLdValue = jsonLdValue;
    this.label = label;
    this.notation = notation;
    this.prefLabel = prefLabel;
  }

  private FieldInstanceArtifact(Builder builder) {
    super(builder.jsonLdTypes, builder.jsonLdId, builder.jsonLdContext, builder.createdBy, builder.modifiedBy, builder.createdOn, builder.lastUpdatedOn);
    this.jsonLdValue = builder.jsonLdValue;
    this.label = builder.label;
    this.notation = builder.notation;
    this.prefLabel = builder.prefLabel;
  }

  public FieldInstanceArtifact(FieldInstanceArtifact fieldInstanceArtifact)
  {
    super(fieldInstanceArtifact);
    this.jsonLdValue = fieldInstanceArtifact.jsonLdValue;
    this.label = fieldInstanceArtifact.label;
    this.notation = fieldInstanceArtifact.notation;
    this.prefLabel = fieldInstanceArtifact.prefLabel;
  }

  public String getJsonLdValue()
  {
    return jsonLdValue;
  }

  public Optional<String> getLabel()
  {
    return label;
  }

  public Optional<String> getNotation()
  {
    return notation;
  }

  public Optional<String> getPrefLabel()
  {
    return prefLabel;
  }


  @Override public String toString()
  {
    return super.toString() + "\n FieldInstanceArtifact{" + "value='" + jsonLdValue + '\'' + ", label='" + label + '\'' + ", notation='" + notation
      + '\'' + ", prefLabel='" + prefLabel + '\'' + '}';
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
    private String jsonLdValue;
    private Optional<String> label = Optional.empty();
    private Optional<String> notation = Optional.empty();
    private Optional<String> prefLabel = Optional.empty();

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

    public Builder withJsonLdValue(String jsonLdValue) {
      this.jsonLdValue = jsonLdValue;
      return this;
    }

    public Builder withLabel(Optional<String> label) {
      this.label = label;
      return this;
    }

    public Builder withNotation(Optional<String> notation) {
      this.notation = notation;
      return this;
    }

    public Builder withPrefLabel(Optional<String> prefLabel) {
      this.prefLabel = prefLabel;
      return this;
    }

    public FieldInstanceArtifact build() {
      return new FieldInstanceArtifact(this);
    }
  }
}

