package org.metadatacenter.artifact.model.core;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ElementUI
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

  public List<String> getOrder()
  {
    return order;
  }

  public Map<String, String> getPropertyLabels()
  {
    return propertyLabels;
  }

  public Map<String, String> getPropertyDescriptions()
  {
    return propertyDescriptions;
  }

  public Optional<String> getHeader()
  {
    return header;
  }

  public Optional<String> getFooter()
  {
    return footer;
  }
}
