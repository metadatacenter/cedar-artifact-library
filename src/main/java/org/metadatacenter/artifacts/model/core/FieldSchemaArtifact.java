package org.metadatacenter.artifacts.model.core;

import org.metadatacenter.model.ModelNodeNames;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateListFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateOptionalFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateUIFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateUriListContains;
import static org.metadatacenter.model.ModelNodeNames.FIELD_SCHEMA_ARTIFACT_TYPE_IRI;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_OBJECT;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_SCHEMA_IRI;
import static org.metadatacenter.model.ModelNodeNames.SKOS_ALTLABEL;
import static org.metadatacenter.model.ModelNodeNames.SKOS_PREFLABEL;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS;
import static org.metadatacenter.model.ModelNodeNames.UI;

public final class FieldSchemaArtifact extends SchemaArtifact implements ChildSchemaArtifact
{
  private final FieldUI fieldUI;
  private final Optional<ValueConstraints> valueConstraints;
  private final Optional<String> skosPrefLabel;
  private final List<String> skosAlternateLabels;
  private final boolean isMultiple;
  private final Optional<URI> propertyURI;

  public FieldSchemaArtifact(SchemaArtifact schemaArtifact, FieldUI fieldUI,
    Optional<ValueConstraints> valueConstraints,
    Optional<String> skosPrefLabel, List<String> skosAlternateLabels,
    boolean isMultiple, Optional<URI> propertyURI)
  {
    super(schemaArtifact);
    this.valueConstraints = valueConstraints;
    this.fieldUI = fieldUI;
    this.skosPrefLabel = skosPrefLabel;
    this.skosAlternateLabels = Collections.unmodifiableList(skosAlternateLabels);
    this.isMultiple = isMultiple;
    this.propertyURI = propertyURI;

    validate();
  }

  public FieldSchemaArtifact(Map<String, URI> jsonLdContext, List<URI> jsonLdTypes, Optional<URI> jsonLdId,
    Optional<URI> createdBy, Optional<URI> modifiedBy, Optional<OffsetDateTime> createdOn, Optional<OffsetDateTime> lastUpdatedOn,
    URI jsonSchemaSchemaUri, String jsonSchemaType, String jsonSchemaTitle, String jsonSchemaDescription,
    String schemaOrgName, String schemaOrgDescription, Optional<String> schemaOrgIdentifier,
    Version modelVersion, Optional<Version> artifactVersion, Optional<Status> artifactVersionStatus,
    Optional<URI> previousVersion, Optional<URI> derivedFrom, FieldUI fieldUI,
    Optional<ValueConstraints> valueConstraints, Optional<String> skosPrefLabel, List<String> skosAlternateLabels,
    boolean isMultiple, Optional<URI> propertyURI)
  {
    super(jsonLdContext, jsonLdTypes, jsonLdId,
      createdBy, modifiedBy, createdOn, lastUpdatedOn,
      jsonSchemaSchemaUri, jsonSchemaType, jsonSchemaTitle, jsonSchemaDescription,
      schemaOrgName, schemaOrgDescription, schemaOrgIdentifier,
      modelVersion, artifactVersion, artifactVersionStatus, previousVersion, derivedFrom);
    this.valueConstraints = valueConstraints;
    this.fieldUI = fieldUI;
    this.skosPrefLabel = skosPrefLabel;
    this.skosAlternateLabels = Collections.unmodifiableList(skosAlternateLabels);
    this.isMultiple = isMultiple;
    this.propertyURI = propertyURI;

    validate();
  }

  private FieldSchemaArtifact(Builder builder) {
    super(builder.jsonLdContext, builder.jsonLdTypes, builder.jsonLdId,
      builder.createdBy, builder.modifiedBy, builder.createdOn, builder.lastUpdatedOn,
      builder.jsonSchemaSchemaUri, builder.jsonSchemaType, builder.jsonSchemaTitle, builder.jsonSchemaDescription,
      builder.schemaOrgName, builder.schemaOrgDescription, builder.schemaOrgIdentifier,
      builder.modelVersion, builder.artifactVersion, builder.artifactVersionStatus,
      builder.previousVersion, builder.derivedFrom);
    this.fieldUI = builder.fieldUI;
    this.valueConstraints = builder.valueConstraints;
    this.skosPrefLabel = builder.skosPrefLabel;
    this.skosAlternateLabels = Collections.unmodifiableList(builder.skosAlternateLabels);
    this.isMultiple = builder.isMultiple;
    this.propertyURI = builder.propertyURI;

    validate();
  }

  public FieldUI getFieldUI()
  {
    return fieldUI;
  }

  public boolean isHidden() { return fieldUI.isHidden(); }

  public boolean isStatic() { return fieldUI.isStatic(); }

