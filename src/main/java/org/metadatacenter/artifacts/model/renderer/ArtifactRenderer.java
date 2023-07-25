package org.metadatacenter.artifacts.model.renderer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.metadatacenter.artifacts.model.core.Artifact;
import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifacts.model.core.SchemaArtifact;
import org.metadatacenter.model.ModelNodeNames;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Optional;

public class ArtifactRenderer
{
  private final ObjectMapper mapper;

  public ArtifactRenderer(ObjectMapper mapper)
  {
    this.mapper = mapper;
  }

  public ObjectNode renderFieldSchemaArtifact(FieldSchemaArtifact fieldSchemaArtifact)
  {
    ObjectNode fieldSchemaArtifactRendering = renderSchemaArtifact(fieldSchemaArtifact);

    // isMultiple!!!

    // required
    // additionalProperties

    // @id
    // @type
    // @context

    // skos:prefLabel
    // skos:identifier
    // skos:alternateLabels[]

    // _valueConstraints
    // _ui

    return fieldSchemaArtifactRendering;
  }


  private ObjectNode renderSchemaArtifact(SchemaArtifact schemaArtifact)
  {
    ObjectNode schemaArtifactRendering = renderArtifact(schemaArtifact);

    schemaArtifactRendering.put(ModelNodeNames.JSON_SCHEMA_SCHEMA, schemaArtifact.getJsonSchemaSchemaUri().toString());
    schemaArtifactRendering.put(ModelNodeNames.JSON_SCHEMA_TYPE, ModelNodeNames.JSON_SCHEMA_OBJECT);
    schemaArtifactRendering.put(ModelNodeNames.JSON_SCHEMA_TITLE, schemaArtifact.getJsonSchemaTitle());
    schemaArtifactRendering.put(ModelNodeNames.JSON_SCHEMA_DESCRIPTION, schemaArtifact.getJsonSchemaDescription());
    // TODO private final List<URI> jsonLdTypes;
    schemaArtifactRendering.put(ModelNodeNames.SCHEMA_ORG_NAME, schemaArtifact.getName());
    schemaArtifactRendering.put(ModelNodeNames.SCHEMA_ORG_DESCRIPTION, schemaArtifact.getDescription());
    schemaArtifactRendering.put(ModelNodeNames.SCHEMA_ORG_SCHEMA_VERSION, schemaArtifact.getVersion().toString());

    if (schemaArtifact.getVersion().isPresent())
      schemaArtifactRendering.put(ModelNodeNames.PAV_VERSION, schemaArtifact.getVersion().get().toString());

    if (schemaArtifact.getStatus().isPresent())
      schemaArtifactRendering.put(ModelNodeNames.BIBO_STATUS, schemaArtifact.getStatus().get().toString());

    if (schemaArtifact.getPreviousVersion().isPresent())
      schemaArtifactRendering.put(ModelNodeNames.PAV_PREVIOUS_VERSION, schemaArtifact.getPreviousVersion().get().toString());

    if (schemaArtifact.getDerivedFrom().isPresent())
      schemaArtifactRendering.put(ModelNodeNames.PAV_DERIVED_FROM, schemaArtifact.getDerivedFrom().get().toString());

    return schemaArtifactRendering;
  }

  private ObjectNode renderArtifact(Artifact artifact)
  {
    ObjectNode artifactRendering = mapper.createObjectNode();

    if (artifact.getJsonLdId().isPresent())
      artifactRendering.put(ModelNodeNames.JSON_LD_ID, artifact.getJsonLdId().get().toString());

    return artifactRendering;
  }

//  private final Map<String, URI> jsonLdContext;
//  private final Optional<URI> createdBy, modifiedBy;
//  private final Optional<OffsetDateTime> createdOn, lastUpdatedOn;

}
