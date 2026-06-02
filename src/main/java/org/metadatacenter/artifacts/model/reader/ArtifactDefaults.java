package org.metadatacenter.artifacts.model.reader;

import org.metadatacenter.artifacts.model.core.Status;
import org.metadatacenter.artifacts.model.core.Version;

import java.util.Optional;

/**
 * The readers' version/status defaulting policy, in one place.
 *
 * <p>A freshly authored artifact carries a default version ({@link Version#DEFAULT}) and status
 * ({@link Status#DRAFT}); the builders apply these eagerly. When reading, the same defaults are
 * applied to the <em>top-level</em> artifact only ({@link Policy#APPLY}). A nested child that omits
 * version or status is left untouched ({@link Policy#PRESERVE}) so that real templates whose child
 * fields carry neither round-trip losslessly.
 */
final class ArtifactDefaults
{
  private ArtifactDefaults() {}

  /** Whether to fill an absent version / status with the standard defaults. */
  enum Policy { APPLY, PRESERVE }

  static Optional<Version> version(Optional<Version> version, Policy policy)
  {
    return policy == Policy.APPLY ? version.or(() -> Optional.of(Version.DEFAULT)) : version;
  }

  static Optional<Status> status(Optional<Status> status, Policy policy)
  {
    return policy == Policy.APPLY ? status.or(() -> Optional.of(Status.DRAFT)) : status;
  }
}
