package org.metadatacenter.artifacts.model.core;

public interface ChildArtifact
{
  void accept(ArtifactVisitor visitor);
}
