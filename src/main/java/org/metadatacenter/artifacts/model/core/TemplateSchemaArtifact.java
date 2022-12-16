package org.metadatacenter.artifacts.model.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

  public TemplateSchemaArtifact(TemplateSchemaArtifact templateSchemaArtifact)
  {
    super(templateSchemaArtifact);
    this.fieldSchemas = templateSchemaArtifact.fieldSchemas;
    this.elementSchemas = templateSchemaArtifact.elementSchemas;
    this.templateUI = templateSchemaArtifact.templateUI;
  }

  public Map<String, FieldSchemaArtifact> getFieldSchemas()
  {
    return fieldSchemas;
  }

  public Map<String, ElementSchemaArtifact> getElementSchemas()
  {
    return elementSchemas;
  }

  public FieldSchemaArtifact getFieldSchemaArtifact(String name)
  {
    if (fieldSchemas.containsKey(name))
      return fieldSchemas.get(name);
    else
      throw new IllegalArgumentException("Field " + name + "not present in template " + getName());
  }

  public ElementSchemaArtifact getElementSchemaArtifact(String name)
  {
    if (elementSchemas.containsKey(name))
      return elementSchemas.get(name);
    else
      throw new IllegalArgumentException("Element " + name + "not present in template " + getName());
  }

  public boolean hasFields() { return !fieldSchemas.isEmpty(); }

  public boolean hasElements() { return !elementSchemas.isEmpty(); }

  public TemplateUI getTemplateUI()
  {
    return templateUI;
  }

  public boolean isField(String name) { return fieldSchemas.containsKey(name); }

  public boolean isElement(String name) { return elementSchemas.containsKey(name); }

  public List<String> getFieldNames()
  {
    ArrayList<String> fieldNames = new ArrayList<>();

    for (String name: templateUI.getOrder())
      if (isField(name))
        fieldNames.add(name);

    return fieldNames;
  }

  public List<String> getElementNames()
  {
    ArrayList<String> elementNames = new ArrayList<>();

    for (String name: templateUI.getOrder())
      if (isElement(name))
        elementNames.add(name);

    return elementNames;
  }

  @Override public String toString()
  {
    return super.toString() + "\n TemplateSchemaArtifact{" + "fieldSchemas=" + fieldSchemas + ", elementSchemas=" + elementSchemas
      + ", templateUI=" + templateUI + '}';
  }
}
