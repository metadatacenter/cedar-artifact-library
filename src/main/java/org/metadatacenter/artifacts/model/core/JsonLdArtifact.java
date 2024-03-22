package org.metadatacenter.artifacts.model.core;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface JsonLdArtifact extends MonitoredArtifact
{
  List<URI> jsonLdTypes();

  Optional<URI> jsonLdId();

  Map<String, URI> jsonLdContext();
}
