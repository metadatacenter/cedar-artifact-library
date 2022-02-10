package org.metadatacenter.model.core;

import java.util.Collections;
import java.util.Map;

public class TemplateSchemaArtifact extends SchemaArtifact
{
  private final Map<String, FieldSchemaArtifact> fieldSchemas;
  private final Map<String, ElementSchemaArtifact> elementSchemas;

  public TemplateSchemaArtifact(SchemaArtifact schemaArtifact, Map<String, FieldSchemaArtifact> fieldSchemas,
    Map<String, ElementSchemaArtifact> elementSchemas)
  {
    super(schemaArtifact);
    this.fieldSchemas = Collections.unmodifiableMap(fieldSchemas);
    this.elementSchemas = Collections.unmodifiableMap(elementSchemas);
  }

  public Map<String, FieldSchemaArtifact> getFieldSchemas()
  {
    return fieldSchemas;
  }

  public Map<String, ElementSchemaArtifact> getElementSchemas()
  {
    return elementSchemas;
  }
}
