package org.metadatacenter.artifacts.model.core;

import java.util.List;
import java.util.Map;

public sealed interface ParentInstanceArtifact permits TemplateInstanceArtifact, ElementInstanceArtifact
{
  String name();

  String description();

  Map<String, List<FieldInstanceArtifact>> fieldInstances();

  Map<String, List<ElementInstanceArtifact>> elementInstances();
}
