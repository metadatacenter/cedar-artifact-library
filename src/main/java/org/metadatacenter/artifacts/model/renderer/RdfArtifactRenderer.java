package org.metadatacenter.artifacts.model.renderer;

import org.metadatacenter.artifacts.model.core.ElementInstanceArtifact;
import org.metadatacenter.artifacts.model.core.ElementSchemaArtifact;
import org.metadatacenter.artifacts.model.core.TemplateInstanceArtifact;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.model.rdf.RdfConverter;

/**
 * Template-aware RDF export, using the same completion as the JSON instance writer.
 * No identifiers are minted and no remote contexts are fetched. An unrepresentable populated
 * value raises IllegalArgumentException. Turtle is emitted as its N-Triples subset and refuses
 * named graphs; use N-Quads for datasets containing named graphs.
 */
public final class RdfArtifactRenderer {
  private final JsonArtifactRenderer json = new JsonArtifactRenderer();

  public String renderNQuads(TemplateSchemaArtifact template, TemplateInstanceArtifact instance) {
    return RdfConverter.toNQuads(json.renderTemplateInstanceArtifact(template, instance),
        json.renderTemplateSchemaArtifact(template));
  }

  public String renderTurtle(TemplateSchemaArtifact template, TemplateInstanceArtifact instance) {
    return RdfConverter.toTurtle(json.renderTemplateInstanceArtifact(template, instance),
        json.renderTemplateSchemaArtifact(template));
  }

  public String renderNQuads(ElementSchemaArtifact element, ElementInstanceArtifact instance) {
    return RdfConverter.toNQuads(standaloneElement(element, instance),
        json.renderElementSchemaArtifact(element));
  }

  public String renderTurtle(ElementSchemaArtifact element, ElementInstanceArtifact instance) {
    return RdfConverter.toTurtle(standaloneElement(element, instance),
        json.renderElementSchemaArtifact(element));
  }

  private com.fasterxml.jackson.databind.node.ObjectNode standaloneElement(ElementSchemaArtifact element,
      ElementInstanceArtifact instance) {
    var rendered = json.renderElementInstanceArtifact(element, instance);
    // An embedded element inherits these from its template instance. A standalone export has no parent.
    var context = JsonLdContextRenderers.renderTemplateInstanceArtifactContextJsonLdSpecification();
    context.setAll((com.fasterxml.jackson.databind.node.ObjectNode) rendered.get("@context"));
    rendered.set("@context", context);
    return rendered;
  }

}
