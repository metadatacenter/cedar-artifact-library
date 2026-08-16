package org.metadatacenter.artifacts.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.core.TemplateInstanceArtifact;
import org.metadatacenter.artifacts.model.reader.JsonArtifactReader;
import org.metadatacenter.artifacts.model.renderer.JsonArtifactRenderer;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * An attribute's property IRI is the server's to assign, and this library does not assign it.
 *
 * <p>A user names an attribute while filling a form, so nothing could have minted an IRI for it
 * earlier: the name did not exist until then. What a draft carries is therefore the attribute's value
 * at the instance root and no {@code @context} term at all, and that is a shape the model permits — an
 * instance's {@code @context} requires the standard prefixes and the system keys, and no attribute
 * name. The server fills the term when the instance is uploaded.
 *
 * <p>So a library that invented one would be asserting an identity nothing assigned, and would do it
 * silently, in the one place a reader cannot tell an invention from a value the document carried. This
 * pins the absence: a term missing on the way in is missing on the way out, and the value it belongs to
 * survives regardless.
 */
public class AttributeValuePropertyIriNotMintedTest
{
  private static final String INSTANCE_FIXTURE = "attribute-values/two-fields-instance.json";
  private static final String CONTEXT = "@context";

  private JsonArtifactReader reader;
  private JsonArtifactRenderer renderer;
  private ObjectMapper mapper;

  @BeforeEach public void setup()
  {
    reader = new JsonArtifactReader();
    renderer = new JsonArtifactRenderer();
    mapper = new ObjectMapper();
  }

  @Test public void anAttributeWithNoTermGetsNoneInvented() throws Exception
  {
    ObjectNode source = load(INSTANCE_FIXTURE);
    List<String> attributes = attributeNames(source);
    assertFalse(attributes.isEmpty(), "the fixture has to name attributes for this to test anything");

    ObjectNode withoutTerms = source.deepCopy();
    ObjectNode context = (ObjectNode) withoutTerms.get(CONTEXT);
    attributes.forEach(context::remove);

    ObjectNode rendered = render(withoutTerms);
    ObjectNode renderedContext = (ObjectNode) rendered.get(CONTEXT);

    for (String attribute : attributes) {
      assertFalse(renderedContext.has(attribute),
        "no property IRI is minted for " + attribute + ", which only the server can assign");
      assertTrue(rendered.has(attribute), "the attribute's value survives without a term: " + attribute);
    }
  }

  @Test public void aTermTheDocumentCarriesIsWrittenBackUnchanged() throws Exception
  {
    ObjectNode source = load(INSTANCE_FIXTURE);
    List<String> attributes = attributeNames(source);

    ObjectNode withTerms = source.deepCopy();
    ObjectNode context = (ObjectNode) withTerms.get(CONTEXT);
    for (String attribute : attributes)
      context.put(attribute, "https://schema.metadatacenter.org/properties/" + attribute);

    ObjectNode renderedContext = (ObjectNode) render(withTerms).get(CONTEXT);

    for (String attribute : attributes)
      assertEquals("https://schema.metadatacenter.org/properties/" + attribute,
        renderedContext.get(attribute).asText(), "an assigned IRI is carried through: " + attribute);
  }

  /** Every attribute an attribute-value field names: the field's value is the list of them. */
  private static List<String> attributeNames(ObjectNode instance)
  {
    List<String> names = new ArrayList<>();
    instance.fields().forEachRemaining(entry -> {
      if (entry.getValue().isArray() && !entry.getValue().isEmpty()
        && entry.getValue().get(0).isTextual())
        entry.getValue().forEach(name -> names.add(name.asText()));
    });
    return names;
  }

  private ObjectNode render(ObjectNode source)
  {
    TemplateInstanceArtifact instance = reader.readTemplateInstanceArtifact(source);
    return renderer.renderTemplateInstanceArtifact(instance);
  }

  private ObjectNode load(String name) throws Exception
  {
    try (InputStream input = getClass().getClassLoader().getResourceAsStream(name)) {
      return (ObjectNode) mapper.readTree(input);
    }
  }
}
