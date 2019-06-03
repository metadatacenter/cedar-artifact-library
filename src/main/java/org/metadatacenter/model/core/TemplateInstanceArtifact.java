package org.metadatacenter.model.core;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TemplateInstanceArtifact extends InstanceArtifact
{
  private final String isBasedOn;
  private final Map<String, List<ElementInstanceArtifact>> elementInstances;
  private final Map<String, List<FieldInstanceArtifact>> fieldInstances;

  public TemplateInstanceArtifact(String jsonLDID, Set<String> jsonLDTypes, String name, String description,
    String createdBy, String modifiedBy, LocalDateTime createdOn, LocalDateTime lastUpdatedOn,
    Map<String, String> context, String isBasedOn, Map<String, List<ElementInstanceArtifact>> elementInstances,
    Map<String, List<FieldInstanceArtifact>> fieldInstances)
  {
    super(jsonLDID, jsonLDTypes, name, description, createdBy, modifiedBy, createdOn, lastUpdatedOn, context);
    this.isBasedOn = isBasedOn;
    this.elementInstances = Collections.unmodifiableMap(elementInstances);
    this.fieldInstances = Collections.unmodifiableMap(fieldInstances);
  }

  public String getIsBasedOn()
  {
    return isBasedOn;
  }

  public Map<String, List<ElementInstanceArtifact>> getElementInstances()
  {
    return elementInstances;
  }

  public Map<String, List<FieldInstanceArtifact>> getFieldInstances()
  {
    return fieldInstances;
  }
}
