package org.metadatacenter.artifacts.model.core;

public interface SchemaArtifactVisitor
{
  void visitTemplateSchemaArtifact(TemplateSchemaArtifact templateSchemaArtifact);

  void visitElementSchemaArtifact(ElementSchemaArtifact elementSchemaArtifact, String path);

  void visitFieldSchemaArtifact(FieldSchemaArtifact fieldSchemaArtifact, String path);
}
