package org.metadatacenter.artifacts.model.core;

public interface ChildInstanceArtifact extends ChildArtifact, InstanceArtifact
{
  void accept(InstanceArtifactVisitor visitor, String path);
}
