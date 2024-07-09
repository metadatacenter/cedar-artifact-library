package org.metadatacenter.artifacts.model.renderer;

import org.metadatacenter.artifacts.model.core.ElementSchemaArtifact;
import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifacts.model.core.FieldSchemaArtifactBuilder;
import org.metadatacenter.artifacts.model.core.TemplateInstanceArtifact;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.artifacts.model.core.fields.constraints.ValueConstraintsBuilder;
import org.metadatacenter.artifacts.model.core.ui.FieldUiBuilder;

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
    FieldSchemaArtifactBuilder fieldSchemaArtifactBuilder = FieldSchemaArtifactBuilder.builder(fieldSchemaArtifact);
    FieldUiBuilder fieldUiBuilder = FieldUiBuilder.builder(fieldSchemaArtifact.fieldUi());

    if (fieldSchemaArtifact.valueConstraints().isPresent()) {
      ValueConstraintsBuilder valueConstraintsBuilder = ValueConstraintsBuilder.builder(
        fieldSchemaArtifact.valueConstraints().get());
      valueConstraintsBuilder.withRequiredValue(false);
      valueConstraintsBuilder.withRecommendedValue(false);

      fieldSchemaArtifactBuilder.withValueConstraints(valueConstraintsBuilder.build());
    }

    fieldUiBuilder.withContinuePreviousLine(false);
    fieldUiBuilder.withHidden(false);
    fieldUiBuilder.withValueRecommendationEnabled(false);

    fieldSchemaArtifactBuilder.withFieldUi(fieldUiBuilder.build());

    return renderFieldSchemaArtifact(fieldSchemaArtifact.name(), fieldSchemaArtifactBuilder.build());
  }

  T renderTemplateInstanceArtifact(TemplateInstanceArtifact templateInstanceArtifact);
}
