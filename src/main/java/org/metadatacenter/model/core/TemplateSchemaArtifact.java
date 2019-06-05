package org.metadatacenter.model.core;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class TemplateSchemaArtifact extends SchemaArtifact
{
  private final Map<String, FieldSchemaArtifact> fields;
  private final Map<String, ElementSchemaArtifact> elements;

  public TemplateSchemaArtifact(String jsonLDID, Set<String> jsonLDTypes, String name, String description,
    String createdBy, String modifiedBy, OffsetDateTime createdOn, OffsetDateTime lastUpdatedOn, String schema,
    String schemaVersion, String version, String status, Map<String, String> context,
    Map<String, FieldSchemaArtifact> fields, Map<String, ElementSchemaArtifact> elements)
  {
    super(jsonLDID, jsonLDTypes, name, description, createdBy, modifiedBy, createdOn, lastUpdatedOn, schema,
      schemaVersion, version, status, context);
    this.fields = Collections.unmodifiableMap(fields);
    this.elements = Collections.unmodifiableMap(elements);
  }

  public Map<String, FieldSchemaArtifact> getFields()
  {
    return fields;
  }

  public Map<String, ElementSchemaArtifact> getElements()
  {
    return elements;
  }
}
