package org.metadatacenter.model.core;

import java.util.Collections;
import java.util.Map;

public class ElementSchemaArtifact extends SchemaArtifact
{
  private final Map<String, FieldSchemaArtifact> fieldSchemas;
  private final Map<String, ElementSchemaArtifact> elementSchemas;
  private final boolean isMultiple;
  private final ElementUI elementUI;

  public ElementSchemaArtifact(SchemaArtifact schemaArtifact, Map<String, FieldSchemaArtifact> fieldSchemas,
    Map<String, ElementSchemaArtifact> elementSchemas, boolean isMultiple, ElementUI elementUI)
  {
    super(schemaArtifact);
    this.fieldSchemas = Collections.unmodifiableMap(fieldSchemas);
    this.elementSchemas = Collections.unmodifiableMap(elementSchemas);
    this.isMultiple = isMultiple;
    this.elementUI = elementUI;
  }

  public ElementSchemaArtifact(ElementSchemaArtifact elementSchemaArtifact)
  {
    super(elementSchemaArtifact);
    this.fieldSchemas = elementSchemaArtifact.fieldSchemas;
    this.elementSchemas = elementSchemaArtifact.elementSchemas;
    this.isMultiple = elementSchemaArtifact.isMultiple;
    this.elementUI = elementSchemaArtifact.elementUI;
  }

  public Map<String, FieldSchemaArtifact> getFieldSchemas()
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

  public ElementUI getElementUI()
  {
    return elementUI;
  }

  @Override public String toString()
  {
    return super.toString() + "\n ElementSchemaArtifact{" + "fieldSchemas=" + fieldSchemas + ", elementSchemas=" + elementSchemas
      + ", isMultiple=" + isMultiple + ", elementUI=" + elementUI + '}';
  }
}
