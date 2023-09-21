package org.metadatacenter.artifacts.model.core;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateListFieldDoesNotHaveDuplicates;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateListFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateMapFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateOptionalFieldNotNull;
import static org.metadatacenter.model.ModelNodeNames.UI_FOOTER;
import static org.metadatacenter.model.ModelNodeNames.UI_HEADER;
import static org.metadatacenter.model.ModelNodeNames.UI_ORDER;
import static org.metadatacenter.model.ModelNodeNames.UI_PAGES;
import static org.metadatacenter.model.ModelNodeNames.UI_PROPERTY_DESCRIPTIONS;
import static org.metadatacenter.model.ModelNodeNames.UI_PROPERTY_LABELS;

public non-sealed interface TemplateUi extends Ui, ParentArtifactUi
{
 static TemplateUi create(List<String> order, List<String> pages, Map<String, String> propertyLabels,
    Map<String, String> propertyDescriptions, Optional<String> header, Optional<String> footer)
  {
    return new TemplateUiRecord(order, pages, propertyLabels, propertyDescriptions, header, footer);
  }

  List<String> pages();

  @JsonIgnore
  default UiType uiType() { return UiType.TEMPLATE_UI; }

  static Builder builder() {
    return new Builder();
  }

  class Builder {
    private List<String> order = new ArrayList<>();
    private List<String> pages = new ArrayList<>();
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

    public Builder withPages(List<String> pages) {
      this.pages = List.copyOf(pages);
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

    public TemplateUi build()
    {
      return new TemplateUiRecord(order, pages, propertyLabels, propertyDescriptions, header, footer);
    }
  }
}

record TemplateUiRecord(List<String> order, List<String> pages, Map<String, String> propertyLabels,
                        Map<String, String> propertyDescriptions, Optional<String> header, Optional<String> footer)
  implements TemplateUi
{
  public TemplateUiRecord
  {
    validateListFieldDoesNotHaveDuplicates(this, order, UI_ORDER);
    validateListFieldNotNull(this, pages, UI_PAGES);
    validateMapFieldNotNull(this, propertyLabels, UI_PROPERTY_LABELS);
    validateMapFieldNotNull(this, propertyDescriptions, UI_PROPERTY_DESCRIPTIONS);
    validateOptionalFieldNotNull(this, header, UI_HEADER);
    validateOptionalFieldNotNull(this, footer, UI_FOOTER);

    if (!order.stream().collect(Collectors.toSet()).containsAll(propertyLabels.keySet()))
      throw new IllegalStateException("propertyLabels field must contain only entries present in the order field in " +
        TemplateUi.class.getName() + ": " + this);

    if (!order.stream().collect(Collectors.toSet()).containsAll(propertyDescriptions.keySet()))
      throw new IllegalStateException("propertyDescriptions field must contain only entries present in the order field in " +
        TemplateUi.class.getName() + ": " + this);

    order = List.copyOf(order);
    pages = List.copyOf(pages);
    propertyLabels = Map.copyOf(propertyLabels);
    propertyDescriptions = Map.copyOf(propertyDescriptions);
  }
}
