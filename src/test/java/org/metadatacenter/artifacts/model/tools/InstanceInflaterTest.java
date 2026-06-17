package org.metadatacenter.artifacts.model.tools;

import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.core.FieldInstanceArtifact;
import org.metadatacenter.artifacts.model.core.TemplateInstanceArtifact;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.artifacts.model.core.TextField;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InstanceInflaterTest
{
  private static FieldInstanceArtifact literal(String value)
  {
    return FieldInstanceArtifact.create(Collections.emptyList(), Optional.empty(),
      Optional.of(value), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
  }

  private static TemplateSchemaArtifact twoFieldTemplate()
  {
    TextField studyId = TextField.builder().withName("Study ID").build();
    TextField notes = TextField.builder().withName("Notes").build();
    return TemplateSchemaArtifact.builder().withName("Study")
      .withFieldSchema(studyId).withFieldSchema(notes).build();
  }

  private static TemplateInstanceArtifact.Builder sparseInstance()
  {
    return TemplateInstanceArtifact.builder().withName("Study Instance")
      .withIsBasedOn(URI.create("https://repo.metadatacenter.org/templates/abc"));
  }

  @Test public void fillsEveryFieldChildOnAnEmptyInstance()
  {
    TemplateSchemaArtifact template = twoFieldTemplate();

    TemplateInstanceArtifact inflated = InstanceInflater.inflate(template, sparseInstance().build());

    // Every non-static field child now has an (empty) single-instance slot, even though the
    // sparse instance carried none — the JSON form requires every template property present.
    for (String childKey : template.getUi().order())
      assertTrue(inflated.singleInstanceFieldInstances().containsKey(childKey),
        "inflated instance is missing field '" + childKey + "'");
    assertEquals(template.getUi().order().size(), inflated.singleInstanceFieldInstances().size());
  }

  @Test public void preservesPresentValuesWhileFillingTheRest()
  {
    TemplateSchemaArtifact template = twoFieldTemplate();
    List<String> order = template.getUi().order();
    String first = order.get(0), second = order.get(1);

    TemplateInstanceArtifact sparse = sparseInstance()
      .withSingleInstanceFieldInstance(first, literal("S-1")).build();

    TemplateInstanceArtifact inflated = InstanceInflater.inflate(template, sparse);

    // The value already set is preserved verbatim...
    assertEquals("S-1", inflated.singleInstanceFieldInstances().get(first).jsonLdValue().orElse(null));
    // ...and the omitted field is materialized as an empty (value-less) slot.
    assertTrue(inflated.singleInstanceFieldInstances().containsKey(second));
    assertTrue(inflated.singleInstanceFieldInstances().get(second).jsonLdValue().isEmpty(),
      "the omitted field should be filled empty, not given a value");
  }
}
