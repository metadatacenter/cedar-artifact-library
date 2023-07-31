package org.metadatacenter.artifacts.model.core;

import org.metadatacenter.model.ModelNodeNames;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateMapFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateUIFieldNotNull;

public final class ElementSchemaArtifact extends SchemaArtifact
  implements ChildSchemaArtifact, ParentSchemaArtifact
{
  private final Map<String, FieldSchemaArtifact> fieldSchemas;
  private final Map<String, ElementSchemaArtifact> elementSchemas;
  private final Map<String, URI> childPropertyURIs;
  private final boolean isMultiple;
  private final ElementUI elementUI;

  public ElementSchemaArtifact(SchemaArtifact schemaArtifact, Map<String, FieldSchemaArtifact> fieldSchemas,
    Map<String, ElementSchemaArtifact> elementSchemas, Map<String, URI> childPropertyURIs,
    boolean isMultiple, ElementUI elementUI)
  {
    super(schemaArtifact);
    this.fieldSchemas = Collections.unmodifiableMap(fieldSchemas);
    this.elementSchemas = Collections.unmodifiableMap(elementSchemas);
    this.childPropertyURIs = Collections.unmodifiableMap(childPropertyURIs);
    this.isMultiple = isMultiple;
    this.elementUI = elementUI;

    validate();
  }

  public ElementSchemaArtifact(List<URI> jsonLdTypes, Optional<URI> jsonLdId, Map<String, URI> jsonLdContext,
    Optional<URI> createdBy, Optional<URI> modifiedBy, Optional<OffsetDateTime> createdOn, Optional<OffsetDateTime> lastUpdatedOn,
    URI jsonSchemaSchemaUri, String jsonSchemaType, String jsonSchemaTitle, String jsonSchemaDescription,
    String schemaOrgName, String schemaOrgDescription, Optional<String> schemaOrgIdentifier,
    Version modelVersion, Optional<Version> artifactVersion, Optional<Status> artifactVersionStatus,
    Optional<URI> previousVersion, Optional<URI> derivedFrom, Map<String, FieldSchemaArtifact> fieldSchemas,
    Map<String, ElementSchemaArtifact> elementSchemas, Map<String, URI> childPropertyURIs, boolean isMultiple, ElementUI elementUI)
  {
    super(jsonLdTypes, jsonLdId, jsonLdContext,
      createdBy, modifiedBy, createdOn, lastUpdatedOn,
      jsonSchemaSchemaUri, jsonSchemaType, jsonSchemaTitle, jsonSchemaDescription,
      schemaOrgName, schemaOrgDescription, schemaOrgIdentifier,
      modelVersion, artifactVersion, artifactVersionStatus, previousVersion, derivedFrom);
    this.fieldSchemas = Collections.unmodifiableMap(fieldSchemas);
    this.elementSchemas = Collections.unmodifiableMap(elementSchemas);
    this.childPropertyURIs = Collections.unmodifiableMap(childPropertyURIs);
    this.isMultiple = isMultiple;
    this.elementUI = elementUI;

    validate();
  }

  public ElementSchemaArtifact(ElementSchemaArtifact elementSchemaArtifact)
  {
    super(elementSchemaArtifact);
    this.fieldSchemas = elementSchemaArtifact.fieldSchemas;
    this.elementSchemas = elementSchemaArtifact.elementSchemas;
    this.childPropertyURIs = elementSchemaArtifact.childPropertyURIs;
    this.isMultiple = elementSchemaArtifact.isMultiple;
    this.elementUI = elementSchemaArtifact.elementUI;

    validate();
  }

  private ElementSchemaArtifact(Builder builder)
  {
    super(builder.jsonLdTypes, builder.jsonLdId, builder.jsonLdContext,
      builder.createdBy, builder.modifiedBy, builder.createdOn, builder.lastUpdatedOn,
      builder.jsonSchemaSchemaUri, builder.jsonSchemaType, builder.jsonSchemaTitle, builder.jsonSchemaDescription,
      builder.schemaOrgName, builder.schemaOrgDescription, builder.schemaOrgIdentifier,
      builder.modelVersion, builder.artifactVersion, builder.artifactVersionStatus, builder.previousVersion,
      builder.derivedFrom);
    this.fieldSchemas = Collections.unmodifiableMap(builder.fieldSchemas);
    this.elementSchemas = Collections.unmodifiableMap(builder.elementSchemas);
    this.childPropertyURIs = Collections.unmodifiableMap(builder.childPropertyURIs);
    this.isMultiple = builder.isMultiple;
    this.elementUI = builder.elementUI;

    validate();
  }

  @Override public LinkedHashMap<String, FieldSchemaArtifact> getFieldSchemas()
  {
    LinkedHashMap<String, FieldSchemaArtifact> orderedFieldSchemas = new LinkedHashMap<>();

    for (String fieldName: getUI().getOrder()) {
      if (fieldSchemas.containsKey(fieldName))
        orderedFieldSchemas.put(fieldName, fieldSchemas.get(fieldName));
    }
    return orderedFieldSchemas;
  }

  @Override public LinkedHashMap<String, ElementSchemaArtifact> getElementSchemas()
  {
    LinkedHashMap<String, ElementSchemaArtifact> orderedElementSchemas = new LinkedHashMap<>();

    for (String elementName : getUI().getOrder()) {
      if (elementSchemas.containsKey(elementName))
        orderedElementSchemas.put(elementName, elementSchemas.get(elementName));
    }
    return orderedElementSchemas;
  }

  @Override public ElementSchemaArtifact getElementSchemaArtifact(String name)
  {
    if (elementSchemas.containsKey(name))
      return elementSchemas.get(name);
    else
      throw new IllegalArgumentException("Element " + name + "not present in template " + getName());
  }

  @Override public FieldSchemaArtifact getFieldSchemaArtifact(String name)
  {
    if (fieldSchemas.containsKey(name))
      return fieldSchemas.get(name);
    else
      throw new IllegalArgumentException("Field " + name + "not present in element " + getName());
  }

  @Override public Map<String, URI> getChildPropertyURIs()
  {
    return Collections.unmodifiableMap(childPropertyURIs);
  }

  @Override public boolean isMultiple()
  {
    return isMultiple;
  }

  @Override public ParentArtifactUI getUI() {return elementUI;}

  public ElementUI getElementUI()
  {
    return elementUI;
  }

  @Override public String toString()
  {
    return "ElementSchemaArtifact{" + "fieldSchemas=" + fieldSchemas + ", elementSchemas=" + elementSchemas
      + ", childPropertyURIs=" + childPropertyURIs + ", isMultiple=" + isMultiple + ", elementUI=" + elementUI + '}';
  }

  private void validate()
  {
    validateMapFieldNotNull(this, fieldSchemas, "fieldSchemas");
    validateMapFieldNotNull(this, elementSchemas, "elementSchemas");
    validateUIFieldNotNull(this, elementUI, ModelNodeNames.UI);
    validateMapFieldNotNull(this, childPropertyURIs, "childPropertyURIs");
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder
  {
    private List<URI> jsonLdTypes = Collections.emptyList();
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
    private String schemaOrgName;
    private String schemaOrgDescription = "";
    private Optional<String> schemaOrgIdentifier = Optional.empty();
    private Version modelVersion = new Version(1, 6, 0); // TODO
    private Optional<Version> artifactVersion = Optional.of(new Version(0, 0, 1)); // TODO
    private Optional<Status> artifactVersionStatus = Optional.of(Status.DRAFT);
    private Optional<URI> previousVersion = Optional.empty();
    private Optional<URI> derivedFrom = Optional.empty();
    private Map<String, FieldSchemaArtifact> fieldSchemas = Collections.emptyMap();
    private Map<String, ElementSchemaArtifact> elementSchemas = Collections.emptyMap();
    private Map<String, URI> childPropertyURIs  = Collections.emptyMap();
    private boolean isMultiple = false;
    private ElementUI elementUI;

    private Builder() {
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

    public Builder withJsonLdContext(Map<String, URI> jsonLdContext)
    {
      this.jsonLdContext = jsonLdContext;
      return this;
    }

    public Builder withCreatedBy(Optional<URI> createdBy)
    {
      this.createdBy = createdBy;
      return this;
    }

    public Builder withModifiedBy(Optional<URI> modifiedBy)
    {
      this.modifiedBy = modifiedBy;
      return this;
    }

    public Builder withCreatedOn(Optional<OffsetDateTime> createdOn)
    {
      this.createdOn = createdOn;
      return this;
    }

    public Builder withLastUpdatedOn(Optional<OffsetDateTime> lastUpdatedOn)
    {
      this.lastUpdatedOn = lastUpdatedOn;
      return this;
    }

    public Builder withJsonSchemaSchemaUri(URI jsonSchemaSchemaUri)
    {
      this.jsonSchemaSchemaUri = jsonSchemaSchemaUri;
      return this;
    }

    public Builder withJsonSchemaType(String jsonSchemaType)
    {
      this.jsonSchemaType = jsonSchemaType;
      return this;
    }

    public Builder withJsonSchemaTitle(String jsonSchemaTitle)
    {
      this.jsonSchemaTitle = jsonSchemaTitle;
      return this;
    }

    public Builder withJsonSchemaDescription(String jsonSchemaDescription)
    {
      this.jsonSchemaDescription = jsonSchemaDescription;
      return this;
    }

    public Builder withJsonLdTypes(List<URI> jsonLdTypes)
    {
      this.jsonLdTypes = jsonLdTypes;
      return this;
    }

    public Builder withName(String schemaOrgName)
    {
      this.schemaOrgName = schemaOrgName;

      if (this.jsonSchemaTitle.isEmpty())
        this.jsonSchemaTitle = schemaOrgName + " element schema";

      if (this.jsonSchemaDescription.isEmpty())
        this.jsonSchemaDescription = schemaOrgName + " element schema generated by the CEDAR Artifact Library";

      return this;
    }

    public Builder withDescription(String schemaOrgDescription)
    {
      this.schemaOrgDescription = schemaOrgDescription;
      return this;
    }

    public Builder withSchemaOrgIdentifier(String schemaOrgIdentifier)
    {
      this.schemaOrgIdentifier = Optional.of(schemaOrgIdentifier);
      return this;
    }

    public Builder withModelVersion(Version modelVersion)
    {
      this.modelVersion = modelVersion;
      return this;
    }

    public Builder withArtifactVersion(Optional<Version> artifactVersion)
    {
      this.artifactVersion = artifactVersion;
      return this;
    }

    public Builder withArtifactVersionStatus(Optional<Status> artifactVersionStatus)
    {
      this.artifactVersionStatus = artifactVersionStatus;
      return this;
    }

    public Builder withPreviousVersion(Optional<URI> previousVersion)
    {
      this.previousVersion = previousVersion;
      return this;
    }

    public Builder withDerivedFrom(Optional<URI> derivedFrom)
    {
      this.derivedFrom = derivedFrom;
      return this;
    }

    public Builder withFieldSchemas(Map<String, FieldSchemaArtifact> fieldSchemas)
    {
      this.fieldSchemas = fieldSchemas;
      return this;
    }

    public Builder withElementSchemas(Map<String, ElementSchemaArtifact> elementSchemas)
    {
      this.elementSchemas = elementSchemas;
      return this;
    }

    public Builder withChildPropertyURI(String childPropertyName, URI childPropertyURI)
    {
      this.childPropertyURIs.put(childPropertyName, childPropertyURI);
      return this;
    }

    public Builder withIsMultiple(boolean isMultiple)
    {
      this.isMultiple = isMultiple;
      return this;
    }

    public Builder withElementUI(ElementUI elementUI)
    {
      this.elementUI = elementUI;
      return this;
    }

    public ElementSchemaArtifact build()
    {
      return new ElementSchemaArtifact(this);
    }
  }
}
