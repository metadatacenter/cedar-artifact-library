package org.metadatacenter.artifacts.model.core.fields.constraints;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.metadatacenter.artifacts.model.core.fields.BooleanDefaultValue;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateMapFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateOptionalFieldNotNull;
import static org.metadatacenter.artifacts.model.core.fields.BooleanConstants.VALUE_CONSTRAINTS_LABELS;
import static org.metadatacenter.artifacts.model.core.fields.BooleanConstants.VALUE_CONSTRAINTS_LABEL_FALSE;
import static org.metadatacenter.artifacts.model.core.fields.BooleanConstants.VALUE_CONSTRAINTS_LABEL_NULL;
import static org.metadatacenter.artifacts.model.core.fields.BooleanConstants.VALUE_CONSTRAINTS_LABEL_TRUE;
import static org.metadatacenter.artifacts.model.core.fields.BooleanConstants.VALUE_CONSTRAINTS_NULL_ENABLED;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_DEFAULT_VALUE;

/**
 * The value constraints of a boolean field.
 * <p>
 * A boolean field carries a {@code nullEnabled} flag (whether a third, null, choice is offered), a
 * three-state default (absent, an explicit {@code true}/{@code false}, or an explicit null), and a
 * {@code labels} map giving the display text for the {@code true}, {@code false}, and {@code null}
 * choices. Existing value-constraint types cannot hold these, so booleans get their own type.
 */
public non-sealed interface BooleanValueConstraints extends ValueConstraints
{
  @JsonInclude(JsonInclude.Include.NON_ABSENT)
  Optional<Boolean> nullEnabled();

  @JsonInclude(JsonInclude.Include.NON_ABSENT)
  Optional<BooleanDefaultValue> defaultValue();

  Map<String, String> labels();

  static BooleanValueConstraints create(Optional<Boolean> nullEnabled, Optional<BooleanDefaultValue> defaultValue,
    Map<String, String> labels, boolean requiredValue, boolean recommendedValue, boolean multipleChoice)
  {
    return new BooleanValueConstraintsRecord(nullEnabled, defaultValue, labels, requiredValue, recommendedValue,
      multipleChoice);
  }

  static BooleanValueConstraintsBuilder builder() {
    return new BooleanValueConstraintsBuilder();
  }

  static BooleanValueConstraintsBuilder builder(BooleanValueConstraints booleanValueConstraints) {
    return new BooleanValueConstraintsBuilder(booleanValueConstraints);
  }

  final class BooleanValueConstraintsBuilder implements ValueConstraintsBuilder
  {
    private Optional<Boolean> nullEnabled = Optional.empty();
    private Optional<BooleanDefaultValue> defaultValue = Optional.empty();
    private Optional<String> trueLabel = Optional.empty();
    private Optional<String> falseLabel = Optional.empty();
    private Optional<String> nullLabel = Optional.empty();
    private boolean requiredValue = false;
    private boolean recommendedValue = false;
    private boolean multipleChoice = false;

    private BooleanValueConstraintsBuilder() {
    }

    private BooleanValueConstraintsBuilder(BooleanValueConstraints booleanValueConstraints) {
      this.nullEnabled = booleanValueConstraints.nullEnabled();
      this.defaultValue = booleanValueConstraints.defaultValue();
      this.trueLabel = Optional.ofNullable(booleanValueConstraints.labels().get(VALUE_CONSTRAINTS_LABEL_TRUE));
      this.falseLabel = Optional.ofNullable(booleanValueConstraints.labels().get(VALUE_CONSTRAINTS_LABEL_FALSE));
      this.nullLabel = Optional.ofNullable(booleanValueConstraints.labels().get(VALUE_CONSTRAINTS_LABEL_NULL));
      this.requiredValue = booleanValueConstraints.requiredValue();
      this.recommendedValue = booleanValueConstraints.recommendedValue();
      this.multipleChoice = booleanValueConstraints.multipleChoice();
    }

    public BooleanValueConstraintsBuilder withNullEnabled(boolean nullEnabled) {
      this.nullEnabled = Optional.of(nullEnabled);
      return this;
    }

    public BooleanValueConstraintsBuilder withDefaultValue(boolean defaultValue) {
      this.defaultValue = Optional.of(new BooleanDefaultValue(defaultValue));
      return this;
    }

    /** Set an explicit null default (distinct from having no default). */
    public BooleanValueConstraintsBuilder withNullDefaultValue() {
      this.defaultValue = Optional.of(new BooleanDefaultValue(null));
      return this;
    }

    public BooleanValueConstraintsBuilder withTrueLabel(String trueLabel) {
      this.trueLabel = Optional.ofNullable(trueLabel);
      return this;
    }

    public BooleanValueConstraintsBuilder withFalseLabel(String falseLabel) {
      this.falseLabel = Optional.ofNullable(falseLabel);
      return this;
    }

    public BooleanValueConstraintsBuilder withNullLabel(String nullLabel) {
      this.nullLabel = Optional.ofNullable(nullLabel);
      return this;
    }

    public BooleanValueConstraintsBuilder withRequiredValue(boolean requiredValue) {
      this.requiredValue = requiredValue;
      return this;
    }

    public BooleanValueConstraintsBuilder withRecommendedValue(boolean recommendedValue) {
      this.recommendedValue = recommendedValue;
      return this;
    }

    public BooleanValueConstraintsBuilder withMultipleChoice(boolean multipleChoice) {
      this.multipleChoice = multipleChoice;
      return this;
    }

    public BooleanValueConstraints build()
    {
      LinkedHashMap<String, String> labels = new LinkedHashMap<>();
      trueLabel.ifPresent(label -> labels.put(VALUE_CONSTRAINTS_LABEL_TRUE, label));
      falseLabel.ifPresent(label -> labels.put(VALUE_CONSTRAINTS_LABEL_FALSE, label));
      nullLabel.ifPresent(label -> labels.put(VALUE_CONSTRAINTS_LABEL_NULL, label));
      return new BooleanValueConstraintsRecord(nullEnabled, defaultValue, labels, requiredValue, recommendedValue,
        multipleChoice);
    }
  }
}

