package org.metadatacenter.artifacts.model.renderer;

import org.metadatacenter.artifacts.model.core.ElementSchemaArtifact;
import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifacts.model.core.TemplateInstanceArtifact;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;

public interface ArtifactRenderer<T>
{
  T renderTemplateSchemaArtifact(TemplateSchemaArtifact templateSchemaArtifact);

  T renderElementSchemaArtifact(String elementKey, ElementSchemaArtifact elementSchemaArtifact);

  default T renderElementSchemaArtifact(ElementSchemaArtifact elementSchemaArtifact)
  {
    return renderElementSchemaArtifact(elementSchemaArtifact.name(), elementSchemaArtifact);
  }

  T renderFieldSchemaArtifact(String fieldKey, FieldSchemaArtifact fieldSchemaArtifact);

  default T renderFieldSchemaArtifact(FieldSchemaArtifact fieldSchemaArtifact)
  {
    // Render the field exactly as it is. The required/recommended/hidden and related flags
    // live on the field artifact itself (value constraints and field UI), and the readers
    // accept them on a standalone field — scrubbing them here would make the standalone
    // round trip lossy.
    return renderFieldSchemaArtifact(fieldSchemaArtifact.name(), fieldSchemaArtifact);
  }

  T renderTemplateInstanceArtifact(TemplateInstanceArtifact templateInstanceArtifact);
}
