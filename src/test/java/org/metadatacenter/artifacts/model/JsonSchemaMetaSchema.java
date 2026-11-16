package org.metadatacenter.artifacts.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SchemaLocation;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;

import java.util.Set;

/**
 * The draft-04 meta-schema, which every JSON Schema this library writes must satisfy. Building a schema from
 * a document checks almost nothing, so a test that a rendering is a JSON Schema validates it against this.
 */
public final class JsonSchemaMetaSchema
{
  private static final JsonSchema DRAFT_04 = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4)
    .getSchema(SchemaLocation.of("http://json-schema.org/draft-04/schema#"));

  private JsonSchemaMetaSchema() {}

  /** What makes the document not a draft-04 JSON Schema; empty when it is one. */
  public static Set<ValidationMessage> violations(JsonNode schema)
  {
    return DRAFT_04.validate(schema);
  }
}
