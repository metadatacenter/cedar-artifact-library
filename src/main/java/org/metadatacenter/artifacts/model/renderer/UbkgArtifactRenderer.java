package org.metadatacenter.artifacts.model.renderer;

import org.metadatacenter.artifacts.model.core.fields.constraints.BranchValueConstraint;
import org.metadatacenter.artifacts.model.core.fields.constraints.ControlledTermValueConstraints;
import org.metadatacenter.artifacts.model.core.ElementSchemaArtifact;
import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifacts.model.core.fields.constraints.NumericValueConstraints;
import org.metadatacenter.artifacts.model.core.Status;
import org.metadatacenter.artifacts.model.core.TemplateInstanceArtifact;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.artifacts.model.core.fields.constraints.TemporalValueConstraints;
import org.metadatacenter.artifacts.model.core.fields.constraints.ValueConstraints;
import org.metadatacenter.artifacts.model.core.Version;
import org.metadatacenter.artifacts.ubkg.UbkgRendering;

import java.net.URI;

import static org.metadatacenter.model.ModelNodeNames.FIELD_SCHEMA_ARTIFACT_TYPE_IRI;
import static org.metadatacenter.model.ModelNodeNames.TEMPLATE_SCHEMA_ARTIFACT_TYPE_IRI;

public class UbkgArtifactRenderer implements ArtifactRenderer<UbkgRendering.Builder>
{
  private final UbkgRendering.Builder ubkgRenderingBuilder;

  private final String RDFS_TYPE = "http://www.w3.org/2000/01/rdf-schema#type";
  private final String HAS_FIELD_UBKG_PREDICATE = "has_field";
  private final String HAS_DATATYPE_UBKG_PREDICATE = "has_datatype";
  private final String HAS_VALUESET_UBKG_PREDICATE = "has_valueset";
  private final String USED_FOR_DATASET_TYPE_UBKG_PREDICATE = "used_for_dataset_type";
  private final String DATASET_TYPE_TEMPLATE_FIELD_NAME = "dataset_type";

  private final URI TEMPLATE_SCHEMA_ARTIFACT_TYPE_URI = URI.create(TEMPLATE_SCHEMA_ARTIFACT_TYPE_IRI);
  private final URI FIELD_SCHEMA_ARTIFACT_TYPE_URI = URI.create(FIELD_SCHEMA_ARTIFACT_TYPE_IRI);
  private final URI XSD_ANY_SIMPLE_TYPE_URI = URI.create("http://www.w3.org/2001/XMLSchema#anySimpleType");
  private final URI XSD_ANY_URI = URI.create("http://www.w3.org/2001/XMLSchema#anyURI");
  private final URI XSD_STRING_URI = URI.create("http://www.w3.org/2001/XMLSchema#string");
  private final URI XSD_BOOLEAN_URI = URI.create("http://www.w3.org/2001/XMLSchema#boolean");
  private final URI XSD_DECIMAL_URI = URI.create("http://www.w3.org/2001/XMLSchema#decimal");
  private final URI XSD_INTEGER_URI = URI.create("http://www.w3.org/2001/XMLSchema#integer");
  private final URI XSD_LONG_URI = URI.create("http://www.w3.org/2001/XMLSchema#long");
  private final URI XSD_BYTE_URI = URI.create("http://www.w3.org/2001/XMLSchema#byte");
  private final URI XSD_SHORT_URI = URI.create("http://www.w3.org/2001/XMLSchema#short");
  private final URI XSD_INT_URI = URI.create("http://www.w3.org/2001/XMLSchema#int");
  private final URI XSD_FLOAT_URI = URI.create("http://www.w3.org/2001/XMLSchema#float");
  private final URI XSD_DOUBLE_URI = URI.create("http://www.w3.org/2001/XMLSchema#double");
  private final URI XSD_TIME_URI = URI.create("http://www.w3.org/2001/XMLSchema#time");
  private final URI XSD_DATE_URI = URI.create("http://www.w3.org/2001/XMLSchema#date");
  private final URI XSD_DATETIME_URI = URI.create("http://www.w3.org/2001/XMLSchema#dateTime");

  public UbkgArtifactRenderer(UbkgRendering.Builder ubkgRenderingBuilder)
  {
    this.ubkgRenderingBuilder = ubkgRenderingBuilder;

    populateCEDARNodes(this.ubkgRenderingBuilder);
    populateXSDNodesAndEdges(this.ubkgRenderingBuilder);
  }

