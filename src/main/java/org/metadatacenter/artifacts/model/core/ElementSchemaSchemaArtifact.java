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

public final class ElementSchemaSchemaArtifact extends SchemaSchemaArtifact
  implements ChildSchemaArtifact, ParentSchemaArtifact
{
  private final Map<String, FieldSchemaSchemaArtifact> fieldSchemas;
  private final Map<String, ElementSchemaSchemaArtifact> elementSchemas;
  private final Map<String, URI> childPropertyURIs;
  private final boolean isMultiple;
  private final ElementUI elementUI;

  public ElementSchemaSchemaArtifact(SchemaSchemaArtifact schemaArtifact, Map<String, FieldSchemaSchemaArtifact> fieldSchemas,
    Map<String, ElementSchemaSchemaArtifact> elementSchemas, Map<String, URI> childPropertyURIs,
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

  public ElementSchemaSchemaArtifact(List<URI> jsonLdTypes, Optional<URI> jsonLdId, Map<String, URI> jsonLdContext,
    Optional<URI> createdBy, Optional<URI> modifiedBy, Optional<OffsetDateTime> createdOn, Optional<OffsetDateTime> lastUpdatedOn,
    URI jsonSchemaSchemaUri, String jsonSchemaType, String jsonSchemaTitle, String jsonSchemaDescription,
    String schemaOrgName, String schemaOrgDescription, Optional<String> schemaOrgIdentifier,
    Version modelVersion, Optional<Version> artifactVersion, Optional<Status> artifactVersionStatus,
    Optional<URI> previousVersion, Optional<URI> derivedFrom, Map<String, FieldSchemaSchemaArtifact> fieldSchemas,
    Map<String, ElementSchemaSchemaArtifact> elementSchemas, Map<String, URI> childPropertyURIs, boolean isMultiple, ElementUI elementUI)
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

  public ElementSchemaSchemaArtifact(ElementSchemaSchemaArtifact elementSchemaArtifact)
  {
    super(elementSchemaArtifact);
    this.fieldSchemas = elementSchemaArtifact.fieldSchemas;
    this.elementSchemas = elementSchemaArtifact.elementSchemas;
    this.childPropertyURIs = elementSchemaArtifact.childPropertyURIs;
    this.isMultiple = elementSchemaArtifact.isMultiple;
    this.elementUI = elementSchemaArtifact.elementUI;

    validate();
  }

  private ElementSchemaSchemaArtifact(Builder builder)
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

  @Override public LinkedHashMap<String, FieldSchemaSchemaArtifact> getFieldSchemas()
  {
    LinkedHashMap<String, FieldSchemaSchemaArtifact> orderedFieldSchemas = new LinkedHashMap<>();

    for (String fieldName : getUI().getOrder()) {
      if (this.fieldSchemas.containsKey(fieldName))
        orderedFieldSchemas.put(fieldName, this.fieldSchemas.get(fieldName));
    }
    return orderedFieldSchemas;
  }

  @Override public LinkedHashMap<String, ElementSchemaSchemaArtifact> getElementSchemas()
  {
    LinkedHashMap<String, ElementSchemaSchemaArtifact> orderedElementSchemas = new LinkedHashMap<>();

    for (String elementName : getUI().getOrder()) {
      if (this.elementSchemas.containsKey(elementName))
        orderedElementSchemas.put(elementName, this.elementSchemas.get(elementName));
    }
    return orderedElementSchemas;
  }

  @Override public ElementSchemaSchemaArtifact getElementSchemaArtifact(String name)
  {
    if (elementSchemas.containsKey(name))
      return elementSchemas.get(name);
    else
      throw new IllegalArgumentException("Element " + name + "not present in template " + getName());
  }

  @Override public FieldSchemaSchemaArtifact getFieldSchemaArtifact(String name)
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

  @Override public boolean hasFields() {return !fieldSchemas.isEmpty();}

  @Override public boolean hasElements() {return !elementSchemas.isEmpty();}

  @Override public boolean isMultiple()
  {
    return isMultiple;
  }

  @Override public boolean isField(String name) {return fieldSchemas.containsKey(name);}

  @Override public boolean isElement(String name) {return elementSchemas.containsKey(name);}

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
    private Optional<Version> artifactVersion = Optional.of(new Version(1, 0, 0)); // TODO
    private Optional<Status> artifactVersionStatus = Optional.of(Status.DRAFT);
    private Optional<URI> previousVersion = Optional.empty();
    private Optional<URI> derivedFrom = Optional.empty();
    private Map<String, FieldSchemaSchemaArtifact> fieldSchemas = Collections.emptyMap();
    private Map<String, ElementSchemaSchemaArtifact> elementSchemas = Collections.emptyMap();
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

    public Builder withSchemaOrgName(String schemaOrgName)
    {
      this.schemaOrgName = schemaOrgName;
      return this;
    }

    public Builder withSchemaOrgDescription(String schemaOrgDescription)
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

    public Builder withFieldSchemas(Map<String, FieldSchemaSchemaArtifact> fieldSchemas)
    {
      this.fieldSchemas = fieldSchemas;
      return this;
    }

    public Builder withElementSchemas(Map<String, ElementSchemaSchemaArtifact> elementSchemas)
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

    public ElementSchemaSchemaArtifact build()
    {
      return new ElementSchemaSchemaArtifact(this);
    }
  }
}
