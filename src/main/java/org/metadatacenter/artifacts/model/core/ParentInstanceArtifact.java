package org.metadatacenter.artifacts.model.core;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public sealed interface ParentInstanceArtifact extends ParentArtifact permits TemplateInstanceArtifact, ElementInstanceArtifact
{
  Optional<String> name();

  Optional<String> description();

  Map<String, List<FieldInstanceArtifact>> fieldInstances();

  Map<String, List<ElementInstanceArtifact>> elementInstances();

  default void accept(ArtifactVisitor visitor) {
    visitor.visitParentArtifact(this);

    for (List<FieldInstanceArtifact> children : fieldInstances().values()) {
      for (FieldInstanceArtifact child : children)
        child.accept(visitor);
    }

    for (List<ElementInstanceArtifact> children : elementInstances().values()) {
      for (ElementInstanceArtifact child : children)
        child.accept(visitor);
    }
  }
}
