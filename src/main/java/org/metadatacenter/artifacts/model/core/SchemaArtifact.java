package org.metadatacenter.artifacts.model.core;

import org.apache.poi.ss.formula.functions.Mode;
import org.metadatacenter.model.ModelNodeNames;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SchemaArtifact extends Artifact
{
  private final URI jsonSchemaSchemaUri;
  private final String jsonSchemaType;
  private final String jsonSchemaTitle;
  private final String jsonSchemaDescription;
  private final List<URI> jsonLdTypes;
  private final String schemaOrgName;
  private final String schemaOrgDescription;
  private final Version modelVersion;
  private final Optional<Version> artifactVersion;
  private final Optional<Status> artifactVersionStatus;
  private final Optional<Version> previousArtifactVersion;
  private final Optional<URI> derivedFrom;

  public SchemaArtifact(Artifact artifact,
    URI jsonSchemaSchemaUri, String jsonSchemaType, String jsonSchemaTitle, String jsonSchemaDescription,
    List<URI> jsonLdTypes,
    String schemaOrgName, String schemaOrgDescription,
    Version modelVersion, Optional<Version> artifactVersion, Optional<Status> artifactVersionStatus,
    Optional<Version> previousArtifactVersion, Optional<URI> derivedFrom)
  {
    super(artifact);
    this.jsonSchemaSchemaUri = jsonSchemaSchemaUri;
    this.jsonSchemaType = jsonSchemaType;
    this.jsonSchemaTitle = jsonSchemaTitle;
    this.jsonSchemaDescription = jsonSchemaDescription;
    this.jsonLdTypes = Collections.unmodifiableList(jsonLdTypes);
    this.schemaOrgName = schemaOrgName;
    this.schemaOrgDescription = schemaOrgDescription;
    this.modelVersion = modelVersion;
    this.artifactVersion = artifactVersion;
    this.artifactVersionStatus = artifactVersionStatus;
    this.previousArtifactVersion = previousArtifactVersion;
    this.derivedFrom = derivedFrom;

    validate();
  }

  public SchemaArtifact(Optional<URI> jsonLdId, Map<String, URI> jsonLdContext,
    Optional<URI> createdBy, Optional<URI> modifiedBy,
    Optional<OffsetDateTime> createdOn, Optional<OffsetDateTime> lastUpdatedOn,
    URI jsonSchemaSchemaUri, String jsonSchemaType, String jsonSchemaTitle, String jsonSchemaDescription,
    List<URI> jsonLdTypes,
    String schemaOrgName, String schemaOrgDescription,
    Version modelVersion, Optional<Version> artifactVersion, Optional<Status> artifactVersionStatus,
    Optional<Version> previousArtifactVersion, Optional<URI> derivedFrom)
  {
    super(jsonLdId, jsonLdContext, createdBy, modifiedBy, createdOn, lastUpdatedOn);
    this.jsonSchemaSchemaUri = jsonSchemaSchemaUri;
    this.jsonSchemaType = jsonSchemaType;
    this.jsonSchemaTitle = jsonSchemaTitle;
    this.jsonSchemaDescription = jsonSchemaDescription;
    this.jsonLdTypes = Collections.unmodifiableList(jsonLdTypes);
    this.schemaOrgName = schemaOrgName;
    this.schemaOrgDescription = schemaOrgDescription;
    this.modelVersion = modelVersion;
    this.artifactVersion = artifactVersion;
    this.artifactVersionStatus = artifactVersionStatus;
    this.previousArtifactVersion = previousArtifactVersion;
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
    this.jsonLdTypes = Collections.unmodifiableList(schemaArtifact.jsonLdTypes);
    this.schemaOrgName = schemaArtifact.schemaOrgName;
    this.schemaOrgDescription = schemaArtifact.schemaOrgDescription;
    this.modelVersion = schemaArtifact.modelVersion;
    this.artifactVersion = schemaArtifact.artifactVersion;
    this.artifactVersionStatus = schemaArtifact.artifactVersionStatus;
    this.previousArtifactVersion = schemaArtifact.previousArtifactVersion;
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

  public Optional<Version> getPreviousVersion()
  {
    return previousArtifactVersion;
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
      + ", artifactVersionStatus=" + artifactVersionStatus + ", previousArtifactVersion=" + previousArtifactVersion
      + ", derivedFrom=" + derivedFrom + '}';
  }

  private void validate()
  {
    validateURIFieldEquals(jsonSchemaSchemaUri, ModelNodeNames.JSON_SCHEMA_SCHEMA, ModelNodeNames.JSON_SCHEMA_SCHEMA_IRI);
    validateStringFieldNotNull(jsonSchemaType, ModelNodeNames.JSON_SCHEMA_TYPE);
    validateStringFieldNotNull(jsonSchemaTitle, ModelNodeNames.JSON_SCHEMA_TITLE);
    validateStringFieldNotNull(jsonSchemaDescription, ModelNodeNames.JSON_SCHEMA_DESCRIPTION);
    validateListFieldNotNull(jsonLdTypes, ModelNodeNames.JSON_LD_TYPE);
    validateStringFieldNotEmpty(schemaOrgName, ModelNodeNames.SCHEMA_ORG_NAME);
    validateStringFieldNotNull(schemaOrgDescription, ModelNodeNames.SCHEMA_ORG_DESCRIPTION);
    validateVersionFieldNotNull(modelVersion, ModelNodeNames.SCHEMA_ORG_SCHEMA_VERSION);
    validateOptionalFieldNotNull(artifactVersion, ModelNodeNames.PAV_VERSION);
    validateOptionalFieldNotNull(artifactVersionStatus, ModelNodeNames.BIBO_STATUS);
    validateOptionalFieldNotNull(previousArtifactVersion, ModelNodeNames.PAV_PREVIOUS_VERSION);
    validateOptionalFieldNotNull(derivedFrom, ModelNodeNames.PAV_DERIVED_FROM);
  }
}
