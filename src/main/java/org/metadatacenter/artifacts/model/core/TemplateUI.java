package org.metadatacenter.artifacts.model.core;

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
}
