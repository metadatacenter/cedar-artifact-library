package org.metadatacenter.artifacts.model.reader;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.metadatacenter.artifacts.model.core.Version;
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
   * Rejects a model version that is not a version, and accepts every version that is.
   *
   * <p>The name overstates what this does, so read it here rather than infer it. {@code
   * readModelVersion} throws on a value it cannot parse, and that is the whole of the enforcement:
   * the comparison against {@link #MODEL_VERSION} below is commented out, so an artifact declaring
   * any well-formed version is accepted whatever that version is.
   *
   * <p>Enabling the comparison would refuse every stored artifact written against an earlier model,
   * which is why it was disabled rather than fixed. The artifacts have to be patched first —
   * {@code cedar-development/ops/cedar_artifact_patch.py} is the tool for that — and until they are,
   * turning this on takes the deployment's existing content out of reach.
   *
   * <p>{@code YamlArtifactReader} declares a method of this name that does compare the value, so
   * one artifact can be accepted as JSON and refused as YAML. YAML has no corpus of older documents
   * to patch, which is why the two differ.
   */
  static void checkSchemaArtifactModelVersion(ObjectNode sourceNode, String path) {
    readModelVersion(sourceNode, path);

    // Re-enable once older artifacts carry the current model version; see the note above.
    //    if (artifactModelVersion.isEmpty() || !artifactModelVersion.get().equals(MODEL_VERSION))
    //      throw new ArtifactParseException("Expecting model version " + MODEL_VERSION + ", got " +
    //      artifactModelVersion,
    //        SCHEMA_ORG_SCHEMA_VERSION, path);
  }

}
