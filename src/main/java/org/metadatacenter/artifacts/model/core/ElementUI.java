package org.metadatacenter.artifacts.model.core;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class ElementUI implements UI, ParentArtifactUI
{
  private final List<String> order;
  private final Map<String, String> propertyLabels;
  private final Map<String, String> propertyDescriptions;
  private final Optional<String> header;
  private final Optional<String> footer;

  public ElementUI(List<String> order, Map<String, String> propertyLabels, Map<String, String> propertyDescriptions,
    Optional<String> header, Optional<String> footer)
  {
    this.order = Collections.unmodifiableList(order);
    this.propertyLabels = Collections.unmodifiableMap(propertyLabels);
    this.propertyDescriptions = Collections.unmodifiableMap(propertyDescriptions);
    this.header = header;
    this.footer = footer;
  }

  @Override public UIType getUIType() { return UIType.ELEMENT_UI; }
  @Override public List<String> getOrder()
  {
    return order;
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
}
