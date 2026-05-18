package org.metadatacenter.artifacts.model.reader;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.metadatacenter.artifacts.model.core.Version;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import static org.metadatacenter.artifacts.model.reader.JsonArtifactShapeChecks.*;
import static org.metadatacenter.artifacts.model.reader.JsonNodeReaders.*;
import static org.metadatacenter.artifacts.model.reader.JsonValueConstraintsReader.*;
import static org.metadatacenter.model.ModelNodeNames.*;

final class JsonArtifactShapeChecks {
  private JsonArtifactShapeChecks() {}

  private static final Version MODEL_VERSION = Version.fromString("1.6.0");
  private static final String JSON_SCHEMA_SCHEMA_URI = JSON_SCHEMA_SCHEMA_IRI;

  public static void checkSchemaArtifactJsonLdType(List<URI> schemaArtifactJsonLdTypes, String path) {
    if (schemaArtifactJsonLdTypes.isEmpty()) {
      throw new ArtifactParseException("Unknown object - must be a JSON-LD type or array of types", JSON_LD_TYPE, path);
    }

    if (schemaArtifactJsonLdTypes.size() != 1) {
      throw new ArtifactParseException(
          "Expecting single JSON-LD @type field for schema artifact, got " + schemaArtifactJsonLdTypes.size(),
          JSON_LD_TYPE, path);
    }

    URI schemaArtifactJsonLdType = schemaArtifactJsonLdTypes.get(0);

    if (!SCHEMA_ARTIFACT_TYPE_IRIS.contains(schemaArtifactJsonLdType.toString())) {
      throw new ArtifactParseException("Unexpected schema artifact JSON-LD @type " + schemaArtifactJsonLdType,
          JSON_LD_TYPE, path);
    }
  }


  public static void checkTemplateSchemaArtifactJsonLdType(List<URI> schemaArtifactJsonLdTypes, String path) {
    checkSchemaArtifactJsonLdType(schemaArtifactJsonLdTypes, path);

    URI schemaArtifactJsonLdType = schemaArtifactJsonLdTypes.get(0);

    if (!schemaArtifactJsonLdType.toString().equals(TEMPLATE_SCHEMA_ARTIFACT_TYPE_IRI)) {
      throw new ArtifactParseException("Unexpected template schema artifact JSON-LD @type " + schemaArtifactJsonLdType,
          JSON_LD_TYPE, path);
    }
  }


  public static void checkElementSchemaArtifactJsonLdType(List<URI> schemaArtifactJsonLdTypes, String path) {
    checkSchemaArtifactJsonLdType(schemaArtifactJsonLdTypes, path);

    URI schemaArtifactJsonLdType = schemaArtifactJsonLdTypes.get(0);

    if (!schemaArtifactJsonLdType.toString().equals(ELEMENT_SCHEMA_ARTIFACT_TYPE_IRI)) {
      throw new ArtifactParseException("Unexpected element schema artifact JSON-LD @type " + schemaArtifactJsonLdType,
          JSON_LD_TYPE, path);
    }
  }


  public static void checkFieldSchemaArtifactJsonLdType(List<URI> schemaArtifactJsonLdTypes, String path) {
    checkSchemaArtifactJsonLdType(schemaArtifactJsonLdTypes, path);

    URI schemaArtifactJsonLdType = schemaArtifactJsonLdTypes.get(0);

    if (!schemaArtifactJsonLdType.toString().equals(FIELD_SCHEMA_ARTIFACT_TYPE_IRI)
        && !schemaArtifactJsonLdType.toString().equals(STATIC_FIELD_SCHEMA_ARTIFACT_TYPE_IRI)) {
      throw new ArtifactParseException("Unexpected field schema artifact JSON-LD @type " + schemaArtifactJsonLdType,
          JSON_LD_TYPE, path);
    }
  }


  public static void checkArtifactJsonSchemaSchemaUri(ObjectNode sourceNode, String path) {
    String artifactJsonSchemaSchemaUri = readRequiredString(sourceNode, path, JSON_SCHEMA_SCHEMA);

    if (!artifactJsonSchemaSchemaUri.equals(JSON_SCHEMA_SCHEMA_URI)) {
      throw new ArtifactParseException("Expecting " + JSON_SCHEMA_SCHEMA_URI + ", got " + artifactJsonSchemaSchemaUri,
          JSON_SCHEMA_SCHEMA, path);
    }
  }


  public static void checkArtifactJsonSchemaType(ObjectNode sourceNode, String path, String expectedJsonSchemaType) {
    String jsonSchemaType = readRequiredString(sourceNode, path, JSON_SCHEMA_TYPE);

    if (!jsonSchemaType.equals(expectedJsonSchemaType)) {
      throw new ArtifactParseException(
          "Expecting artifact JSON Schema type " + expectedJsonSchemaType + ", got " + jsonSchemaType, JSON_SCHEMA_TYPE,
          path);
    }
  }


  public static void checkSchemaArtifactModelVersion(ObjectNode sourceNode, String path) {
    Optional<Version> artifactModelVersion = readModelVersion(sourceNode, path);

    // TODO Renable eventually after patching older artifacts
    //    if (artifactModelVersion.isEmpty() || !artifactModelVersion.get().equals(MODEL_VERSION))
    //      throw new ArtifactParseException("Expecting model version " + MODEL_VERSION + ", got " +
    //      artifactModelVersion,
    //        SCHEMA_ORG_SCHEMA_VERSION, path);
  }

}
