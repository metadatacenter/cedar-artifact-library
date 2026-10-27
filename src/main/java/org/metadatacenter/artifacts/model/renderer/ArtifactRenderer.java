package org.metadatacenter.artifacts.model.renderer;

import org.metadatacenter.artifacts.model.core.ElementSchemaArtifact;
import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifacts.model.core.ElementInstanceArtifact;
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
    // A field written on its own. A renderer whose form states a parent's settings on the field,
    // as JSON states a requirement, writes none of them here.
    return renderFieldSchemaArtifact(fieldSchemaArtifact.name(), fieldSchemaArtifact);
  }

  T renderTemplateInstanceArtifact(TemplateInstanceArtifact templateInstanceArtifact);

  T renderElementInstanceArtifact(ElementInstanceArtifact elementInstanceArtifact);
}
