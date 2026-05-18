package org.metadatacenter.artifacts.model.renderer;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.metadatacenter.artifacts.model.core.ElementSchemaArtifact;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;

import java.net.URI;

import static org.metadatacenter.artifacts.model.renderer.JsonArtifactRenderer.MAPPER;
import static org.metadatacenter.artifacts.model.renderer.JsonLdContextRenderers.*;
import static org.metadatacenter.artifacts.model.renderer.JsonSchemaArtifactPropertyRenderers.*;
import static org.metadatacenter.artifacts.model.renderer.JsonSchemaSpecRenderers.*;
import static org.metadatacenter.model.ModelNodeNames.*;
import static org.metadatacenter.model.ModelNodeValues.OSLC_IRI;
import static org.metadatacenter.model.ModelNodeValues.PAV_IRI;
import static org.metadatacenter.model.ModelNodeValues.RDFS_IRI;
import static org.metadatacenter.model.ModelNodeValues.SCHEMA_IRI;
import static org.metadatacenter.model.ModelNodeValues.SKOS_IRI;
import static org.metadatacenter.model.ModelNodeValues.XSD_IRI;

final class JsonSchemaArtifactPropertyRenderers {
  private JsonSchemaArtifactPropertyRenderers() {}

  public static ObjectNode renderAdditionalPropertiesForAttributeValueFieldJsonSchemaSpecification() {
    ObjectNode rendering = MAPPER.createObjectNode();

    rendering.put(JSON_SCHEMA_TYPE, JSON_SCHEMA_OBJECT);

    rendering.put(JSON_SCHEMA_PROPERTIES, MAPPER.createObjectNode());
    rendering.withObject("/" + JSON_SCHEMA_PROPERTIES)
        .put(JSON_LD_VALUE, renderStringOrNullJsonSchemaTypeSpecification());
    rendering.withObject("/" + JSON_SCHEMA_PROPERTIES).put(JSON_LD_TYPE, renderUriJsonSchemaTypeSpecification());
    rendering.put(JSON_SCHEMA_REQUIRED, MAPPER.createArrayNode());
    rendering.withArray(JSON_SCHEMA_REQUIRED).add(JSON_LD_VALUE);
    rendering.put(JSON_SCHEMA_ADDITIONAL_PROPERTIES, false);

    return rendering;
  }


  public static ObjectNode renderAdditionalPropertiesForAttributeValueFieldContextPropertiesJsonSchemaSpecification() {
    ObjectNode rendering = MAPPER.createObjectNode();

    rendering.put(JSON_SCHEMA_TYPE, JSON_SCHEMA_STRING);
    rendering.put(JSON_SCHEMA_FORMAT, JSON_SCHEMA_FORMAT_URI);

    return rendering;
  }


  public static ObjectNode renderTemplateSchemaArtifactPropertiesJsonSchemaSpecification(
      TemplateSchemaArtifact templateSchemaArtifact) {
    ObjectNode rendering = MAPPER.createObjectNode();

    rendering.put(JSON_LD_CONTEXT,
        renderTemplateSchemaArtifactContextPropertiesJsonSchemaSpecification(templateSchemaArtifact));

    rendering.put(JSON_LD_ID, renderUriOrNullJsonSchemaTypeSpecification());

    if (templateSchemaArtifact.instanceJsonLdType().isEmpty()) {
      rendering.put(JSON_LD_TYPE, renderUriOrUriArrayJsonSchemaTypeSpecification(1, true));
    } else {
      URI instanceJsonLdType = templateSchemaArtifact.instanceJsonLdType().get();
      rendering.put(JSON_LD_TYPE, renderUriOrUriArrayJsonSchemaTypeEnumSpecification(1, true, instanceJsonLdType));
    }

    rendering.put(SCHEMA_IS_BASED_ON, renderUriJsonSchemaTypeSpecification());
    rendering.put(SCHEMA_ORG_NAME, renderStringJsonSchemaTypeSpecification(1));
    rendering.put(SCHEMA_ORG_DESCRIPTION, renderStringJsonSchemaTypeSpecification(0));
    rendering.put(PAV_DERIVED_FROM, renderUriJsonSchemaTypeSpecification());
    rendering.put(PAV_CREATED_ON, renderDateTimeOrNullJsonSchemaTypeSpecification());
    rendering.put(PAV_CREATED_BY, renderUriOrNullJsonSchemaTypeSpecification());
    rendering.put(PAV_LAST_UPDATED_ON, renderDateTimeOrNullJsonSchemaTypeSpecification());
    rendering.put(OSLC_MODIFIED_BY, renderUriOrNullJsonSchemaTypeSpecification());

    return rendering;
  }


