package org.metadatacenter.artifacts.model.core;

public sealed interface ChildSchemaArtifact permits ElementSchemaArtifact, FieldSchemaArtifact
{
  String getName();

  boolean isMultiple();
}
