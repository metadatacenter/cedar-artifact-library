package org.metadatacenter.artifacts.model.renderer;

import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.core.*;
import java.net.URI;
import static org.junit.jupiter.api.Assertions.*;

class RdfArtifactRendererTest {
  private final RdfArtifactRenderer rdf = new RdfArtifactRenderer();
  private final URI templateId = URI.create("https://example.org/template");
  private final TextField field = TextField.builder().withName("Date/year")
      .withPropertyUri(URI.create("https://example.org/date")).build();

  @Test void completesSparseTemplateInstanceWithoutMutatingIt() {
    var template = TemplateSchemaArtifact.builder().withName("Test").withJsonLdId(templateId)
        .withFieldSchema(field).build();
    var instance = TemplateInstanceArtifact.builder().withName("Instance").withIsBasedOn(templateId)
        .withJsonLdId(URI.create("https://example.org/instance"))
        .withSingleInstanceFieldInstance("Date/year", TextFieldInstance.builder().withValue("2026").build()).build();
    var json = new JsonArtifactRenderer();
    var before = json.renderTemplateInstanceArtifact(instance);
    String nquads = rdf.renderNQuads(template, instance);
    assertTrue(nquads.contains("<https://example.org/instance> <https://example.org/date> \"2026\""), nquads);
    assertEquals(nquads, rdf.renderTurtle(template, instance));
    assertEquals(before, json.renderTemplateInstanceArtifact(instance));
  }

  @Test void completesStandaloneElementWithoutMintingAnIdentifier() {
    var element = ElementSchemaArtifact.builder().withName("Part").withFieldSchema(field).build();
    var instance = ElementInstanceArtifact.builder()
        .withSingleInstanceFieldInstance("Date/year", TextFieldInstance.builder().withValue("2026").build()).build();
    String nquads = rdf.renderNQuads(element, instance);
    assertTrue(nquads.contains("<https://example.org/date> \"2026\""), nquads);
    assertTrue(nquads.startsWith("_:"), nquads);
    assertEquals(nquads, rdf.renderTurtle(element, instance));
    assertTrue(instance.jsonLdId().isEmpty());
  }

  @Test void standaloneElementResolvesBuiltInDatatypeAndProvenancePrefixes() {
    var number = NumericField.builder().withName("Amount")
        .withNumericType(org.metadatacenter.artifacts.model.core.fields.XsdNumericDatatype.DOUBLE)
        .withPropertyUri(URI.create("https://example.org/amount")).build();
    var element = ElementSchemaArtifact.builder().withName("Part").withFieldSchema(number).build();
    var instance = ElementInstanceArtifact.builder()
        .withCreatedBy(URI.create("https://example.org/user"))
        .withSingleInstanceFieldInstance("Amount", NumericFieldInstance.builder().withValue(120)
            .withType(org.metadatacenter.artifacts.model.core.fields.XsdNumericDatatype.DOUBLE).build()).build();
    String nquads = rdf.renderNQuads(element, instance);
    assertTrue(nquads.contains("\"120\"^^<http://www.w3.org/2001/XMLSchema#double>"), nquads);
    assertTrue(nquads.contains("<http://purl.org/pav/createdBy> <https://example.org/user>"), nquads);
  }

}