  public boolean hasIRIValue()
  {
    return (getFieldUI().isTextField() && valueConstraints.isPresent() && valueConstraints.get().hasOntologyValueBasedConstraints()) ||
      getFieldUI().isLink() || getFieldUI().isImage() || getFieldUI().isYouTube();
  }

  @Override public boolean isMultiple() { return isMultiple; }

  @Override public Optional<URI> getPropertyURI()
  {
    return propertyURI;
  }

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
    validateUriListContains(this, getJsonLdTypes(), "jsonLdTypes", URI.create(FIELD_SCHEMA_ARTIFACT_TYPE_IRI));
    validateUIFieldNotNull(this, fieldUI, UI);
    validateOptionalFieldNotNull(this, valueConstraints, VALUE_CONSTRAINTS);
    validateOptionalFieldNotNull(this, skosPrefLabel, SKOS_PREFLABEL);
    validateListFieldNotNull(this, skosAlternateLabels, SKOS_ALTLABEL);
    validateOptionalFieldNotNull(this, propertyURI, "propertyURI");
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private List<URI> jsonLdTypes = Arrays.asList(URI.create(FIELD_SCHEMA_ARTIFACT_TYPE_IRI));
    private Optional<URI> jsonLdId = Optional.empty();
    private Map<String, URI> jsonLdContext = new HashMap<>();
    private Optional<URI> createdBy = Optional.empty();
    private Optional<URI> modifiedBy = Optional.empty();
    private Optional<OffsetDateTime> createdOn = Optional.empty();
    private Optional<OffsetDateTime> lastUpdatedOn = Optional.empty();
    private URI jsonSchemaSchemaUri = URI.create(JSON_SCHEMA_SCHEMA_IRI);
    private String jsonSchemaType = JSON_SCHEMA_OBJECT;
    private String jsonSchemaTitle = "";
    private String jsonSchemaDescription = "";
    private String schemaOrgName;
    private String schemaOrgDescription = "";
    private Optional<String> schemaOrgIdentifier = Optional.empty();
    private Version modelVersion = new Version(1, 6, 0); // TODO
    private Optional<Version> artifactVersion = Optional.of(new Version(0, 0, 1)); // TODO
    private Optional<Status> artifactVersionStatus = Optional.of(Status.DRAFT);
    private Optional<URI> previousVersion = Optional.empty();
    private Optional<URI> derivedFrom = Optional.empty();
    private FieldUI fieldUI;
    private Optional<ValueConstraints> valueConstraints = Optional.empty();
    private Optional<String> skosPrefLabel = Optional.empty();
    private List<String> skosAlternateLabels = Collections.emptyList();
    private boolean isMultiple = false;
    private Optional<URI> propertyURI = Optional.empty();

    private Builder() {
    }

    public Builder withJsonLdId(URI jsonLdId) {
      this.jsonLdId = Optional.of(jsonLdId);
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

    public Builder withName(String schemaOrgName) {
      this.schemaOrgName = schemaOrgName;

      if (this.jsonSchemaTitle.isEmpty())
        this.jsonSchemaTitle = schemaOrgName + " field schema";

      if (this.jsonSchemaDescription.isEmpty())
        this.jsonSchemaDescription = schemaOrgName + " field schema generated by the CEDAR Artifact Library";

      return this;
    }

    public Builder withDescription(String schemaOrgDescription) {
      this.schemaOrgDescription = schemaOrgDescription;
      return this;
    }

    public Builder withSchemaOrgIdentifier(String schemaOrgIdentifier) {
      this.schemaOrgIdentifier = Optional.of(schemaOrgIdentifier);
      return this;
    }

    public Builder withModelVersion(Version modelVersion) {
      this.modelVersion = modelVersion;
      return this;
    }

    public Builder withArtifactVersion(Version artifactVersion) {
      this.artifactVersion = Optional.of(artifactVersion);
      return this;
    }

    public Builder withArtifactVersionStatus(Status artifactVersionStatus) {
      this.artifactVersionStatus = Optional.of(artifactVersionStatus);
      return this;
    }

    public Builder withPreviousVersion(URI previousVersion) {
      this.previousVersion = Optional.of(previousVersion);
      return this;
    }

    public Builder withDerivedFrom(URI derivedFrom) {
      this.derivedFrom = Optional.of(derivedFrom);
      return this;
    }

    public Builder withFieldUI(FieldUI fieldUI) {
      this.fieldUI = fieldUI;
      return this;
    }

    public Builder withValueConstraints(ValueConstraints valueConstraints) {
      this.valueConstraints = Optional.of(valueConstraints);
      return this;
    }

    public Builder withSkosPrefLabel(String skosPrefLabel) {
      this.skosPrefLabel = Optional.of(skosPrefLabel);
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

    public Builder withPropertyURI(URI propertyURI) {
      this.propertyURI = Optional.of(propertyURI);
      return this;
    }

    public FieldSchemaArtifact build() {
      return new FieldSchemaArtifact(this);
    }
  }

}
