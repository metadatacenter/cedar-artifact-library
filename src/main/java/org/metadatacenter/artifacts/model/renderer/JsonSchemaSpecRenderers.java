package org.metadatacenter.artifacts.model.renderer;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.metadatacenter.artifacts.model.core.fields.XsdDatatype;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Optional;

import static org.metadatacenter.artifacts.model.renderer.JsonArtifactRenderer.DATETIME_FORMATTER;
import static org.metadatacenter.artifacts.model.renderer.JsonArtifactRenderer.MAPPER;
import static org.metadatacenter.artifacts.model.renderer.JsonLdContextRenderers.*;
import static org.metadatacenter.artifacts.model.renderer.JsonSchemaArtifactPropertyRenderers.*;
import static org.metadatacenter.artifacts.model.renderer.JsonSchemaSpecRenderers.*;
import static org.metadatacenter.model.ModelNodeNames.*;
import static org.metadatacenter.model.ModelNodeValues.XSD_STRING;

final class JsonSchemaSpecRenderers {
  private JsonSchemaSpecRenderers() {}

  public static ObjectNode renderJsonSchemaTypeUriEnumSpecification(String uri) {
    ObjectNode rendering = MAPPER.createObjectNode();

    rendering.put(JSON_SCHEMA_TYPE, JSON_SCHEMA_STRING);
    rendering.put(JSON_SCHEMA_FORMAT, JSON_SCHEMA_FORMAT_URI);
    rendering.put(JSON_SCHEMA_ENUM, MAPPER.createArrayNode());
    rendering.withArray(JSON_SCHEMA_ENUM).add(uri);

    return rendering;
  }


  public static ObjectNode renderJsonSchemaEnumSpecification(String value) {
    ObjectNode rendering = MAPPER.createObjectNode();

    rendering.put(JSON_SCHEMA_ENUM, MAPPER.createArrayNode());
    rendering.withArray(JSON_SCHEMA_ENUM).add(value);

    return rendering;
  }


  public static ObjectNode renderJsonSchemaJsonLdDatatypeSpecification(String datatype) {
    ObjectNode rendering = MAPPER.createObjectNode();

    rendering.put(JSON_SCHEMA_TYPE, JSON_SCHEMA_OBJECT);
    rendering.put(JSON_SCHEMA_PROPERTIES, MAPPER.createObjectNode());
    rendering.withObject("/" + JSON_SCHEMA_PROPERTIES).put(JSON_LD_TYPE, MAPPER.createObjectNode());
    rendering.withObject("/" + JSON_SCHEMA_PROPERTIES).withObject("/" + JSON_LD_TYPE)
        .put(JSON_SCHEMA_TYPE, JSON_SCHEMA_STRING);
    rendering.withObject("/" + JSON_SCHEMA_PROPERTIES).withObject("/" + JSON_LD_TYPE)
        .put(JSON_SCHEMA_ENUM, MAPPER.createArrayNode());
    rendering.withObject("/" + JSON_SCHEMA_PROPERTIES).withObject("/" + JSON_LD_TYPE).withArray(JSON_SCHEMA_ENUM)
        .add(datatype);

    return rendering;
  }


  public static ObjectNode renderLiteralFieldArtifactPropertiesJsonSchemaSpecification() {
    ObjectNode rendering = MAPPER.createObjectNode();

    rendering.put(JSON_LD_TYPE, renderUriJsonSchemaTypeSpecification());
    rendering.put(RDFS_LABEL, renderStringOrNullJsonSchemaTypeSpecification());
    rendering.put(JSON_LD_VALUE, renderStringOrNullJsonSchemaTypeSpecification());
    rendering.put(JSON_LD_LANGUAGE, renderStringOrNullJsonSchemaTypeSpecification(1));

    return rendering;
  }


  public static ObjectNode renderIRIFieldArtifactPropertiesJsonSchemaSpecification() {
    ObjectNode rendering = MAPPER.createObjectNode();

    rendering.put(JSON_LD_TYPE, renderUriJsonSchemaTypeSpecification());
    rendering.put(JSON_LD_ID, renderUriJsonSchemaTypeSpecification());
    rendering.put(RDFS_LABEL, renderStringOrNullJsonSchemaTypeSpecification());
    rendering.put(SKOS_NOTATION, renderStringOrNullJsonSchemaTypeSpecification());

    return rendering;
  }


