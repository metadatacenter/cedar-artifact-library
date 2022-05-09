package org.metadatacenter.model.core;

/**
 * While field instances may not necessarily have JSON-LD identifiers or provenance fields (name, description,
 * createdBy, modifiedBy, createdOn, lastUpdatedOn) the model allows them.
 */
public class FieldInstanceArtifact extends InstanceArtifact
{
  private final String value;
  private final String label;
  private final String notation;
  private final String prefLabel;

  public FieldInstanceArtifact(InstanceArtifact instanceArtifact, String value, String label, String notation,
    String prefLabel)
  {
    super(instanceArtifact);
    this.value = value;
    this.label = label;
    this.notation = notation;
    this.prefLabel = prefLabel;
  }

  public String getJSONLDValue()
  {
    return value;
  }

  public String getLabel()
  {
    return label;
  }

  public String getNotation()
  {
    return notation;
  }

  public String getPrefLabel()
  {
    return prefLabel;
  }


  @Override public String toString()
  {
    return super.toString() + "\n FieldInstanceArtifact{" + "value='" + value + '\'' + ", label='" + label + '\'' + ", notation='" + notation
      + '\'' + ", prefLabel='" + prefLabel + '\'' + '}';
  }
}

