package org.metadatacenter.artifacts.model.core;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public sealed interface ParentInstanceArtifact extends ParentArtifact permits TemplateInstanceArtifact,
  ElementInstanceArtifact
{
  Optional<String> name();

  Optional<String> description();

  List<String> childNames();

  // field name->field instance artifact
  Map<String, FieldInstanceArtifact> singleInstanceFieldInstances();

  // field name->[field instance artifact]
  Map<String, List<FieldInstanceArtifact>> multiInstanceFieldInstances();

  // element name->element instance artifact
  Map<String, ElementInstanceArtifact> singleInstanceElementInstances();

  // field name->[field instance artifact]
  Map<String, List<ElementInstanceArtifact>> multiInstanceElementInstances();

  // attribute-value field name->(attribute-value field instance name->field instance artifact)
  Map<String, Map<String, FieldInstanceArtifact>> attributeValueFieldInstances();
}