  public static ObjectNode renderStringJsonSchemaTypeSpecification(int minLength) {
    ObjectNode rendering = MAPPER.createObjectNode();

    rendering.put(JSON_SCHEMA_TYPE, JSON_SCHEMA_STRING);

    if (minLength > 0) {
      rendering.put(JSON_SCHEMA_MIN_LENGTH, minLength);
    }

    return rendering;
  }


  public static ObjectNode renderStringOrNullJsonSchemaTypeSpecification() {
    ObjectNode rendering = MAPPER.createObjectNode();

    rendering.put(JSON_SCHEMA_TYPE, MAPPER.createArrayNode());
    rendering.withArray(JSON_SCHEMA_TYPE).add(JSON_SCHEMA_STRING);
    rendering.withArray(JSON_SCHEMA_TYPE).add(JSON_SCHEMA_NULL);

    return rendering;
  }


  public static ObjectNode renderStringOrNullJsonSchemaTypeSpecification(int minLength) {
    ObjectNode rendering = MAPPER.createObjectNode();

    rendering.put(JSON_SCHEMA_TYPE, MAPPER.createArrayNode());
    rendering.withArray(JSON_SCHEMA_TYPE).add(JSON_SCHEMA_STRING);
    rendering.withArray(JSON_SCHEMA_TYPE).add(JSON_SCHEMA_NULL);

    if (minLength > 0) {
      rendering.put(JSON_SCHEMA_MIN_LENGTH, minLength);
    }

    return rendering;
  }


  public static ObjectNode renderUriOrNullJsonSchemaTypeSpecification() {
    ObjectNode rendering = MAPPER.createObjectNode();

    rendering.put(JSON_SCHEMA_TYPE, MAPPER.createArrayNode());
    rendering.withArray(JSON_SCHEMA_TYPE).add(JSON_SCHEMA_STRING);
    rendering.withArray(JSON_SCHEMA_TYPE).add(JSON_SCHEMA_NULL);

    rendering.put(JSON_SCHEMA_FORMAT, JSON_SCHEMA_FORMAT_URI);

    return rendering;
  }


  public static ObjectNode renderXsdStringJsonLdSpecification() {
    ObjectNode rendering = MAPPER.createObjectNode();

    rendering.put(JSON_LD_TYPE, XSD_STRING);

    return rendering;
  }


  public static ObjectNode renderXsdDateTimeJsonLdSpecification() {
    ObjectNode rendering = MAPPER.createObjectNode();

    rendering.put(JSON_LD_TYPE, XSD_DATETIME);

    return rendering;
  }


  public static ObjectNode renderIriJsonLdSpecification() {
    ObjectNode rendering = MAPPER.createObjectNode();

    rendering.put(JSON_LD_TYPE, JSON_LD_ID);

    return rendering;
  }


  public static ObjectNode renderDateTimeOrNullJsonSchemaTypeSpecification() {
    ObjectNode rendering = MAPPER.createObjectNode();

    rendering.put(JSON_SCHEMA_TYPE, MAPPER.createArrayNode());
    rendering.withArray(JSON_SCHEMA_TYPE).add(JSON_SCHEMA_STRING);
    rendering.withArray(JSON_SCHEMA_TYPE).add(JSON_SCHEMA_NULL);
    rendering.put(JSON_SCHEMA_FORMAT, JSON_SCHEMA_FORMAT_DATE_TIME);

    return rendering;
  }


  public static ObjectNode renderUriJsonSchemaTypeSpecification() {
    ObjectNode rendering = MAPPER.createObjectNode();

    rendering.put(JSON_SCHEMA_TYPE, JSON_SCHEMA_STRING);
    rendering.put(JSON_SCHEMA_FORMAT, JSON_SCHEMA_FORMAT_URI);

    return rendering;
  }


  public static ObjectNode renderUriOrUriArrayJsonSchemaTypeSpecification(int minItems, boolean uniqueItems) {
    ObjectNode rendering = MAPPER.createObjectNode();

    rendering.put(JSON_SCHEMA_ONE_OF, MAPPER.createArrayNode());
    rendering.withArray(JSON_SCHEMA_ONE_OF).add(renderUriJsonSchemaTypeSpecification());
    rendering.withArray(JSON_SCHEMA_ONE_OF).add(renderUriArrayJsonSchemaTypeSpecification(minItems, uniqueItems));

    return rendering;
  }


