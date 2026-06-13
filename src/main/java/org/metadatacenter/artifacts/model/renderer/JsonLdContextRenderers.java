package org.metadatacenter.artifacts.model.renderer;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifacts.model.core.SchemaArtifact;


import static org.metadatacenter.artifacts.model.renderer.JsonArtifactRenderer.MAPPER;
import static org.metadatacenter.artifacts.model.renderer.JsonLdContextRenderers.*;
import static org.metadatacenter.artifacts.model.renderer.JsonSchemaArtifactPropertyRenderers.*;
import static org.metadatacenter.artifacts.model.renderer.JsonSchemaSpecRenderers.*;
import static org.metadatacenter.model.ModelNodeNames.*;

final class JsonLdContextRenderers {
  private JsonLdContextRenderers() {}

  public static ObjectNode renderParentSchemaArtifactContextJsonLdSpecification(SchemaArtifact schemaArtifact) {
    ObjectNode rendering = renderParentSchemaArtifactContextPrefixesJsonLdSpecification();

    rendering.put(SCHEMA_ORG_NAME, renderXsdStringJsonLdSpecification());
    rendering.put(SCHEMA_ORG_DESCRIPTION, renderXsdStringJsonLdSpecification());
    rendering.put(PAV_CREATED_ON, renderXsdDateTimeJsonLdSpecification());
    rendering.put(PAV_CREATED_BY, renderIriJsonLdSpecification());
    rendering.put(PAV_LAST_UPDATED_ON, renderXsdDateTimeJsonLdSpecification());
    rendering.put(OSLC_MODIFIED_BY, renderIriJsonLdSpecification());

    if (schemaArtifact.language().isPresent()) {
      String language = schemaArtifact.language().get();
      if (!language.isEmpty()) {
        rendering.put(JSON_LD_LANGUAGE, language);
      }
    }

    return rendering;
  }


  public static ObjectNode renderTemplateInstanceArtifactContextJsonLdSpecification() {
    ObjectNode rendering = MAPPER.createObjectNode();

    for (var entry : INSTANCE_ARTIFACT_CONTEXT_PREFIX_MAPPINGS.entrySet()) {
      rendering.put(entry.getKey(), entry.getValue().toString());
    }

    rendering.put(RDFS_LABEL, renderXsdStringJsonLdSpecification());
    rendering.put(SCHEMA_IS_BASED_ON, renderIriJsonLdSpecification());
    rendering.put(SCHEMA_ORG_NAME, renderXsdStringJsonLdSpecification());
    rendering.put(SCHEMA_ORG_DESCRIPTION, renderXsdStringJsonLdSpecification());
    rendering.put(PAV_DERIVED_FROM, renderIriJsonLdSpecification());
    rendering.put(PAV_CREATED_ON, renderXsdDateTimeJsonLdSpecification());
    rendering.put(PAV_CREATED_BY, renderIriJsonLdSpecification());
    rendering.put(PAV_LAST_UPDATED_ON, renderXsdDateTimeJsonLdSpecification());
    rendering.put(OSLC_MODIFIED_BY, renderIriJsonLdSpecification());
    rendering.put(SKOS_NOTATION, renderXsdStringJsonLdSpecification());

    return rendering;
  }


  public static ObjectNode renderParentSchemaArtifactContextPrefixesJsonLdSpecification() {
    ObjectNode rendering = MAPPER.createObjectNode();

    for (var entry : PARENT_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS.entrySet()) {
      rendering.put(entry.getKey(), entry.getValue().toString());
    }

    return rendering;
  }


  public static ObjectNode renderFieldSchemaArtifactContextPrefixesJsonLdSpecification(
      FieldSchemaArtifact fieldSchemaArtifact) {
    ObjectNode rendering = MAPPER.createObjectNode();

    for (var entry : FIELD_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS.entrySet()) {
      rendering.put(entry.getKey(), entry.getValue().toString());
    }

    rendering.put(SCHEMA_ORG_NAME, renderXsdStringJsonLdSpecification());
    rendering.put(SCHEMA_ORG_DESCRIPTION, renderXsdStringJsonLdSpecification());
    rendering.put(SKOS_PREFLABEL, renderXsdStringJsonLdSpecification());
    rendering.put(SKOS_ALTLABEL, renderXsdStringJsonLdSpecification());
    rendering.put(PAV_CREATED_ON, renderXsdDateTimeJsonLdSpecification());
    rendering.put(PAV_CREATED_BY, renderIriJsonLdSpecification());
    rendering.put(PAV_LAST_UPDATED_ON, renderXsdDateTimeJsonLdSpecification());
    rendering.put(OSLC_MODIFIED_BY, renderIriJsonLdSpecification());

    if (fieldSchemaArtifact.language().isPresent()) {
      String language = fieldSchemaArtifact.language().get();
      if (!language.isEmpty()) {
        rendering.put(JSON_LD_LANGUAGE, language);
      }
    }

    return rendering;
  }


  public static ObjectNode renderStaticFieldSchemaArtifactContextPrefixesJsonLdSpecification(
      FieldSchemaArtifact fieldSchemaArtifact) {
    ObjectNode rendering = MAPPER.createObjectNode();

    for (var entry : STATIC_FIELD_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS.entrySet()) {
      rendering.put(entry.getKey(), entry.getValue().toString());
    }

    if (fieldSchemaArtifact.language().isPresent()) {
      String language = fieldSchemaArtifact.language().get();
      if (!language.isEmpty()) {
        rendering.put(JSON_LD_LANGUAGE, language);
      }
    }

    return rendering;
  }


  public static ObjectNode renderInstanceArtifactContextPrefixesJsonLdSpecification() {
    ObjectNode rendering = MAPPER.createObjectNode();

    for (var entry : INSTANCE_ARTIFACT_CONTEXT_PREFIX_MAPPINGS.entrySet()) {
      rendering.put(entry.getKey(), entry.getValue().toString());
    }

    return rendering;
  }

}
