package org.metadatacenter.artifacts.model.visitors;

import org.metadatacenter.artifacts.model.core.ElementInstanceArtifact;
import org.metadatacenter.artifacts.model.core.FieldInstanceArtifact;
import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifacts.model.core.InstanceArtifactVisitor;
import org.metadatacenter.artifacts.model.core.TemplateInstanceArtifact;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.artifacts.model.core.fields.constraints.ValueConstraints;
import org.metadatacenter.model.ModelNodeNames;

public class InstanceFixer implements InstanceArtifactVisitor
{
  private final TemplateSchemaArtifact templateSchemaArtifact;
  private final TemplateInstanceArtifact templateInstanceArtifact;
  private final TemplateReporter templateReporter;

  public InstanceFixer(TemplateSchemaArtifact templateSchemaArtifact, TemplateInstanceArtifact templateInstanceArtifact)
  {
    this.templateSchemaArtifact = templateSchemaArtifact;
    this.templateInstanceArtifact = templateInstanceArtifact;

    if (!templateSchemaArtifact.jsonLdId().isPresent())
      throw new IllegalArgumentException("Template " + templateSchemaArtifact.name() + " does not have an @id");

    if (!templateInstanceArtifact.isBasedOn().equals(templateSchemaArtifact.jsonLdId().get()))
      throw new IllegalArgumentException(
        "Template " + templateSchemaArtifact.name() + " has @id " + templateSchemaArtifact.jsonLdId().get()
          + " which does not match the " + ModelNodeNames.SCHEMA_IS_BASED_ON + templateInstanceArtifact.isBasedOn()
          + " in the supplied instance");

    this.templateReporter = new TemplateReporter(templateSchemaArtifact);
  }

  @Override public void visitTemplateInstanceArtifact(TemplateInstanceArtifact templateInstanceArtifact)
  {

  }

  @Override public void visitElementInstanceArtifact(ElementInstanceArtifact elementInstanceArtifact, String path)
  {

  }

  @Override public void visitFieldInstanceArtifact(FieldInstanceArtifact fieldInstanceArtifact, String path)
  {
    if (!templateReporter.getFieldSchema(path).isPresent())
      throw new RuntimeException(
        "no field schema specification for at path " + path + " when processing template" + templateName());

    FieldSchemaArtifact fieldSchemaArtifact = templateReporter.getFieldSchema(path).get();
    String fieldName = fieldSchemaArtifact.name();

    if (templateReporter.getValueConstraints(path).isPresent()) {
      ValueConstraints valueConstraints = templateReporter.getValueConstraints(path).get();

      if (valueConstraints.isTextValueConstraint()) { // TODO Use typesafe switch when available

      } else if (valueConstraints.isNumericValueConstraint()) {

      } else if (valueConstraints.isTemporalValueConstraint()) {

      } else if (valueConstraints.isLinkValueConstraint()) {

      } else if (valueConstraints.isControlledTermValueConstraint()) {

      } else
        throw new RuntimeException(
          "Unknown value constraint type " + valueConstraints.getClass().getName() + " when processing field "
            + fieldName + " in template " + templateName() + " at path " + path);
    }
  }

  @Override public void visitAttributeValueFieldInstanceArtifact(FieldInstanceArtifact fieldInstanceArtifact,
    String path, String specificationPath)
  {
    // TOD
  }

  private String templateName() {return templateSchemaArtifact.name();}
}
