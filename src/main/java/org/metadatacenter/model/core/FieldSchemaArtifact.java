package org.metadatacenter.model.core;

public class FieldSchemaArtifact extends SchemaArtifact
{
  private final String skosPrefLabel;
  private final String fieldInputType;
  private final boolean isMultiple;

  public FieldSchemaArtifact(SchemaArtifact schemaArtifact, String skosPrefLabel, String fieldInputType, boolean isMultiple)
  {
    super(schemaArtifact);
    this.skosPrefLabel = skosPrefLabel;
    this.fieldInputType = fieldInputType;
    this.isMultiple = isMultiple;
  }

  public String getSKOSPrefLabel()
  {
    return skosPrefLabel;
  }

  public String getFieldInputType()
  {
    return fieldInputType;
  }

  public boolean isMultiple()
  {
    return isMultiple;
  }

  @Override public String toString()
  {
    return super.toString() + "\n FieldSchemaArtifact{" + "skosPrefLabel='" + skosPrefLabel + '\'' + ", fieldInputType='" + fieldInputType
      + '\'' + ", isMultiple=" + isMultiple + '}';
  }
}
