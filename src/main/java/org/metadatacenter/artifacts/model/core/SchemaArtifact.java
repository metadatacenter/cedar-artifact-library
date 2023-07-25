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
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateStringFieldNotEmpty;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateStringFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateURIFieldEquals;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateVersionFieldNotNull;

public class SchemaArtifact extends Artifact
{
  private final List<URI> jsonLdTypes;
  private final URI jsonSchemaSchemaUri;
  private final String jsonSchemaType;
  private final String jsonSchemaTitle;
  private final String jsonSchemaDescription;
  private final String schemaOrgName;
  private final String schemaOrgDescription;
  private final Version modelVersion;
  private final Optional<Version> artifactVersion;
  private final Optional<Status> artifactVersionStatus;
  private final Optional<URI> previousVersion;
  private final Optional<URI> derivedFrom;

  public SchemaArtifact(Artifact artifact, List<URI> jsonLdTypes,
    URI jsonSchemaSchemaUri, String jsonSchemaType, String jsonSchemaTitle, String jsonSchemaDescription,
    String schemaOrgName, String schemaOrgDescription,
    Version modelVersion, Optional<Version> artifactVersion, Optional<Status> artifactVersionStatus,
    Optional<URI> previousVersion, Optional<URI> derivedFrom)
  {
    super(artifact);
    this.jsonLdTypes = Collections.unmodifiableList(jsonLdTypes);
    this.jsonSchemaSchemaUri = jsonSchemaSchemaUri;
    this.jsonSchemaType = jsonSchemaType;
    this.jsonSchemaTitle = jsonSchemaTitle;
    this.jsonSchemaDescription = jsonSchemaDescription;
    this.schemaOrgName = schemaOrgName;
    this.schemaOrgDescription = schemaOrgDescription;
    this.modelVersion = modelVersion;
    this.artifactVersion = artifactVersion;
    this.artifactVersionStatus = artifactVersionStatus;
    this.previousVersion = previousVersion;
    this.derivedFrom = derivedFrom;

    validate();
  }

  public SchemaArtifact(Optional<URI> jsonLdId, Map<String, URI> jsonLdContext, List<URI> jsonLdTypes,
    Optional<URI> createdBy, Optional<URI> modifiedBy,
    Optional<OffsetDateTime> createdOn, Optional<OffsetDateTime> lastUpdatedOn,
    URI jsonSchemaSchemaUri, String jsonSchemaType, String jsonSchemaTitle, String jsonSchemaDescription,
    String schemaOrgName, String schemaOrgDescription,
    Version modelVersion, Optional<Version> artifactVersion, Optional<Status> artifactVersionStatus,
    Optional<URI> previousVersion, Optional<URI> derivedFrom)
  {
    super(jsonLdId, jsonLdContext, createdBy, modifiedBy, createdOn, lastUpdatedOn);
    this.jsonLdTypes = Collections.unmodifiableList(jsonLdTypes);
    this.jsonSchemaSchemaUri = jsonSchemaSchemaUri;
    this.jsonSchemaType = jsonSchemaType;
    this.jsonSchemaTitle = jsonSchemaTitle;
    this.jsonSchemaDescription = jsonSchemaDescription;
    this.schemaOrgName = schemaOrgName;
    this.schemaOrgDescription = schemaOrgDescription;
    this.modelVersion = modelVersion;
    this.artifactVersion = artifactVersion;
    this.artifactVersionStatus = artifactVersionStatus;
    this.previousVersion = previousVersion;
    this.derivedFrom = derivedFrom;

    validate();
  }

