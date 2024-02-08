package org.metadatacenter.artifacts.model.core.ui;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateListFieldDoesNotHaveDuplicates;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateMapFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateOptionalFieldNotNull;
import static org.metadatacenter.model.ModelNodeNames.UI_FOOTER;
import static org.metadatacenter.model.ModelNodeNames.UI_HEADER;
import static org.metadatacenter.model.ModelNodeNames.UI_ORDER;
import static org.metadatacenter.model.ModelNodeNames.UI_PROPERTY_DESCRIPTIONS;
import static org.metadatacenter.model.ModelNodeNames.UI_PROPERTY_LABELS;

public non-sealed interface ElementUi extends Ui, ParentArtifactUi
{
  static ElementUi create(List<String> order, Map<String, String> propertyLabels, Map<String, String> propertyDescriptions,
    Optional<String> header, Optional<String> footer)
  {
    return new ElementUiRecord(order, propertyLabels, propertyDescriptions, header, footer);
  }

  @JsonIgnore
  default UiType uiType() { return UiType.ELEMENT_UI; }

  static Builder builder() {
    return new Builder();
  }

  class Builder {
    private final List<String> order = new ArrayList<>();
    private final Map<String, String> propertyLabels = new HashMap<>();
    private final Map<String, String> propertyDescriptions = new HashMap<>();
    private Optional<String> header = Optional.empty();
    private Optional<String> footer = Optional.empty();

    private Builder() {
    }

    public Builder withOrder(String fieldName) {

      if (fieldName == null)
        throw new IllegalArgumentException("Null order field name passed to " + this.getClass().getName());

      if (order.contains(fieldName))
        throw new IllegalArgumentException("Duplicate order field name " + fieldName + " passed to " + this.getClass().getName());

      this.order.add(fieldName);
      return this;
    }

    public Builder withPropertyLabel(String fieldName, String propertyLabel) {
      if (fieldName == null)
        throw new IllegalArgumentException("Null property label field name passed to " + this.getClass().getName());

      if (propertyLabel == null)
        throw new IllegalArgumentException("Null property label for field " + fieldName + " passed to " + this.getClass().getName());

      if (this.propertyLabels.containsKey(fieldName))
        throw new IllegalArgumentException("Duplicate property label field name " + fieldName + " passed to " + this.getClass().getName());

      this.propertyLabels.put(fieldName, propertyLabel);
      return this;
    }

    public Builder withPropertyDescription(String fieldName, String propertyDescription) {

      if (fieldName == null)
        throw new IllegalArgumentException("Null property description field name passed to " + this.getClass().getName());

      if (propertyDescription == null)
        throw new IllegalArgumentException("Null property description for field " + fieldName + " passed to " + this.getClass().getName());

      if (this.propertyDescriptions.containsKey(fieldName))
        throw new IllegalArgumentException("Duplicate property description field name " + fieldName + " passed to " + this.getClass().getName());

      this.propertyDescriptions.put(fieldName, propertyDescription);
      return this;
    }

    public Builder withHeader(String header) {
      this.header = Optional.ofNullable(header);
      return this;
    }

    public Builder withFooter(String footer) {
      this.footer = Optional.ofNullable(footer);
      return this;
    }

    public ElementUi build()
    {
      return new ElementUiRecord(order, propertyLabels, propertyDescriptions, header, footer);
    }
  }
}

record ElementUiRecord(List<String> order, Map<String, String> propertyLabels, Map<String, String> propertyDescriptions,
                       Optional<String> header, Optional<String> footer) implements ElementUi
{
  public ElementUiRecord
  {
    validateListFieldDoesNotHaveDuplicates(this, order, UI_ORDER);
    validateMapFieldNotNull(this, propertyLabels, UI_PROPERTY_LABELS);
    validateMapFieldNotNull(this, propertyDescriptions, UI_PROPERTY_DESCRIPTIONS);
    validateOptionalFieldNotNull(this, header, UI_HEADER);
    validateOptionalFieldNotNull(this, footer, UI_FOOTER);

    // TODO Many CEDAR templates will have extra mappings for property labels and descriptions; probably remove silently
    /*
    if (!order.containsAll(propertyLabels.keySet())) {
      Set<String> extraPropertyLabels = propertyLabels.keySet();
      extraPropertyLabels.removeAll(order);
      throw new IllegalStateException(
        "propertyLabels field must contain only entries present in the order field in " +
          ElementUi.class.getName() + " " + this.toString() + "; extra labels: " + extraPropertyLabels);
    }

    if (!order.containsAll(propertyDescriptions.keySet())) {
      Set<String> extraPropertyDescriptions = propertyDescriptions.keySet();
      extraPropertyDescriptions.removeAll(order);
      throw new IllegalStateException(
        "propertyDescriptions field must contain only entries present in the order field in " +
          ElementUi.class.getName() + ": " + this.toString() + "; extra descriptions: " + extraPropertyDescriptions);
    }
    **/

    order = List.copyOf(order);
    propertyLabels = Map.copyOf(propertyLabels);
    propertyDescriptions = Map.copyOf(propertyDescriptions);
  }
}

