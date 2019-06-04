package org.metadatacenter.model.core;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Set;

/**
 * While field instances may not necessarily have JSON-LD identifiers or provenance fields (name, description,
 * createdBy, modifiedBy, createdOn, lastUpdatedOn) the model allows them.
 */
public class FieldInstanceArtifact extends InstanceArtifact
{
  private final String value;

  public FieldInstanceArtifact(String jsonLDID, Set<String> jsonLDTypes, String name, String description,
    String createdBy, String modifiedBy, OffsetDateTime createdOn, OffsetDateTime lastUpdatedOn,
    Map<String, String> context, String value)
  {
    super(jsonLDID, jsonLDTypes, name, description, createdBy, modifiedBy, createdOn, lastUpdatedOn, context);
    this.value = value;
  }

  public String getValue()
  {
    return value;
  }
}
