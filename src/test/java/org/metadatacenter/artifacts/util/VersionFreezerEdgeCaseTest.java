package org.metadatacenter.artifacts.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.metadatacenter.artifacts.model.core.fields.constraints.BranchValueConstraint;
import org.metadatacenter.artifacts.model.core.fields.constraints.ClassValueConstraint;
import org.metadatacenter.artifacts.model.core.fields.constraints.ControlledTermValueConstraints;
import org.metadatacenter.artifacts.model.core.fields.constraints.OntologyValueConstraint;
import org.metadatacenter.artifacts.model.core.fields.constraints.ValueSetValueConstraint;
import org.metadatacenter.artifacts.model.core.fields.constraints.ValueType;
import org.metadatacenter.artifacts.model.core.fields.constraints.VersionSpec;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Edge cases and invariants beyond the freezer happy-path tests. */
class VersionFreezerEdgeCaseTest {

  private enum Kind { ONTOLOGY, BRANCH, CLASS, VALUE_SET }

  private static final VersionSpec CURRENT =
      new VersionSpec("current-id", Optional.of("2026-07-01"), Optional.of("v2026"));
  private static final VersionSpec PINNED =
      new VersionSpec("pinned-id", Optional.empty(), Optional.empty());
  private static final URI CLASS_URI = URI.create("http://purl.obolibrary.org/obo/DOID_9351");

  private final ObjectMapper mapper = new ObjectMapper();

  // Four cases: one per entry kind.
  @ParameterizedTest
  @EnumSource(Kind.class)
  void pinnedEntriesNeverInvokeAResolver(Kind kind) {
    TrackingResolver resolver = new TrackingResolver(true);

    ControlledTermValueConstraints frozen =
        ControlledTermVersionFreezer.freeze(constraints(kind, Optional.of(PINNED)), resolver);

    assertEquals(Optional.of(PINNED), versionOf(frozen, kind));
    assertEquals(0, resolver.totalCalls());
  }

  // Four cases: one per entry kind.
  @ParameterizedTest
  @EnumSource(Kind.class)
  void unresolvedEntriesRemainUnpinned(Kind kind) {
    TrackingResolver resolver = new TrackingResolver(false);

    ControlledTermValueConstraints frozen =
        ControlledTermVersionFreezer.freeze(constraints(kind, Optional.empty()), resolver);

    assertTrue(versionOf(frozen, kind).isEmpty());
    assertEquals(1, resolver.totalCalls());
  }

  @Test
  void freezingPreservesConstraintLevelState() {
    URI defaultUri = URI.create("http://purl.obolibrary.org/obo/DOID_4");
    ControlledTermValueConstraints input = ControlledTermValueConstraints.builder()
        .withOntologyValueConstraint(ontology("DOID", Optional.empty()))
        .withDefaultValue(defaultUri, "disease")
        .withRequiredValue(true)
        .withRecommendedValue(true)
        .withMultipleChoice(true)
        .build();

    ControlledTermValueConstraints frozen =
        ControlledTermVersionFreezer.freeze(input, new TrackingResolver(true));

    assertEquals(input.defaultValue(), frozen.defaultValue());
    assertEquals(input.actions(), frozen.actions());
    assertEquals(input.requiredValue(), frozen.requiredValue());
    assertEquals(input.recommendedValue(), frozen.recommendedValue());
    assertEquals(input.multipleChoice(), frozen.multipleChoice());
  }

