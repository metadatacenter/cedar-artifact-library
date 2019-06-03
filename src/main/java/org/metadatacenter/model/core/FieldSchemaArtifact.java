package org.metadatacenter.model.core;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

public class FieldSchemaArtifact extends SchemaArtifact
{
  private final String inputType;
  private final boolean isMultiple;

  public FieldSchemaArtifact(String jsonLDID, Set<String> jsonLDTypes, String name, String description,
    String createdBy, String modifiedBy, LocalDateTime createdOn, LocalDateTime lastUpdatedOn, String schema,
    String schemaVersion, String version, String status, Map<String, String> context, String inputType,
    boolean isMultiple)
  {
    super(jsonLDID, jsonLDTypes, name, description, createdBy, modifiedBy, createdOn, lastUpdatedOn, schema,
      schemaVersion, version, status, context);
    this.inputType = inputType;
    this.isMultiple = isMultiple;
  }

  public String getInputType()
  {
    return inputType;
  }

  public boolean isMultiple()
  {
    return isMultiple;
  }
}
