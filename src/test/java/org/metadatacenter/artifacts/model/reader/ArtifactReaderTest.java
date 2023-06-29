package org.metadatacenter.artifacts.model.reader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Before;
import org.junit.Test;
import org.metadatacenter.model.ModelNodeNames;

import java.net.URI;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ArtifactReaderTest {
  private ArtifactReader artifactReader;
  private ObjectMapper mapper;

  @Before
  public void setup() {
    mapper = new ObjectMapper();
    artifactReader = new ArtifactReader(mapper);
  }

  @Test
  public void getChildPropertyURIs_ValidInput_ReturnsMap() {
    String path = "/test/path";
    ObjectNode objectNode = mapper.createObjectNode();

    objectNode.put(ModelNodeNames.JSON_SCHEMA_PROPERTIES, mapper.createObjectNode());
    objectNode.with(ModelNodeNames.JSON_SCHEMA_PROPERTIES).set(ModelNodeNames.JSON_LD_CONTEXT, mapper.createObjectNode());
    objectNode.with(ModelNodeNames.JSON_SCHEMA_PROPERTIES).with(ModelNodeNames.JSON_LD_CONTEXT).put("field1", mapper.createObjectNode());
    objectNode.with(ModelNodeNames.JSON_SCHEMA_PROPERTIES).with(ModelNodeNames.JSON_LD_CONTEXT).put("field2", mapper.createObjectNode());
    objectNode.with(ModelNodeNames.JSON_SCHEMA_PROPERTIES).with(ModelNodeNames.JSON_LD_CONTEXT).with("field1").put(ModelNodeNames.JSON_SCHEMA_ENUM, mapper.createArrayNode());
    objectNode.with(ModelNodeNames.JSON_SCHEMA_PROPERTIES).with(ModelNodeNames.JSON_LD_CONTEXT).with("field1").withArray(ModelNodeNames.JSON_SCHEMA_ENUM).add("https://example.com/enum1");
    objectNode.with(ModelNodeNames.JSON_SCHEMA_PROPERTIES).with(ModelNodeNames.JSON_LD_CONTEXT).with("field2").put(ModelNodeNames.JSON_SCHEMA_ENUM, mapper.createArrayNode());
    objectNode.with(ModelNodeNames.JSON_SCHEMA_PROPERTIES).with(ModelNodeNames.JSON_LD_CONTEXT).with("field2").withArray(ModelNodeNames.JSON_SCHEMA_ENUM).add("https://example.com/enum2");

    Map<String, URI> result = artifactReader.getChildPropertyURIs(objectNode, path);

    assertEquals(2, result.size());
    assertTrue(result.containsKey("field1"));
    assertTrue(result.containsKey("field2"));
    assertEquals("https://example.com/enum1", result.get("field1").toString());
    assertEquals("https://example.com/enum2", result.get("field2").toString());
  }
}