  @Test
  void mixedOntologyEntriesKeepTheirOrderAndFreezeOnlyResolvableOnes() {
    ControlledTermValueConstraints input = ControlledTermValueConstraints.builder()
        .withOntologyValueConstraint(ontology("A", Optional.empty()))
        .withOntologyValueConstraint(ontology("B", Optional.empty()))
        .withOntologyValueConstraint(ontology("C", Optional.empty()))
        .build();
    ControlledTermVersionFreezer.VersionResolver resolver = new EmptyResolver() {
      @Override public Optional<VersionSpec> currentVersionByAcronym(String acronym) {
        return acronym.equals("B") ? Optional.of(CURRENT) : Optional.empty();
      }
    };

    ControlledTermValueConstraints frozen = ControlledTermVersionFreezer.freeze(input, resolver);

    assertEquals(List.of("A", "B", "C"), frozen.ontologies().stream().map(OntologyValueConstraint::acronym).toList());
    assertTrue(frozen.ontologies().get(0).version().isEmpty());
    assertEquals(Optional.of(CURRENT), frozen.ontologies().get(1).version());
    assertTrue(frozen.ontologies().get(2).version().isEmpty());
  }

  @Test
  void freezingReturnsANewContainerWithoutMutatingTheInput() {
    ControlledTermValueConstraints input = constraints(Kind.ONTOLOGY, Optional.empty());

    ControlledTermValueConstraints frozen =
        ControlledTermVersionFreezer.freeze(input, new TrackingResolver(true));

    assertNotSame(input, frozen);
    assertTrue(input.ontologies().get(0).version().isEmpty());
    assertEquals(Optional.of(CURRENT), frozen.ontologies().get(0).version());
  }

  @Test
  void eachEntryKindUsesOnlyItsNaturalLookupKey() {
    ControlledTermValueConstraints input = ControlledTermValueConstraints.builder()
        .withOntologyValueConstraint(ontology("DOID", Optional.empty()))
        .withBranchValueConstraint(branch(Optional.empty()))
        .withClassValueConstraint(klass(Optional.empty()))
        .withValueSetValueConstraint(valueSet(Optional.empty()))
        .build();
    TrackingResolver resolver = new TrackingResolver(true);

    ControlledTermValueConstraints frozen = ControlledTermVersionFreezer.freeze(input, resolver);

    assertEquals(List.of("DOID", "DOID"), resolver.acronyms);
    assertEquals(List.of(CLASS_URI), resolver.classUris);
    assertEquals(List.of("HRAVS"), resolver.collections);
    assertTrue(frozen.ontologies().get(0).version().isPresent());
    assertTrue(frozen.branches().get(0).version().isPresent());
    assertTrue(frozen.classes().get(0).version().isPresent());
    assertTrue(frozen.valueSets().get(0).version().isPresent());
  }

  // Eight cases: missing and blank natural keys for all four entry collections.
  @ParameterizedTest(name = "{0}: {1}")
  @MethodSource("missingAndBlankKeys")
  void missingOrBlankLookupKeysAreIgnored(String collection, String key, String value) {
    ObjectNode document = documentWithEntry(collection);
    ObjectNode entry = entry(document, collection);
    if (value != null) entry.put(key, value);
    TrackingResolver resolver = new TrackingResolver(true);

    TemplateVersionFreezer.freeze(document, resolver);

    assertFalse(entry.has("version"));
    assertEquals(0, resolver.totalCalls());
  }

  private static Stream<Arguments> missingAndBlankKeys() {
    return Stream.of(
        Arguments.of("ontologies", "acronym", null), Arguments.of("ontologies", "acronym", "  "),
        Arguments.of("branches", "acronym", null), Arguments.of("branches", "acronym", "  "),
        Arguments.of("classes", "uri", null), Arguments.of("classes", "uri", "  "),
        Arguments.of("valueSets", "vsCollection", null), Arguments.of("valueSets", "vsCollection", "  "));
  }

  // Four cases: malformed collection shape for every entry kind.
  @ParameterizedTest
  @MethodSource("collectionNames")
  void nonArrayConstraintCollectionsAreIgnored(String collection) {
    ObjectNode document = mapper.createObjectNode();
    ObjectNode vc = document.putObject("_valueConstraints");
    vc.put(collection, "not-an-array");
    String before = document.toString();
    TrackingResolver resolver = new TrackingResolver(true);

    TemplateVersionFreezer.freeze(document, resolver);

    assertEquals(before, document.toString());
    assertEquals(0, resolver.totalCalls());
  }

