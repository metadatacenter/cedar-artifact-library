package org.metadatacenter.artifacts.model.renderer;

import org.metadatacenter.artifacts.model.core.ElementSchemaArtifact;
import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifacts.model.core.FieldSchemaArtifactBuilder;
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
    FieldSchemaArtifactBuilder fieldSchemaArtifactBuilder = FieldSchemaArtifactBuilder.builder(fieldSchemaArtifact);

    fieldSchemaArtifactBuilder.withRequiredValue(false);
    fieldSchemaArtifactBuilder.withRecommendedValue(false);
    fieldSchemaArtifactBuilder.withContinuePreviousLine(false);
    fieldSchemaArtifactBuilder.withHidden(false);
    fieldSchemaArtifactBuilder.withValueRecommendationEnabled(false);

    FieldSchemaArtifact updatedFieldSchemaArtifact = fieldSchemaArtifactBuilder.build();

    return renderFieldSchemaArtifact(fieldSchemaArtifact.name(), updatedFieldSchemaArtifact);
  }

  T renderTemplateInstanceArtifact(TemplateInstanceArtifact templateInstanceArtifact);
}
