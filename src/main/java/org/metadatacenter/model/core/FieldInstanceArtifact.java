package org.metadatacenter.model.core;

/**
 * While field instances may not necessarily have JSON-LD identifiers or provenance fields (name, description,
 * createdBy, modifiedBy, createdOn, lastUpdatedOn) the model allows them.
 */
public class FieldInstanceArtifact extends InstanceArtifact
{
  private final String jsonLDValue;
  private final String rdfsLabel;
  private final String skosNotation;
  private final String skosPrefLabel;
  private final String skosAltLabel;

  public FieldInstanceArtifact(InstanceArtifact instanceArtifact, String jsonLDValue, String rdfsLabel,
    String skosNotation, String skosPrefLabel, String skosAltLabel)
  {
    super(instanceArtifact);
    this.jsonLDValue = jsonLDValue;
    this.rdfsLabel = rdfsLabel;
    this.skosNotation = skosNotation;
    this.skosPrefLabel = skosPrefLabel;
    this.skosAltLabel = skosAltLabel;
  }

  public String getJSONLDValue()
  {
    return jsonLDValue;
  }

  public String getRdfsLabel()
  {
    return rdfsLabel;
  }

  public String getSkosNotation()
  {
    return skosNotation;
  }

  public String getSkosPrefLabel()
  {
    return skosPrefLabel;
  }

  public String getSkosAltLabel()
  {
    return skosAltLabel;
  }
}
