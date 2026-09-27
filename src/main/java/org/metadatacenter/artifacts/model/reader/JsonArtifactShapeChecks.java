package org.metadatacenter.artifacts.model.reader;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.metadatacenter.artifacts.model.core.Version;

import java.util.Optional;
import org.metadatacenter.model.ModelNodeNames;

import java.net.URI;
import java.util.List;

import static org.metadatacenter.artifacts.model.reader.JsonArtifactShapeChecks.*;
import static org.metadatacenter.artifacts.model.reader.JsonNodeReaders.*;
import static org.metadatacenter.artifacts.model.reader.JsonValueConstraintsReader.*;
import static org.metadatacenter.model.ModelNodeNames.*;

final class JsonArtifactShapeChecks {
  private JsonArtifactShapeChecks() {}

  private static final Version MODEL_VERSION = Version.fromString(ModelNodeNames.MODEL_VERSION);
  private static final String JSON_SCHEMA_SCHEMA_URI = JSON_SCHEMA_SCHEMA_IRI;

  static void checkSchemaArtifactJsonLdType(List<URI> schemaArtifactJsonLdTypes, String path) {
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


  static void checkTemplateSchemaArtifactJsonLdType(List<URI> schemaArtifactJsonLdTypes, String path) {
    checkSchemaArtifactJsonLdType(schemaArtifactJsonLdTypes, path);

    URI schemaArtifactJsonLdType = schemaArtifactJsonLdTypes.get(0);

    if (!schemaArtifactJsonLdType.toString().equals(TEMPLATE_SCHEMA_ARTIFACT_TYPE_IRI)) {
      throw new ArtifactParseException("Unexpected template schema artifact JSON-LD @type " + schemaArtifactJsonLdType,
          JSON_LD_TYPE, path);
    }
  }


  static void checkElementSchemaArtifactJsonLdType(List<URI> schemaArtifactJsonLdTypes, String path) {
    checkSchemaArtifactJsonLdType(schemaArtifactJsonLdTypes, path);

    URI schemaArtifactJsonLdType = schemaArtifactJsonLdTypes.get(0);

    if (!schemaArtifactJsonLdType.toString().equals(ELEMENT_SCHEMA_ARTIFACT_TYPE_IRI)) {
      throw new ArtifactParseException("Unexpected element schema artifact JSON-LD @type " + schemaArtifactJsonLdType,
          JSON_LD_TYPE, path);
    }
  }


  static void checkFieldSchemaArtifactJsonLdType(List<URI> schemaArtifactJsonLdTypes, String path) {
    checkSchemaArtifactJsonLdType(schemaArtifactJsonLdTypes, path);

    URI schemaArtifactJsonLdType = schemaArtifactJsonLdTypes.get(0);

    if (!schemaArtifactJsonLdType.toString().equals(FIELD_SCHEMA_ARTIFACT_TYPE_IRI)
        && !schemaArtifactJsonLdType.toString().equals(STATIC_FIELD_SCHEMA_ARTIFACT_TYPE_IRI)) {
      throw new ArtifactParseException("Unexpected field schema artifact JSON-LD @type " + schemaArtifactJsonLdType,
          JSON_LD_TYPE, path);
    }
  }


  static void checkArtifactJsonSchemaSchemaUri(ObjectNode sourceNode, String path) {
    checkArtifactJsonSchemaSchemaUri(sourceNode, path, true);
  }


  static void checkArtifactJsonSchemaSchemaUri(ObjectNode sourceNode, String path, boolean required) {
    if (!required && !sourceNode.has(JSON_SCHEMA_SCHEMA)) {
      return;
    }

    String artifactJsonSchemaSchemaUri = readRequiredString(sourceNode, path, JSON_SCHEMA_SCHEMA);

    if (!artifactJsonSchemaSchemaUri.equals(JSON_SCHEMA_SCHEMA_URI)) {
      throw new ArtifactParseException("Expecting " + JSON_SCHEMA_SCHEMA_URI + ", got " + artifactJsonSchemaSchemaUri,
          JSON_SCHEMA_SCHEMA, path);
    }
  }


  static void checkArtifactJsonSchemaType(ObjectNode sourceNode, String path, String expectedJsonSchemaType) {
    String jsonSchemaType = readRequiredString(sourceNode, path, JSON_SCHEMA_TYPE);

    if (!jsonSchemaType.equals(expectedJsonSchemaType)) {
      throw new ArtifactParseException(
          "Expecting artifact JSON Schema type " + expectedJsonSchemaType + ", got " + jsonSchemaType, JSON_SCHEMA_TYPE,
          path);
    }
  }


  /**
   * Refuses an artifact that does not declare the model this library implements.
   *
   * <p>An artifact's {@code schema:schemaVersion} asserts which model it conforms to, so reading one
   * that names another model, or names none, is reading a document this library cannot speak for.
   * Absence is the harder half and refused on the same terms: an artifact that never declared a
   * version is not thereby conformant.
   *
   * <p>The comparison was disabled for years because enabling it would have refused stored
   * artifacts written against an earlier model. That is no longer so. A walk of every schema
   * artifact a deployment serves found {@code schema:schemaVersion} declared on all of them and
   * holding one value throughout, the current one, so nothing is taken out of reach by asking.
   */
  static void checkSchemaArtifactModelVersion(ObjectNode sourceNode, String path) {
    Optional<Version> artifactModelVersion = readModelVersion(sourceNode, path);

    if (artifactModelVersion.isEmpty() || !artifactModelVersion.get().equals(MODEL_VERSION))
      throw new ArtifactParseException(
        "Expecting model version " + MODEL_VERSION + ", got " + artifactModelVersion.map(Version::toString)
          .orElse("none"), SCHEMA_ORG_SCHEMA_VERSION, path);
  }

}
