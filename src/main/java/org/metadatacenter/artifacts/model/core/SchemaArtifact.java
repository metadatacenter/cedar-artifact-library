package org.metadatacenter.artifacts.model.core;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateOptionalFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateStringFieldNotEmpty;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateStringFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateUriFieldEquals;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateVersionFieldNotNull;
import static org.metadatacenter.model.ModelNodeNames.BIBO_STATUS;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_DESCRIPTION;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_SCHEMA;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_SCHEMA_IRI;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_TITLE;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_TYPE;
import static org.metadatacenter.model.ModelNodeNames.PAV_DERIVED_FROM;
import static org.metadatacenter.model.ModelNodeNames.PAV_PREVIOUS_VERSION;
import static org.metadatacenter.model.ModelNodeNames.PAV_VERSION;
import static org.metadatacenter.model.ModelNodeNames.SCHEMA_ORG_DESCRIPTION;
import static org.metadatacenter.model.ModelNodeNames.SCHEMA_ORG_NAME;
import static org.metadatacenter.model.ModelNodeNames.SCHEMA_ORG_SCHEMA_VERSION;

public class SchemaArtifact extends Artifact implements JsonSchemaArtifact, SchemaOrgArtifact,
  ModelSchemaArtifact, VersionedArtifact
{
  private final URI jsonSchemaSchemaUri;
  private final String jsonSchemaType;
  private final String jsonSchemaTitle;
  private final String jsonSchemaDescription;
  private final String schemaOrgName;
  private final String schemaOrgDescription;
  private final Optional<String> schemaOrgIdentifier;
  private final Version modelVersion;
  private final Optional<Version> artifactVersion;
  private final Optional<Status> artifactVersionStatus;
  private final Optional<URI> previousVersion;
  private final Optional<URI> derivedFrom;

  public SchemaArtifact(Artifact artifact,
    URI jsonSchemaSchemaUri, String jsonSchemaType, String jsonSchemaTitle, String jsonSchemaDescription,
    String schemaOrgName, String schemaOrgDescription, Optional<String> schemaOrgIdentifier,
    Version modelVersion, Optional<Version> artifactVersion, Optional<Status> artifactVersionStatus,
    Optional<URI> previousVersion, Optional<URI> derivedFrom)
  {
    super(artifact);
    this.jsonSchemaSchemaUri = jsonSchemaSchemaUri;
    this.jsonSchemaType = jsonSchemaType;
    this.jsonSchemaTitle = jsonSchemaTitle;
    this.jsonSchemaDescription = jsonSchemaDescription;
    this.schemaOrgName = schemaOrgName;
    this.schemaOrgDescription = schemaOrgDescription;
    this.schemaOrgIdentifier = schemaOrgIdentifier;
    this.modelVersion = modelVersion;
    this.artifactVersion = artifactVersion;
    this.artifactVersionStatus = artifactVersionStatus;
    this.previousVersion = previousVersion;
    this.derivedFrom = derivedFrom;

    validate();
  }

  public SchemaArtifact(Map<String, URI> jsonLdContext, List<URI> jsonLdTypes, Optional<URI> jsonLdId,
    Optional<URI> createdBy, Optional<URI> modifiedBy,
    Optional<OffsetDateTime> createdOn, Optional<OffsetDateTime> lastUpdatedOn,
    URI jsonSchemaSchemaUri, String jsonSchemaType, String jsonSchemaTitle, String jsonSchemaDescription,
    String schemaOrgName, String schemaOrgDescription, Optional<String> schemaOrgIdentifier,
    Version modelVersion, Optional<Version> artifactVersion, Optional<Status> artifactVersionStatus,
    Optional<URI> previousVersion, Optional<URI> derivedFrom)
  {
    super(jsonLdContext, jsonLdTypes, jsonLdId, createdBy, modifiedBy, createdOn, lastUpdatedOn);
    this.jsonSchemaSchemaUri = jsonSchemaSchemaUri;
    this.jsonSchemaType = jsonSchemaType;
    this.jsonSchemaTitle = jsonSchemaTitle;
    this.jsonSchemaDescription = jsonSchemaDescription;
    this.schemaOrgName = schemaOrgName;
    this.schemaOrgDescription = schemaOrgDescription;
    this.schemaOrgIdentifier = schemaOrgIdentifier;
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
    this.jsonSchemaSchemaUri = schemaArtifact.jsonSchemaSchemaUri;
    this.jsonSchemaType = schemaArtifact.jsonSchemaType;
    this.jsonSchemaTitle = schemaArtifact.jsonSchemaTitle;
    this.jsonSchemaDescription = schemaArtifact.jsonSchemaDescription;
    this.schemaOrgIdentifier = schemaArtifact.schemaOrgIdentifier;
    this.schemaOrgName = schemaArtifact.schemaOrgName;
    this.schemaOrgDescription = schemaArtifact.schemaOrgDescription;
    this.modelVersion = schemaArtifact.modelVersion;
    this.artifactVersion = schemaArtifact.artifactVersion;
    this.artifactVersionStatus = schemaArtifact.artifactVersionStatus;
    this.previousVersion = schemaArtifact.previousVersion;
    this.derivedFrom = schemaArtifact.derivedFrom;

    validate();
  }

  @Override
  public URI getJsonSchemaSchemaUri()
  {
    return jsonSchemaSchemaUri;
  }

  @Override
  public String getJsonSchemaType()
  {
    return jsonSchemaType;
  }

  @Override
  public String getJsonSchemaTitle()
  {
    return jsonSchemaTitle;
  }

  @Override
  public String getJsonSchemaDescription()
  {
    return jsonSchemaDescription;
  }

  public String getName()
  {
    return schemaOrgName;
  }

  public String getDescription()
  {
    return schemaOrgDescription;
  }

  public Optional<String> getIdentifier()
  {
    return schemaOrgIdentifier;
  }

  @Override
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

  @Override
  public Optional<URI> getPreviousVersion()
  {
    return previousVersion;
  }

  @Override
  public Optional<URI> getDerivedFrom()
  {
    return derivedFrom;
  }

  @Override public String toString()
  {
    return "SchemaArtifact{" + "jsonSchemaSchemaUri=" + jsonSchemaSchemaUri + ", jsonSchemaType='" + jsonSchemaType
      + '\'' + ", jsonSchemaTitle='" + jsonSchemaTitle + '\'' + ", jsonSchemaDescription='" + jsonSchemaDescription
      + '\'' + ", schemaOrgName='" + schemaOrgName + '\'' + ", schemaOrgDescription='" + schemaOrgDescription + '\''
      + ", modelVersion=" + modelVersion + ", artifactVersion=" + artifactVersion + ", artifactVersionStatus="
      + artifactVersionStatus + ", previousVersion=" + previousVersion + ", derivedFrom=" + derivedFrom + '}';
  }

  private void validate()
  {
    validateUriFieldEquals(this, jsonSchemaSchemaUri, JSON_SCHEMA_SCHEMA, URI.create(JSON_SCHEMA_SCHEMA_IRI));
    validateStringFieldNotNull(this, jsonSchemaType, JSON_SCHEMA_TYPE);
    validateStringFieldNotNull(this, jsonSchemaTitle, JSON_SCHEMA_TITLE);
    validateStringFieldNotNull(this, jsonSchemaDescription, JSON_SCHEMA_DESCRIPTION);
    validateStringFieldNotEmpty(this, schemaOrgName, SCHEMA_ORG_NAME);
    validateStringFieldNotNull(this, schemaOrgDescription, SCHEMA_ORG_DESCRIPTION);
    validateVersionFieldNotNull(this, modelVersion, SCHEMA_ORG_SCHEMA_VERSION);
    validateOptionalFieldNotNull(this, artifactVersion, PAV_VERSION);
    validateOptionalFieldNotNull(this, artifactVersionStatus, BIBO_STATUS);
    validateOptionalFieldNotNull(this, previousVersion, PAV_PREVIOUS_VERSION);
    validateOptionalFieldNotNull(this, derivedFrom, PAV_DERIVED_FROM);
  }
}
