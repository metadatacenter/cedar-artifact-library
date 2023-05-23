package org.metadatacenter.artifacts.model.core;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public sealed interface ParentArtifactUI permits TemplateUI, ElementUI
{
  List<String> getOrder();

  Map<String, String> getPropertyLabels();

  Map<String, String> getPropertyDescriptions();

  Optional<String> getHeader();

  Optional<String> getFooter();
}