  public static ObjectNode renderUriOrUriArrayJsonSchemaTypeEnumSpecification(int minItems, boolean uniqueItems, URI uri) {
    ObjectNode rendering = MAPPER.createObjectNode();

    rendering.put(JSON_SCHEMA_ONE_OF, MAPPER.createArrayNode());
    rendering.withArray(JSON_SCHEMA_ONE_OF).add(renderJsonSchemaTypeUriEnumSpecification(uri.toString()));
    rendering.withArray(JSON_SCHEMA_ONE_OF)
        .add(renderUriArrayJsonSchemaTypeEnumSpecification(minItems, uniqueItems, uri));

    return rendering;
  }


  public static ObjectNode renderUriArrayJsonSchemaTypeSpecification(int minItems, boolean uniqueItems) {
    ObjectNode rendering = MAPPER.createObjectNode();

    rendering.put(JSON_SCHEMA_TYPE, JSON_SCHEMA_ARRAY);
    rendering.put(JSON_SCHEMA_MIN_ITEMS, minItems);
    rendering.put(JSON_SCHEMA_ITEMS, MAPPER.createObjectNode());
    rendering.withObject("/" + JSON_SCHEMA_ITEMS).put(JSON_SCHEMA_TYPE, JSON_SCHEMA_STRING);
    rendering.withObject("/" + JSON_SCHEMA_ITEMS).put(JSON_SCHEMA_FORMAT, JSON_SCHEMA_FORMAT_URI);
    rendering.put(JSON_SCHEMA_UNIQUE_ITEMS, uniqueItems);

    return rendering;
  }


  public static ObjectNode renderUriArrayJsonSchemaTypeEnumSpecification(int minItems, boolean uniqueItems, URI uri) {
    ObjectNode rendering = MAPPER.createObjectNode();

    rendering.put(JSON_SCHEMA_TYPE, JSON_SCHEMA_ARRAY);
    rendering.put(JSON_SCHEMA_MIN_ITEMS, minItems);
    rendering.put(JSON_SCHEMA_ITEMS, MAPPER.createObjectNode());
    rendering.withObject("/" + JSON_SCHEMA_ITEMS).put(JSON_SCHEMA_TYPE, JSON_SCHEMA_STRING);
    rendering.withObject("/" + JSON_SCHEMA_ITEMS).put(JSON_SCHEMA_FORMAT, JSON_SCHEMA_FORMAT_URI);
    rendering.withObject("/" + JSON_SCHEMA_ITEMS).put(JSON_SCHEMA_ENUM, MAPPER.createArrayNode());
    rendering.withObject("/" + JSON_SCHEMA_ITEMS).withArray(JSON_SCHEMA_ENUM).add(uri.toString());
    rendering.put(JSON_SCHEMA_UNIQUE_ITEMS, uniqueItems);

    return rendering;
  }


  public static ObjectNode renderJsonSchemaArrayWrapperSpecification(ObjectNode wrappedRendering, Optional<Integer> minItems,
                                                               Optional<Integer> maxItems) {
    ObjectNode wrapperRendering = MAPPER.createObjectNode();

    wrapperRendering.put(JSON_SCHEMA_TYPE, JSON_SCHEMA_ARRAY);

    if (minItems.isPresent()) {
      wrapperRendering.put(JSON_SCHEMA_MIN_ITEMS, minItems.get());
    } else {
      wrapperRendering.put(JSON_SCHEMA_MIN_ITEMS, 0);
    }

    if (maxItems.isPresent()) {
      wrapperRendering.put(JSON_SCHEMA_MAX_ITEMS, maxItems.get());
    }

    wrapperRendering.put(JSON_SCHEMA_ITEMS, wrappedRendering);

    return wrapperRendering;
  }


  public static String renderOffsetDateTime(OffsetDateTime offsetDateTime) {
    return offsetDateTime.format(DATETIME_FORMATTER);
  }


  public static String renderUri(URI uri) {
    return uri.toString();
  }


  public static String renderPossiblyXsdPrefixedUri(URI uri) {
    if (XsdDatatype.isKnownXsdDatatypeUri(uri)) {
      return XsdDatatype.fromUri(uri).getText(); // We render the prefixed form of XSD datatypes
    } else {
      return uri.toString();
    }
  }

}
