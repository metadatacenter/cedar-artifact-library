package org.metadatacenter.artifacts.model.core.fields.constraints;

import org.metadatacenter.artifacts.model.core.fields.XsdTemporalDatatype;
import org.metadatacenter.artifacts.model.core.fields.TemporalDefaultValue;

import java.util.Optional;
import java.util.regex.Pattern;

import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateOptionalFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateXsdTemporalDatatypeFieldNotNull;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_DEFAULT_VALUE;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_TEMPORAL_TYPE;

public non-sealed interface TemporalValueConstraints extends ValueConstraints
{
  XsdTemporalDatatype temporalType();

  Optional<TemporalDefaultValue> defaultValue();

  static TemporalValueConstraints create(XsdTemporalDatatype temporalType, Optional<TemporalDefaultValue> defaultValue,
    boolean requiredValue, boolean recommendedValue, boolean multipleChoice)
  {
    return new TemporalValueConstraintsRecord(temporalType, defaultValue, requiredValue, recommendedValue, multipleChoice);
  }

  static TemporalValueConstraintsBuilder builder() {
    return new TemporalValueConstraintsBuilder();
  }

  static TemporalValueConstraintsBuilder builder(TemporalValueConstraints temporalValueConstraints) {
    return new TemporalValueConstraintsBuilder(temporalValueConstraints);
  }

  final class TemporalValueConstraintsBuilder implements ValueConstraintsBuilder
  {
    private XsdTemporalDatatype temporalType;
    private Optional<TemporalDefaultValue> defaultValue = Optional.empty();
    private boolean requiredValue = false;
    private boolean recommendedValue = false;
    private boolean multipleChoice = false;

    private TemporalValueConstraintsBuilder() {
    }

    private TemporalValueConstraintsBuilder(TemporalValueConstraints temporalValueConstraints) {
      this.temporalType = temporalValueConstraints.temporalType();
      this.defaultValue = temporalValueConstraints.defaultValue();
      this.requiredValue = temporalValueConstraints.requiredValue();
      this.recommendedValue = temporalValueConstraints.recommendedValue();
      this.multipleChoice = temporalValueConstraints.multipleChoice();
    }

    public TemporalValueConstraintsBuilder withTemporalType(XsdTemporalDatatype temporalType) {
      this.temporalType = temporalType;
      return this;
    }

    public TemporalValueConstraintsBuilder withDefaultValue(String defaultValue) {
      this.defaultValue = Optional.of(new TemporalDefaultValue(defaultValue));
      return this;
    }

    public TemporalValueConstraintsBuilder withRequiredValue(boolean requiredValue) {
      this.requiredValue = requiredValue;
      return this;
    }

    public TemporalValueConstraintsBuilder withRecommendedValue(boolean recommendedValue) {
      this.recommendedValue = recommendedValue;
      return this;
    }

    public TemporalValueConstraintsBuilder withMultipleChoice(boolean multipleChoice) {
      this.multipleChoice = multipleChoice;
      return this;
    }

    public TemporalValueConstraints build()
    {
      return new TemporalValueConstraintsRecord(temporalType, defaultValue, requiredValue, recommendedValue, multipleChoice);
    }
  }
}

record TemporalValueConstraintsRecord(XsdTemporalDatatype temporalType, Optional<TemporalDefaultValue> defaultValue,
                                      boolean requiredValue, boolean recommendedValue, boolean multipleChoice)
  implements TemporalValueConstraints
{
  // Lenient per-datatype temporal syntax. The granularity (year / month / day / hour / minute /
  // second) lives in TemporalFieldUi, not here, so a date may legitimately be year- or month-only
  // and a time may stop at any component; these patterns accept every granularity variant for the
  // datatype plus an optional trailing zone/offset. They exist to reject a value of the wrong
  // temporal kind (e.g. a time supplied for an xsd:date field), not to pin an exact granularity.
  private static final Pattern DATE_PATTERN = Pattern.compile("\\d{4}(-\\d{2}(-\\d{2})?)?");
  private static final Pattern TIME_PATTERN =
    Pattern.compile("\\d{2}(:\\d{2}(:\\d{2}(\\.\\d+)?)?)?(Z|[+-]\\d{2}:\\d{2})?");
  private static final Pattern DATE_TIME_PATTERN =
    Pattern.compile("\\d{4}-\\d{2}-\\d{2}(T\\d{2}(:\\d{2}(:\\d{2}(\\.\\d+)?)?)?(Z|[+-]\\d{2}:\\d{2})?)?");

  public TemporalValueConstraintsRecord
  {
    validateXsdTemporalDatatypeFieldNotNull(this, temporalType, VALUE_CONSTRAINTS_TEMPORAL_TYPE);
    validateOptionalFieldNotNull(this, defaultValue, VALUE_CONSTRAINTS_DEFAULT_VALUE);

    if (defaultValue.isPresent()) {
      String value = defaultValue.get().value();
      Pattern pattern = switch (temporalType) {
        case DATE -> DATE_PATTERN;
        case TIME -> TIME_PATTERN;
        case DATETIME -> DATE_TIME_PATTERN;
      };
      if (!pattern.matcher(value).matches())
        throw new IllegalStateException(
          "default value " + value + " is not a valid " + temporalType.getText() + " value in " + this);
    }
  }
}
