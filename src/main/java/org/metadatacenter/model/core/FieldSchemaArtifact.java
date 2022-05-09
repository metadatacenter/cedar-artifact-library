package org.metadatacenter.model.core;

import java.util.Collections;
import java.util.List;

public class FieldSchemaArtifact extends SchemaArtifact
{
  private final String skosPrefLabel;
  private final String fieldInputType;
  private final String defaultValue;
  private final List<String> skosAlternateLabels;

  public FieldSchemaArtifact(SchemaArtifact schemaArtifact, String skosPrefLabel, String fieldInputType,
    String defaultValue, List<String> skosAlternateLabels)
  {
    super(schemaArtifact);
    this.skosPrefLabel = skosPrefLabel;
    this.fieldInputType = fieldInputType;
    this.defaultValue = defaultValue;
    this.skosAlternateLabels = Collections.unmodifiableList(skosAlternateLabels);
  }

  public String getSKOSPrefLabel()
  {
    return skosPrefLabel;
  }

  public String getFieldInputType()
  {
    return fieldInputType;
  }

  public List<String> getSkosAlternateLabels() { return skosAlternateLabels; }

  @Override public String toString()
  {
    return super.toString() + "FieldSchemaArtifact{" + "skosPrefLabel='" + skosPrefLabel + '\'' + ", fieldInputType='" + fieldInputType
      + '\'' + ", defaultValue='" + defaultValue + '\'' + ", skosAlternateLabels=" + skosAlternateLabels + '}';
  }
}