  public UbkgRendering.Builder renderTemplateSchemaArtifact(TemplateSchemaArtifact templateSchemaArtifact)
  {
    if (templateSchemaArtifact.jsonLdId().isEmpty())
      throw new IllegalArgumentException("Template " + templateSchemaArtifact.name() + " has no JSON-LD identifier");

    if (templateSchemaArtifact.version().isEmpty())
      throw new IllegalArgumentException("Template " + templateSchemaArtifact.name() + " has no version");

    if (templateSchemaArtifact.status().isEmpty())
      throw new IllegalArgumentException("Template " + templateSchemaArtifact.name() + " has no release status");

    URI templateUri = templateSchemaArtifact.jsonLdId().get();
    String templateName = templateSchemaArtifact.name();
    Version templateVersion = templateSchemaArtifact.version().get();
    Status templateStatus = templateSchemaArtifact.status().get();
    String templateDescription = templateStatus.isPublished() ? templateVersion.toString() : templateVersion + ":draft";

    ubkgRenderingBuilder.withNode(templateUri, templateName, templateDescription);
    ubkgRenderingBuilder.withEdge(templateUri, RDFS_TYPE, TEMPLATE_SCHEMA_ARTIFACT_TYPE_URI);

    for (var entry : templateSchemaArtifact.fieldSchemas().entrySet()) {
      FieldSchemaArtifact fieldSchemaArtifact = entry.getValue();

      if (fieldSchemaArtifact.jsonLdId().isEmpty())
        throw new IllegalArgumentException("Field " + fieldSchemaArtifact.name() + " has no JSON-LD identifier");

      URI fieldUri = fieldSchemaArtifact.jsonLdId().get();
      String fieldKey = fieldSchemaArtifact.name();

      // We used the predicate 'used_for_dataset_type' to associate a template with a dataset type in the UBKG edges file.
      // We get this value from the default value for a dataset_type field present in a template.
      if (fieldKey.equals(DATASET_TYPE_TEMPLATE_FIELD_NAME)) {
        if (fieldSchemaArtifact.valueConstraints().isPresent() &&
          fieldSchemaArtifact.valueConstraints().get().defaultValue().isPresent() &&
          fieldSchemaArtifact.valueConstraints().get().defaultValue().get().isControlledTermDefaultValue()) {
          URI termURI = fieldSchemaArtifact.valueConstraints().get().defaultValue().get().asControlledTermDefaultValue().termUri();
          ubkgRenderingBuilder.withEdge(templateUri, USED_FOR_DATASET_TYPE_UBKG_PREDICATE, termURI);
        }
      }

      ubkgRenderingBuilder.withEdge(templateUri, HAS_FIELD_UBKG_PREDICATE, fieldUri);

      renderFieldSchemaArtifact(fieldSchemaArtifact);
    }

    return ubkgRenderingBuilder;
  }

  public UbkgRendering.Builder renderFieldSchemaArtifact(String fieldKey, FieldSchemaArtifact fieldSchemaArtifact)
  {
    URI fieldUri = fieldSchemaArtifact.jsonLdId().get();
    String fieldDescription = fieldSchemaArtifact.description();

    ubkgRenderingBuilder.withNode(fieldUri, fieldKey, fieldDescription);

    ubkgRenderingBuilder.withEdge(fieldUri, RDFS_TYPE, FIELD_SCHEMA_ARTIFACT_TYPE_URI);

    if (fieldSchemaArtifact.valueConstraints().isPresent()) {
      ValueConstraints valueConstraints = fieldSchemaArtifact.valueConstraints().get();

      if (valueConstraints instanceof NumericValueConstraints) { // TODO Use typesafe switch when available
        NumericValueConstraints numericValueConstraints = (NumericValueConstraints)valueConstraints;
        ubkgRenderingBuilder.withEdge(fieldUri, HAS_DATATYPE_UBKG_PREDICATE, numericValueConstraints.numberType().toUri());
      } else if (valueConstraints instanceof TemporalValueConstraints) {
        TemporalValueConstraints temporalValueConstraints = (TemporalValueConstraints)valueConstraints;
        ubkgRenderingBuilder.withEdge(fieldUri, HAS_DATATYPE_UBKG_PREDICATE, temporalValueConstraints.temporalType().toUri());
      } else if (valueConstraints instanceof ControlledTermValueConstraints) {
        ControlledTermValueConstraints controlledTermValueConstraints = (ControlledTermValueConstraints)valueConstraints;
        ubkgRenderingBuilder.withEdge(fieldUri, HAS_DATATYPE_UBKG_PREDICATE, XSD_ANY_URI);

        for (BranchValueConstraint branchValueConstraint : controlledTermValueConstraints.branches())
          ubkgRenderingBuilder.withEdge(fieldUri, HAS_VALUESET_UBKG_PREDICATE, branchValueConstraint.uri());

      } else
        ubkgRenderingBuilder.withEdge(fieldUri, HAS_DATATYPE_UBKG_PREDICATE, XSD_STRING_URI);
    }

    return ubkgRenderingBuilder;
  }

  public UbkgRendering.Builder renderElementSchemaArtifact(String elementKey, ElementSchemaArtifact elementSchemaArtifact)
  {
    return ubkgRenderingBuilder;
  }

  public UbkgRendering.Builder renderTemplateInstanceArtifact(TemplateInstanceArtifact templateInstanceArtifact)
  {
    return ubkgRenderingBuilder;
  }

