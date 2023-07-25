package org.metadatacenter.artifacts.model.core;

public sealed interface ChildSchemaArtifact permits ElementSchemaSchemaArtifact, FieldSchemaSchemaArtifact
{
  boolean isMultiple();
}
