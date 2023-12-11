package org.metadatacenter.artifacts.model.core;

interface ArtifactVisitor
{
  void visitChildArtifact(ChildArtifact childArtifact);

  void visitParentArtifact(ParentArtifact parentArtifact);
}
