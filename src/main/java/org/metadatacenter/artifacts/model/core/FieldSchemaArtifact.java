package org.metadatacenter.artifacts.model.core;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class FieldSchemaArtifact extends SchemaArtifact
{
  private final FieldUI fieldUI;
  private final Optional<ValueConstraints> valueConstraints;
  private final Optional<String> skosPrefLabel;
  private final List<String> skosAlternateLabels;

  public FieldSchemaArtifact(SchemaArtifact schemaArtifact, FieldUI fieldUI,
    Optional<ValueConstraints> valueConstraints, Optional<String> skosPrefLabel, List<String> skosAlternateLabels)
  {
    super(schemaArtifact);
    this.valueConstraints = valueConstraints;
    this.fieldUI = fieldUI;
    this.skosPrefLabel = skosPrefLabel;
    this.skosAlternateLabels = Collections.unmodifiableList(skosAlternateLabels);
  }

  public FieldUI getFieldUI()
  {
    return fieldUI;
  }

  public boolean isHidden() { return fieldUI.isHidden(); }

  public Optional<ValueConstraints> getValueConstraints()
  {
    return valueConstraints;
  }

  public Optional<String> getSkosPrefLabel()
  {
    return skosPrefLabel;
  }

  public List<String> getSkosAlternateLabels()
  {
    return skosAlternateLabels;
  }

  @Override public String toString()
  {
    return "FieldSchemaArtifact{" + "fieldUI=" + fieldUI + ", valueConstraints=" + valueConstraints + ", skosPrefLabel="
      + skosPrefLabel + ", skosAlternateLabels=" + skosAlternateLabels + '}';
  }
}
