package org.metadatacenter.model.core;

import java.util.Map;

public class FieldSchemaArtifact extends SchemaArtifact
{
  private final String inputType;
  private final boolean isMultiple;

  public FieldSchemaArtifact(SchemaArtifact schemaArtifact, Map<String, FieldSchemaArtifact> fields, String inputType,
    boolean isMultiple)
  {
    super(schemaArtifact);
    this.inputType = inputType;
    this.isMultiple = isMultiple;
  }

  public String getInputType()
  {
    return inputType;
  }

  public boolean isMultiple()
  {
    return isMultiple;
  }
}
