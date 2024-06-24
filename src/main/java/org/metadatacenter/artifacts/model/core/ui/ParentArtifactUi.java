package org.metadatacenter.artifacts.model.core.ui;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

public sealed interface ParentArtifactUi permits TemplateUi, ElementUi
{
  List<String> order();

  LinkedHashMap<String, String> propertyLabels();

  LinkedHashMap<String, String> propertyDescriptions();

  Optional<String> header();

  Optional<String> footer();

  default LinkedHashMap<String, String> processPropertyLabels(LinkedHashMap<String, String> propertiesMap, List<String> keyOrder)
  {
    LinkedHashMap<String, String> reorderedMap = new LinkedHashMap<>();

    for (String key : keyOrder) {
      if (propertiesMap.containsKey(key))
        reorderedMap.put(key, propertiesMap.get(key));
      else
        reorderedMap.put(key, key);
    }

    return reorderedMap;
  }

  default LinkedHashMap<String, String> processPropertyDescriptions(LinkedHashMap<String, String> propertiesMap, List<String> keyOrder)
  {
    LinkedHashMap<String, String> reorderedMap = new LinkedHashMap<>();

    for (String key : keyOrder) {
      if (propertiesMap.containsKey(key))
        reorderedMap.put(key, propertiesMap.get(key));
      else
        reorderedMap.put(key, "");
    }

    return reorderedMap;
  }

}
