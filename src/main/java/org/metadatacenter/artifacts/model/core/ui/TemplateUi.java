package org.metadatacenter.artifacts.model.core.ui;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateListFieldDoesNotHaveDuplicates;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateMapFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateOptionalFieldNotNull;
import static org.metadatacenter.model.ModelNodeNames.UI_FOOTER;
import static org.metadatacenter.model.ModelNodeNames.UI_HEADER;
import static org.metadatacenter.model.ModelNodeNames.UI_ORDER;
import static org.metadatacenter.model.ModelNodeNames.UI_PROPERTY_DESCRIPTIONS;
import static org.metadatacenter.model.ModelNodeNames.UI_PROPERTY_LABELS;

public non-sealed interface TemplateUi extends Ui, ParentArtifactUi
{
 static TemplateUi create(List<String> order, LinkedHashMap<String, String> propertyLabels,
    LinkedHashMap<String, String> propertyDescriptions, Optional<String> header, Optional<String> footer)
  {
    return new TemplateUiRecord(order, propertyLabels, propertyDescriptions, header, footer);
  }

  Optional<String> header();

  Optional<String> footer();

  @JsonIgnore
  default UiType uiType() { return UiType.TEMPLATE_UI; }

  static Builder builder() {
    return new Builder();
  }

  static Builder builder(TemplateUi templateUi) {
    return new Builder(templateUi);
  }

  class Builder {
    private List<String> order = new ArrayList<>();
    private LinkedHashMap<String, String> propertyLabels = new LinkedHashMap<>();
    private LinkedHashMap<String, String> propertyDescriptions = new LinkedHashMap<>();
    private Optional<String> header = Optional.empty();
    private Optional<String> footer = Optional.empty();

    private Builder() {
    }

    private Builder(TemplateUi templateUi)
    {
      this.order = List.copyOf(templateUi.order());
      this.propertyLabels = new LinkedHashMap<>(templateUi.propertyLabels());
      this.propertyDescriptions = new LinkedHashMap<>(templateUi.propertyDescriptions());
      this.header = templateUi.header();
      this.footer = templateUi.footer();
    }

    public Builder withOrder(String fieldKey) {

      if (fieldKey == null)
        throw new IllegalArgumentException("Null order field key passed to " + this.getClass().getName());

      if (order.contains(fieldKey))
        throw new IllegalArgumentException("Duplicate order field key " + fieldKey + " passed to " + this.getClass().getName());

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
      return new TemplateUiRecord(order, propertyLabels, propertyDescriptions, header, footer);
    }
  }
}

record TemplateUiRecord(List<String> order,
                        LinkedHashMap<String, String> propertyLabels,
                        LinkedHashMap<String, String> propertyDescriptions,
                        Optional<String> header, Optional<String> footer)
  implements TemplateUi
{
  public TemplateUiRecord
  {
    validateListFieldDoesNotHaveDuplicates(this, order, UI_ORDER);
    validateMapFieldNotNull(this, propertyLabels, UI_PROPERTY_LABELS);
    validateMapFieldNotNull(this, propertyDescriptions, UI_PROPERTY_DESCRIPTIONS);
    validateOptionalFieldNotNull(this, header, UI_HEADER);
    validateOptionalFieldNotNull(this, footer, UI_FOOTER);

    order = List.copyOf(order);
    propertyLabels = new LinkedHashMap<>(propertyLabels);
    propertyDescriptions = new LinkedHashMap<>(propertyDescriptions);
  }
}
