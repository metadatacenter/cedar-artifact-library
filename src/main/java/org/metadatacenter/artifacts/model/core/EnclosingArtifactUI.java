package org.metadatacenter.artifacts.model.core;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface EnclosingArtifactUI
{
  List<String> getOrder();

  Map<String, String> getPropertyLabels();

  Map<String, String> getPropertyDescriptions();

  Optional<String> getHeader();

  Optional<String> getFooter();
}
