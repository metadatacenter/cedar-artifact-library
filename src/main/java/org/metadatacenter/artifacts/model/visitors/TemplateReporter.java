package org.metadatacenter.artifacts.model.visitors;

import org.metadatacenter.artifacts.model.core.ElementSchemaArtifact;
import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifacts.model.core.SchemaArtifactVisitor;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.artifacts.model.core.fields.constraints.ValueConstraints;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TemplateReporter
{
  private final TemplateSchemaArtifact templateSchema;
  private final Map<String, FieldSchemaArtifact> fieldSchemas;
  private final Map<String, ElementSchemaArtifact> elementSchemas;
  private final Map<String, ValueConstraints> valueConstraints;

  public TemplateReporter(TemplateSchemaArtifact templateSchema)
  {
    ReporterVisitor reporterVisitor = new ReporterVisitor(templateSchema);

    templateSchema.accept(reporterVisitor);

    this.templateSchema = templateSchema;
    fieldSchemas = Map.copyOf(reporterVisitor.getFieldSchemas());
    elementSchemas = Map.copyOf(reporterVisitor.getElementSchemas());
    valueConstraints = Map.copyOf(reporterVisitor.getValueConstraints());
  }

  public TemplateSchemaArtifact getTemplateSchema()
  {
    return templateSchema;
  }

  public Optional<FieldSchemaArtifact> getFieldSchema(String path)
  {
    String normalizedPath = path.replaceAll("\\[\\d+\\]", "");

    return Optional.ofNullable(this.fieldSchemas.get(normalizedPath));
  }

  public Optional<ElementSchemaArtifact> getElementSchema(String path)
  {
    String normalizedPath = path.replaceAll("\\[\\d+\\]", "");

    return Optional.ofNullable(this.elementSchemas.get(normalizedPath));
  }

  public Optional<ValueConstraints> getValueConstraints(String path)
  {
    String normalizedPath = path.replaceAll("\\[\\d+\\]", "");

    return Optional.ofNullable(this.valueConstraints.get(normalizedPath));
  }

  private class ReporterVisitor implements SchemaArtifactVisitor
  {
    private final TemplateSchemaArtifact templateSchemaArtifact;
    private final Map<String, FieldSchemaArtifact> fieldSchemas;
    private final Map<String, ElementSchemaArtifact> elementSchemas;
    private final Map<String, ValueConstraints> valueConstraints;

    public ReporterVisitor(TemplateSchemaArtifact templateSchemaArtifact)
    {
      this.templateSchemaArtifact = templateSchemaArtifact;
      this.fieldSchemas = new HashMap<>();
      this.elementSchemas = new HashMap<>();
      this.valueConstraints = new HashMap<>();
    }

    public Map<String, FieldSchemaArtifact> getFieldSchemas()
    {
      return this.fieldSchemas;
    }

    public Map<String, ElementSchemaArtifact> getElementSchemas()
    {
      return this.elementSchemas;
    }

    public Map<String, ValueConstraints> getValueConstraints()
    {
      return this.valueConstraints;
    }

    @Override public void visitTemplateSchemaArtifact(TemplateSchemaArtifact templateSchemaArtifact)
    {
    }

    @Override public void visitElementSchemaArtifact(ElementSchemaArtifact elementSchemaArtifact, String path)
    {
      if (elementSchemas.containsKey(path))
        throw new RuntimeException("duplicate element schema path " + path + " when processing template " + templateSchemaArtifact.name());

      elementSchemas.put(path, elementSchemaArtifact);
    }

    @Override public void visitFieldSchemaArtifact(FieldSchemaArtifact fieldSchemaArtifact, String path)
    {
      if (fieldSchemaArtifact.valueConstraints().isPresent()) {
        if (valueConstraints.containsKey(path))
          throw new RuntimeException("duplicate value constraints path " + path + " when processing template " + templateSchemaArtifact.name());

        valueConstraints.put(path, fieldSchemaArtifact.valueConstraints().get());
      }

      if (fieldSchemas.containsKey(path))
        throw new RuntimeException("duplicate field schema path " + path + " when processing template " + templateSchemaArtifact.name());

      fieldSchemas.put(path, fieldSchemaArtifact);
    }
  }
}
