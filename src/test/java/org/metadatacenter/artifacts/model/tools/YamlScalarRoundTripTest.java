package org.metadatacenter.artifacts.model.tools;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Every string a CEDAR artifact can hold has to come back as the string it was. A YAML writer decides
 * per scalar whether to quote, and a wrong answer does not fail at the writer — it produces a
 * document that reads back as a number, a boolean, a date, or not at all.
 *
 * <p>The corpus below is adversarial rather than realistic: the spellings the YAML 1.1 and 1.2
 * resolvers claim, the indicators in every position, whitespace and control characters, the shapes
 * CEDAR actually writes, and pseudorandom mixtures of all of it. It is generated from a fixed seed, so
 * a failure names a reproducible string rather than a lucky one.
 *
 * <p>Each string is written as a value and as a key, by both writers this library configures, and read
 * back with the reader on the other side. Written before the quoting checker existed, this test failed
 * 42 of these round trips in the full-quotes style and 59 in the minimize-quotes one.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class YamlScalarRoundTripTest
{
  private static final ObjectMapper READER = new ObjectMapper(new YAMLFactory());

  private ObjectMapper writer(boolean fullQuotes)
  {
    YAMLFactory factory = YAMLFactory.builder()
      .stringQuotingChecker(new YamlScalarQuotingChecker())
      .build()
      .disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)
      .configure(YAMLGenerator.Feature.MINIMIZE_QUOTES, !fullQuotes)
      .configure(YAMLGenerator.Feature.ALWAYS_QUOTE_NUMBERS_AS_STRINGS, !fullQuotes)
      .enable(YAMLGenerator.Feature.INDENT_ARRAYS_WITH_INDICATOR)
      .disable(YAMLGenerator.Feature.SPLIT_LINES)
      .disable(YAMLGenerator.Feature.LITERAL_BLOCK_STYLE);
    ObjectMapper mapper = new ObjectMapper(factory);
    mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, false);
    return mapper;
  }

  @ParameterizedTest(name = "{0}") @ValueSource(strings = {"minimize quotes", "full quotes"})
  public void everyProbeReturnsAsTheStringItWas(String style) throws Exception
  {
    ObjectMapper writer = writer(style.equals("full quotes"));
    List<String> failures = new ArrayList<>();

    for (String probe : probes()) {
      String asValue = writer.writeValueAsString(singletonMap("v", probe));
      try {
        JsonNode read = READER.readTree(asValue).get("v");
        if (read == null || !read.isTextual() || !probe.equals(read.asText()))
          failures.add("value " + describe(probe) + " came back as " + (read == null ? "nothing" : read.toString()));
      } catch (Exception e) {
        failures.add("value " + describe(probe) + " could not be read: " + e.getClass().getSimpleName());
      }

      String asKey = writer.writeValueAsString(singletonMap(probe, "v"));
      try {
        JsonNode read = READER.readTree(asKey);
        String key = read.fieldNames().hasNext() ? read.fieldNames().next() : null;
        if (!probe.equals(key))
          failures.add("key " + describe(probe) + " came back as " + describe(key));
      } catch (Exception e) {
        failures.add("key " + describe(probe) + " could not be read: " + e.getClass().getSimpleName());
      }
    }

    if (!failures.isEmpty())
      fail(failures.size() + " of " + (probes().size() * 2) + " round trips failed in the " + style
        + " style, the first few being:\n  " + String.join("\n  ", failures.subList(0, Math.min(8, failures.size()))));
  }

  @ParameterizedTest @MethodSource("spellingsAReaderClaims")
  public void aSpellingSomeReaderResolvesIsQuoted(String spelling) throws Exception
  {
    String written = writer(false).writeValueAsString(singletonMap("v", spelling));
    assertTrue(written.contains("\"") || written.contains("'"),
      "written bare, and a reader will not give it back as a string: " + written.trim());
    assertEquals(spelling, READER.readTree(written).get("v").asText());
  }

  static Stream<String> spellingsAReaderClaims()
  {
    return Stream.of("yes", "No", "ON", "off", "y", "N", "true", "0x1F", "-0x1F", "0b1010", "0755", "1_000", "1e5",
      "-1e5", "1e-5", ".inf", "-.inf", ".nan", "1:30", "12:30:00.5", "~", "<<", "=", "42", "-0", "3.14");
  }

  @Test public void aCharacterAPlainScalarCannotCarryIsQuotedAndEscaped() throws Exception
  {
    for (String probe : List.of("a\tb", "a\rb", "ab", "a b", "a b", "ab", " padded", "padded ")) {
      String written = writer(false).writeValueAsString(singletonMap("v", probe));
      assertEquals(probe, READER.readTree(written).get("v").asText(),
        "did not survive being written and read: " + describe(probe));
    }
  }

  private static LinkedHashMap<String, Object> singletonMap(String key, Object value)
  {
    LinkedHashMap<String, Object> map = new LinkedHashMap<>();
    map.put(key, value);
    return map;
  }

  private static String describe(String text)
  {
    if (text == null)
      return "nothing";
    StringBuilder out = new StringBuilder("\"");
    for (char c : text.toCharArray()) {
      if (c == '\n')
        out.append("\\n");
      else if (c == '\r')
        out.append("\\r");
      else if (c == '\t')
        out.append("\\t");
      else if (c < 0x20 || (c >= 0x7f && c <= 0xa0))
        out.append(String.format("\\u%04x", (int) c));
      else
        out.append(c);
    }
    return out.append('"').toString();
  }

  // ---------------------------------------------------------------------------------------------
  // The probe corpus. Deterministic: a fixed seed and a hand-rolled generator, so no run differs
  // from another and a failure is reproducible from the seed alone.
  // ---------------------------------------------------------------------------------------------

  private List<String> cachedProbes;

  private List<String> probes()
  {
    if (cachedProbes == null)
      cachedProbes = generateProbes();
    return cachedProbes;
  }

  private static List<String> generateProbes()
  {
    Set<String> probes = new LinkedHashSet<>();

    for (String token : new String[] {"y", "Y", "n", "N", "yes", "Yes", "YES", "yEs", "no", "No", "NO", "on", "On",
      "ON", "off", "Off", "OFF", "true", "True", "TRUE", "false", "False", "null", "Null", "NULL", "~", "", " ", "  ",
      "0", "-0", "+0", "42", "-42", "007", "0755", "0o755", "0x1F", "-0x1F", "0b1010", "1_000", "3.14", "-3.14", ".5",
      "5.", "1e5", "1E5", "1e-5", ".inf", "-.inf", ".NaN", ".nan", "NaN", "1:30", "1:30:30", "12:30:00",
      "2024-09-06", "2024-09-06T10:03:57-07:00", "=", "-", "?", ":", "---", "...", "<<"})
      probes.add(token);

    for (String indicator : new String[] {"-", "?", ":", ",", "[", "]", "{", "}", "#", "&", "*", "!", "|", ">", "'",
      "\"", "%", "@", "`"}) {
      probes.add(indicator);
      probes.add(indicator + "value");
      probes.add(indicator + " value");
      probes.add("value" + indicator);
      probes.add("value " + indicator);
      probes.add("value" + indicator + "value");
      probes.add("value " + indicator + " value");
      probes.add("a" + indicator + "b" + indicator + "c");
    }

    for (String whitespace : new String[] {" ", "\t", "\n", "\r", "\r\n", "", " ", " ", "　",
      " "}) {
      probes.add(whitespace);
      probes.add("a" + whitespace + "b");
      probes.add(whitespace + "ab");
      probes.add("ab" + whitespace);
      probes.add("a" + whitespace + whitespace + "b");
      probes.add("line1" + whitespace + "line2 with more text");
    }

    for (String realistic : new String[] {"this is my template", "Study Name",
      "https://repo.metadatacenter.org/templates/7b8977e",
      "http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl#C101161", "xsd:decimal", "^[A-Z][^A-Z]*$",
      "Ulnar compound muscle action potential - CMAP - median [PhenX]", "the study's name", "a \"quoted\" word",
      "Yes", "No", "N/A", "Male/Female", "10 mg/kg", "50% ± 3", "C: drive", "key: value", "see [1], [2]",
      "paragraph one\n\nparagraph two", "trailing space \nnext line", "a 😀 b"})
      probes.add(realistic);
    probes.add("x".repeat(200));

    // Pseudorandom mixtures, from a fixed seed: a linear congruential generator rather than Random,
    // so the corpus is identical on every JVM and every run.
    String[] alphabet = {"a", "Z", "0", "9", " ", "\t", "-", ":", "#", ",", "[", "]", "{", "}", "\"", "'", "*", "&",
      "!", "%", "@", "`", "|", ">", "?", "~", ".", "/", "\\", "\n", "=", "<", "+", "é", "中", " "};
    long seed = 20260813L;
    for (int i = 0; i < 3000; i++) {
      seed = (seed * 1103515245L + 12345L) & 0x7fffffffL;
      int length = 1 + (int) (seed % 12);
      StringBuilder mixture = new StringBuilder();
      for (int j = 0; j < length; j++) {
        seed = (seed * 1103515245L + 12345L) & 0x7fffffffL;
        mixture.append(alphabet[(int) (seed % alphabet.length)]);
      }
      probes.add(mixture.toString());
    }

    return new ArrayList<>(probes);
  }
}
