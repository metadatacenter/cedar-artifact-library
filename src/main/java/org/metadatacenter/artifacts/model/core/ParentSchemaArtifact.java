package org.metadatacenter.artifacts.model.core;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public sealed interface ParentSchemaArtifact permits TemplateSchemaArtifact, ElementSchemaArtifact
{
  String getName();

  default boolean isField(String name) {return getFieldSchemas().containsKey(name);}

  default boolean isElement(String name) {return getElementSchemas().containsKey(name);}

  default boolean hasFields() {return !getFieldSchemas().isEmpty();}

  default boolean hasElements() {return !getElementSchemas().isEmpty();}

  default boolean hasAttributeValueField()
  {
    return this.getFieldSchemas().values().stream().anyMatch(fs -> fs.getFieldUI().isAttributeValue());
  }

  LinkedHashMap<String, FieldSchemaArtifact> getFieldSchemas();

  LinkedHashMap<String, ElementSchemaArtifact> getElementSchemas();

  FieldSchemaArtifact getFieldSchemaArtifact(String name);

  ElementSchemaArtifact getElementSchemaArtifact(String name);

  default Map<String, URI> getChildPropertyURIs()
  {
    Map<String, URI> childPropertyURIs = new HashMap<>();

    for (ChildSchemaArtifact childSchemaArtifact : getChildSchemas())
      if (childSchemaArtifact.getPropertyURI().isPresent())
        childPropertyURIs.put(childSchemaArtifact.getName(), childSchemaArtifact.getPropertyURI().get());

    return childPropertyURIs;
  }

  ParentArtifactUI getUI();

  default void addChild(ChildSchemaArtifact childSchemaArtifact)
  {
    String childName = childSchemaArtifact.getName();

    if (getChildNames().contains(childSchemaArtifact.getName()))
      throw new IllegalArgumentException("schema artifact " + getName() + " already has a child named " + childName);

    // TODO UI.order
    // TODO UI.propertyLabels
    // TODO UI.propertyDescriptions
    // TODO childPropertyURIs
  }

  default List<ChildSchemaArtifact> getChildSchemas()
  {
    var childSchemas = new ArrayList<ChildSchemaArtifact>();

    for (String childName : getUI().getOrder()) {
      if (getElementSchemas().containsKey(childName))
        childSchemas.add(getElementSchemas().get(childName));
      else if (getFieldSchemas().containsKey(childName))
        childSchemas.add(getFieldSchemas().get(childName));
      else
        throw new RuntimeException("internal error: no child " + childName + " present in artifact");
    }

    return childSchemas;
  }

  default List<String> getFieldNames()
  {
    ArrayList<String> fieldNames = new ArrayList<>();

    for (String name : getUI().getOrder())
      if (isField(name))
        fieldNames.add(name);

    return fieldNames;
  }

  default List<String> getElementNames()
  {
    ArrayList<String> elementNames = new ArrayList<>();

    for (String name : getUI().getOrder())
      if (isElement(name))
        elementNames.add(name);

    return elementNames;
  }

  default List<String> getChildNames()
  {
    ArrayList<String> childNames = new ArrayList<>();

    for (String name : getUI().getOrder())
      childNames.add(name);

    return childNames;
  }
}
