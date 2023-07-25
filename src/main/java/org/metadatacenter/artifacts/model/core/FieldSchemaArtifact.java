package org.metadatacenter.artifacts.model.core;

import org.metadatacenter.model.ModelNodeNames;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateListFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateOptionalFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateUIFieldNotNull;

public final class FieldSchemaArtifact extends SchemaArtifact implements ChildSchemaArtifact
{
  private final FieldUI fieldUI;
  private final Optional<ValueConstraints> valueConstraints;
  private final Optional<String> skosPrefLabel;
  private final List<String> skosAlternateLabels;
  private final boolean isMultiple;

  public FieldSchemaArtifact(SchemaArtifact schemaArtifact, FieldUI fieldUI,
    Optional<ValueConstraints> valueConstraints, Optional<String> skosPrefLabel, List<String> skosAlternateLabels,
    boolean isMultiple)
  {
    super(schemaArtifact);
    this.valueConstraints = valueConstraints;
    this.fieldUI = fieldUI;
    this.skosPrefLabel = skosPrefLabel;
    this.skosAlternateLabels = Collections.unmodifiableList(skosAlternateLabels);
    this.isMultiple = isMultiple;

    validate();
  }

  public FieldSchemaArtifact(Optional<URI> jsonLdId, Map<String, URI> jsonLdContext,
    Optional<URI> createdBy, Optional<URI> modifiedBy,
    Optional<OffsetDateTime> createdOn, Optional<OffsetDateTime> lastUpdatedOn,
    URI jsonSchemaSchemaUri, String jsonSchemaType, String jsonSchemaTitle, String jsonSchemaDescription,
    List<URI> jsonLdTypes,
    String schemaOrgName, String schemaOrgDescription,
    Version modelVersion, Optional<Version> artifactVersion, Optional<Status> artifactVersionStatus,
    Optional<URI> previousVersion, Optional<URI> derivedFrom, FieldUI fieldUI,
    Optional<ValueConstraints> valueConstraints, Optional<String> skosPrefLabel, List<String> skosAlternateLabels,
    boolean isMultiple)
  {
    super(jsonLdId, jsonLdContext, jsonLdTypes,
      createdBy, modifiedBy, createdOn, lastUpdatedOn, jsonSchemaSchemaUri, jsonSchemaType,
      jsonSchemaTitle, jsonSchemaDescription, schemaOrgName, schemaOrgDescription, modelVersion,
      artifactVersion, artifactVersionStatus, previousVersion, derivedFrom);
    this.valueConstraints = valueConstraints;
    this.fieldUI = fieldUI;
    this.skosPrefLabel = skosPrefLabel;
    this.skosAlternateLabels = Collections.unmodifiableList(skosAlternateLabels);
    this.isMultiple = isMultiple;

    validate();
  }

  private FieldSchemaArtifact(Builder builder) {
    super(builder.jsonLdId, builder.jsonLdContext, builder.jsonLdTypes, builder.createdBy, builder.modifiedBy, builder.createdOn,
      builder.lastUpdatedOn, builder.jsonSchemaSchemaUri, builder.jsonSchemaType, builder.jsonSchemaTitle,
      builder.jsonSchemaDescription, builder.schemaOrgName, builder.schemaOrgDescription,
      builder.modelVersion, builder.artifactVersion, builder.artifactVersionStatus,
      builder.previousVersion, builder.derivedFrom);
    this.fieldUI = builder.fieldUI;
    this.valueConstraints = builder.valueConstraints;
    this.skosPrefLabel = builder.skosPrefLabel;
    this.skosAlternateLabels = Collections.unmodifiableList(builder.skosAlternateLabels);
    this.isMultiple = builder.isMultiple;

    validate();
  }

  public FieldUI getFieldUI()
  {
    return fieldUI;
  }

  public boolean isHidden() { return fieldUI.isHidden(); }

  @Override public boolean isMultiple() { return isMultiple; }

  public Optional<ValueConstraints> getValueConstraints()
  {
    return valueConstraints;
  }

  public Optional<String> getSkosPrefLabel()
  {
    return skosPrefLabel;
  }

  public List<String> getSkosAlternateLabels()
  {
    return skosAlternateLabels;
  }

  @Override public String toString()
  {
    return "FieldSchemaArtifact{" + "fieldUI=" + fieldUI + ", valueConstraints=" + valueConstraints + ", skosPrefLabel="
      + skosPrefLabel + ", skosAlternateLabels=" + skosAlternateLabels + '}';
  }

