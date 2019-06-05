package org.metadatacenter.model.core;

import java.util.Collections;
import java.util.Map;

public class ElementSchemaArtifact extends SchemaArtifact
{
  private final Map<String, FieldSchemaArtifact> fields;
  private final Map<String, ElementSchemaArtifact> elements;
  private final boolean isMultiple;

  public ElementSchemaArtifact(SchemaArtifact schemaArtifact, Map<String, FieldSchemaArtifact> fields,
    Map<String, ElementSchemaArtifact> elements, boolean isMultiple)
  {
    super(schemaArtifact);
    this.fields = Collections.unmodifiableMap(fields);
    this.elements = Collections.unmodifiableMap(elements);
    this.isMultiple = isMultiple;
  }

  public Map<String, FieldSchemaArtifact> getFields()
  {
    return fields;
  }

  public Map<String, ElementSchemaArtifact> getElements()
  {
    return elements;
  }

  public boolean isMultiple()
  {
    return isMultiple;
  }
}
