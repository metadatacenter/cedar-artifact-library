package org.metadatacenter.model.core;

public class FieldSchemaArtifact extends SchemaArtifact
{
  private final String fieldInputType;
  private final boolean isMultiple;

  public FieldSchemaArtifact(SchemaArtifact schemaArtifact, String fieldInputType, boolean isMultiple)
  {
    super(schemaArtifact);
    this.fieldInputType = fieldInputType;
    this.isMultiple = isMultiple;
  }

  public String getFieldInputType()
  {
    return fieldInputType;
  }

  public boolean isMultiple()
  {
    return isMultiple;
  }
}
