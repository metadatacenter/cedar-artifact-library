package org.metadatacenter.artifacts.model.core;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Optional;

public interface MonitoredArtifact
{
  Optional<URI> getCreatedBy();

  Optional<URI> getModifiedBy();

  Optional<OffsetDateTime> getCreatedOn();

  Optional<OffsetDateTime> getLastUpdatedOn();
}
