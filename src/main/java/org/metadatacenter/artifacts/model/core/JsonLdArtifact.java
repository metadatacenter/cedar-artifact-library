package org.metadatacenter.artifacts.model.core;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface JsonLdArtifact
{
  List<URI> getJsonLdTypes();

  Optional<URI> getJsonLdId();

  Map<String, URI> getJsonLdContext();
}
