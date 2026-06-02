package org.metadatacenter.artifacts.model.reader;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.metadatacenter.artifacts.model.core.fields.ControlledTermDefaultValue;
import org.metadatacenter.artifacts.model.core.fields.DefaultValue;
import org.metadatacenter.artifacts.model.core.fields.LinkDefaultValue;
import org.metadatacenter.artifacts.model.core.fields.NumericDefaultValue;
import org.metadatacenter.artifacts.model.core.fields.TemporalDefaultValue;
import org.metadatacenter.artifacts.model.core.fields.TextDefaultValue;
import org.metadatacenter.artifacts.model.core.fields.XsdNumericDatatype;
import org.metadatacenter.artifacts.model.core.fields.XsdTemporalDatatype;
import org.metadatacenter.artifacts.model.core.fields.constraints.BranchValueConstraint;
import org.metadatacenter.artifacts.model.core.fields.constraints.ClassValueConstraint;
import org.metadatacenter.artifacts.model.core.fields.constraints.ControlledTermValueConstraints;
import org.metadatacenter.artifacts.model.core.fields.constraints.ControlledTermValueConstraintsAction;
import org.metadatacenter.artifacts.model.core.fields.constraints.LinkValueConstraints;
import org.metadatacenter.artifacts.model.core.fields.constraints.LiteralValueConstraint;
import org.metadatacenter.artifacts.model.core.fields.constraints.NumericValueConstraints;
import org.metadatacenter.artifacts.model.core.fields.constraints.OntologyValueConstraint;
import org.metadatacenter.artifacts.model.core.fields.constraints.TemporalValueConstraints;
import org.metadatacenter.artifacts.model.core.fields.constraints.TextValueConstraints;
import org.metadatacenter.artifacts.model.core.fields.constraints.ValueConstraints;
import org.metadatacenter.artifacts.model.core.fields.constraints.ValueConstraintsActionType;
import org.metadatacenter.artifacts.model.core.fields.constraints.ValueSetValueConstraint;
import org.metadatacenter.artifacts.model.core.fields.constraints.ValueType;
import org.metadatacenter.artifacts.model.core.fields.FieldInputType;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.metadatacenter.artifacts.model.reader.JsonArtifactShapeChecks.*;
import static org.metadatacenter.artifacts.model.reader.JsonNodeReaders.*;
import static org.metadatacenter.artifacts.model.reader.JsonValueConstraintsReader.*;
import static org.metadatacenter.model.ModelNodeNames.*;

final class JsonValueConstraintsReader {
  private JsonValueConstraintsReader() {}

