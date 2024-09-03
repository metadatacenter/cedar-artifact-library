package org.metadatacenter.artifacts.model.core;

import org.metadatacenter.artifacts.model.core.ui.ParentArtifactUi;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public sealed interface ParentSchemaArtifact extends ParentArtifact permits TemplateSchemaArtifact,
  ElementSchemaArtifact
{
  String name();

  ParentArtifactUi getUi();

  Map<String, FieldSchemaArtifact> fieldSchemas();

  Map<String, ElementSchemaArtifact> elementSchemas();

  default boolean isField(String fieldKey) { return fieldSchemas().containsKey(fieldKey); }

  default boolean isStaticField(String fieldKey) {
    return fieldSchemas().containsKey(fieldKey) && fieldSchemas().get(fieldKey).isStatic();
  }

  default boolean isAttributeValueField(String fieldKey)
  {
    return fieldSchemas().containsKey(fieldKey) && fieldSchemas().get(fieldKey).isAttributeValue();
  }

  default boolean isElement(String elementKey) { return elementSchemas().containsKey(elementKey); }

  default boolean hasFields() { return !fieldSchemas().isEmpty(); }

  default boolean hasElements() { return !elementSchemas().isEmpty(); }

  default boolean hasAttributeValueField()
  {
    return this.fieldSchemas().values().stream().anyMatch(fs -> fs.isAttributeValue());
  }

  default LinkedHashMap<String, FieldSchemaArtifact> orderedFieldSchemas()
  {
    LinkedHashMap<String, FieldSchemaArtifact> orderedFieldSchemas = new LinkedHashMap<>();

    for (String fieldKey: getUi().order()) {
      if (fieldSchemas().containsKey(fieldKey))
        orderedFieldSchemas.put(fieldKey, fieldSchemas().get(fieldKey));
    }
    return orderedFieldSchemas;
  }

  default LinkedHashMap<String, ElementSchemaArtifact> orderedElementSchemas()
  {
    LinkedHashMap<String, ElementSchemaArtifact> orderedElementSchemas = new LinkedHashMap<>();

    for (String elementKey : getUi().order()) {
      if (elementSchemas().containsKey(elementKey))
        orderedElementSchemas.put(elementKey, elementSchemas().get(elementKey));
    }
    return orderedElementSchemas;
  }

  default ElementSchemaArtifact getElementSchemaArtifact(String elementKey)
  {
    if (elementSchemas().containsKey(elementKey))
      return elementSchemas().get(elementKey);
    else
      throw new IllegalArgumentException("Element " + elementKey + " not present in template " + name());
  }

  default FieldSchemaArtifact getFieldSchemaArtifact(String fieldKey)
  {
    if (fieldSchemas().containsKey(fieldKey))
      return fieldSchemas().get(fieldKey);
    else
      throw new IllegalArgumentException("Field " + fieldKey + " not present in element " + name());
  }

  Optional<URI> instanceJsonLdType();

  default LinkedHashMap<String, URI> getChildPropertyUris()
  {
    LinkedHashMap<String, URI> childPropertyUris = new LinkedHashMap<>();

    for (Map.Entry<String, ChildSchemaArtifact> childSchemaArtifactEntry : getChildSchemas().entrySet()) {
      String childKey = childSchemaArtifactEntry.getKey();
      if (!isStaticField(childKey) && !isAttributeValueField(childKey)) {
        ChildSchemaArtifact childSchemaArtifact = childSchemaArtifactEntry.getValue();
        if (childSchemaArtifact.propertyUri().isPresent())
          childPropertyUris.put(childKey, childSchemaArtifact.propertyUri().get());
        else // Missing property-IRI mapping, generate one
          childPropertyUris.put(childKey, generatePropertyUri(childKey));
      }
    }

    return childPropertyUris;
  }

  default boolean hasChildren()
  {
    return !elementSchemas().isEmpty() || !fieldSchemas().isEmpty();
  }

  default LinkedHashMap<String, ChildSchemaArtifact> getChildSchemas()
  {
    var childSchemas = new LinkedHashMap<String, ChildSchemaArtifact>();

    for (String childKey : getUi().order()) {
      if (elementSchemas().containsKey(childKey))
        childSchemas.put(childKey, elementSchemas().get(childKey));
      else if (fieldSchemas().containsKey(childKey))
        childSchemas.put(childKey, fieldSchemas().get(childKey));
    }

    return childSchemas;
  }

  default LinkedHashMap<String, String> getChildSchemaOrgNames()
  {
    var childSchemaOrgNames = new LinkedHashMap<String, String>();

    for (String childKey : getUi().order()) {
      if (elementSchemas().containsKey(childKey))
        childSchemaOrgNames.put(childKey, elementSchemas().get(childKey).name());
      else if (fieldSchemas().containsKey(childKey))
        childSchemaOrgNames.put(childKey, fieldSchemas().get(childKey).name());
    }

    return childSchemaOrgNames;
  }

  default List<String> getFieldKeys()
  {
    ArrayList<String> fieldKeys = new ArrayList<>();

    for (String childKey : getUi().order())
      if (isField(childKey))
        fieldKeys.add(childKey);

    return fieldKeys;
  }

  default List<String> getElementKeys()
  {
    ArrayList<String> elementKeys = new ArrayList<>();

    for (String childKey : getUi().order())
      if (isElement(childKey))
        elementKeys.add(childKey);

    return elementKeys;
  }

  default List<String> getChildKeys()
  {
    ArrayList<String> childKeys = new ArrayList<>(getUi().order());

    return childKeys;
  }

  default List<String> getNonStaticChildKeys()
  {

    List<String> childKeys = getUi().order().stream().filter(name -> !isStaticField(name)).toList();

    return childKeys;
  }

  default List<String> getNonStaticNonAttributeValueChildKeys()
  {

    List<String> childKeys = getUi().order().stream().filter(name -> !isStaticField(name) && !isAttributeValueField(name)).toList();

    return childKeys;
  }

  default URI generatePropertyUri(String childKey)
  { // TODO Put constant in ModelNodeNames; childKey is temporary
    return URI.create("https://schema.metadatacenter.org/properties/" +
      URLEncoder.encode(childKey, StandardCharsets.UTF_8));
  }

}
