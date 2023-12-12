package org.metadatacenter.artifacts.model.core;

interface InstanceArtifactVisitor
{
  void visitTemplateInstanceArtifact(ParentInstanceArtifact parentInstanceArtifact, String path);

  void visitElementInstanceArtifact(ElementInstanceArtifact elementInstanceArtifact, String path);

  void visitFieldInstanceArtifact(FieldInstanceArtifact fieldInstanceArtifact, String path);
}
