package org.metadatacenter.artifacts.model.renderer;

import org.metadatacenter.artifacts.model.core.ElementSchemaArtifact;
import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifacts.model.core.TemplateInstanceArtifact;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;

public interface ArtifactRenderer<T>
{
  T renderTemplateSchemaArtifact(TemplateSchemaArtifact templateSchemaArtifact);

  default T renderElementSchemaArtifact(ElementSchemaArtifact elementSchemaArtifact)
  {
    return renderElementSchemaArtifact(elementSchemaArtifact.name(), elementSchemaArtifact);
  }

  T renderElementSchemaArtifact(String elementName, ElementSchemaArtifact elementSchemaArtifact);

  default T renderFieldSchemaArtifact(FieldSchemaArtifact fieldSchemaArtifact)
  {
    return renderFieldSchemaArtifact(fieldSchemaArtifact.name(), fieldSchemaArtifact);
  }

  T renderFieldSchemaArtifact(String fieldName, FieldSchemaArtifact fieldSchemaArtifact);

  T renderTemplateInstanceArtifact(TemplateInstanceArtifact templateInstanceArtifact);
}
