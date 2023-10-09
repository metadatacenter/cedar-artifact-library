package org.metadatacenter.artifacts.model.renderer;

import org.metadatacenter.artifacts.model.core.ElementSchemaArtifact;
import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifacts.model.core.TemplateInstanceArtifact;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;

public interface ArtifactRenderer<T>
{
  T renderTemplateSchemaArtifact(TemplateSchemaArtifact templateSchemaArtifact);
  T renderElementSchemaArtifact(ElementSchemaArtifact elementSchemaArtifact);
  T renderFieldSchemaArtifact(FieldSchemaArtifact fieldSchemaArtifact);

  T renderTemplateInstanceArtifact(TemplateInstanceArtifact templateInstanceArtifact);
}