  public SchemaArtifact(SchemaArtifact schemaArtifact)
  {
    super(schemaArtifact);
    this.jsonLdTypes = Collections.unmodifiableList(schemaArtifact.jsonLdTypes);
    this.jsonSchemaSchemaUri = schemaArtifact.jsonSchemaSchemaUri;
    this.jsonSchemaType = schemaArtifact.jsonSchemaType;
    this.jsonSchemaTitle = schemaArtifact.jsonSchemaTitle;
    this.jsonSchemaDescription = schemaArtifact.jsonSchemaDescription;
    this.schemaOrgName = schemaArtifact.schemaOrgName;
    this.schemaOrgDescription = schemaArtifact.schemaOrgDescription;
    this.modelVersion = schemaArtifact.modelVersion;
    this.artifactVersion = schemaArtifact.artifactVersion;
    this.artifactVersionStatus = schemaArtifact.artifactVersionStatus;
    this.previousVersion = schemaArtifact.previousVersion;
    this.derivedFrom = schemaArtifact.derivedFrom;

    validate();
  }

  public URI getJsonSchemaSchemaUri()
  {
    return jsonSchemaSchemaUri;
  }

  public String getJsonSchemaType()
  {
    return jsonSchemaType;
  }

  public String getJsonSchemaTitle()
  {
    return jsonSchemaTitle;
  }

  public String getJsonSchemaDescription()
  {
    return jsonSchemaDescription;
  }

  public List<URI> getJsonLdTypes()
  {
    return jsonLdTypes;
  }

  public String getName()
  {
    return schemaOrgName;
  }

  public String getDescription()
  {
    return schemaOrgDescription;
  }

  public Version getModelVersion()
  {
    return modelVersion;
  }

  public Optional<Version> getVersion()
  {
    return artifactVersion;
  }

  public Optional<Status> getStatus()
  {
    return artifactVersionStatus;
  }

  public Optional<URI> getPreviousVersion()
  {
    return previousVersion;
  }

  public Optional<URI> getDerivedFrom()
  {
    return derivedFrom;
  }

  @Override public String toString()
  {
    return "SchemaArtifact{" + "jsonSchemaSchemaUri=" + jsonSchemaSchemaUri + ", jsonSchemaType='" + jsonSchemaType
      + '\'' + ", jsonSchemaTitle='" + jsonSchemaTitle + '\'' + ", jsonSchemaDescription='" + jsonSchemaDescription
      + '\'' + ", jsonLdTypes=" + jsonLdTypes + ", schemaOrgName='" + schemaOrgName + '\'' + ", schemaOrgDescription='"
      + schemaOrgDescription + '\'' + ", modelVersion=" + modelVersion + ", artifactVersion=" + artifactVersion
      + ", artifactVersionStatus=" + artifactVersionStatus + ", previousArtifactVersion=" + previousVersion
      + ", derivedFrom=" + derivedFrom + '}';
  }

  private void validate()
  {
    validateURIFieldEquals(this, jsonSchemaSchemaUri, ModelNodeNames.JSON_SCHEMA_SCHEMA, ModelNodeNames.JSON_SCHEMA_SCHEMA_IRI);
    validateStringFieldNotNull(this, jsonSchemaType, ModelNodeNames.JSON_SCHEMA_TYPE);
    validateStringFieldNotNull(this, jsonSchemaTitle, ModelNodeNames.JSON_SCHEMA_TITLE);
    validateStringFieldNotNull(this, jsonSchemaDescription, ModelNodeNames.JSON_SCHEMA_DESCRIPTION);
    validateListFieldNotNull(this, jsonLdTypes, ModelNodeNames.JSON_LD_TYPE);
    validateStringFieldNotEmpty(this, schemaOrgName, ModelNodeNames.SCHEMA_ORG_NAME);
    validateStringFieldNotNull(this, schemaOrgDescription, ModelNodeNames.SCHEMA_ORG_DESCRIPTION);
    validateVersionFieldNotNull(this, modelVersion, ModelNodeNames.SCHEMA_ORG_SCHEMA_VERSION);
    validateOptionalFieldNotNull(this, artifactVersion, ModelNodeNames.PAV_VERSION);
    validateOptionalFieldNotNull(this, artifactVersionStatus, ModelNodeNames.BIBO_STATUS);
    validateOptionalFieldNotNull(this, previousVersion, ModelNodeNames.PAV_PREVIOUS_VERSION);
    validateOptionalFieldNotNull(this, derivedFrom, ModelNodeNames.PAV_DERIVED_FROM);
  }
}
