package org.metadatacenter.artifacts.model.renderer;

import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.core.ControlledTermField;
import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifacts.model.core.NumericField;
import org.metadatacenter.artifacts.model.core.SectionBreakField;
import org.metadatacenter.artifacts.model.core.Status;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.artifacts.model.core.TemporalField;
import org.metadatacenter.artifacts.model.core.TextField;
import org.metadatacenter.artifacts.model.core.Version;
import org.metadatacenter.artifacts.model.core.fields.TemporalGranularity;
import org.metadatacenter.artifacts.model.core.fields.XsdNumericDatatype;
import org.metadatacenter.artifacts.model.core.fields.XsdTemporalDatatype;
import org.metadatacenter.artifacts.ubkg.UbkgRendering;

import java.net.URI;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.metadatacenter.artifacts.ubkg.UbkgRendering.NODE_DEFINITION;
import static org.metadatacenter.artifacts.ubkg.UbkgRendering.NODE_LABEL;

public class UbkgArtifactRendererTest
{
  private static final String TYPE = "http://www.w3.org/2000/01/rdf-schema#type";
  private static final String XSD = "http://www.w3.org/2001/XMLSchema#";
  private static final URI TEMPLATE_TYPE = URI.create("https://schema.metadatacenter.org/core/Template");
  private static final URI FIELD_TYPE = URI.create("https://schema.metadatacenter.org/core/TemplateField");
  private static final URI TEMPLATE_ID = URI.create("https://repo.metadatacenter.org/templates/study");
  private static final Set<String> XSD_TYPES = Set.of("anyURI", "string", "boolean", "decimal", "integer", "long",
    "byte", "short", "int", "float", "double", "time", "date", "dateTime");

  @Test public void aNewRendererStartsWithTheCedarAndXsdTypeNodes()
  {
    UbkgRendering.Builder builder = UbkgRendering.builder();
    new UbkgArtifactRenderer(builder);
    UbkgRendering rendering = builder.build();

    assertEquals(Map.of(NODE_LABEL, "cedar:Template", NODE_DEFINITION, "A CEDAR template schema artifact"),
      rendering.getNode(TEMPLATE_TYPE));
    assertEquals(Map.of(NODE_LABEL, "cedar:Field", NODE_DEFINITION, "A CEDAR field schema artifact"),
      rendering.getNode(FIELD_TYPE));
    assertEquals("xsd:anySimpleType", rendering.getNode(URI.create(XSD + "anySimpleType")).get(NODE_LABEL));
    assertEquals(17, rendering.nodes().size());
    // Every XSD datatype is a kind of anySimpleType, and nothing else has an edge yet.
    assertEquals(XSD_TYPES.stream().map(name -> URI.create(XSD + name)).collect(Collectors.toSet()),
      rendering.edges().keySet());
    for (URI datatype : rendering.edgeUris())
      assertEquals(Map.of(TYPE, Set.of(URI.create(XSD + "anySimpleType"))), rendering.getEdge(datatype));
  }

  @Test public void aTemplateBecomesANodeLinkedToEachFieldAndItsDatatype()
  {
    URI branch1 = URI.create("http://purl.obolibrary.org/obo/DOID_4");
    URI branch2 = URI.create("http://purl.obolibrary.org/obo/DOID_7");
    TemplateSchemaArtifact template = template(Status.PUBLISHED,
      TextField.builder().withName("Title").withDescription("The study title").withJsonLdId(field(1)).build(),
      NumericField.builder().withName("Count").withNumericType(XsdNumericDatatype.INT).withJsonLdId(field(2)).build(),
      TemporalField.builder().withName("When").withTemporalType(XsdTemporalDatatype.DATE)
        .withTemporalGranularity(TemporalGranularity.DAY).withJsonLdId(field(3)).build(),
      ControlledTermField.builder().withName("Disease").withJsonLdId(field(4))
        .withBranchValueConstraint(branch1, "DOID", "DOID", "disease", 0)
        .withBranchValueConstraint(branch2, "DOID", "DOID", "disease of anatomical entity", 0)
        .withOntologyValueConstraint(URI.create("https://data.bioontology.org/ontologies/UBERON"), "UBERON", "Uberon")
        .build(),
      SectionBreakField.builder().withName("Section").withJsonLdId(field(5)).build());

    UbkgRendering rendering = render(template);

    assertEquals(Map.of(NODE_LABEL, "Study", NODE_DEFINITION, "1.2.3"), rendering.getNode(TEMPLATE_ID));
    assertEquals(Map.of(TYPE, Set.of(TEMPLATE_TYPE),
      "has_field", Set.of(field(1), field(2), field(3), field(4), field(5))), rendering.getEdge(TEMPLATE_ID));

    assertEquals(Map.of(NODE_LABEL, "Title", NODE_DEFINITION, "The study title"), rendering.getNode(field(1)));
    assertEquals(Map.of(TYPE, Set.of(FIELD_TYPE), "has_datatype", Set.of(URI.create(XSD + "string"))),
      rendering.getEdge(field(1)));
    assertEquals(Map.of(TYPE, Set.of(FIELD_TYPE), "has_datatype", Set.of(URI.create(XSD + "int"))),
      rendering.getEdge(field(2)));
    assertEquals(Map.of(TYPE, Set.of(FIELD_TYPE), "has_datatype", Set.of(URI.create(XSD + "date"))),
      rendering.getEdge(field(3)));
    // A controlled term holds an IRI, and only its branches become value sets.
    assertEquals(Map.of(TYPE, Set.of(FIELD_TYPE), "has_datatype", Set.of(URI.create(XSD + "anyURI")),
      "has_valueset", Set.of(branch1, branch2)), rendering.getEdge(field(4)));
    // A static field has no value, so it has no datatype.
    assertEquals(Map.of(TYPE, Set.of(FIELD_TYPE)), rendering.getEdge(field(5)));
  }

