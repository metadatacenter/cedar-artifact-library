package org.metadatacenter.artifacts.model.reader;

import org.metadatacenter.artifacts.model.core.ElementSchemaArtifact;
import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifacts.model.core.TemplateInstanceArtifact;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;

public interface ArtifactReader<T>
{
  TemplateSchemaArtifact readTemplateSchemaArtifact(T source);

  ElementSchemaArtifact readElementSchemaArtifact(T source);

  FieldSchemaArtifact readFieldSchemaArtifact(T source);

  TemplateInstanceArtifact readTemplateInstanceArtifact(T source);
}
