package org.metadatacenter.artifacts.model.core;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class TemplateSchemaArtifact extends SchemaArtifact implements ParentSchemaArtifact
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

  @Override public LinkedHashMap<String, FieldSchemaArtifact> getFieldSchemas()
  {
    LinkedHashMap<String, FieldSchemaArtifact> fieldSchemas = new LinkedHashMap<>();

    for (String fieldName: getUI().getOrder()) {
      if (fieldSchemas.containsKey(fieldName))
        fieldSchemas.put(fieldName, fieldSchemas.get(fieldName));
    }
    return fieldSchemas;
  }

  @Override public LinkedHashMap<String, ElementSchemaArtifact> getElementSchemas()
  {
    LinkedHashMap<String, ElementSchemaArtifact> elementSchemas = new LinkedHashMap<>();

    for (String elementName: getUI().getOrder()) {
      if (elementSchemas.containsKey(elementName))
        elementSchemas.put(elementName, elementSchemas.get(elementName));
    }
    return elementSchemas;
  }

  @Override public FieldSchemaArtifact getFieldSchemaArtifact(String name)
  {
    if (fieldSchemas.containsKey(name))
      return fieldSchemas.get(name);
    else
      throw new IllegalArgumentException("Field " + name + "not present in template " + getName());
  }

  @Override public ElementSchemaArtifact getElementSchemaArtifact(String name)
  {
    if (elementSchemas.containsKey(name))
      return elementSchemas.get(name);
    else
      throw new IllegalArgumentException("Element " + name + "not present in template " + getName());
  }

  @Override public boolean hasFields() { return !fieldSchemas.isEmpty(); }

  @Override public boolean hasElements() { return !elementSchemas.isEmpty(); }

  @Override public boolean isField(String name) { return fieldSchemas.containsKey(name); }

  @Override public boolean isElement(String name) { return elementSchemas.containsKey(name); }

  @Override public ParentArtifactUI getUI() { return templateUI; }

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
