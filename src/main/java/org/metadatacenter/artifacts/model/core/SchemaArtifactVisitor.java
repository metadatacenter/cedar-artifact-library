package org.metadatacenter.artifacts.model.core;

interface SchemaArtifactVisitor
{
  void visitParentSchemaArtifact(ParentSchemaArtifact parentSchemaArtifact);

  void visitChildSchemaArtifact(ChildSchemaArtifact childSchemaArtifact);
}
