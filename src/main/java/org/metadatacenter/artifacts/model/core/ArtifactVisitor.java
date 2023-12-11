package org.metadatacenter.artifacts.model.core;

public interface ArtifactVisitor
{
  void visitChildArtifact(ChildArtifact childArtifact);

  void visitParentArtifact(ParentArtifact parentArtifact);
}
