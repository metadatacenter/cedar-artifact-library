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

  default LinkedHashMap<String, String> processPropertyLabels(LinkedHashMap<String, String> propertyLabels, List<String> keyOrder)
  {
    LinkedHashMap<String, String> reorderedMap = new LinkedHashMap<>();

    for (String key : keyOrder) {
      if (propertyLabels.containsKey(key))
        reorderedMap.put(key, propertyLabels.get(key));
      else
        reorderedMap.put(key, key);
    }

    return reorderedMap;
  }

  default LinkedHashMap<String, String> processPropertyDescriptions(LinkedHashMap<String, String> propertyDescriptions, List<String> keyOrder)
  {
    LinkedHashMap<String, String> reorderedMap = new LinkedHashMap<>();

    for (String key : keyOrder) {
      if (propertyDescriptions.containsKey(key))
        reorderedMap.put(key, propertyDescriptions.get(key));
      else
        reorderedMap.put(key, "");
    }

    return reorderedMap;
  }

}
