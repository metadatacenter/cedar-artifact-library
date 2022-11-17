package org.metadatacenter.model.core;

import java.util.Optional;

/**
 * While field instances may not necessarily have JSON-LD identifiers or provenance fields (name, description,
 * createdBy, modifiedBy, createdOn, lastUpdatedOn) the model allows them.
 */
public class FieldInstanceArtifact extends InstanceArtifact
{
  private final String value;
  private final String label;
  private final Optional<String> notation;
  private final Optional<String> prefLabel;

  public FieldInstanceArtifact(InstanceArtifact instanceArtifact, String value, String label,
    Optional<String> notation, Optional<String> prefLabel)
  {
    super(instanceArtifact);
    this.value = value;
    this.label = label;
    this.notation = notation;
    this.prefLabel = prefLabel;
  }

  public FieldInstanceArtifact(FieldInstanceArtifact fieldInstanceArtifact)
  {
    super(fieldInstanceArtifact);
    this.value = fieldInstanceArtifact.value;
    this.label = fieldInstanceArtifact.label;
    this.notation = fieldInstanceArtifact.notation;
    this.prefLabel = fieldInstanceArtifact.prefLabel;
  }

  public String getJSONLDValue()
  {
    return value;
  }

  public String getLabel()
  {
    return label;
  }

  public Optional<String> getNotation()
  {
    return notation;
  }

  public Optional<String> getPrefLabel()
  {
    return prefLabel;
  }


  @Override public String toString()
  {
    return super.toString() + "\n FieldInstanceArtifact{" + "value='" + value + '\'' + ", label='" + label + '\'' + ", notation='" + notation
      + '\'' + ", prefLabel='" + prefLabel + '\'' + '}';
  }
}

