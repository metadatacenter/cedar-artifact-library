package org.metadatacenter.model.core;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * While element instances may not necessarily have JSON-LD identifiers or provenance fields (name, description,
 * createdBy, modifiedBy, createdOn, lastUpdatedOn) the model allows them.
 */
public class ElementInstanceArtifact extends InstanceArtifact
{
  private final Map<String, List<ElementInstanceArtifact>> elementInstances;
  private final Map<String, List<FieldInstanceArtifact>> fieldInstances;

  public ElementInstanceArtifact(String jsonLDID, Set<String> jsonLDTypes, String name, String description,
    String createdBy, String modifiedBy, OffsetDateTime createdOn, OffsetDateTime lastUpdatedOn,
    Map<String, String> context, Map<String, List<ElementInstanceArtifact>> elementInstances,
    Map<String, List<FieldInstanceArtifact>> fieldInstances)
  {
    super(jsonLDID, jsonLDTypes, name, description, createdBy, modifiedBy, createdOn, lastUpdatedOn, context);
    this.elementInstances = Collections.unmodifiableMap(elementInstances);
    this.fieldInstances = Collections.unmodifiableMap(fieldInstances);
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
