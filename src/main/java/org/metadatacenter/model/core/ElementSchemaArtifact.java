package org.metadatacenter.model.core;

import java.util.Collections;
import java.util.Map;

public class ElementSchemaArtifact extends SchemaArtifact
{
  private final Map<String, FieldSchemaArtifact> fieldSchemas;
  private final Map<String, ElementSchemaArtifact> elementSchemas;
  private final boolean isMultiple;

  public ElementSchemaArtifact(SchemaArtifact schemaArtifact, Map<String, FieldSchemaArtifact> fieldSchemas,
    Map<String, ElementSchemaArtifact> elementSchemas, boolean isMultiple)
  {
    super(schemaArtifact);
    this.fieldSchemas = Collections.unmodifiableMap(fieldSchemas);
    this.elementSchemas = Collections.unmodifiableMap(elementSchemas);
    this.isMultiple = isMultiple;
  }

  public Map<String, FieldSchemaArtifact> getFieldsSchemas()
  {
    return fieldSchemas;
  }

  public Map<String, ElementSchemaArtifact> getElementSchemas()
  {
    return elementSchemas;
  }

  public boolean isMultiple()
  {
    return isMultiple;
  }
}
