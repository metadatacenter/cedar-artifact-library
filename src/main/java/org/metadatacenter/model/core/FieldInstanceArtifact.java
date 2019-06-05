package org.metadatacenter.model.core;

/**
 * While field instances may not necessarily have JSON-LD identifiers or provenance fields (name, description,
 * createdBy, modifiedBy, createdOn, lastUpdatedOn) the model allows them.
 */
public class FieldInstanceArtifact extends InstanceArtifact
{
  private final String value;

  public FieldInstanceArtifact(InstanceArtifact instanceArtifact, String value)
  {
    super(instanceArtifact);
    this.value = value;
  }

  public String getValue()
  {
    return value;
  }
}
