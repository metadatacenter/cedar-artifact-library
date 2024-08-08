package org.metadatacenter.artifacts.model.core.ui;

import java.util.LinkedHashMap;
import java.util.List;

public sealed interface ParentArtifactUi permits TemplateUi, ElementUi
{
  List<String> order();

  LinkedHashMap<String, String> propertyLabels();

  LinkedHashMap<String, String> propertyDescriptions();
}
