package org.metadatacenter.artifacts.model.core;

public interface InstanceArtifactVisitor
{
  void visitTemplateInstanceArtifact(TemplateInstanceArtifact templateInstanceArtifact);

  void visitElementInstanceArtifact(ElementInstanceArtifact elementInstanceArtifact, String path);

  void visitFieldInstanceArtifact(FieldInstanceArtifact fieldInstanceArtifact, String path);

  void visitAttributeValueFieldInstanceArtifact(FieldInstanceArtifact fieldInstanceArtifact, String path, String specificationPath);
}