  public static Optional<ValueConstraints> readValueConstraints(ObjectNode sourceNode, String path, String fieldKey,
                                                          FieldInputType fieldInputType, boolean isMultiInstance,
                                                          boolean isStandalone) {
    String vcPath = path + "/" + fieldKey;
    ObjectNode vcNode = readValueConstraintsNode(sourceNode, path, fieldKey);

    if (vcNode == null) {
      // No _valueConstraints in the JSON. Static and attribute-value fields carry none, so leave
      // them empty. For value-bearing fields, synthesize the type-appropriate empty constraints —
      // an empty node makes every read below yield its default — so a JSON-read field matches a
      // builder-built and a YAML-read one (which both always carry value constraints).
      if (fieldInputType.isStatic() || fieldInputType == FieldInputType.ATTRIBUTE_VALUE)
        return Optional.empty();
      vcNode = JsonNodeFactory.instance.objectNode();
    }

    {
      boolean requiredValue = readBoolean(vcNode, vcPath, VALUE_CONSTRAINTS_REQUIRED_VALUE, false);
      boolean recommendedValue = readBoolean(vcNode, vcPath, VALUE_CONSTRAINTS_RECOMMENDED_VALUE, false);
      // multipleChoice carries semantic meaning for LIST-typed fields (where it
      // discriminates single-select vs multi-select) — read it directly from JSON for
      // those. For other field input types, multipleChoice in JSON is not authoritative
      // (the renderer strips it from non-LIST output, and historical files contain
      // residual noise values), so fall back to the isMultiInstance approximation when
      // standalone reads are not in play. Standalone reads still honor the JSON value
      // for backwards compatibility with consumers that build constraints directly.
      boolean multipleChoice;
      if (fieldInputType == FieldInputType.LIST && vcNode.has(VALUE_CONSTRAINTS_MULTIPLE_CHOICE)) {
        multipleChoice = readBoolean(vcNode, vcPath, VALUE_CONSTRAINTS_MULTIPLE_CHOICE, false);
      } else if (isStandalone) {
        multipleChoice = readBoolean(vcNode, vcPath, VALUE_CONSTRAINTS_MULTIPLE_CHOICE, false);
      } else {
        multipleChoice = isMultiInstance;
      }
      Optional<XsdNumericDatatype> numberType = readNumberType(vcNode, vcPath, VALUE_CONSTRAINTS_NUMBER_TYPE);
      Optional<XsdTemporalDatatype> temporalType = readTemporalType(vcNode, vcPath, VALUE_CONSTRAINTS_TEMPORAL_TYPE);
      Optional<String> unitOfMeasure = readString(vcNode, vcPath, VALUE_CONSTRAINTS_UNIT_OF_MEASURE);
      Optional<Number> minValue = readNumber(vcNode, vcPath, VALUE_CONSTRAINTS_MIN_NUMBER_VALUE);
      Optional<Number> maxValue = readNumber(vcNode, vcPath, VALUE_CONSTRAINTS_MAX_NUMBER_VALUE);
      Optional<Integer> decimalPlaces = readInteger(vcNode, vcPath, VALUE_CONSTRAINTS_DECIMAL_PLACE);
      Optional<Integer> minLength = readInteger(vcNode, vcPath, VALUE_CONSTRAINTS_MIN_STRING_LENGTH);
      Optional<Integer> maxLength = readInteger(vcNode, vcPath, VALUE_CONSTRAINTS_MAX_STRING_LENGTH);
      Optional<String> regex = readString(vcNode, vcPath, "regex"); // TODO Add 'regex' to ModelNodeNames
      List<OntologyValueConstraint> ontologies = readOntologyValueConstraints(vcNode, vcPath,
          VALUE_CONSTRAINTS_ONTOLOGIES);
      List<ValueSetValueConstraint> valueSets = readValueSetValueConstraints(vcNode, vcPath,
          VALUE_CONSTRAINTS_VALUE_SETS);
      List<ClassValueConstraint> classes = readClassValueConstraints(vcNode, vcPath, VALUE_CONSTRAINTS_CLASSES);
      List<BranchValueConstraint> branches = readBranchValueConstraints(vcNode, vcPath, VALUE_CONSTRAINTS_BRANCHES);
      List<LiteralValueConstraint> literals = readLiteralValueConstraints(vcNode, vcPath, VALUE_CONSTRAINTS_LITERALS);
      List<ControlledTermValueConstraintsAction> actions = readValueConstraintsActions(vcNode, vcPath,
          VALUE_CONSTRAINTS_ACTIONS);
      Optional<? extends DefaultValue> defaultValue = readDefaultValue(vcNode, vcPath, VALUE_CONSTRAINTS_DEFAULT_VALUE,
          fieldInputType);

      if (fieldInputType == FieldInputType.NUMERIC) {
        Optional<NumericDefaultValue> numericDefaultValue = defaultValue.isPresent() ?
            Optional.of(defaultValue.get().asNumericDefaultValue()) :
            Optional.empty();
        if (!numberType.isPresent()) {
          numberType = Optional.of(XsdNumericDatatype.DECIMAL); // Default to xsd:decimal if unspecifed
        }
        return Optional.of(
            NumericValueConstraints.create(numberType.get(), minValue, maxValue, decimalPlaces, unitOfMeasure,
                numericDefaultValue, requiredValue, recommendedValue, multipleChoice));
      } else if (fieldInputType == FieldInputType.TEMPORAL) {
        Optional<TemporalDefaultValue> temporalDefaultValue = defaultValue.isPresent() ?
            Optional.of(defaultValue.get().asTemporalDefaultValue()) :
            Optional.empty();
        return Optional.of(
            TemporalValueConstraints.create(temporalType.orElse(XsdTemporalDatatype.DATETIME), temporalDefaultValue,
                requiredValue, recommendedValue, multipleChoice));

      } else if (fieldInputType.isIri()) {
        Optional<LinkDefaultValue> linkDefaultValue = defaultValue.isPresent() ?
            Optional.of(defaultValue.get().asLinkDefaultValue()) :
            Optional.empty();
        return Optional.of(
            LinkValueConstraints.create(linkDefaultValue, requiredValue, recommendedValue, multipleChoice));
      } else if (fieldInputType == FieldInputType.ATTRIBUTE_VALUE) {
        return Optional.empty();
      } else if (fieldInputType == FieldInputType.TEXTFIELD && (!ontologies.isEmpty() || !valueSets.isEmpty()
          || !classes.isEmpty() || !branches.isEmpty())) {
        Optional<ControlledTermDefaultValue> controlledTermDefaultValue =
            defaultValue.isPresent() && defaultValue.get().asControlledTermDefaultValue() != null ?
                Optional.of(defaultValue.get().asControlledTermDefaultValue()) :
                Optional.empty();
        return Optional.of(
            ControlledTermValueConstraints.create(ontologies, valueSets, classes, branches, controlledTermDefaultValue,
                actions, requiredValue, recommendedValue, multipleChoice));
      } else {
        Optional<TextDefaultValue> textDefaultValue = defaultValue.isPresent() ?
            Optional.of(defaultValue.get().asTextDefaultValue()) :
            Optional.empty();
        return Optional.of(
            TextValueConstraints.create(minLength, maxLength, textDefaultValue, literals, requiredValue,
                recommendedValue,
                multipleChoice, regex));
      }
    }
  }


