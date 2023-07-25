package org.metadatacenter.artifacts.model.renderer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.metadatacenter.artifacts.model.core.Artifact;
import org.metadatacenter.artifacts.model.core.ElementSchemaArtifact;
import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifacts.model.core.SchemaArtifact;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
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

  public ObjectNode renderTemplateSchemaArtifact(TemplateSchemaArtifact templateSchemaArtifact)
  {
    ObjectNode templateSchemaArtifactRendering = renderSchemaArtifact(templateSchemaArtifact);

    //  @context

    // properties
    // required
    // additionalProperties

    // _ui

    return templateSchemaArtifactRendering;
  }

  public ObjectNode renderElementSchemaArtifact(ElementSchemaArtifact elementSchemaArtifact)
  {
    ObjectNode elementSchemaArtifactRendering = renderSchemaArtifact(elementSchemaArtifact);

    // @context

    // properties
    // required
    // additionalProperties

    // _ui

    return elementSchemaArtifactRendering;
  }

  public ObjectNode renderFieldSchemaArtifact(FieldSchemaArtifact fieldSchemaArtifact)
  {
    ObjectNode fieldSchemaArtifactRendering = renderSchemaArtifact(fieldSchemaArtifact);

    // isMultiple!!!

    // properties
    // required
    // additionalProperties

    // @context

    // skos:prefLabel
    // skos:identifier
    // skos:alternateLabels[]

    // _ui
    // _valueConstraints

    return fieldSchemaArtifactRendering;
  }

  private ObjectNode renderSchemaArtifact(SchemaArtifact schemaArtifact)
  {
    ObjectNode schemaArtifactRendering = renderArtifact(schemaArtifact);

    schemaArtifactRendering.put(ModelNodeNames.JSON_SCHEMA_SCHEMA, schemaArtifact.getJsonSchemaSchemaUri().toString());
    schemaArtifactRendering.put(ModelNodeNames.JSON_SCHEMA_TYPE, ModelNodeNames.JSON_SCHEMA_OBJECT);
    schemaArtifactRendering.put(ModelNodeNames.JSON_SCHEMA_TITLE, schemaArtifact.getJsonSchemaTitle());
    schemaArtifactRendering.put(ModelNodeNames.JSON_SCHEMA_DESCRIPTION, schemaArtifact.getJsonSchemaDescription());
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

    // TODO private final List<URI> jsonLdTypes;

    if (artifact.getJsonLdId().isPresent())
      artifactRendering.put(ModelNodeNames.JSON_LD_ID, artifact.getJsonLdId().get().toString());

    if (artifact.getCreatedBy().isPresent())
      artifactRendering.put(ModelNodeNames.PAV_CREATED_BY, artifact.getCreatedBy().get().toString());

    if (artifact.getModifiedBy().isPresent())
      artifactRendering.put(ModelNodeNames.OSLC_MODIFIED_BY, artifact.getModifiedBy().get().toString());

    if (artifact.getCreatedOn().isPresent())
      artifactRendering.put(ModelNodeNames.PAV_CREATED_ON, artifact.getCreatedOn().get().toString());

    if (artifact.getCreatedBy().isPresent())
      artifactRendering.put(ModelNodeNames.PAV_CREATED_BY, artifact.getCreatedBy().get().toString());

    return artifactRendering;
  }
}
