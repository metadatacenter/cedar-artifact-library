package org.metadatacenter.model.core;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ElementSchemaArtifact extends SchemaArtifact
{
  private final List<FieldSchemaArtifact> fields;
  private final List<ElementSchemaArtifact> elements;
  private final boolean isMultiple;

  public ElementSchemaArtifact(String jsonLDID, Set<String> jsonLDTypes, String name, String description, String createdBy,
    String modifiedBy, OffsetDateTime createdOn, OffsetDateTime lastUpdatedOn, String schema, String schemaVersion,
    String version, String status, Map<String, String> context, List<FieldSchemaArtifact> fields,
    List<ElementSchemaArtifact> elements, boolean isMultiple)
  {
    super(jsonLDID, jsonLDTypes, name, description, createdBy, modifiedBy, createdOn, lastUpdatedOn, schema,
      schemaVersion, version, status, context);
    this.fields = Collections.unmodifiableList(fields);
    this.elements = Collections.unmodifiableList(elements);
    this.isMultiple = isMultiple;
  }

  public List<FieldSchemaArtifact> getFields()
  {
    return fields;
  }

  public List<ElementSchemaArtifact> getElements()
  {
    return elements;
  }

  public boolean isMultiple()
  {
    return isMultiple;
  }
}
