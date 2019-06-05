package org.metadatacenter.model.core;

import java.util.Collections;
import java.util.Map;

public class TemplateSchemaArtifact extends SchemaArtifact
{
  private final Map<String, FieldSchemaArtifact> fields;
  private final Map<String, ElementSchemaArtifact> elements;

  public TemplateSchemaArtifact(SchemaArtifact schemaArtifact, Map<String, FieldSchemaArtifact> fields,
    Map<String, ElementSchemaArtifact> elements)
  {
    super(schemaArtifact);
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
