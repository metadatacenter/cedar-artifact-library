package org.metadatacenter.artifacts.model.core.ui;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public sealed interface ParentArtifactUi permits TemplateUi, ElementUi
{
  List<String> order();

  Map<String, String> propertyLabels();

  Map<String, String> propertyDescriptions();

  Optional<String> header();

  Optional<String> footer();
}
