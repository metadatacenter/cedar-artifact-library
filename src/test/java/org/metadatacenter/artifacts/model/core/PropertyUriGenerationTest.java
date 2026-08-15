package org.metadatacenter.artifacts.model.core;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.net.URI;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * A child with no property IRI of its own is given one derived from its name, and it has to be the same
 * IRI the TypeScript library derives. The name becomes a path segment, so it is percent-encoded: a
 * space is {@code %20}, and the characters a segment takes literally — {@code !}, {@code '}, {@code (},
 * {@code )}, {@code ~} and {@code *} — are left alone.
 *
 * <p>Both libraries used to reach for form encoding, which is meant for a query string and writes a
 * space as {@code +}. In a path a {@code +} is a literal plus, so the IRI did not decode back to the
 * name it came from; and the two encoders disagreed over those six characters besides, so the same
 * field was identified as {@code Dose+%28mg%29} here and {@code Dose+(mg)} there.
 *
 * <p>The table below is the shared answer, pinned on both sides: {@code PropertyIri.spec.ts} carries the
 * same pairs. A change to either encoder breaks one of the two.
 */
public class PropertyUriGenerationTest
{
  private static final String NAMESPACE = "https://schema.metadatacenter.org/properties/";

  static Stream<Arguments> namesAndTheirEncodings()
  {
    return Stream.of(
      Arguments.of("Study Name", "Study%20Name"),
      Arguments.of("Dose (mg)", "Dose%20(mg)"),
      Arguments.of("Patient's age", "Patient's%20age"),
      Arguments.of("A~B", "A~B"),
      Arguments.of("E!F", "E!F"),
      Arguments.of("C*D", "C*D"),
      Arguments.of("50% ± 3", "50%25%20%C2%B1%203"),
      Arguments.of("a/b", "a%2Fb"),
      Arguments.of("a+b", "a%2Bb"),
      Arguments.of("x,y", "x%2Cy"),
      Arguments.of("a#b", "a%23b"),
      Arguments.of("a?b", "a%3Fb"),
      Arguments.of("a&b", "a%26b"),
      Arguments.of("a=b", "a%3Db"),
      Arguments.of("a:b", "a%3Ab"),
      Arguments.of("Ω αβγ", "%CE%A9%20%CE%B1%CE%B2%CE%B3"),
      Arguments.of("中文", "%E4%B8%AD%E6%96%87"),
      Arguments.of("emoji 😀", "emoji%20%F0%9F%98%80"),
      Arguments.of("a.b-c_d", "a.b-c_d"),
      Arguments.of("a\tb", "a%09b"),
      Arguments.of("a\nb", "a%0Ab"),
      Arguments.of("!'()~", "!'()~"));
  }

  @ParameterizedTest(name = "{0}") @MethodSource("namesAndTheirEncodings")
  public void aChildWithoutOneIsGivenAnIriDerivedFromItsName(String name, String encoded)
  {
    TemplateSchemaArtifact template = TemplateSchemaArtifact.builder()
      .withName("Template")
      .withFieldSchema(TextField.builder().withName(name).build())
      .build();

    URI generated = template.getChildPropertyUris().get(name);

    assertEquals(URI.create(NAMESPACE + encoded), generated,
      "the IRI derived from a child's name must be the one the TypeScript library derives");
  }
}
