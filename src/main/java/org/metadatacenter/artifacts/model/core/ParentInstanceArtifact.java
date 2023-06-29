package org.metadatacenter.artifacts.model.core;

import java.util.List;
import java.util.Map;

public sealed interface ParentInstanceArtifact permits TemplateInstanceArtifact, ElementInstanceArtifact
{
  Map<String, List<FieldInstanceArtifact>> getFieldInstances();

  Map<String, List<ElementInstanceArtifact>> getElementInstances();
}
