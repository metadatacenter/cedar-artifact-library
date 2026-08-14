package org.metadatacenter.artifacts.model.core;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.net.URI;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * A child with no property IRI of its own is given one derived from its name, and it has to be the same
 * IRI the TypeScript library derives. The two disagreed over five characters — {@code !}, {@code '},
 * {@code (}, {@code )} and {@code ~}, which this library's {@code URLEncoder} escapes and JavaScript's
 * {@code encodeURIComponent} does not — so the same field was identified as {@code Dose+%28mg%29} here
 * and {@code Dose+(mg)} there.
 *
 * <p>The table below is the shared answer, pinned on both sides: {@code PropertyIri.spec.ts} carries the
 * same pairs. A change to either encoder breaks one of the two.
 *
 * <p>What the table does not settle is whether form encoding is the right choice for a path segment. A
 * {@code +} there is a literal plus rather than a space, so the IRI does not decode back to the name.
 * That is a question about the scheme; this test is about the two libraries agreeing on whatever it is.
 */
public class PropertyUriGenerationTest
{
  private static final String NAMESPACE = "https://schema.metadatacenter.org/properties/";

  static Stream<Arguments> namesAndTheirEncodings()
  {
    return Stream.of(
      Arguments.of("Study Name", "Study+Name"),
      Arguments.of("Dose (mg)", "Dose+%28mg%29"),
      Arguments.of("Patient's age", "Patient%27s+age"),
      Arguments.of("A~B", "A%7EB"),
      Arguments.of("E!F", "E%21F"),
      Arguments.of("C*D", "C*D"),
      Arguments.of("50% ± 3", "50%25+%C2%B1+3"),
      Arguments.of("a/b", "a%2Fb"),
      Arguments.of("a+b", "a%2Bb"),
      Arguments.of("x,y", "x%2Cy"),
      Arguments.of("a#b", "a%23b"),
      Arguments.of("a?b", "a%3Fb"),
      Arguments.of("a&b", "a%26b"),
      Arguments.of("a=b", "a%3Db"),
      Arguments.of("a:b", "a%3Ab"),
      Arguments.of("Ω αβγ", "%CE%A9+%CE%B1%CE%B2%CE%B3"),
      Arguments.of("中文", "%E4%B8%AD%E6%96%87"),
      Arguments.of("emoji 😀", "emoji+%F0%9F%98%80"),
      Arguments.of("a.b-c_d", "a.b-c_d"),
      Arguments.of("a\tb", "a%09b"),
      Arguments.of("a\nb", "a%0Ab"),
      Arguments.of("!'()~", "%21%27%28%29%7E"));
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
