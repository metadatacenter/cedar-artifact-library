package org.metadatacenter.artifacts.model.core;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public sealed interface ParentSchemaArtifact extends ParentArtifact permits TemplateSchemaArtifact, ElementSchemaArtifact
{
  String name();

  ParentArtifactUi getUi();

  Map<String, FieldSchemaArtifact> fieldSchemas();

  Map<String, ElementSchemaArtifact> elementSchemas();

  default boolean isField(String name) { return fieldSchemas().containsKey(name); }

  default boolean isElement(String name) { return elementSchemas().containsKey(name); }

  default boolean hasFields() { return !fieldSchemas().isEmpty(); }

  default boolean hasElements() { return !elementSchemas().isEmpty(); }

  default boolean hasAttributeValueField()
  {
    return this.fieldSchemas().values().stream().anyMatch(fs -> fs.fieldUi().isAttributeValue());
  }

  default LinkedHashMap<String, FieldSchemaArtifact> orderedFieldSchemas()
  {
    LinkedHashMap<String, FieldSchemaArtifact> orderedFieldSchemas = new LinkedHashMap<>();

    for (String fieldName: getUi().order()) {
      if (fieldSchemas().containsKey(fieldName))
        orderedFieldSchemas.put(fieldName, fieldSchemas().get(fieldName));
    }
    return orderedFieldSchemas;
  }

  default LinkedHashMap<String, ElementSchemaArtifact> orderedElementSchemas()
  {
    LinkedHashMap<String, ElementSchemaArtifact> orderedElementSchemas = new LinkedHashMap<>();

    for (String elementName : getUi().order()) {
      if (elementSchemas().containsKey(elementName))
        orderedElementSchemas.put(elementName, elementSchemas().get(elementName));
    }
    return orderedElementSchemas;
  }

  default ElementSchemaArtifact getElementSchemaArtifact(String name)
  {
    if (elementSchemas().containsKey(name))
      return elementSchemas().get(name);
    else
      throw new IllegalArgumentException("Element " + name + "not present in template " + name());
  }

  default FieldSchemaArtifact getFieldSchemaArtifact(String name)
  {
    if (fieldSchemas().containsKey(name))
      return fieldSchemas().get(name);
    else
      throw new IllegalArgumentException("Field " + name + "not present in element " + name());
  }

  default Map<String, URI> getChildPropertyUris()
  {
    Map<String, URI> childPropertyUris = new HashMap<>();

    for (ChildSchemaArtifact childSchemaArtifact : getChildSchemas())
      if (childSchemaArtifact.propertyUri().isPresent())
        childPropertyUris.put(childSchemaArtifact.name(), childSchemaArtifact.propertyUri().get());

    return childPropertyUris;
  }

  default boolean hasChildren()
  {
    return !elementSchemas().isEmpty() || !fieldSchemas().isEmpty();
  }

  default List<ChildSchemaArtifact> getChildSchemas()
  {
    var childSchemas = new ArrayList<ChildSchemaArtifact>();

    for (String childName : getUi().order()) {
      if (elementSchemas().containsKey(childName))
        childSchemas.add(elementSchemas().get(childName));
      else if (fieldSchemas().containsKey(childName))
        childSchemas.add(fieldSchemas().get(childName));
      else
        throw new RuntimeException("internal error: no child " + childName + " present in artifact");
    }

    return childSchemas;
  }

  default List<String> getFieldNames()
  {
    ArrayList<String> fieldNames = new ArrayList<>();

    for (String name : getUi().order())
      if (isField(name))
        fieldNames.add(name);

    return fieldNames;
  }

  default List<String> getElementNames()
  {
    ArrayList<String> elementNames = new ArrayList<>();

    for (String name : getUi().order())
      if (isElement(name))
        elementNames.add(name);

    return elementNames;
  }

  default List<String> getChildNames()
  {
    ArrayList<String> childNames = new ArrayList<>();

    for (String name : getUi().order())
      childNames.add(name);

    return childNames;
  }

  default void accept(SchemaArtifactVisitor visitor) {
    visitor.visitParentSchemaArtifact(this);

    for (FieldSchemaArtifact child : fieldSchemas().values()) {
        child.accept(visitor);
    }

    for (ElementSchemaArtifact child : elementSchemas().values()) {
      child.accept(visitor);
    }
  }
}
