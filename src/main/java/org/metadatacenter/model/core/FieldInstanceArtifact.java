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
  private final String altLabel;

  public FieldInstanceArtifact(InstanceArtifact instanceArtifact, String value, String label, String notation,
    String prefLabel, String altLabel)
  {
    super(instanceArtifact);
    this.value = value;
    this.label = label;
    this.notation = notation;
    this.prefLabel = prefLabel;
    this.altLabel = altLabel;
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

  public String getAltLabel()
  {
    return altLabel;
  }
}
