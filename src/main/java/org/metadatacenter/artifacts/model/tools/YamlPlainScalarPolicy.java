package org.metadatacenter.artifacts.model.tools;

import org.metadatacenter.artifacts.model.core.Version;
import org.metadatacenter.artifacts.model.core.fields.InputTimeFormat;
import org.metadatacenter.artifacts.model.core.fields.TemporalGranularity;
import org.metadatacenter.artifacts.model.core.fields.XsdDatatype;
import org.metadatacenter.artifacts.model.core.fields.constraints.ValueConstraintsActionType;
import org.metadatacenter.artifacts.model.core.fields.constraints.ValueType;
import org.metadatacenter.artifacts.model.yaml.YamlConstants;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.metadatacenter.artifacts.model.yaml.YamlConstants.*;

/** The CEDAR-owned string vocabularies whose YAML values are deliberately written plain. */
final class YamlPlainScalarPolicy
{
  private static final YamlScalarQuotingChecker QUOTING_CHECKER = new YamlScalarQuotingChecker();
  private static final Map<String, Predicate<String>> EXEMPTIONS = buildExemptions();
  private static final Pattern QUOTED_MAPPING_VALUE = Pattern.compile(
    "(?m)^(\\s*(?:- )?)(type|modelVersion|status|version|datatype|action|granularity|termType|inputTimeFormat): \\\"([^\\\"\\\\]*)\\\"(\\r?)$");

  private YamlPlainScalarPolicy()
  {
  }

  /**
   * Applies the field-aware policy after Jackson has safely escaped and quoted every string value.
   * Jackson's quoting hook receives only scalar content, not its mapping key, so it cannot express
   * this policy itself. A candidate must occupy the complete mapping value, contain no escape, belong
   * to the key's vocabulary, and independently pass the generic plain-scalar safety check.
   */
  static String apply(String yaml)
  {
    Matcher matcher = QUOTED_MAPPING_VALUE.matcher(yaml);
    StringBuilder result = new StringBuilder(yaml.length());
    while (matcher.find()) {
      String field = matcher.group(2);
      String value = matcher.group(3);
      if (mayWritePlain(field, value)) {
        String replacement = matcher.group(1) + field + ": " + value + matcher.group(4);
        matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
      }
    }
    matcher.appendTail(result);
    return result.toString();
  }

  static boolean mayWritePlain(String field, String value)
  {
    Predicate<String> vocabulary = EXEMPTIONS.get(field);
    return vocabulary != null && vocabulary.test(value) && !QUOTING_CHECKER.needToQuoteValue(value);
  }

  static Set<String> exemptFields()
  {
    return EXEMPTIONS.keySet();
  }

  private static Map<String, Predicate<String>> buildExemptions()
  {
    Map<String, Predicate<String>> exemptions = new HashMap<>();
    exemptions.put(TYPE, members(typeValues()));
    exemptions.put(MODEL_VERSION, Version::isValidVersion);
    exemptions.put(STATUS, members(Set.of(DRAFT_STATUS, PUBLISHED_STATUS)));
    exemptions.put(VERSION, Version::isValidVersion);
    exemptions.put(DATATYPE, members(datatypeValues()));
    exemptions.put(ACTION, members(enumText(ValueConstraintsActionType.values())));
    exemptions.put(GRANULARITY, members(enumText(TemporalGranularity.values())));
    exemptions.put(TERM_TYPE, members(enumText(ValueType.values())));
    exemptions.put(INPUT_TIME_FORMAT, members(enumText(InputTimeFormat.values())));
    return Map.copyOf(exemptions);
  }

  private static Set<String> typeValues()
  {
    Set<String> values = new HashSet<>(YamlConstants.FIELD_TYPES);
    values.addAll(Set.of(TEMPLATE, ELEMENT, INSTANCE, ELEMENT_INSTANCE, CLASS, VALUE, ONTOLOGY, BRANCH, VALUE_SET));
    return Set.copyOf(values);
  }

  private static Set<String> datatypeValues()
  {
    Set<String> values = new HashSet<>(enumText(XsdDatatype.values()));
    values.add(IRI);
    return Set.copyOf(values);
  }

  private static Predicate<String> members(Set<String> members)
  {
    return members::contains;
  }

  private static Set<String> enumText(Object[] values)
  {
    Set<String> text = new HashSet<>();
    for (Object value : values)
      text.add(value.toString());
    return Set.copyOf(text);
  }
}
