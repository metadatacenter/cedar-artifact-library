package org.metadatacenter.artifacts.model.core;

import java.net.URI;
import java.util.Map;

import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateMapFieldNotNull;

public record Annotations(Map<String, String> literalAnnotations, Map<String, URI> iriAnnotations)
{
  public Annotations {
    validateMapFieldNotNull(this, literalAnnotations, "literalAnnotations");
    validateMapFieldNotNull(this, iriAnnotations, "iriAnnotations");
  }
}