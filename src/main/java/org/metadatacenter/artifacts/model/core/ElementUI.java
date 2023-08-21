package org.metadatacenter.artifacts.model.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateListFieldDoesNotHaveDuplicates;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateMapFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateOptionalFieldNotNull;
import static org.metadatacenter.model.ModelNodeNames.UI_FOOTER;
import static org.metadatacenter.model.ModelNodeNames.UI_HEADER;
import static org.metadatacenter.model.ModelNodeNames.UI_ORDER;
import static org.metadatacenter.model.ModelNodeNames.UI_PROPERTY_DESCRIPTIONS;
import static org.metadatacenter.model.ModelNodeNames.UI_PROPERTY_LABELS;

public non-sealed interface ElementUI extends UI, ParentArtifactUI
{
  static ElementUI create(List<String> order, Map<String, String> propertyLabels, Map<String, String> propertyDescriptions,
    Optional<String> header, Optional<String> footer)
  {
    return new ElementUIRecord(order, propertyLabels, propertyDescriptions, header, footer);
  }

  default UIType getUIType() { return UIType.ELEMENT_UI; }

  static Builder builder() {
    return new Builder();
  }

  class Builder {
    private List<String> order = new ArrayList<>();
    private Map<String, String> propertyLabels = new HashMap<>();
    private Map<String, String> propertyDescriptions = new HashMap<>();
    private Optional<String> header = Optional.empty();
    private Optional<String> footer = Optional.empty();

    private Builder() {
    }

    public Builder withOrder(List<String> order) {
      this.order = List.copyOf(order);
      return this;
    }

    public Builder withPropertyLabels(Map<String, String> propertyLabels) {
      this.propertyLabels = Map.copyOf(propertyLabels);
      return this;
    }

    public Builder withPropertyDescriptions(Map<String, String> propertyDescriptions) {
      this.propertyDescriptions = Map.copyOf(propertyDescriptions);
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

    public ElementUI build()
    {
      return new ElementUIRecord(order, propertyLabels, propertyDescriptions, header, footer);
    }
  }
}

record ElementUIRecord(List<String> order, Map<String, String> propertyLabels, Map<String, String> propertyDescriptions,
                       Optional<String> header, Optional<String> footer) implements ElementUI
{
  public ElementUIRecord
  {
    validateListFieldDoesNotHaveDuplicates(this, order, UI_ORDER);
    validateMapFieldNotNull(this, propertyLabels, UI_PROPERTY_LABELS);
    validateMapFieldNotNull(this, propertyDescriptions, UI_PROPERTY_DESCRIPTIONS);
    validateOptionalFieldNotNull(this, header, UI_HEADER);
    validateOptionalFieldNotNull(this, footer, UI_FOOTER);

    if (!order.stream().collect(Collectors.toSet()).containsAll(propertyLabels.keySet()))
      throw new IllegalStateException(
        "propertyLabels field must contain only entries present in the order field in " + ElementUI.class.getName() + ": " + this.toString());

    if (!order.stream().collect(Collectors.toSet()).containsAll(propertyDescriptions.keySet()))
      throw new IllegalStateException(
        "propertyDescriptions field must contain only entries present in the order field in " + ElementUI.class.getName() + ": " + this.toString());

    order = List.copyOf(order);
    propertyLabels = Map.copyOf(propertyLabels);
    propertyDescriptions = Map.copyOf(propertyDescriptions);
  }
}

