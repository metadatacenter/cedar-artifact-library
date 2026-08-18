package org.metadatacenter.artifacts.model.tools;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.metadatacenter.artifacts.model.core.Version;
import org.metadatacenter.artifacts.model.core.fields.InputTimeFormat;
import org.metadatacenter.artifacts.model.core.fields.TemporalGranularity;
import org.metadatacenter.artifacts.model.core.fields.XsdDatatype;
import org.metadatacenter.artifacts.model.core.fields.constraints.ValueConstraintsActionType;
import org.metadatacenter.artifacts.model.core.fields.constraints.ValueType;
import org.metadatacenter.artifacts.model.yaml.YamlConstants;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.*;

public class YamlPlainScalarPolicyTest
{
  private static final ObjectMapper YAML_READER = new ObjectMapper(new YAMLFactory());

  @Test public void onlyTheNineCedarOwnedFieldsAreExempt()
  {
    assertEquals(Set.of(TYPE, MODEL_VERSION, STATUS, VERSION, DATATYPE, ACTION, GRANULARITY, TERM_TYPE,
      INPUT_TIME_FORMAT), YamlPlainScalarPolicy.exemptFields());
  }

  @ParameterizedTest(name = "{0}: {1}") @MethodSource("closedVocabularyMembers")
  public void everyClosedVocabularyMemberIsPlainAndReturnsAsAString(String field, String value) throws Exception
  {
    String input = field + ": \"" + value + "\"\n";
    String output = YamlPlainScalarPolicy.apply(input);

    assertEquals(field + ": " + value + "\n", output);
    JsonNode read = YAML_READER.readTree(output).get(field);
    assertTrue(read.isTextual(), output);
    assertEquals(value, read.asText());
  }

  @ParameterizedTest(name = "{0}: {1}") @MethodSource("nonMembers")
  public void aValueOutsideTheFieldsVocabularyStaysQuoted(String field, String value)
  {
    String input = field + ": \"" + value + "\"\n";
    assertEquals(input, YamlPlainScalarPolicy.apply(input));
    assertFalse(YamlPlainScalarPolicy.mayWritePlain(field, value));
  }

  @Test public void nestedAndSequenceMappingValuesAreHandledWithoutChangingOpenStrings()
  {
    String input = "type: \"template\"\n"
      + "name: \"published\"\n"
      + "children:\n"
      + "- type: \"temporal-field\"\n"
      + "  name: \"12h\"\n"
      + "  configuration:\n"
      + "    granularity: \"second\"\n"
      + "    inputTimeFormat: \"12h\"\n";

    assertEquals("type: template\n"
      + "name: \"published\"\n"
      + "children:\n"
      + "- type: temporal-field\n"
      + "  name: \"12h\"\n"
      + "  configuration:\n"
      + "    granularity: second\n"
      + "    inputTimeFormat: 12h\n", YamlPlainScalarPolicy.apply(input));
  }

  private static Stream<Arguments> closedVocabularyMembers()
  {
    Stream.Builder<Arguments> members = Stream.builder();
    typeValues().forEach(value -> members.add(Arguments.of(TYPE, value)));
    Stream.of("0.0.0", "1.6.0", "2147483647.2147483647.2147483647").forEach(value -> {
      assertTrue(Version.isValidVersion(value));
      members.add(Arguments.of(VERSION, value));
      members.add(Arguments.of(MODEL_VERSION, value));
    });
    Stream.of(DRAFT_STATUS, PUBLISHED_STATUS).forEach(value -> members.add(Arguments.of(STATUS, value)));
    enumText(XsdDatatype.values()).forEach(value -> members.add(Arguments.of(DATATYPE, value)));
    members.add(Arguments.of(DATATYPE, IRI));
    enumText(ValueConstraintsActionType.values()).forEach(value -> members.add(Arguments.of(ACTION, value)));
    enumText(TemporalGranularity.values()).forEach(value -> members.add(Arguments.of(GRANULARITY, value)));
    enumText(ValueType.values()).forEach(value -> members.add(Arguments.of(TERM_TYPE, value)));
    enumText(InputTimeFormat.values()).forEach(value -> members.add(Arguments.of(INPUT_TIME_FORMAT, value)));
    return members.build();
  }

  private static Stream<Arguments> nonMembers()
  {
    return Stream.of(
      Arguments.of("sourceSystem", "bioportal"),
      Arguments.of("id", "https://repo.metadatacenter.org/templates/example"),
      Arguments.of("createdOn", "2026-08-18T08:00:00-07:00"),
      Arguments.of("language", "no"),
      Arguments.of(TYPE, "object"),
      Arguments.of(TYPE, "no"),
      Arguments.of(STATUS, "archived"),
      Arguments.of(DATATYPE, "xsd:integer"),
      Arguments.of(ACTION, "replace"),
      Arguments.of(GRANULARITY, "week"),
      Arguments.of(TERM_TYPE, "ontology"),
      Arguments.of(INPUT_TIME_FORMAT, "am/pm"),
      Arguments.of(VERSION, "1.0"),
      Arguments.of(MODEL_VERSION, "1.6.0-rc1"));
  }

  private static Set<String> typeValues()
  {
    Set<String> values = new HashSet<>(YamlConstants.FIELD_TYPES);
    values.addAll(Set.of(TEMPLATE, ELEMENT, INSTANCE, ELEMENT_INSTANCE, CLASS, VALUE, ONTOLOGY, BRANCH, VALUE_SET));
    return values;
  }

  private static Set<String> enumText(Object[] values)
  {
    Set<String> text = new HashSet<>();
    for (Object value : values)
      text.add(value.toString());
    return text;
  }
}
