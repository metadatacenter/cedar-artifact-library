package org.metadatacenter.artifacts.model.core;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Optional;

public interface MonitoredArtifact
{
  Optional<URI> createdBy();

  Optional<URI> modifiedBy();

  Optional<OffsetDateTime> createdOn();

  Optional<OffsetDateTime> lastUpdatedOn();
}
