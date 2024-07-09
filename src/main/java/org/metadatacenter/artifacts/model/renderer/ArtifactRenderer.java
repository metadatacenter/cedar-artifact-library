package org.metadatacenter.artifacts.model.renderer;

import org.metadatacenter.artifacts.model.core.ElementSchemaArtifact;
import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifacts.model.core.TemplateInstanceArtifact;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;

public interface ArtifactRenderer<T>
{
  T renderTemplateSchemaArtifact(TemplateSchemaArtifact templateSchemaArtifact);

  T renderElementSchemaArtifact(String elementName, ElementSchemaArtifact elementSchemaArtifact);

  default T renderElementSchemaArtifact(ElementSchemaArtifact elementSchemaArtifact)
  {
    return renderElementSchemaArtifact(elementSchemaArtifact.name(), elementSchemaArtifact);
  }

  T renderFieldSchemaArtifact(String fieldName, FieldSchemaArtifact fieldSchemaArtifact);

  default T renderFieldSchemaArtifact(FieldSchemaArtifact fieldSchemaArtifact)
  {
    return renderFieldSchemaArtifact(fieldSchemaArtifact.name(), fieldSchemaArtifact);
  }

  T renderTemplateInstanceArtifact(TemplateInstanceArtifact templateInstanceArtifact);
}
