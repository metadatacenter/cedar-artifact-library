package org.metadatacenter.artifacts.model.core;

import org.apache.poi.poifs.property.Child;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ElementSchemaArtifact extends SchemaArtifact implements ChildSchemaArtifact, ParentSchemaArtifact
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

  @Override public ElementSchemaArtifact getElementSchemaArtifact(String name)
  {
    if (elementSchemas.containsKey(name))
      return elementSchemas.get(name);
    else
      throw new IllegalArgumentException("Element " + name + "not present in template " + getName());
  }

  @Override public FieldSchemaArtifact getFieldSchemaArtifact(String name)
  {
    if (fieldSchemas.containsKey(name))
      return fieldSchemas.get(name);
    else
      throw new IllegalArgumentException("Field " + name + "not present in element " + getName());
  }

  @Override public boolean hasFields() { return !fieldSchemas.isEmpty(); }

  @Override public boolean hasElements() { return !elementSchemas.isEmpty(); }

  @Override public boolean isMultiple()
  {
    return isMultiple;
  }

  @Override public boolean isField(String name) { return fieldSchemas.containsKey(name); }

  @Override public boolean isElement(String name) { return elementSchemas.containsKey(name); }

  @Override public ParentArtifactUI getUI() { return elementUI; }

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
