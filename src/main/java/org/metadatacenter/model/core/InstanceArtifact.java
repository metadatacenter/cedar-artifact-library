package org.metadatacenter.model.core;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Set;

public abstract class InstanceArtifact extends Artifact
{
  public InstanceArtifact(String jsonLDID, Set<String> jsonLDTypes, String name, String description, String createdBy,
    String modifiedBy, OffsetDateTime createdOn, OffsetDateTime lastUpdatedOn,
    Map<String, String> context)
  {
    super(jsonLDID, jsonLDTypes, name, description, createdBy, modifiedBy, createdOn, lastUpdatedOn, context);
  }

}