record BooleanValueConstraintsRecord(@JsonInclude(JsonInclude.Include.NON_ABSENT) Optional<Boolean> nullEnabled,
                                     @JsonInclude(JsonInclude.Include.NON_ABSENT) Optional<BooleanDefaultValue> defaultValue,
                                     @JsonInclude(JsonInclude.Include.NON_EMPTY) Map<String, String> labels,
                                     boolean requiredValue, boolean recommendedValue, boolean multipleChoice)
  implements BooleanValueConstraints
{
  BooleanValueConstraintsRecord
  {
    validateOptionalFieldNotNull(this, nullEnabled, VALUE_CONSTRAINTS_NULL_ENABLED);
    validateOptionalFieldNotNull(this, defaultValue, VALUE_CONSTRAINTS_DEFAULT_VALUE);
    validateMapFieldNotNull(this, labels, VALUE_CONSTRAINTS_LABELS);

    // Store labels in the canonical true/false/null order so the rendering is deterministic
    // regardless of the order the entries were supplied in.
    LinkedHashMap<String, String> orderedLabels = new LinkedHashMap<>();
    if (labels.containsKey(VALUE_CONSTRAINTS_LABEL_TRUE))
      orderedLabels.put(VALUE_CONSTRAINTS_LABEL_TRUE, labels.get(VALUE_CONSTRAINTS_LABEL_TRUE));
    if (labels.containsKey(VALUE_CONSTRAINTS_LABEL_FALSE))
      orderedLabels.put(VALUE_CONSTRAINTS_LABEL_FALSE, labels.get(VALUE_CONSTRAINTS_LABEL_FALSE));
    if (labels.containsKey(VALUE_CONSTRAINTS_LABEL_NULL))
      orderedLabels.put(VALUE_CONSTRAINTS_LABEL_NULL, labels.get(VALUE_CONSTRAINTS_LABEL_NULL));
    labels = Collections.unmodifiableMap(orderedLabels);
  }
}