  private void populateCEDARNodes(UbkgRendering.Builder ubkgRenderingBuilder)
  {
    ubkgRenderingBuilder.withNode(TEMPLATE_SCHEMA_ARTIFACT_TYPE_URI, "cedar:Template", "A CEDAR template schema artifact");
    ubkgRenderingBuilder.withNode(FIELD_SCHEMA_ARTIFACT_TYPE_URI, "cedar:Field", "A CEDAR field schema artifact");
  }

  private void populateXSDNodesAndEdges(UbkgRendering.Builder ubkgRenderingBuilder)
  {
    ubkgRenderingBuilder.withNode(XSD_ANY_SIMPLE_TYPE_URI, "xsd:anySimpleType",
      "The base type for all primitive types that are defined in the XML Schema Definition language");
    ubkgRenderingBuilder.withNode(XSD_ANY_URI, "xsd:anyURI",
      "XML Schema datatype specifying URI values as defined in RFC3987 - Internationalized Resource Identifiers (IRIs)");
    ubkgRenderingBuilder.withNode(XSD_STRING_URI, "xsd:string", "Represents character strings in XML");
    ubkgRenderingBuilder.withNode(XSD_BOOLEAN_URI, "xsd:boolean",
      "Supports the mathematical concept of binary-valued logic: true or false");
    ubkgRenderingBuilder.withNode(XSD_DECIMAL_URI, "xsd:decimal",
      "Represents a subset of the real numbers that can be represented by decimal numerals");
    ubkgRenderingBuilder.withNode(XSD_INTEGER_URI, "xsd:integer", "Represents an integer");
    ubkgRenderingBuilder.withNode(XSD_LONG_URI, "xsd:long", "Represents a long");
    ubkgRenderingBuilder.withNode(XSD_BYTE_URI, "xsd:byte", "Represents a byte");
    ubkgRenderingBuilder.withNode(XSD_SHORT_URI, "xsd:short", "Represents a short");
    ubkgRenderingBuilder.withNode(XSD_INT_URI, "xsd:int", "Represents an int");
    ubkgRenderingBuilder.withNode(XSD_FLOAT_URI, "xsd:float", "Represents a float");
    ubkgRenderingBuilder.withNode(XSD_DOUBLE_URI, "xsd:double", "Represents a double");
    ubkgRenderingBuilder.withNode(XSD_TIME_URI, "xsd:time", "Represents a time");
    ubkgRenderingBuilder.withNode(XSD_DATE_URI, "xsd:date", "Represents a date");
    ubkgRenderingBuilder.withNode(XSD_DATETIME_URI, "xsd:dateTime", "Represents a date time");

    ubkgRenderingBuilder.withEdge(XSD_ANY_URI, RDFS_TYPE, XSD_ANY_SIMPLE_TYPE_URI);
    ubkgRenderingBuilder.withEdge(XSD_STRING_URI, RDFS_TYPE, XSD_ANY_SIMPLE_TYPE_URI);
    ubkgRenderingBuilder.withEdge(XSD_BOOLEAN_URI, RDFS_TYPE, XSD_ANY_SIMPLE_TYPE_URI);
    ubkgRenderingBuilder.withEdge(XSD_DECIMAL_URI, RDFS_TYPE, XSD_ANY_SIMPLE_TYPE_URI);
    ubkgRenderingBuilder.withEdge(XSD_INTEGER_URI, RDFS_TYPE, XSD_ANY_SIMPLE_TYPE_URI);
    ubkgRenderingBuilder.withEdge(XSD_LONG_URI, RDFS_TYPE, XSD_ANY_SIMPLE_TYPE_URI);
    ubkgRenderingBuilder.withEdge(XSD_BYTE_URI, RDFS_TYPE, XSD_ANY_SIMPLE_TYPE_URI);
    ubkgRenderingBuilder.withEdge(XSD_SHORT_URI, RDFS_TYPE, XSD_ANY_SIMPLE_TYPE_URI);
    ubkgRenderingBuilder.withEdge(XSD_INT_URI, RDFS_TYPE, XSD_ANY_SIMPLE_TYPE_URI);
    ubkgRenderingBuilder.withEdge(XSD_FLOAT_URI, RDFS_TYPE, XSD_ANY_SIMPLE_TYPE_URI);
    ubkgRenderingBuilder.withEdge(XSD_DOUBLE_URI, RDFS_TYPE, XSD_ANY_SIMPLE_TYPE_URI);
    ubkgRenderingBuilder.withEdge(XSD_TIME_URI, RDFS_TYPE, XSD_ANY_SIMPLE_TYPE_URI);
    ubkgRenderingBuilder.withEdge(XSD_DATE_URI, RDFS_TYPE, XSD_ANY_SIMPLE_TYPE_URI);
    ubkgRenderingBuilder.withEdge(XSD_DATETIME_URI, RDFS_TYPE, XSD_ANY_SIMPLE_TYPE_URI);
  }

}