  public static Optional<DefaultValue> readDefaultValue(ObjectNode sourceNode, String path, String fieldKey,
                                                  FieldInputType fieldInputType) {
    JsonNode childNode = sourceNode.get(fieldKey);

    if (childNode == null || childNode.isNull()) {
      return Optional.empty();
    } else if (childNode.isObject()) {
      String nestedPath = path + "/" + fieldKey;
      ObjectNode defaultValueNode = (ObjectNode) childNode;
      URI termUri = readRequiredUri(defaultValueNode, nestedPath, VALUE_CONSTRAINTS_DEFAULT_VALUE_TERM_URI);
      Optional<String> rdfsLabel = readString(defaultValueNode, nestedPath, RDFS_LABEL);
      return Optional.of(new ControlledTermDefaultValue(termUri, rdfsLabel.orElse("")));
    } else if (childNode.isNumber()) {
      // Legacy bare-number defaults are tolerated on read for backward compatibility,
      // even though the canonical CEDAR encoding renders numeric defaults as strings.
      return Optional.of(new NumericDefaultValue(childNode.numberValue()));
    } else if (childNode.isTextual()) {
      String textValue = childNode.asText();
      if (textValue.isEmpty()) {
        return Optional.empty();
      } else {
        if (fieldInputType.isIri()) {
          return Optional.of(new LinkDefaultValue(URI.create(textValue)));
        } else if (fieldInputType.isNumeric()) {
          return Optional.of(new NumericDefaultValue(parseNumericDefault(textValue, path, fieldKey)));
        } else if (fieldInputType.isTemporal()) {
          return Optional.of(new TemporalDefaultValue(textValue));
        } else {
          return Optional.of(new TextDefaultValue(textValue));
        }
      }
    } else {
      throw new ArtifactParseException(
          "default value must be a string, a number, or an object containing URI/string pair", fieldKey, path);
    }
  }

