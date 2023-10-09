package org.metadatacenter.artifacts.model.core;

import java.net.URI;

public interface JsonSchemaArtifact
{
  URI jsonSchemaSchemaUri();

  String jsonSchemaType();

  String jsonSchemaTitle();

  String jsonSchemaDescription();
}
