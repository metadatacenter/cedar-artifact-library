package org.metadatacenter.model.core;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * While element instances may not necessarily have JSON-LD identifiers or provenance fields (name, description,
 * createdBy, modifiedBy, createdOn, lastUpdatedOn) the model allows them.
 */
public class ElementInstanceArtifact extends InstanceArtifact
{
  private final Map<String, List<FieldInstanceArtifact>> fieldInstances;
  private final Map<String, List<ElementInstanceArtifact>> elementInstances;

  public ElementInstanceArtifact(InstanceArtifact instanceArtifact,
    Map<String, List<FieldInstanceArtifact>> fieldInstances,
    Map<String, List<ElementInstanceArtifact>> elementInstances)
  {
    super(instanceArtifact);
    this.fieldInstances = Collections.unmodifiableMap(fieldInstances);
    this.elementInstances = Collections.unmodifiableMap(elementInstances);
  }

  public Map<String, List<FieldInstanceArtifact>> getFieldInstances()
  {
    return fieldInstances;
  }

  public Map<String, List<ElementInstanceArtifact>> getElementInstances()
  {
    return elementInstances;
  }
}
