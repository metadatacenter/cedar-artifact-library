package org.metadatacenter.model.core;

import java.util.Collections;
import java.util.List;

public class FieldSchemaArtifact extends SchemaArtifact
{
  private final String skosPrefLabel;
  private final String defaultValue;
  private final List<String> skosAlternateLabels;
  private final FieldUI fieldUI;

  public FieldSchemaArtifact(SchemaArtifact schemaArtifact, String skosPrefLabel, String defaultValue, List<String> skosAlternateLabels,
    FieldUI fieldUI)
  {
    super(schemaArtifact);
    this.skosPrefLabel = skosPrefLabel;
    this.defaultValue = defaultValue;
    this.skosAlternateLabels = Collections.unmodifiableList(skosAlternateLabels);
    this.fieldUI = fieldUI;
  }

  public String getSkosPrefLabel()
  {
    return skosPrefLabel;
  }

  public String getDefaultValue()
  {
    return defaultValue;
  }

  public List<String> getSkosAlternateLabels()
  {
    return skosAlternateLabels;
  }

  public FieldUI getFieldUI()
  {
    return fieldUI;
  }

  @Override public String toString()
  {
    return super.toString() + "\n FieldSchemaArtifact{" + "skosPrefLabel='" + skosPrefLabel + '\'' + ", defaultValue='" + defaultValue + '\''
      + ", skosAlternateLabels=" + skosAlternateLabels + ", fieldUI=" + fieldUI + '}';
  }
}