  // Parse the string-encoded numeric default emitted by the renderer. We try
  // increasingly permissive numeric types so that integer-shaped values stay integers
  // (Long when in range) and decimals stay decimals (Double). Anything unparseable is
  // an authoring error and surfaces as a parse exception.
  private static Number parseNumericDefault(String textValue, String path, String fieldKey)
  {
    try {
      if (textValue.indexOf('.') < 0 && textValue.indexOf('e') < 0 && textValue.indexOf('E') < 0) {
        try {
          return Long.parseLong(textValue);
        } catch (NumberFormatException ignored) {
          // Fall through to double parsing for out-of-range integers.
        }
      }
      return Double.parseDouble(textValue);
    } catch (NumberFormatException e) {
      throw new ArtifactParseException(
          "numeric default value '" + textValue + "' is not a valid number", fieldKey, path);
    }
  }


  public static List<OntologyValueConstraint> readOntologyValueConstraints(ObjectNode sourceNode, String path,
                                                                     String fieldKey) {
    List<OntologyValueConstraint> ontologyValueConstraints = new ArrayList<>();

    JsonNode ontologyValueConstraintArrayNode = sourceNode.get(fieldKey);

    if (ontologyValueConstraintArrayNode != null && !ontologyValueConstraintArrayNode.isNull()
        && ontologyValueConstraintArrayNode.isArray()) {

      for (JsonNode valueConstraintNode : ontologyValueConstraintArrayNode) {
        if (valueConstraintNode != null) {
          if (!valueConstraintNode.isObject()) {
            throw new ArtifactParseException("Value in array must be an object", fieldKey, path);
          }
          OntologyValueConstraint ontologyValueConstraint =
              readOntologyValueConstraint((ObjectNode) valueConstraintNode,
                  path + "/" + fieldKey);
          ontologyValueConstraints.add(ontologyValueConstraint);
        }
      }
    }
    return ontologyValueConstraints;
  }


  public static List<ClassValueConstraint> readClassValueConstraints(ObjectNode sourceNode, String path, String fieldKey) {
    List<ClassValueConstraint> classValueConstraints = new ArrayList<>();

    JsonNode classValueConstraintArrayNode = sourceNode.get(fieldKey);

    if (classValueConstraintArrayNode != null && !classValueConstraintArrayNode.isNull()
        && classValueConstraintArrayNode.isArray()) {

      for (JsonNode valueConstraintNode : classValueConstraintArrayNode) {
        if (valueConstraintNode != null) {
          if (!valueConstraintNode.isObject()) {
            throw new ArtifactParseException("Value in array must be an object", fieldKey, path);
          }
          ClassValueConstraint classValueConstraint = readClassValueConstraint((ObjectNode) valueConstraintNode,
              path + "/" + fieldKey);
          classValueConstraints.add(classValueConstraint);
        }
      }
    }
    return classValueConstraints;
  }


  public static List<ValueSetValueConstraint> readValueSetValueConstraints(ObjectNode sourceNode, String path,
                                                                     String fieldKey) {
    List<ValueSetValueConstraint> valueSetValueConstraints = new ArrayList<>();

    JsonNode valueSetValueConstraintArrayNode = sourceNode.get(fieldKey);

    if (valueSetValueConstraintArrayNode != null && valueSetValueConstraintArrayNode.isArray()) {

      for (JsonNode valueConstraintNode : valueSetValueConstraintArrayNode) {
        if (valueConstraintNode != null) {
          if (!valueConstraintNode.isObject()) {
            throw new ArtifactParseException("Value in array must be an object", fieldKey, path);
          }
          ValueSetValueConstraint valueSetValueConstraint =
              readValueSetValueConstraint((ObjectNode) valueConstraintNode,
                  path + "/" + fieldKey);
          valueSetValueConstraints.add(valueSetValueConstraint);
        }
      }
    }
    return valueSetValueConstraints;
  }


