package org.metadatacenter.model.reader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.metadatacenter.model.core.ElementSchemaArtifact;
import org.metadatacenter.model.core.FieldSchemaArtifact;
import org.metadatacenter.model.core.TemplateSchemaArtifact;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SchemaArtifactReader extends ArtifactReader
{
  public SchemaArtifactReader(ObjectMapper mapper)
  {
    super(mapper);
  }

  public TemplateSchemaArtifact readTemplateSchemaArtifact(ObjectNode objectNode)
  {
    String jsonLDID;
    Set<String> jsonLDTypes;
    String name;
    String description;
    String createdBy;
    String modifiedBy;
    LocalDateTime createdOn;
    LocalDateTime lastUpdatedOn;
    String schema;
    String schemaVersion;
    String version;
    String status;
    Map<String, String> context;
    List< FieldSchemaArtifact > fields;
    List< ElementSchemaArtifact > elements;

  }
}