  private static Stream<String> collectionNames() {
    return Stream.of("ontologies", "branches", "classes", "valueSets");
  }

  @Test
  void anIdOnlyVersionOmitsBothOptionalProperties() {
    ObjectNode entry = freezeOntologyWith(new VersionSpec("id-only", Optional.empty(), Optional.empty()));
    JsonNode version = entry.get("version");

    assertEquals("id-only", version.get("id").asText());
    assertFalse(version.has("effectiveDate"));
    assertFalse(version.has("declaredVersion"));
  }

  @Test
  void anEffectiveDateOnlyVersionOmitsDeclaredVersion() {
    ObjectNode entry = freezeOntologyWith(new VersionSpec("id", Optional.of("2026-07-01"), Optional.empty()));
    JsonNode version = entry.get("version");

    assertEquals("2026-07-01", version.get("effectiveDate").asText());
    assertFalse(version.has("declaredVersion"));
  }

  @Test
  void aDeclaredVersionOnlyVersionOmitsEffectiveDate() {
    ObjectNode entry = freezeOntologyWith(new VersionSpec("id", Optional.empty(), Optional.of("v3")));
    JsonNode version = entry.get("version");

    assertEquals("v3", version.get("declaredVersion").asText());
    assertFalse(version.has("effectiveDate"));
  }

  @Test
  void theWalkerFindsConstraintsInsideNestedArrays() {
    ObjectNode document = mapper.createObjectNode();
    ObjectNode nested = document.putArray("items").addObject().putArray("children").addObject();
    ObjectNode ontology = nested.putObject("_valueConstraints").putArray("ontologies").addObject();
    ontology.put("acronym", "DOID");

    TemplateVersionFreezer.freeze(document, new TrackingResolver(true));

    assertEquals("current-id", ontology.get("version").get("id").asText());
  }

  @Test
  void freezingTwiceIsIdempotentAndDoesNotResolveTwice() {
    ObjectNode document = documentWithEntry("ontologies");
    entry(document, "ontologies").put("acronym", "DOID");
    TrackingResolver resolver = new TrackingResolver(true);

    TemplateVersionFreezer.freeze(document, resolver);
    String once = document.toString();
    TemplateVersionFreezer.freeze(document, resolver);

    assertEquals(once, document.toString());
    assertEquals(1, resolver.totalCalls());
  }

  @Test
  void anInvalidClassUriIsLeftUnpinnedRatherThanBreakingPublication() {
    ObjectNode document = documentWithEntry("classes");
    ObjectNode classEntry = entry(document, "classes");
    classEntry.put("uri", "not a URI because it contains spaces");
    TrackingResolver resolver = new TrackingResolver(true);

    TemplateVersionFreezer.freeze(document, resolver);

    assertFalse(classEntry.has("version"));
    assertEquals(0, resolver.totalCalls());
  }

  private ObjectNode freezeOntologyWith(VersionSpec version) {
    ObjectNode document = documentWithEntry("ontologies");
    ObjectNode ontology = entry(document, "ontologies");
    ontology.put("acronym", "DOID");
    TemplateVersionFreezer.freeze(document, resolverReturning(version));
    return ontology;
  }

  private ControlledTermVersionFreezer.VersionResolver resolverReturning(VersionSpec version) {
    return new EmptyResolver() {
      @Override public Optional<VersionSpec> currentVersionByAcronym(String acronym) {
        return Optional.of(version);
      }
    };
  }

  private ObjectNode documentWithEntry(String collection) {
    ObjectNode document = mapper.createObjectNode();
    document.putObject("_valueConstraints").putArray(collection).addObject();
    return document;
  }