  public static List<BranchValueConstraint> readBranchValueConstraints(ObjectNode sourceNode, String path, String fieldKey) {
    List<BranchValueConstraint> branchValueConstraints = new ArrayList<>();

    JsonNode branchValueConstraintArrayNode = sourceNode.get(fieldKey);

    if (branchValueConstraintArrayNode != null && branchValueConstraintArrayNode.isArray()) {

      for (JsonNode valueConstraintNode : branchValueConstraintArrayNode) {
        if (valueConstraintNode != null) {
          if (!valueConstraintNode.isObject()) {
            throw new ArtifactParseException("Value in array must be an object", fieldKey, path);
          }
          BranchValueConstraint branchValueConstraint = readBranchValueConstraint((ObjectNode) valueConstraintNode,
              path + "/" + fieldKey);
          branchValueConstraints.add(branchValueConstraint);
        }
      }
    }

    return branchValueConstraints;
  }


  public static List<LiteralValueConstraint> readLiteralValueConstraints(ObjectNode sourceNode, String path,
                                                                   String fieldKey) {
    List<LiteralValueConstraint> literalValueConstraints = new ArrayList<>();

    JsonNode literValueConstraintArrayNode = sourceNode.get(fieldKey);
    String literalsPath = path + "/" + fieldKey;

    if (literValueConstraintArrayNode != null && literValueConstraintArrayNode.isArray()) {

      for (JsonNode valueConstraintsNode : literValueConstraintArrayNode) {
        if (valueConstraintsNode != null) {
          if (!valueConstraintsNode.isObject()) {
            throw new ArtifactParseException("Value in array must be an object", fieldKey, literalsPath);
          }
          LiteralValueConstraint literalValueConstraint = readLiteralValueConstraint((ObjectNode) valueConstraintsNode,
              literalsPath);
          literalValueConstraints.add(literalValueConstraint);
        }
      }
    }
    return literalValueConstraints;
  }


  public static List<ControlledTermValueConstraintsAction> readValueConstraintsActions(ObjectNode sourceNode, String path,
                                                                                 String fieldKey) {
    List<ControlledTermValueConstraintsAction> actions = new ArrayList<>();

    JsonNode controlledTermValueConstraintsActionArrayNode = sourceNode.get(fieldKey);

    if (controlledTermValueConstraintsActionArrayNode != null
        && controlledTermValueConstraintsActionArrayNode.isArray()) {

      for (JsonNode actionNode : controlledTermValueConstraintsActionArrayNode) {
        if (actionNode != null) {
          if (!actionNode.isObject()) {
            throw new ArtifactParseException("Value in array must be an object", fieldKey, path);
          }
          ControlledTermValueConstraintsAction action = readValueConstraintsAction((ObjectNode) actionNode,
              path + "/" + fieldKey);
          actions.add(action);
        }
      }
    }
    return actions;
  }


  public static ControlledTermValueConstraintsAction readValueConstraintsAction(ObjectNode sourceNode, String path) {
    URI termUri = readRequiredUri(sourceNode, path, VALUE_CONSTRAINTS_TERM_URI);
    Optional<String> source = readString(sourceNode, path, VALUE_CONSTRAINTS_SOURCE);
    ValueConstraintsActionType actionType = readValueConstraintsActionType(sourceNode, path, VALUE_CONSTRAINTS_ACTION);
    ValueType valueType = readValueType(sourceNode, path, VALUE_CONSTRAINTS_TYPE);
    Optional<URI> sourceUri = readUri(sourceNode, path, VALUE_CONSTRAINTS_SOURCE_URI);
    Optional<Integer> to = readInteger(sourceNode, path, VALUE_CONSTRAINTS_ACTION_TO);

    return new ControlledTermValueConstraintsAction(termUri, source.orElse(""), valueType, actionType, sourceUri, to);
  }


  public static Optional<XsdTemporalDatatype> readTemporalType(ObjectNode sourceNode, String path, String fieldKey) {
    Optional<String> temporalTypeValue = readString(sourceNode, path, fieldKey);

    if (temporalTypeValue.isPresent()) {
      return Optional.of(XsdTemporalDatatype.fromString(temporalTypeValue.get()));
    } else {
      return Optional.empty();
    }
  }


