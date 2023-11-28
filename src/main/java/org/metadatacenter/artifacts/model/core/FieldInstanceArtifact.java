package org.metadatacenter.artifacts.model.core;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateListFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateMapFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateOptionalFieldNotNull;
import static org.metadatacenter.model.ModelNodeNames.JSON_LD_CONTEXT;
import static org.metadatacenter.model.ModelNodeNames.JSON_LD_ID;
import static org.metadatacenter.model.ModelNodeNames.JSON_LD_TYPE;
import static org.metadatacenter.model.ModelNodeNames.OSLC_MODIFIED_BY;
import static org.metadatacenter.model.ModelNodeNames.PAV_CREATED_BY;
import static org.metadatacenter.model.ModelNodeNames.PAV_CREATED_ON;
import static org.metadatacenter.model.ModelNodeNames.PAV_LAST_UPDATED_ON;
import static org.metadatacenter.model.ModelNodeNames.RDFS_LABEL;
import static org.metadatacenter.model.ModelNodeNames.SKOS_NOTATION;
import static org.metadatacenter.model.ModelNodeNames.SKOS_PREFLABEL;

/**
 * While field instances may not necessarily have JSON-LD identifiers or provenance fields (name, description,
 * createdBy, modifiedBy, createdOn, lastUpdatedOn), the model allows them.
 */
public interface FieldInstanceArtifact extends InstanceArtifact
{
  static FieldInstanceArtifact create(Map<String, URI> jsonLdContext, List<URI> jsonLdTypes, Optional<URI> jsonLdId,
    String jsonLdValue, Optional<String> label, Optional<String> notation, Optional<String> prefLabel,
    Optional<URI> createdBy, Optional<URI> modifiedBy, Optional<OffsetDateTime> createdOn, Optional<OffsetDateTime> lastUpdatedOn)
  {
    return new FieldInstanceArtifactRecord(jsonLdContext, jsonLdTypes, jsonLdId,
      jsonLdValue, label, notation, prefLabel,
      createdBy, modifiedBy, createdOn, lastUpdatedOn);
  }

  static Builder builder()
  {
    return new Builder();
  }

  class Builder
  {
    private Map<String, URI> jsonLdContext = new HashMap<>();
    private List<URI> jsonLdTypes = new ArrayList<>();
    private Optional<URI> jsonLdId = Optional.empty();
    private String jsonLdValue;
    private Optional<String> label = Optional.empty();
    private Optional<String> notation = Optional.empty();
    private Optional<String> prefLabel = Optional.empty();
    private Optional<URI> createdBy = Optional.empty();
    private Optional<URI> modifiedBy = Optional.empty();
    private Optional<OffsetDateTime> createdOn = Optional.empty();
    private Optional<OffsetDateTime> lastUpdatedOn = Optional.empty();

    private Builder()
    {
    }

    public Builder withJsonLdContext(Map<String, URI> jsonLdContext)
    {
      this.jsonLdContext = jsonLdContext;
      return this;
    }

    public Builder withJsonLdType(URI jsonLdType)
    {
      this.jsonLdTypes.add(jsonLdType);
      return this;
    }

    public Builder withJsonLdId(Optional<URI> jsonLdId)
    {
      this.jsonLdId = jsonLdId;
      return this;
    }

    public Builder withCreatedBy(URI createdBy)
    {
      this.createdBy = Optional.ofNullable(createdBy);
      return this;
    }

    public Builder withModifiedBy(URI modifiedBy)
    {
      this.modifiedBy = Optional.ofNullable(modifiedBy);
      return this;
    }

    public Builder withCreatedOn(OffsetDateTime createdOn)
    {
      this.createdOn = Optional.ofNullable(createdOn);
      return this;
    }

    public Builder withLastUpdatedOn(OffsetDateTime lastUpdatedOn)
    {
      this.lastUpdatedOn = Optional.ofNullable(lastUpdatedOn);
      return this;
    }

    public Builder withJsonLdValue(String jsonLdValue)
    {
      this.jsonLdValue = jsonLdValue;
      return this;
    }

    public Builder withLabel(String label)
    {
      this.label = Optional.ofNullable(label);
      return this;
    }

    public Builder withNotation(String notation)
    {
      this.notation = Optional.ofNullable(notation);
      return this;
    }

    public Builder withPrefLabel(String prefLabel)
    {
      this.prefLabel = Optional.ofNullable(prefLabel);
      return this;
    }

    public FieldInstanceArtifact build()
    {
      return new FieldInstanceArtifactRecord(jsonLdContext, jsonLdTypes, jsonLdId,
        jsonLdValue, label, notation, prefLabel,
        createdBy, modifiedBy, createdOn, lastUpdatedOn);
    }
  }
}

record FieldInstanceArtifactRecord(Map<String, URI> jsonLdContext, List<URI> jsonLdTypes, Optional<URI> jsonLdId,
                                   String jsonLdValue, Optional<String> label, Optional<String> notation, Optional<String> prefLabel,
                                   Optional<URI> createdBy, Optional<URI> modifiedBy,
                                   Optional<OffsetDateTime> createdOn, Optional<OffsetDateTime> lastUpdatedOn) implements FieldInstanceArtifact
{
  public FieldInstanceArtifactRecord
  {
    validateMapFieldNotNull(this, jsonLdContext, JSON_LD_CONTEXT);
    validateListFieldNotNull(this, jsonLdTypes, JSON_LD_TYPE);
    validateOptionalFieldNotNull(this, jsonLdId, JSON_LD_ID);
    validateOptionalFieldNotNull(this, label, RDFS_LABEL);
    validateOptionalFieldNotNull(this, notation, SKOS_NOTATION);
    validateOptionalFieldNotNull(this, prefLabel, SKOS_PREFLABEL);
    validateOptionalFieldNotNull(this, createdBy, PAV_CREATED_BY);
    validateOptionalFieldNotNull(this, modifiedBy, OSLC_MODIFIED_BY);
    validateOptionalFieldNotNull(this, createdOn, PAV_CREATED_ON);
    validateOptionalFieldNotNull(this, lastUpdatedOn, PAV_LAST_UPDATED_ON);
  }
}

