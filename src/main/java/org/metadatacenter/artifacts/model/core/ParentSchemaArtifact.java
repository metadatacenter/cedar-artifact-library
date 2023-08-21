package org.metadatacenter.artifacts.model.core;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public sealed interface ParentSchemaArtifact permits TemplateSchemaArtifact, ElementSchemaArtifact
{
  String name();

  default boolean isField(String name) {return fieldSchemas().containsKey(name);}

  default boolean isElement(String name) {return elementSchemas().containsKey(name);}

  default boolean hasFields() {return !fieldSchemas().isEmpty();}

  default boolean hasElements() {return !elementSchemas().isEmpty();}

  default boolean hasAttributeValueField()
  {
    return this.fieldSchemas().values().stream().anyMatch(fs -> fs.fieldUI().isAttributeValue());
  }

  Map<String, FieldSchemaArtifact> fieldSchemas();

  Map<String, ElementSchemaArtifact> elementSchemas();

  LinkedHashMap<String, FieldSchemaArtifact> orderedFieldSchemas();

  LinkedHashMap<String, ElementSchemaArtifact> orderedElementSchemas();

  FieldSchemaArtifact getFieldSchemaArtifact(String name);

  ElementSchemaArtifact getElementSchemaArtifact(String name);

  default Map<String, URI> getChildPropertyURIs()
  {
    Map<String, URI> childPropertyURIs = new HashMap<>();

    for (ChildSchemaArtifact childSchemaArtifact : getChildSchemas())
      if (childSchemaArtifact.propertyURI().isPresent())
        childPropertyURIs.put(childSchemaArtifact.name(), childSchemaArtifact.propertyURI().get());

    return childPropertyURIs;
  }

  ParentArtifactUI getUI();

  default void addChild(ChildSchemaArtifact childSchemaArtifact)
  {
    String childName = childSchemaArtifact.name();

    if (getChildNames().contains(childSchemaArtifact.name()))
      throw new IllegalArgumentException("schema artifact " + name() + " already has a child named " + childName);

    // TODO UI.order
    // TODO UI.propertyLabels
    // TODO UI.propertyDescriptions
    // TODO childPropertyURIs
  }

  default List<ChildSchemaArtifact> getChildSchemas()
  {
    var childSchemas = new ArrayList<ChildSchemaArtifact>();

    for (String childName : getUI().order()) {
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

    for (String name : getUI().order())
      if (isField(name))
        fieldNames.add(name);

    return fieldNames;
  }

  default List<String> getElementNames()
  {
    ArrayList<String> elementNames = new ArrayList<>();

    for (String name : getUI().order())
      if (isElement(name))
        elementNames.add(name);

    return elementNames;
  }

  default List<String> getChildNames()
  {
    ArrayList<String> childNames = new ArrayList<>();

    for (String name : getUI().order())
      childNames.add(name);

    return childNames;
  }
}
