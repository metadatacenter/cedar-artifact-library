package org.metadatacenter.artifacts.model.core;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public sealed interface ParentSchemaArtifact permits TemplateSchemaArtifact, ElementSchemaSchemaArtifact
{
  boolean isField(String name);

  boolean isElement(String name);

  boolean hasFields();

  boolean hasElements();

  FieldSchemaSchemaArtifact getFieldSchemaArtifact(String name);

  ElementSchemaSchemaArtifact getElementSchemaArtifact(String name);

  LinkedHashMap<String, FieldSchemaSchemaArtifact> getFieldSchemas();

  LinkedHashMap<String, ElementSchemaSchemaArtifact> getElementSchemas();

  Map<String, URI> getChildPropertyURIs();

  ParentArtifactUI getUI();

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

    for (String name: getUI().getOrder())
      if (isField(name))
        fieldNames.add(name);

    return fieldNames;
  }

  default List<String> getElementNames()
  {
    ArrayList<String> elementNames = new ArrayList<>();

    for (String name: getUI().getOrder())
      if (isElement(name))
        elementNames.add(name);

    return elementNames;
  }
}
