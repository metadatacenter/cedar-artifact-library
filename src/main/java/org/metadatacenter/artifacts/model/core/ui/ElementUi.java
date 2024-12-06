package org.metadatacenter.artifacts.model.core.ui;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateListFieldDoesNotHaveDuplicates;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateMapFieldNotNull;
import static org.metadatacenter.model.ModelNodeNames.UI_ORDER;
import static org.metadatacenter.model.ModelNodeNames.UI_PROPERTY_DESCRIPTIONS;
import static org.metadatacenter.model.ModelNodeNames.UI_PROPERTY_LABELS;

public non-sealed interface ElementUi extends Ui, ParentArtifactUi
{
  static ElementUi create(List<String> order, LinkedHashMap<String, String> propertyLabels,
    LinkedHashMap<String, String> propertyDescriptions)
  {
    return new ElementUiRecord(order, propertyLabels, propertyDescriptions);
  }

  @JsonIgnore
  default UiType uiType() { return UiType.ELEMENT_UI; }

  static Builder builder() {
    return new Builder();
  }

  static Builder builder(ElementUi elementUi) {
    return new Builder(elementUi);
  }

  class Builder {
    private List<String> order = new ArrayList<>();
    private LinkedHashMap<String, String> propertyLabels = new LinkedHashMap<>();
    private LinkedHashMap<String, String> propertyDescriptions = new LinkedHashMap<>();

    private Builder() {
    }

    private Builder(ElementUi elementUi) {
      this.order = List.copyOf(elementUi.order());
      this.propertyLabels = new LinkedHashMap<>(elementUi.propertyLabels());
      this.propertyDescriptions = new LinkedHashMap<>(elementUi.propertyDescriptions());
    }

    public Builder withOrder(String fieldKey) {

      if (fieldKey == null)
        throw new IllegalArgumentException("Null order field key passed to " + this.getClass().getName());

      if (order.contains(fieldKey))
        throw new IllegalArgumentException("Duplicate order field " + fieldKey + " passed to " + this.getClass().getName());

      this.order.add(fieldKey);
      return this;
    }

    public Builder withPropertyLabel(String fieldKey, String propertyLabel) {
      if (fieldKey == null)
        throw new IllegalArgumentException("Null property label field key passed to " + this.getClass().getName());

      if (propertyLabel == null)
        throw new IllegalArgumentException("Null property label for field " + fieldKey + " passed to " + this.getClass().getName());

      if (this.propertyLabels.containsKey(fieldKey))
        throw new IllegalArgumentException("Duplicate property label field " + fieldKey + " passed to " + this.getClass().getName());

      this.propertyLabels.put(fieldKey, propertyLabel);
      return this;
    }

    public Builder withPropertyDescription(String fieldKey, String propertyDescription) {

      if (fieldKey == null)
        throw new IllegalArgumentException("Null property description field key passed to " + this.getClass().getName());

      if (propertyDescription == null)
        throw new IllegalArgumentException("Null property description for field " + fieldKey + " passed to " + this.getClass().getName());

      if (this.propertyDescriptions.containsKey(fieldKey))
        throw new IllegalArgumentException("Duplicate property description field " + fieldKey + " passed to " + this.getClass().getName());

      this.propertyDescriptions.put(fieldKey, propertyDescription);
      return this;
    }

    public ElementUi build()
    {
      return new ElementUiRecord(order, propertyLabels, propertyDescriptions);
    }
  }
}

record ElementUiRecord(List<String> order,
                       LinkedHashMap<String, String> propertyLabels,
                       LinkedHashMap<String, String> propertyDescriptions) implements ElementUi
{
  public ElementUiRecord
  {
    validateListFieldDoesNotHaveDuplicates(this, order, UI_ORDER);
    validateMapFieldNotNull(this, propertyLabels, UI_PROPERTY_LABELS);
    validateMapFieldNotNull(this, propertyDescriptions, UI_PROPERTY_DESCRIPTIONS);

    order = List.copyOf(order);
    propertyLabels = new LinkedHashMap<>(propertyLabels);
    propertyDescriptions = new LinkedHashMap<>(propertyDescriptions);
  }
}

