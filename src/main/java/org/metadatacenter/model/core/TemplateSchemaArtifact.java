package org.metadatacenter.model.core;

import java.util.Collections;
import java.util.Map;

public class TemplateSchemaArtifact extends SchemaArtifact
{
  private final Map<String, FieldSchemaArtifact> fieldSchemas;
  private final Map<String, ElementSchemaArtifact> elementSchemas;
  private final TemplateUI templateUI;

  public TemplateSchemaArtifact(SchemaArtifact schemaArtifact, Map<String, FieldSchemaArtifact> fieldSchemas,
    Map<String, ElementSchemaArtifact> elementSchemas, TemplateUI templateUI)
  {
    super(schemaArtifact);
    this.fieldSchemas = Collections.unmodifiableMap(fieldSchemas);
    this.elementSchemas = Collections.unmodifiableMap(elementSchemas);
    this.templateUI = templateUI;
  }

  public Map<String, FieldSchemaArtifact> getFieldSchemas()
  {
    return fieldSchemas;
  }

  public Map<String, ElementSchemaArtifact> getElementSchemas()
  {
    return elementSchemas;
  }

  public TemplateUI getTemplateUI()
  {
    return templateUI;
  }

  @Override public String toString()
  {
    return super.toString() + "\n TemplateSchemaArtifact{" + "fieldSchemas=" + fieldSchemas + ", elementSchemas=" + elementSchemas
      + ", templateUI=" + templateUI + '}';
  }
}