  public static ObjectNode renderElementSchemaArtifactPropertiesJsonSchemaSpecification(
      ElementSchemaArtifact elementSchemaArtifact) {
    ObjectNode rendering = MAPPER.createObjectNode();

    rendering.put(JSON_LD_CONTEXT,
        renderElementSchemaArtifactContextPropertiesJsonSchemaSpecification(elementSchemaArtifact));
    rendering.put(JSON_LD_ID, renderUriJsonSchemaTypeSpecification());

    if (elementSchemaArtifact.instanceJsonLdType().isEmpty()) {
      rendering.put(JSON_LD_TYPE, renderUriOrUriArrayJsonSchemaTypeSpecification(1, true));
    } else {
      URI instanceJsonLdType = elementSchemaArtifact.instanceJsonLdType().get();
      rendering.put(JSON_LD_TYPE, renderUriOrUriArrayJsonSchemaTypeEnumSpecification(1, true, instanceJsonLdType));
    }

    return rendering;
  }


  public static ObjectNode renderTemplateSchemaArtifactContextPropertiesJsonSchemaSpecification(
      TemplateSchemaArtifact templateSchemaArtifact) {
    ObjectNode rendering = MAPPER.createObjectNode();

    rendering.put(JSON_SCHEMA_TYPE, JSON_SCHEMA_OBJECT);

    rendering.put(JSON_SCHEMA_PROPERTIES, MAPPER.createObjectNode());

    rendering.withObject("/" + JSON_SCHEMA_PROPERTIES).put(RDFS, renderJsonSchemaTypeUriEnumSpecification(RDFS_IRI));
    rendering.withObject("/" + JSON_SCHEMA_PROPERTIES).put(XSD, renderJsonSchemaTypeUriEnumSpecification(XSD_IRI));
    rendering.withObject("/" + JSON_SCHEMA_PROPERTIES).put(PAV, renderJsonSchemaTypeUriEnumSpecification(PAV_IRI));
    rendering.withObject("/" + JSON_SCHEMA_PROPERTIES)
        .put(SCHEMA, renderJsonSchemaTypeUriEnumSpecification(SCHEMA_IRI));
    rendering.withObject("/" + JSON_SCHEMA_PROPERTIES).put(OSLC, renderJsonSchemaTypeUriEnumSpecification(OSLC_IRI));
    rendering.withObject("/" + JSON_SCHEMA_PROPERTIES).put(SKOS, renderJsonSchemaTypeUriEnumSpecification(SKOS_IRI));

    rendering.withObject("/" + JSON_SCHEMA_PROPERTIES)
        .put(RDFS_LABEL, renderJsonSchemaJsonLdDatatypeSpecification("xsd:string"));
    rendering.withObject("/" + JSON_SCHEMA_PROPERTIES)
        .put(SCHEMA_IS_BASED_ON, renderJsonSchemaJsonLdDatatypeSpecification(JSON_LD_ID));
    rendering.withObject("/" + JSON_SCHEMA_PROPERTIES)
        .put(SCHEMA_ORG_NAME, renderJsonSchemaJsonLdDatatypeSpecification("xsd:string"));
    rendering.withObject("/" + JSON_SCHEMA_PROPERTIES)
        .put(SCHEMA_ORG_DESCRIPTION, renderJsonSchemaJsonLdDatatypeSpecification("xsd:string"));
    rendering.withObject("/" + JSON_SCHEMA_PROPERTIES)
        .put(PAV_DERIVED_FROM, renderJsonSchemaJsonLdDatatypeSpecification(JSON_LD_ID));
    rendering.withObject("/" + JSON_SCHEMA_PROPERTIES)
        .put(PAV_CREATED_ON, renderJsonSchemaJsonLdDatatypeSpecification("xsd:dateTime"));
    rendering.withObject("/" + JSON_SCHEMA_PROPERTIES)
        .put(PAV_CREATED_BY, renderJsonSchemaJsonLdDatatypeSpecification(JSON_LD_ID));
    rendering.withObject("/" + JSON_SCHEMA_PROPERTIES)
        .put(PAV_LAST_UPDATED_ON, renderJsonSchemaJsonLdDatatypeSpecification("xsd:dateTime"));
    rendering.withObject("/" + JSON_SCHEMA_PROPERTIES)
        .put(OSLC_MODIFIED_BY, renderJsonSchemaJsonLdDatatypeSpecification(JSON_LD_ID));
    rendering.withObject("/" + JSON_SCHEMA_PROPERTIES)
        .put(SKOS_NOTATION, renderJsonSchemaJsonLdDatatypeSpecification("xsd:string"));

    for (var entry : templateSchemaArtifact.getChildPropertyUris().entrySet()) {
      String childKey = entry.getKey();
      URI propertyUri = entry.getValue();
      rendering.withObject("/" + JSON_SCHEMA_PROPERTIES)
          .put(childKey, renderJsonSchemaEnumSpecification(renderUri(propertyUri)));
    }

    rendering.put(JSON_SCHEMA_REQUIRED, MAPPER.createArrayNode());
    rendering.withArray(JSON_SCHEMA_REQUIRED).add(XSD);
    rendering.withArray(JSON_SCHEMA_REQUIRED).add(PAV);
    rendering.withArray(JSON_SCHEMA_REQUIRED).add(SCHEMA);
    rendering.withArray(JSON_SCHEMA_REQUIRED).add(OSLC);
    rendering.withArray(JSON_SCHEMA_REQUIRED).add(SCHEMA_IS_BASED_ON);
    rendering.withArray(JSON_SCHEMA_REQUIRED).add(SCHEMA_ORG_NAME);
    rendering.withArray(JSON_SCHEMA_REQUIRED).add(SCHEMA_ORG_DESCRIPTION);
    rendering.withArray(JSON_SCHEMA_REQUIRED).add(PAV_CREATED_ON);
    rendering.withArray(JSON_SCHEMA_REQUIRED).add(PAV_CREATED_BY);
    rendering.withArray(JSON_SCHEMA_REQUIRED).add(PAV_LAST_UPDATED_ON);
    rendering.withArray(JSON_SCHEMA_REQUIRED).add(OSLC_MODIFIED_BY);

    for (String childKey : templateSchemaArtifact.getChildPropertyUris().keySet()) {
      rendering.withArray(JSON_SCHEMA_REQUIRED).add(childKey);
    }

    if (templateSchemaArtifact.hasAttributeValueField()) {
      rendering.put(JSON_SCHEMA_ADDITIONAL_PROPERTIES,
          renderAdditionalPropertiesForAttributeValueFieldContextPropertiesJsonSchemaSpecification());
    } else {
      rendering.put(JSON_SCHEMA_ADDITIONAL_PROPERTIES, false);
    }

    return rendering;
  }


