package org.metadatacenter.artifacts.model.core;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TemplateInstanceArtifact extends InstanceArtifact
{
  private final String isBasedOn;
  private final Map<String, List<ElementInstanceArtifact>> elementInstances;
  private final Map<String, List<FieldInstanceArtifact>> fieldInstances;

  public TemplateInstanceArtifact(InstanceArtifact instanceArtifact, String isBasedOn,
    Map<String, List<ElementInstanceArtifact>> elementInstances,
    Map<String, List<FieldInstanceArtifact>> fieldInstances)
  {
    super(instanceArtifact);
    this.isBasedOn = isBasedOn;
    this.elementInstances = Collections.unmodifiableMap(elementInstances);
    this.fieldInstances = Collections.unmodifiableMap(fieldInstances);
  }


  public TemplateInstanceArtifact(TemplateInstanceArtifact templateInstanceArtifact)
  {
    super(templateInstanceArtifact);
    this.isBasedOn = templateInstanceArtifact.isBasedOn;
    this.elementInstances = templateInstanceArtifact.elementInstances;
    this.fieldInstances = templateInstanceArtifact.fieldInstances;
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

  @Override public String toString()
  {
    return super.toString() + "\n TemplateInstanceArtifact{" + "isBasedOn='" + isBasedOn + '\'' + ", elementInstances=" + elementInstances
      + ", fieldInstances=" + fieldInstances + '}';
  }
}