  @Test public void aDraftTemplatesNodeSaysItIsADraft()
  {
    UbkgRendering rendering = render(template(Status.DRAFT));

    assertEquals("1.2.3:draft", rendering.getNode(TEMPLATE_ID).get(NODE_DEFINITION));
  }

  @Test public void aDatasetTypeFieldTiesTheTemplateToTheDatasetTypeItDefaultsTo()
  {
    URI rnaSeq = URI.create("http://purl.bioontology.org/ontology/HRAVS/RNASEQ");
    UbkgRendering withDefault = render(template(Status.PUBLISHED, ControlledTermField.builder()
      .withName("dataset_type").withJsonLdId(field(1)).withDefaultValue(rnaSeq, "RNAseq").build()));
    UbkgRendering withoutDefault = render(template(Status.PUBLISHED, ControlledTermField.builder()
      .withName("dataset_type").withJsonLdId(field(1)).build()));

    assertEquals(Set.of(rnaSeq), withDefault.getEdge(TEMPLATE_ID).get("used_for_dataset_type"));
    assertFalse(withoutDefault.getEdge(TEMPLATE_ID).containsKey("used_for_dataset_type"));
  }

  @Test public void templatesRenderedIntoOneBuilderShareAFieldTheyBothUse()
  {
    URI otherTemplateId = URI.create("https://repo.metadatacenter.org/templates/sample");
    FieldSchemaArtifact title = TextField.builder().withName("Title").withJsonLdId(field(1)).build();
    UbkgRendering.Builder builder = UbkgRendering.builder();
    UbkgArtifactRenderer renderer = new UbkgArtifactRenderer(builder);

    renderer.renderTemplateSchemaArtifact(template(Status.PUBLISHED, title));
    renderer.renderTemplateSchemaArtifact(TemplateSchemaArtifact.builder(template(Status.PUBLISHED, title))
      .withJsonLdId(otherTemplateId).withName("Sample").build());
    UbkgRendering rendering = builder.build();

    assertEquals(Set.of(field(1)), rendering.getEdge(TEMPLATE_ID).get("has_field"));
    assertEquals(Set.of(field(1)), rendering.getEdge(otherTemplateId).get("has_field"));
    assertEquals(17 + 3, rendering.nodes().size());
  }

  @Test public void aTemplateOrFieldMissingWhatTheGraphNeedsIsRefused()
  {
    TemplateSchemaArtifact complete = template(Status.PUBLISHED);
    var cases = Map.of(
      "Template Study has no JSON-LD identifier", TemplateSchemaArtifact.builder(complete).withJsonLdId(null).build(),
      "Template Study has no version", TemplateSchemaArtifact.builder(complete).withVersion(null).build(),
      "Template Study has no release status", TemplateSchemaArtifact.builder(complete).withStatus(null).build(),
      "Field Title has no JSON-LD identifier",
      template(Status.PUBLISHED, TextField.builder().withName("Title").build()));
    for (var entry : cases.entrySet()) {
      IllegalArgumentException refusal = assertThrows(IllegalArgumentException.class,
        () -> new UbkgArtifactRenderer(UbkgRendering.builder()).renderTemplateSchemaArtifact(entry.getValue()));
      assertEquals(entry.getKey(), refusal.getMessage());
    }
    assertThrows(IllegalArgumentException.class, () -> UbkgRendering.builder().build().getNode(TEMPLATE_ID));
    assertThrows(IllegalArgumentException.class, () -> UbkgRendering.builder().build().getEdge(TEMPLATE_ID));
  }

  private static URI field(int number)
  {
    return URI.create("https://repo.metadatacenter.org/template-fields/f" + number);
  }

  private static TemplateSchemaArtifact template(Status status, FieldSchemaArtifact... fields)
  {
    TemplateSchemaArtifact.Builder builder = TemplateSchemaArtifact.builder().withName("Study")
      .withJsonLdId(TEMPLATE_ID).withVersion(Version.fromString("1.2.3")).withStatus(status);
    Stream.of(fields).forEach(builder::withFieldSchema);
    return builder.build();
  }

  private static UbkgRendering render(TemplateSchemaArtifact template)
  {
    return new UbkgArtifactRenderer(UbkgRendering.builder()).renderTemplateSchemaArtifact(template).build();
  }
}