  public static ObjectNode renderElementSchemaArtifactContextPropertiesJsonSchemaSpecification(
      ElementSchemaArtifact elementSchemaArtifact) {
    ObjectNode rendering = MAPPER.createObjectNode();

    rendering.put(JSON_SCHEMA_TYPE, JSON_SCHEMA_OBJECT);

    rendering.put(JSON_SCHEMA_PROPERTIES, MAPPER.createObjectNode());

    for (var entry : elementSchemaArtifact.getChildPropertyUris().entrySet()) {
      String childKey = entry.getKey();
      URI propertyUri = entry.getValue();
      rendering.withObject("/" + JSON_SCHEMA_PROPERTIES)
          .put(childKey, renderJsonSchemaEnumSpecification(renderUri(propertyUri)));
    }

    if (!elementSchemaArtifact.getChildPropertyUris().isEmpty()) {
      rendering.put(JSON_SCHEMA_REQUIRED, MAPPER.createArrayNode());
      for (String childKey : elementSchemaArtifact.getChildPropertyUris().keySet()) {
        rendering.withArray(JSON_SCHEMA_REQUIRED).add(childKey);
      }
    }

    if (elementSchemaArtifact.hasAttributeValueField()) {
      rendering.put(JSON_SCHEMA_ADDITIONAL_PROPERTIES,
          renderAdditionalPropertiesForAttributeValueFieldContextPropertiesJsonSchemaSpecification());
    } else {
      rendering.put(JSON_SCHEMA_ADDITIONAL_PROPERTIES, false);
    }

    return rendering;
  }

}
