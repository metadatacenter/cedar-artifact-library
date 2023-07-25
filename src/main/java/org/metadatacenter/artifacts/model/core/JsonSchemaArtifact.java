package org.metadatacenter.artifacts.model.core;

import java.net.URI;

public interface JsonSchemaArtifact
{
  URI getJsonSchemaSchemaUri();

  String getJsonSchemaType();

  String getJsonSchemaTitle();

  String getJsonSchemaDescription();
}
