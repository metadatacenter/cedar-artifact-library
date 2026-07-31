package org.metadatacenter.artifacts.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.core.fields.constraints.VersionSpec;

import java.net.URI;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TemplateVersionFreezerTest {

  private final ObjectMapper mapper = new ObjectMapper();

  private static final VersionSpec DOID_V =
      new VersionSpec("63ef56dff672", Optional.of("2026-07-01"), Optional.of("2026-06-30"));

  private ControlledTermVersionFreezer.VersionResolver resolver(String acronym, URI classUri,
                                                                String vsCollection, VersionSpec v) {
    return new ControlledTermVersionFreezer.VersionResolver() {
      @Override public Optional<VersionSpec> currentVersionByAcronym(String a) {
        return a.equals(acronym) ? Optional.of(v) : Optional.empty();
      }
      @Override public Optional<VersionSpec> currentVersionByClassUri(URI u) {
        return u.equals(classUri) ? Optional.of(v) : Optional.empty();
      }
      @Override public Optional<VersionSpec> currentVersionByValueSetCollection(String c) {
        return c.equals(vsCollection) ? Optional.of(v) : Optional.empty();
      }
    };
  }

  /** A minimal template shape: one field, nested under "properties", with an ontology constraint. */
  private ObjectNode templateWithOntologyField() {
    ObjectNode template = mapper.createObjectNode();
    ObjectNode properties = template.putObject("properties");
    ObjectNode field = properties.putObject("diseaseField");
    ObjectNode vc = field.putObject("_valueConstraints");
    ObjectNode ontology = vc.putArray("ontologies").addObject();
    ontology.put("uri", "https://data.bioontology.org/ontologies/DOID");
    ontology.put("acronym", "DOID");
    ontology.put("name", "Human Disease Ontology");
    return template;
  }

  private ObjectNode firstOntology(ObjectNode template) {
    return (ObjectNode) template.get("properties").get("diseaseField").get("_valueConstraints")
        .get("ontologies").get(0);
  }

  @Test public void freezesUnpinnedOntologyEntryInPlace() {
    ObjectNode template = templateWithOntologyField();

    TemplateVersionFreezer.freeze(template, resolver("DOID", null, null, DOID_V));

    ObjectNode version = (ObjectNode) firstOntology(template).get("version");
    assertEquals("63ef56dff672", version.get("id").asText());
    assertEquals("2026-07-01", version.get("effectiveDate").asText());
    assertEquals("2026-06-30", version.get("declaredVersion").asText());
    // Surgical: the original fields are untouched.
    assertEquals("DOID", firstOntology(template).get("acronym").asText());
  }

  @Test public void leavesAnAlreadyPinnedEntryUntouched() {
    ObjectNode template = templateWithOntologyField();
    ObjectNode existing = firstOntology(template).putObject("version");
    existing.put("id", "already-pinned");

    TemplateVersionFreezer.freeze(template, resolver("DOID", null, null, DOID_V));

    assertEquals("already-pinned", firstOntology(template).get("version").get("id").asText());
  }

  @Test public void leavesAnUnresolvableEntryUnpinned() {
    ObjectNode template = templateWithOntologyField();
    firstOntology(template).put("acronym", "NOPE"); // resolver knows only DOID

    TemplateVersionFreezer.freeze(template, resolver("DOID", null, null, DOID_V));

    assertFalse(firstOntology(template).has("version"));
  }

  @Test public void freezesClassAndBranchEntriesByTheirOwnKeys() {
    // A class entry (frozen by its class IRI) and a branch entry (by acronym) in one field.
    ObjectNode template = mapper.createObjectNode();
    ObjectNode vc = template.putObject("properties").putObject("f").putObject("_valueConstraints");
    ObjectNode klass = vc.putArray("classes").addObject();
    klass.put("uri", "http://purl.obolibrary.org/obo/DOID_9351");
    klass.put("prefLabel", "diabetes mellitus");
    ObjectNode branch = vc.putArray("branches").addObject();
    branch.put("acronym", "DOID");
    branch.put("uri", "http://purl.obolibrary.org/obo/DOID_4");

    TemplateVersionFreezer.freeze(template,
        resolver("DOID", URI.create("http://purl.obolibrary.org/obo/DOID_9351"), null, DOID_V));

    assertTrue(klass.has("version"));
    assertEquals("63ef56dff672", klass.get("version").get("id").asText());
    assertTrue(branch.has("version"));
  }

  @Test public void aResolverThatServesNothingLeavesTheTemplateUnchanged() {
    // The prod-default case: the terminology local store is off, so nothing resolves and publish is
    // a no-op on the template's constraints.
    ObjectNode template = templateWithOntologyField();
    String before = template.toString();

    TemplateVersionFreezer.freeze(template, resolver("NOTHING", null, null, DOID_V));

    assertEquals(before, template.toString());
  }
}
