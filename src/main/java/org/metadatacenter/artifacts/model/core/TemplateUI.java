package org.metadatacenter.artifacts.model.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class TemplateUI implements UI, ParentArtifactUI
{
  private final List<String> order;
  private final List<String> pages;
  private final Map<String, String> propertyLabels;
  private final Map<String, String> propertyDescriptions;
  private final Optional<String> header;
  private final Optional<String> footer;

  public TemplateUI(List<String> order, List<String> pages, Map<String, String> propertyLabels,
    Map<String, String> propertyDescriptions, Optional<String> header, Optional<String> footer)
  {
    this.order = Collections.unmodifiableList(order);
    this.pages = Collections.unmodifiableList(pages);
    this.propertyLabels = Collections.unmodifiableMap(propertyLabels);
    this.propertyDescriptions = Collections.unmodifiableMap(propertyDescriptions);
    this.header = header;
    this.footer = footer;
  }

  private TemplateUI(Builder builder) {
    this.order = Collections.unmodifiableList(builder.order);
    this.pages = Collections.unmodifiableList(builder.pages);
    this.propertyLabels = Collections.unmodifiableMap(builder.propertyLabels);
    this.propertyDescriptions = Collections.unmodifiableMap(builder.propertyDescriptions);
    this.header = builder.header;
    this.footer = builder.footer;
  }

  @Override public UIType getUIType() { return UIType.TEMPLATE_UI; }

  @Override public List<String> getOrder()
  {
    return order;
  }

  public List<String> getPages()
  {
    return pages;
  }

  @Override public Map<String, String> getPropertyLabels()
  {
    return propertyLabels;
  }

  @Override public Map<String, String> getPropertyDescriptions()
  {
    return propertyDescriptions;
  }

  @Override public Optional<String> getHeader()
  {
    return header;
  }

  @Override public Optional<String> getFooter()
  {
    return footer;
  }

  @Override public String toString()
  {
    return "TemplateUI{" + "order=" + order + ", pages=" + pages + ", propertyLabels=" + propertyLabels
      + ", propertyDescriptions=" + propertyDescriptions + ", header='" + header + '\'' + ", footer='" + footer + '\''
      + '}';
  }

  public static class Builder {
    private List<String> order = new ArrayList<>();
    private List<String> pages = new ArrayList<>();
    private Map<String, String> propertyLabels = Collections.emptyMap();
    private Map<String, String> propertyDescriptions = Collections.emptyMap();
    private Optional<String> header = Optional.empty();
    private Optional<String> footer = Optional.empty();

    private Builder() {
    }

    public Builder withOrder(List<String> order) {
      this.order = new ArrayList<>(order);
      return this;
    }

    public Builder withPages(List<String> pages) {
      this.pages = new ArrayList<>(pages);
      return this;
    }

    public Builder withPropertyLabels(Map<String, String> propertyLabels) {
      this.propertyLabels = propertyLabels;
      return this;
    }

    public Builder withPropertyDescriptions(Map<String, String> propertyDescriptions) {
      this.propertyDescriptions = propertyDescriptions;
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

    public TemplateUI build() {
      return new TemplateUI(this);
    }
  }
}
