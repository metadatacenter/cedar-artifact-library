package org.metadatacenter.model.core;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ElementUI
{
  private final List<String> order;
  private final Map<String, String> propertyLabels;
  private final Map<String, String> propertyDescriptions;
  private final String header;
  private final String footer;

  public ElementUI(List<String> order, Map<String, String> propertyLabels, Map<String, String> propertyDescriptions,
    String header, String footer)
  {
    this.order = order;
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

  public String getHeader()
  {
    return header;
  }

  public String getFooter()
  {
    return footer;
  }
}