  public static Optional<XsdNumericDatatype> readNumberType(ObjectNode sourceNode, String path, String fieldKey) {
    Optional<String> numberTypeValue = readString(sourceNode, path, fieldKey);

    if (numberTypeValue.isPresent()) {
      return Optional.of(XsdNumericDatatype.fromString(numberTypeValue.get()));
    } else {
      return Optional.empty();
    }
  }


  public static ValueConstraintsActionType readValueConstraintsActionType(ObjectNode sourceNode, String path,
                                                                    String fieldKey) {
    String actionType = readRequiredString(sourceNode, path, fieldKey);

    return ValueConstraintsActionType.fromString(actionType);
  }


  public static ValueType readValueType(ObjectNode sourceNode, String path, String fieldKey) {
    String valueType = readRequiredString(sourceNode, path, fieldKey);

    return ValueType.fromString(valueType);
  }


  public static OntologyValueConstraint readOntologyValueConstraint(ObjectNode sourceNode, String path) {
    URI uri = readRequiredUri(sourceNode, path, VALUE_CONSTRAINTS_URI);
    String acronym = readRequiredString(sourceNode, path, VALUE_CONSTRAINTS_ACRONYM);
    String name = readRequiredString(sourceNode, path, VALUE_CONSTRAINTS_NAME);
    Optional<Integer> numTerms = readInteger(sourceNode, path, VALUE_CONSTRAINTS_NUM_TERMS);

    return new OntologyValueConstraint(uri, acronym, name, numTerms);
  }


  public static ClassValueConstraint readClassValueConstraint(ObjectNode sourceNode, String path) {
    URI uri = readRequiredUri(sourceNode, path, VALUE_CONSTRAINTS_URI);
    String preferredLabel = readRequiredString(sourceNode, path, VALUE_CONSTRAINTS_PREFLABEL);
    ValueType valueType = readValueType(sourceNode, path, VALUE_CONSTRAINTS_TYPE);
    String label = readRequiredString(sourceNode, path, VALUE_CONSTRAINTS_LABEL);
    String source = readRequiredString(sourceNode, path, VALUE_CONSTRAINTS_SOURCE);

    return new ClassValueConstraint(uri, source, label, preferredLabel, valueType);
  }


  public static ValueSetValueConstraint readValueSetValueConstraint(ObjectNode sourceNode, String path) {
    URI uri = readRequiredUri(sourceNode, path, VALUE_CONSTRAINTS_URI);
    String name = readRequiredString(sourceNode, path, VALUE_CONSTRAINTS_NAME);
    String vsCollection = readRequiredString(sourceNode, path, VALUE_CONSTRAINTS_VS_COLLECTION);
    Optional<Integer> numTerms = readInteger(sourceNode, path, VALUE_CONSTRAINTS_NUM_TERMS);

    return new ValueSetValueConstraint(uri, vsCollection, name, numTerms);
  }


  public static BranchValueConstraint readBranchValueConstraint(ObjectNode sourceNode, String path) {
    URI uri = readRequiredUri(sourceNode, path, VALUE_CONSTRAINTS_URI);
    String source = readRequiredString(sourceNode, path, VALUE_CONSTRAINTS_SOURCE);
    String acronym = readRequiredString(sourceNode, path, VALUE_CONSTRAINTS_ACRONYM);
    String name = readRequiredString(sourceNode, path, VALUE_CONSTRAINTS_NAME);
    int maxDepth = readRequiredInt(sourceNode, path, VALUE_CONSTRAINTS_MAX_DEPTH);

    return new BranchValueConstraint(uri, source, acronym, name, maxDepth);
  }


  public static LiteralValueConstraint readLiteralValueConstraint(ObjectNode sourceNode, String path) {
    String label = readRequiredString(sourceNode, path, VALUE_CONSTRAINTS_LABEL);
    boolean selectedByDefault = readBoolean(sourceNode, path, VALUE_CONSTRAINTS_SELECTED_BY_DEFAULT, false);

    return new LiteralValueConstraint(label, selectedByDefault);
  }

}
