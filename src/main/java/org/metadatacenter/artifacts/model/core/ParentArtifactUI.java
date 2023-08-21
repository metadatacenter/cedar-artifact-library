package org.metadatacenter.artifacts.model.core;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public sealed interface ParentArtifactUI permits TemplateUI, ElementUI
{
  List<String> order();

  Map<String, String> propertyLabels();

  Map<String, String> propertyDescriptions();

  Optional<String> header();

  Optional<String> footer();
}
