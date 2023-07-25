package org.metadatacenter.artifacts.model.core;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.List;
import java.util.Optional;

public class InstanceArtifact extends Artifact
{
  public InstanceArtifact(Artifact artifact)
  {
    super(artifact);
  }

  public InstanceArtifact(List<URI> jsonLdTypes, Optional<URI> jsonLdId, Map<String, URI> jsonLdContext, Optional<URI> createdBy, Optional<URI> modifiedBy,
    Optional<OffsetDateTime> createdOn, Optional<OffsetDateTime> lastUpdatedOn)
  {
    super(jsonLdTypes, jsonLdId, jsonLdContext, createdBy, modifiedBy, createdOn, lastUpdatedOn);

    validate();
  }

  private void validate()
  {
  }
}
