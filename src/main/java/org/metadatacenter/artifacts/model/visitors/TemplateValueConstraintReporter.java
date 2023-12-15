package org.metadatacenter.artifacts.model.visitors;

import org.metadatacenter.artifacts.model.core.ElementSchemaArtifact;
import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifacts.model.core.SchemaArtifactVisitor;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.artifacts.model.core.fields.constraints.ValueConstraints;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TemplateValueConstraintReporter
{
  private final Map<String, ValueConstraints> valueConstraints;

  public TemplateValueConstraintReporter(TemplateSchemaArtifact templateSchemaArtifact)
  {
    ValueConstraintsVisitor valueConstraintsVisitor = new ValueConstraintsVisitor(templateSchemaArtifact);

    templateSchemaArtifact.accept(valueConstraintsVisitor, "/");

    valueConstraints = valueConstraintsVisitor.getValueConstraints();
  }

  public Optional<ValueConstraints> getValueConstraints(String path)
  {
     return Optional.ofNullable(this.valueConstraints.get(path));
  }

  private class ValueConstraintsVisitor implements SchemaArtifactVisitor
  {
    private final TemplateSchemaArtifact templateSchemaArtifact;
    private final Map<String, ValueConstraints> valueConstraints;

    public ValueConstraintsVisitor(TemplateSchemaArtifact templateSchemaArtifact)
    {
      this.templateSchemaArtifact = templateSchemaArtifact;
      this.valueConstraints = new HashMap<>();
    }

    public Map<String, ValueConstraints> getValueConstraints()
    {
      return this.valueConstraints;
    }

    @Override public void visitTemplateSchemaArtifact(TemplateSchemaArtifact parentSchemaArtifact, String path)
    {

    }

    @Override public void visitElementSchemaArtifact(ElementSchemaArtifact elementSchemaArtifact, String path)
    {

    }

    @Override public void visitFieldSchemaArtifact(FieldSchemaArtifact fieldSchemaArtifact, String path)
    {
      if (fieldSchemaArtifact.valueConstraints().isPresent()) {
        if (valueConstraints.containsKey(path))
          throw new RuntimeException("duplicate path " + path + " when processing template " + templateSchemaArtifact.name());

        valueConstraints.put(path, fieldSchemaArtifact.valueConstraints().get());
      }
    }
  }
}