  private ObjectNode entry(ObjectNode document, String collection) {
    return (ObjectNode) document.get("_valueConstraints").get(collection).get(0);
  }

  private static ControlledTermValueConstraints constraints(Kind kind, Optional<VersionSpec> version) {
    ControlledTermValueConstraints.ControlledTermValueConstraintsBuilder builder =
        ControlledTermValueConstraints.builder();
    return switch (kind) {
      case ONTOLOGY -> builder.withOntologyValueConstraint(ontology("DOID", version)).build();
      case BRANCH -> builder.withBranchValueConstraint(branch(version)).build();
      case CLASS -> builder.withClassValueConstraint(klass(version)).build();
      case VALUE_SET -> builder.withValueSetValueConstraint(valueSet(version)).build();
    };
  }

  private static Optional<VersionSpec> versionOf(ControlledTermValueConstraints constraints, Kind kind) {
    return switch (kind) {
      case ONTOLOGY -> constraints.ontologies().get(0).version();
      case BRANCH -> constraints.branches().get(0).version();
      case CLASS -> constraints.classes().get(0).version();
      case VALUE_SET -> constraints.valueSets().get(0).version();
    };
  }

  private static OntologyValueConstraint ontology(String acronym, Optional<VersionSpec> version) {
    return new OntologyValueConstraint(URI.create("https://example.org/ontologies/" + acronym), acronym,
        acronym + " ontology", Optional.of(10), Optional.of(URI.create("https://example.org/" + acronym)),
        Optional.of("local"), version);
  }

  private static BranchValueConstraint branch(Optional<VersionSpec> version) {
    return new BranchValueConstraint(URI.create("http://purl.obolibrary.org/obo/DOID_4"), "DOID source",
        "DOID", "disease", 2, Optional.of(URI.create("http://purl.obolibrary.org/obo/DOID_4")),
        Optional.of("local"), version);
  }

  private static ClassValueConstraint klass(Optional<VersionSpec> version) {
    return new ClassValueConstraint(CLASS_URI, "DOID source", "diabetes", "diabetes",
        ValueType.ONTOLOGY_CLASS, Optional.of(CLASS_URI), Optional.of("local"), version);
  }

  private static ValueSetValueConstraint valueSet(Optional<VersionSpec> version) {
    URI uri = URI.create("https://example.org/value-sets/HRAVS");
    return new ValueSetValueConstraint(uri, "HRAVS", "Area unit", Optional.of(40), Optional.of(uri),
        Optional.of("local"), version);
  }

  private static class EmptyResolver implements ControlledTermVersionFreezer.VersionResolver {
    @Override public Optional<VersionSpec> currentVersionByAcronym(String acronym) { return Optional.empty(); }
    @Override public Optional<VersionSpec> currentVersionByClassUri(URI classUri) { return Optional.empty(); }
    @Override public Optional<VersionSpec> currentVersionByValueSetCollection(String collection) { return Optional.empty(); }
  }

  private static final class TrackingResolver extends EmptyResolver {
    private final boolean resolves;
    private final List<String> acronyms = new ArrayList<>();
    private final List<URI> classUris = new ArrayList<>();
    private final List<String> collections = new ArrayList<>();

    private TrackingResolver(boolean resolves) { this.resolves = resolves; }

    @Override public Optional<VersionSpec> currentVersionByAcronym(String acronym) {
      acronyms.add(acronym);
      return resolves ? Optional.of(CURRENT) : Optional.empty();
    }

    @Override public Optional<VersionSpec> currentVersionByClassUri(URI classUri) {
      classUris.add(classUri);
      return resolves ? Optional.of(CURRENT) : Optional.empty();
    }

    @Override public Optional<VersionSpec> currentVersionByValueSetCollection(String collection) {
      collections.add(collection);
      return resolves ? Optional.of(CURRENT) : Optional.empty();
    }

    private int totalCalls() { return acronyms.size() + classUris.size() + collections.size(); }
  }
}