  private void validate()
  {
    validateUIFieldNotNull(this, fieldUI, ModelNodeNames.UI);
    validateOptionalFieldNotNull(this, valueConstraints, ModelNodeNames.VALUE_CONSTRAINTS);
    validateOptionalFieldNotNull(this, skosPrefLabel, ModelNodeNames.SKOS_PREFLABEL);
    validateListFieldNotNull(this, skosAlternateLabels, ModelNodeNames.SKOS_ALTLABEL);
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private Optional<URI> jsonLdId = Optional.empty();
    private Map<String, URI> jsonLdContext = Collections.emptyMap();
    private Optional<URI> createdBy = Optional.empty();
    private Optional<URI> modifiedBy = Optional.empty();
    private Optional<OffsetDateTime> createdOn = Optional.empty();
    private Optional<OffsetDateTime> lastUpdatedOn = Optional.empty();
    private URI jsonSchemaSchemaUri = URI.create(ModelNodeNames.JSON_SCHEMA_SCHEMA_IRI);
    private String jsonSchemaType = ModelNodeNames.JSON_SCHEMA_OBJECT;
    private String jsonSchemaTitle = "";
    private String jsonSchemaDescription = "";
    private List<URI> jsonLdTypes = Collections.emptyList();
    private String schemaOrgName;
    private String schemaOrgDescription = "";
    private Version modelVersion = new Version(1, 6, 0); // TODO
    private Optional<Version> artifactVersion = Optional.of(new Version(1, 0, 0)); // TODO
    private Optional<Status> artifactVersionStatus = Optional.of(Status.DRAFT);
    private Optional<URI> previousVersion = Optional.empty();
    private Optional<URI> derivedFrom = Optional.empty();
    private FieldUI fieldUI;
    private Optional<ValueConstraints> valueConstraints = Optional.empty();
    private Optional<String> skosPrefLabel = Optional.empty();
    private List<String> skosAlternateLabels = Collections.emptyList();
    private boolean isMultiple = false;

    private Builder() {
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

    public Builder withJsonSchemaSchemaUri(URI jsonSchemaSchemaUri) {
      this.jsonSchemaSchemaUri = jsonSchemaSchemaUri;
      return this;
    }

    public Builder withJsonSchemaType(String jsonSchemaType) {
      this.jsonSchemaType = jsonSchemaType;
      return this;
    }

    public Builder withJsonSchemaTitle(String jsonSchemaTitle) {
      this.jsonSchemaTitle = jsonSchemaTitle;
      return this;
    }

    public Builder withJsonSchemaDescription(String jsonSchemaDescription) {
      this.jsonSchemaDescription = jsonSchemaDescription;
      return this;
    }

    public Builder withJsonLdTypes(List<URI> jsonLdTypes) {
      this.jsonLdTypes = jsonLdTypes;
      return this;
    }

    public Builder withSchemaOrgName(String schemaOrgName) {
      this.schemaOrgName = schemaOrgName;
      return this;
    }

    public Builder withSchemaOrgDescription(String schemaOrgDescription) {
      this.schemaOrgDescription = schemaOrgDescription;
      return this;
    }

    public Builder withModelVersion(Version modelVersion) {
      this.modelVersion = modelVersion;
      return this;
    }

    public Builder withArtifactVersion(Optional<Version> artifactVersion) {
      this.artifactVersion = artifactVersion;
      return this;
    }

    public Builder withArtifactVersionStatus(Optional<Status> artifactVersionStatus) {
      this.artifactVersionStatus = artifactVersionStatus;
      return this;
    }

    public Builder withPreviousVersion(Optional<URI> previousVersion) {
      this.previousVersion = previousVersion;
      return this;
    }

    public Builder withDerivedFrom(Optional<URI> derivedFrom) {
      this.derivedFrom = derivedFrom;
      return this;
    }

    public Builder withFieldUI(FieldUI fieldUI) {
      this.fieldUI = fieldUI;
      return this;
    }

    public Builder withValueConstraints(Optional<ValueConstraints> valueConstraints) {
      this.valueConstraints = valueConstraints;
      return this;
    }

    public Builder withSkosPrefLabel(Optional<String> skosPrefLabel) {
      this.skosPrefLabel = skosPrefLabel;
      return this;
    }

    public Builder withSkosAlternateLabels(List<String> skosAlternateLabels) {
      this.skosAlternateLabels = skosAlternateLabels;
      return this;
    }

    public Builder withIsMultiple(boolean isMultiple) {
      this.isMultiple = isMultiple;
      return this;
    }

    public FieldSchemaArtifact build() {
      return new FieldSchemaArtifact(this);
    }
  }

}
