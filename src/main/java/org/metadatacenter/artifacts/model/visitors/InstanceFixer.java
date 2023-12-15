package org.metadatacenter.artifacts.model.visitors;

import org.metadatacenter.artifacts.model.core.ElementInstanceArtifact;
import org.metadatacenter.artifacts.model.core.FieldInstanceArtifact;
import org.metadatacenter.artifacts.model.core.InstanceArtifactVisitor;
import org.metadatacenter.artifacts.model.core.TemplateInstanceArtifact;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.model.ModelNodeNames;

public class InstanceFixer implements InstanceArtifactVisitor
{
  private final TemplateSchemaArtifact templateSchemaArtifact;
  private final TemplateInstanceArtifact templateInstanceArtifact;

  public InstanceFixer(TemplateSchemaArtifact templateSchemaArtifact, TemplateInstanceArtifact templateInstanceArtifact)
  {
    this.templateSchemaArtifact = templateSchemaArtifact;
    this.templateInstanceArtifact = templateInstanceArtifact;

    if (!templateSchemaArtifact.jsonLdId().isPresent())
      throw new IllegalArgumentException("Template " + templateSchemaArtifact.name() + " does not have an @id");

    if (!templateInstanceArtifact.isBasedOn().equals(templateSchemaArtifact.jsonLdId().get()))
      throw new IllegalArgumentException("Template " + templateSchemaArtifact.name() + " has @id " +
        templateSchemaArtifact.jsonLdId().get() + " which does not match the " + ModelNodeNames.SCHEMA_IS_BASED_ON +
        templateInstanceArtifact.isBasedOn() + " in the supplied instance");

  }

  @Override public void visitTemplateInstanceArtifact(TemplateInstanceArtifact templateInstanceArtifact, String path)
  {

  }

  @Override public void visitElementInstanceArtifact(ElementInstanceArtifact elementInstanceArtifact, String path)
  {

  }

  @Override public void visitFieldInstanceArtifact(FieldInstanceArtifact fieldInstanceArtifact, String path)
  {

  }
}
