package org.metadatacenter.model.core;

import java.util.Collections;
import java.util.List;

public class FieldSchemaArtifact extends SchemaArtifact
{
  private final String skosPrefLabel;
  private final List<String> skosAlternateLabels;
  private final FieldUI fieldUI;
  private final ValueConstraints valueConstraints;

  public FieldSchemaArtifact(SchemaArtifact schemaArtifact, String skosPrefLabel,
    List<String> skosAlternateLabels, FieldUI fieldUI, ValueConstraints valueConstraints)
  {
    super(schemaArtifact);
    this.skosPrefLabel = skosPrefLabel;
    this.skosAlternateLabels = Collections.unmodifiableList(skosAlternateLabels);
    this.valueConstraints = valueConstraints;
    this.fieldUI = fieldUI;
  }

  public FieldSchemaArtifact(FieldSchemaArtifact fieldSchemaArtifact)
  {
    super(fieldSchemaArtifact);
    this.skosPrefLabel = fieldSchemaArtifact.skosPrefLabel;
    this.skosAlternateLabels = fieldSchemaArtifact.skosAlternateLabels;
    this.valueConstraints = fieldSchemaArtifact.valueConstraints;
    this.fieldUI = fieldSchemaArtifact.fieldUI;
  }

  public String getSkosPrefLabel()
  {
    return skosPrefLabel;
  }

  public List<String> getSkosAlternateLabels()
  {
    return skosAlternateLabels;
  }

  public FieldUI getFieldUI()
  {
    return fieldUI;
  }

  public ValueConstraints getValueConstraints()
  {
    return valueConstraints;
  }

  @Override public String toString()
  {
    return "FieldSchemaArtifact{" + "skosPrefLabel='" + skosPrefLabel + '\'' + ", skosAlternateLabels="
      + skosAlternateLabels + ", fieldUI=" + fieldUI + ", valueConstraints=" + valueConstraints + '}';
  }
}
