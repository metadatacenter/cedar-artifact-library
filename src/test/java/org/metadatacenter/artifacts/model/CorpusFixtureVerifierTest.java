package org.metadatacenter.artifacts.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CorpusFixtureVerifierTest
{
  private static final String SOURCE = "fields/001/field-001.json";
  private static final String FIXTURE = "fields/001/field-001-generated-java-artifact-lib.json";
  @TempDir Path corpus;

  @Test void knownRejectionIsVerifiedAndHistoricalFixtureIsNeverRewritten() throws Exception
  {
    byte[] source = invalidField();
    put(SOURCE, source);
    put(FIXTURE, "historical output".getBytes(StandardCharsets.UTF_8));
    for (boolean write : new boolean[] { false, true }) {
      var result = CorpusFixtureVerifier.verify(corpus, write, Map.of(SOURCE, expected(source)));
      assertTrue(result.successful(write), result.problems().toString());
      assertEquals(1, result.expectedRejections().size());
      assertEquals(0, result.checked());
      assertEquals("historical output", Files.readString(corpus.resolve("artifacts").resolve(FIXTURE)));
    }
  }

  @Test void unlistedUnreadableArtifactFailsEvenInWriteMode() throws Exception
  {
    put(SOURCE, invalidField());
    var result = CorpusFixtureVerifier.verify(corpus, true, Map.of());
    assertFalse(result.successful(true));
    assertTrue(result.problems().get(0).contains("unlisted rejection"));
  }

  @Test void changedKnownSourceMustBeReviewedEvenIfItStillHasTheSameDefect() throws Exception
  {
    byte[] source = invalidField();
    put(SOURCE, (new String(source, StandardCharsets.UTF_8) + "\n").getBytes(StandardCharsets.UTF_8));
    var result = CorpusFixtureVerifier.verify(corpus, false, Map.of(SOURCE, expected(source)));
    assertFalse(result.successful(false));
    assertTrue(result.problems().get(0).contains("source bytes changed"));
  }

  @Test void differentValidationFindingCannotMasqueradeAsTheExpectedReaderFailure() throws Exception
  {
    byte[] source = invalidField();
    put(SOURCE, source);
    var expected = expected(source);
    for (var wrong : new CorpusFixtureVerifier.ExpectedRejection[] {
      new CorpusFixtureVerifier.ExpectedRejection(expected.sha256(), "choiceMembership", expected.path(),
        expected.exceptionClass(), expected.exceptionMessage(), expected.reason()),
      new CorpusFixtureVerifier.ExpectedRejection(expected.sha256(), expected.code(), "/wrong/path",
        expected.exceptionClass(), expected.exceptionMessage(), expected.reason()) }) {
      var result = CorpusFixtureVerifier.verify(corpus, false, Map.of(SOURCE, wrong));
      assertFalse(result.successful(false));
      assertTrue(result.problems().get(0).contains("rejection differs"));
    }
  }

  @Test void differentReaderFailureCannotBeHiddenByAnExpectedRejection() throws Exception
  {
    byte[] source = invalidField();
    put(SOURCE, source);
    var expected = expected(source);
    for (var wrong : new CorpusFixtureVerifier.ExpectedRejection[] {
      new CorpusFixtureVerifier.ExpectedRejection(expected.sha256(), expected.code(), expected.path(),
        "java.lang.IllegalArgumentException", expected.exceptionMessage(), expected.reason()),
      new CorpusFixtureVerifier.ExpectedRejection(expected.sha256(), expected.code(), expected.path(),
        expected.exceptionClass(), "A different failure", expected.reason()) }) {
      var result = CorpusFixtureVerifier.verify(corpus, false, Map.of(SOURCE, wrong));
      assertFalse(result.successful(false));
      assertTrue(result.problems().get(0).contains("rejection differs"));
    }
  }

  @Test void newlyAcceptedDefinitionRequiresRemovingItsRejectionExpectation() throws Exception
  {
    byte[] source = validField();
    put(SOURCE, source);
    var result = CorpusFixtureVerifier.verify(corpus, false, Map.of(SOURCE, expected(source)));
    assertFalse(result.successful(false));
    assertTrue(result.problems().get(0).contains("was accepted"));
  }

  @Test void missingKnownSourceDoesNotSilentlyPass() throws Exception
  {
    Files.createDirectories(corpus.resolve("artifacts"));
    var result = CorpusFixtureVerifier.verify(corpus, false, Map.of(SOURCE, expected(invalidField())));
    assertFalse(result.successful(false));
    assertTrue(result.problems().get(0).contains("is missing"));
  }

  @Test void validArtifactStillRequiresCurrentFixturesAndWriteProducesVerifiableOutput() throws Exception
  {
    put(SOURCE, validField());
    put(FIXTURE, "stale output".getBytes(StandardCharsets.UTF_8));
    var stale = CorpusFixtureVerifier.verify(corpus, false, Map.of());
    assertFalse(stale.successful(false));
    assertEquals(1, stale.stale().size());
    assertEquals("stale output", Files.readString(corpus.resolve("artifacts").resolve(FIXTURE)));
    assertTrue(CorpusFixtureVerifier.verify(corpus, true, Map.of()).successful(true));
    var current = CorpusFixtureVerifier.verify(corpus, false, Map.of());
    assertTrue(current.successful(false));
    assertEquals(1, current.checked());
  }

  @Test void committedMetadataDescribesTheThreeHistoricalDefaultsAndTheIntegerWithDecimalPlaces() throws Exception
  {
    var expectations = CorpusFixtureVerifier.expectedRejections();
    assertEquals(4, expectations.size());
    assertEquals("regex", expectations.get(SOURCE).code());
    assertEquals("regex", expectations.get("templates/008/template-008.json").code());
    assertEquals("choiceMembership", expectations.get("templates/020/template-020.json").code());
    assertEquals("templateConstraint", expectations.get("cee-suite/004/template-004.json").code());
  }

  private static final String CEE_SOURCE = "cee-suite/001/template-001.json";

  /** A template whose Weight field is an integer that states decimal places, or a decimal one when not {@code refused}. */
  private static byte[] ceeTemplate(boolean refused) throws Exception
  {
    var template = org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact.builder().withName("Measures")
      .withFieldSchema(org.metadatacenter.artifacts.model.core.NumericField.builder().withName("Weight")
        .withNumericType(org.metadatacenter.artifacts.model.core.fields.XsdNumericDatatype.DECIMAL)
        .withDecimalPlaces(2).build())
      .build();
    ObjectNode json = new org.metadatacenter.artifacts.model.renderer.JsonArtifactRenderer().renderTemplateSchemaArtifact(template);
    if (refused) json.withObject("/properties/Weight/_valueConstraints").put("numberType", "xsd:long");
    return new ObjectMapper().writeValueAsBytes(json);
  }

  private static CorpusFixtureVerifier.ExpectedRejection ceeExpected(byte[] source)
  {
    return new CorpusFixtureVerifier.ExpectedRejection(CorpusFixtureVerifier.sha256(source), "templateConstraint",
      "/properties/Weight/_valueConstraints/decimalPlace", "java.lang.IllegalStateException",
      "An integer datatype cannot have decimal places.", "An integer field states decimal places.");
  }

  @Test void ceeSuiteRejectionIsPinnedLikeANumberedOne() throws Exception
  {
    byte[] source = ceeTemplate(true);
    put(CEE_SOURCE, source);
    var result = CorpusFixtureVerifier.verify(corpus, false, Map.of(CEE_SOURCE, ceeExpected(source)));
    assertTrue(result.successful(false), result.problems().toString());
    assertEquals(1, result.expectedRejections().size());
  }

  @Test void ceeSuiteDefinitionNoLongerRefusedFailsItsPin() throws Exception
  {
    byte[] source = ceeTemplate(false);
    put(CEE_SOURCE, source);
    var result = CorpusFixtureVerifier.verify(corpus, false, Map.of(CEE_SOURCE, ceeExpected(source)));
    assertFalse(result.successful(false));
    assertTrue(result.problems().get(0).contains("was accepted"), result.problems().toString());
  }

  private void put(String relative, byte[] bytes) throws Exception
  {
    Path file = corpus.resolve("artifacts").resolve(relative);
    Files.createDirectories(file.getParent());
    Files.write(file, bytes);
  }

  private byte[] invalidField() throws Exception
  {
    try (var input = getClass().getResourceAsStream("/fields/field-001.json")) {
      return input.readAllBytes();
    }
  }

  private byte[] validField() throws Exception
  {
    var mapper = new ObjectMapper();
    var source = (ObjectNode) mapper.readTree(invalidField());
    source.withObject("/_valueConstraints").put("regex", ".*");
    return mapper.writeValueAsBytes(source);
  }

  private CorpusFixtureVerifier.ExpectedRejection expected(byte[] source)
  {
    return new CorpusFixtureVerifier.ExpectedRejection(CorpusFixtureVerifier.sha256(source), "regex",
      "/_valueConstraints/defaultValue", "java.lang.IllegalStateException", "Does not match regex.",
      "The default does not match the declared regex.");
  }
}
