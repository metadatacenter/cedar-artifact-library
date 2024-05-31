package org.metadatacenter.artifacts.model.core;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

public interface JsonLdArtifact extends MonitoredArtifact
{
  List<URI> jsonLdTypes();

  Optional<URI> jsonLdId();

  LinkedHashMap<String, URI> jsonLdContext();
}
