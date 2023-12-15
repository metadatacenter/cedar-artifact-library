package org.metadatacenter.artifacts.model.core;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public sealed interface ParentInstanceArtifact extends ParentArtifact permits TemplateInstanceArtifact,
  ElementInstanceArtifact
{
  Optional<String> name();

  Optional<String> description();

  Map<String, List<FieldInstanceArtifact>> fieldInstances();

  Map<String, List<ElementInstanceArtifact>> elementInstances();
}
