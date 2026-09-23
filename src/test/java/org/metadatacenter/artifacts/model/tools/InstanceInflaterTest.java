package org.metadatacenter.artifacts.model.tools;

import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.core.FieldInstanceArtifact;
import org.metadatacenter.artifacts.model.core.ElementInstanceArtifact;
import org.metadatacenter.artifacts.model.core.ElementSchemaArtifact;
import org.metadatacenter.artifacts.model.core.AttributeValueField;
import org.metadatacenter.artifacts.model.core.PageBreakField;
import org.metadatacenter.artifacts.model.core.TemplateInstanceArtifact;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.artifacts.model.core.TextField;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

  @Test public void missingMultipleFieldIsFilledToTheLowerBoundItsTemplateStates()
  {
    // A template always states a lower bound for a multi-instance child, and states the default of
    // one when the artifact names none, so an empty array fails the very template the instance
    // names. Completing an instance fills the bound the same way it fills a missing single field.
    TextField aliases = TextField.builder().withName("Aliases").withIsMultiple(true).build();
    TemplateSchemaArtifact template = TemplateSchemaArtifact.builder().withName("Study")
      .withFieldSchema(aliases).build();
    String key = template.getUi().order().get(0);

    TemplateInstanceArtifact inflated = InstanceInflater.inflate(template, sparseInstance().build());

    assertEquals(1, inflated.multiInstanceFieldInstances().get(key).size());
    assertTrue(inflated.multiInstanceFieldInstances().get(key).get(0).jsonLdValue().isEmpty(),
      "the filled occurrence should carry no value");
  }

  @Test public void aStatedLowerBoundAboveOneIsFilled()
  {
    TextField aliases = TextField.builder().withName("Aliases")
      .withIsMultiple(true).withMinItems(3).build();
    TemplateSchemaArtifact template = TemplateSchemaArtifact.builder().withName("Study")
      .withFieldSchema(aliases).build();
    String key = template.getUi().order().get(0);

    TemplateInstanceArtifact inflated = InstanceInflater.inflate(template, sparseInstance().build());

    assertEquals(3, inflated.multiInstanceFieldInstances().get(key).size());
  }

  @Test public void aLowerBoundOfZeroLeavesTheListEmpty()
  {
    TextField aliases = TextField.builder().withName("Aliases")
      .withIsMultiple(true).withMinItems(0).build();
    TemplateSchemaArtifact template = TemplateSchemaArtifact.builder().withName("Study")
      .withFieldSchema(aliases).build();
    String key = template.getUi().order().get(0);

    TemplateInstanceArtifact inflated = InstanceInflater.inflate(template, sparseInstance().build());

    assertTrue(inflated.multiInstanceFieldInstances().get(key).isEmpty(),
      "a schema that asks for none must not be given one");
  }

  @Test public void aShortListIsToppedUpAndItsValuesKept()
  {
    TextField aliases = TextField.builder().withName("Aliases")
      .withIsMultiple(true).withMinItems(3).build();
    TemplateSchemaArtifact template = TemplateSchemaArtifact.builder().withName("Study")
      .withFieldSchema(aliases).build();
    String key = template.getUi().order().get(0);

    TemplateInstanceArtifact sparse = sparseInstance()
      .withMultiInstanceFieldInstances(key, List.of(literal("A-1"))).build();

    TemplateInstanceArtifact inflated = InstanceInflater.inflate(template, sparse);

    List<FieldInstanceArtifact> occurrences = inflated.multiInstanceFieldInstances().get(key);
    assertEquals(3, occurrences.size());
    assertEquals("A-1", occurrences.get(0).jsonLdValue().orElse(null));
    assertTrue(occurrences.get(1).jsonLdValue().isEmpty());
    assertTrue(occurrences.get(2).jsonLdValue().isEmpty());
  }

  @Test public void aChildHeldInTheWrongSlotIsNamedRatherThanCollided()
  {
    // Two production instances hold a child as a list where their template declares an object,
    // and are invalid against that template for exactly that reason. Completing one used to fail
    // with "child X already present in instance", which reports a key collision inside the
    // builder rather than the disagreement that caused it.
    TextField optionSection = TextField.builder().withName("optionSection").build();
    TemplateSchemaArtifact template = TemplateSchemaArtifact.builder().withName("Study")
      .withFieldSchema(optionSection).build();

    TemplateInstanceArtifact sparse = sparseInstance()
      .withMultiInstanceFieldInstances("optionSection", List.of(literal("Next option"))).build();

    IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
      () -> InstanceInflater.inflate(template, sparse));
    assertTrue(thrown.getMessage().contains("disagree about child optionSection"),
      "the error should name the disagreement: " + thrown.getMessage());
    assertTrue(thrown.getMessage().contains("a single field")
        && thrown.getMessage().contains("a list of fields"),
      "the error should say what each side declares: " + thrown.getMessage());
  }

  @Test public void anElementHeldAsAListWhereTheTemplateDeclaresOneIsNamed()
  {
    ElementSchemaArtifact funder = ElementSchemaArtifact.builder().withName("funder")
      .withFieldSchema(TextField.builder().withName("name").build()).build();
    TemplateSchemaArtifact template = TemplateSchemaArtifact.builder().withName("Grant")
      .withElementSchema(funder).build();

    TemplateInstanceArtifact sparse = sparseInstance()
      .withMultiInstanceElementInstances("funder",
        List.of(ElementInstanceArtifact.builder().build())).build();

    IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
      () -> InstanceInflater.inflate(template, sparse));
    assertTrue(thrown.getMessage().contains("disagree about child funder"),
      "the error should name the disagreement: " + thrown.getMessage());
  }

  @Test public void aMissingRepeatedElementIsFilledToItsLowerBound()
  {
    ElementSchemaArtifact address = ElementSchemaArtifact.builder().withName("Address")
      .withIsMultiple(true)
      .withFieldSchema(TextField.builder().withName("Street").build()).build();
    TemplateSchemaArtifact template = TemplateSchemaArtifact.builder().withName("Study")
      .withElementSchema(address).build();

    TemplateInstanceArtifact inflated = InstanceInflater.inflate(template, sparseInstance().build());

    List<ElementInstanceArtifact> occurrences =
      inflated.multiInstanceElementInstances().get("Address");
    assertEquals(1, occurrences.size());
    assertTrue(occurrences.get(0).singleInstanceFieldInstances().containsKey("Street"),
      "the filled occurrence should itself be complete");
  }

  @Test public void omittedAuthorityFieldsInRepeatedNestedElementsKeepArrayShape()
  {
    var nih = org.metadatacenter.artifacts.model.core.NihGrantIdField.builder()
      .withName("NIH Grant ID Field").withIsMultiple(true).build();
    var doi = org.metadatacenter.artifacts.model.core.DoiField.builder()
      .withName("DOI Field").withIsMultiple(true).build();
    var nested = ElementSchemaArtifact.builder().withName("Nested").withIsMultiple(true)
      .withFieldSchema(nih).withFieldSchema(doi).build();
    var wrapper = ElementSchemaArtifact.builder().withName("Wrapper")
      .withElementSchema(nested).build();
    var template = TemplateSchemaArtifact.builder().withName("Authority fields")
      .withElementSchema(wrapper).build();
    var sparse = sparseInstance().withSingleInstanceElementInstance("Wrapper",
      ElementInstanceArtifact.builder().withMultiInstanceElementInstances("Nested", List.of(
        ElementInstanceArtifact.builder().build(), ElementInstanceArtifact.builder().build())).build()).build();
    var inflated = InstanceInflater.inflate(template, sparse);
    var occurrences = inflated.singleInstanceElementInstances().get("Wrapper")
      .multiInstanceElementInstances().get("Nested");
    assertEquals(2, occurrences.size());
    for (var occurrence : occurrences) {
      // Each occurrence keeps its array shape, filled to the lower bound its template states.
      assertEquals(1, occurrence.multiInstanceFieldInstances().get("NIH Grant ID Field").size());
      assertEquals(1, occurrence.multiInstanceFieldInstances().get("DOI Field").size());
      assertTrue(occurrence.singleInstanceFieldInstances().isEmpty());
    }
    assertEquals(inflated, InstanceInflater.inflate(template, inflated));
  }

  @Test public void staticFieldsNeverAcquireInstanceSlots()
  {
    TemplateSchemaArtifact template = TemplateSchemaArtifact.builder().withName("Study")
      .withFieldSchema(PageBreakField.builder().withName("Break").build()).build();
    String key = template.getUi().order().get(0);

    TemplateInstanceArtifact inflated = InstanceInflater.inflate(template, sparseInstance().build());

    assertFalse(inflated.singleInstanceFieldInstances().containsKey(key));
    assertFalse(inflated.multiInstanceFieldInstances().containsKey(key));
  }

  @Test public void missingAttributeValueFieldBecomesAnEmptyGroup()
  {
    TemplateSchemaArtifact template = TemplateSchemaArtifact.builder().withName("Study")
      .withFieldSchema(AttributeValueField.builder().withName("Attributes").build()).build();
    String key = template.getUi().order().get(0);

    TemplateInstanceArtifact inflated = InstanceInflater.inflate(template, sparseInstance().build());

    assertTrue(inflated.attributeValueFieldInstanceGroups().containsKey(key));
    assertTrue(inflated.attributeValueFieldInstanceGroups().get(key).isEmpty());
  }

  /**
   * An attribute-value field naming no attribute is written as an empty array, and an instance
   * is read without its template, so {@code JsonArtifactReader} records that array as an empty
   * multi-instance field. Inflating is where the schema settles what it is.
   */
  @Test public void emptyAttributeValueFieldReadAsAMultiInstanceFieldBecomesAGroup()
  {
    TemplateSchemaArtifact template = TemplateSchemaArtifact.builder().withName("Study")
      .withFieldSchema(AttributeValueField.builder().withName("Attributes").build()).build();
    String key = template.getUi().order().get(0);
    TemplateInstanceArtifact sparse =
      sparseInstance().withMultiInstanceFieldInstances(key, List.of()).build();

    TemplateInstanceArtifact inflated = InstanceInflater.inflate(template, sparse);

    assertTrue(inflated.attributeValueFieldInstanceGroups().containsKey(key));
    assertTrue(inflated.attributeValueFieldInstanceGroups().get(key).isEmpty());
    assertFalse(inflated.multiInstanceFieldInstances().containsKey(key));
  }

  /**
   * The withdrawal above covers a key the reader could not classify. A key carrying values is a
   * disagreement about what the child is, and silently dropping the values would lose them.
   */
  @Test public void attributeValueFieldHoldingValuesUnderAnotherKindIsReported()
  {
    TemplateSchemaArtifact template = TemplateSchemaArtifact.builder().withName("Study")
      .withFieldSchema(AttributeValueField.builder().withName("Attributes").build()).build();
    String key = template.getUi().order().get(0);
    TemplateInstanceArtifact sparse =
      sparseInstance().withMultiInstanceFieldInstances(key, List.of(literal("value"))).build();

    IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
      () -> InstanceInflater.inflate(template, sparse));

    assertTrue(thrown.getMessage().contains(key), "the message should name the child");
    assertTrue(thrown.getMessage().contains("attribute-value field"),
      "the message should say what the schema declares");
  }

  @Test public void existingAttributeValueGroupIsPreservedAndDetached()
  {
    TemplateSchemaArtifact template = TemplateSchemaArtifact.builder().withName("Study")
      .withFieldSchema(AttributeValueField.builder().withName("Attributes").build()).build();
    String key = template.getUi().order().get(0);
    LinkedHashMap<String, FieldInstanceArtifact> group = new LinkedHashMap<>();
    group.put("custom", literal("value"));
    TemplateInstanceArtifact sparse = sparseInstance().withAttributeValueFieldGroup(key, group).build();

    TemplateInstanceArtifact inflated = InstanceInflater.inflate(template, sparse);

    assertEquals("value", inflated.attributeValueFieldInstanceGroups().get(key).get("custom")
      .jsonLdValue().orElseThrow());
    assertFalse(inflated.attributeValueFieldInstanceGroups().get(key)
      == sparse.attributeValueFieldInstanceGroups().get(key));
  }

  @Test public void missingNestedElementIsRecursivelyMaterialized()
  {
    ElementSchemaArtifact element = ElementSchemaArtifact.builder().withName("Address")
      .withFieldSchema(TextField.builder().withName("Street").build()).build();
    TemplateSchemaArtifact template = TemplateSchemaArtifact.builder().withName("Study")
      .withElementSchema(element).build();
    String elementKey = template.getUi().order().get(0);
    String fieldKey = element.getUi().order().get(0);

    TemplateInstanceArtifact inflated = InstanceInflater.inflate(template, sparseInstance().build());

    ElementInstanceArtifact nested = inflated.singleInstanceElementInstances().get(elementKey);
    assertTrue(nested.singleInstanceFieldInstances().containsKey(fieldKey));
    assertTrue(nested.singleInstanceFieldInstances().get(fieldKey).jsonLdValue().isEmpty());
  }

  @Test public void existingMultipleElementsAreEachRecursivelyInflated()
  {
    ElementSchemaArtifact element = ElementSchemaArtifact.builder().withName("Address")
      .withIsMultiple(true).withFieldSchema(TextField.builder().withName("Street").build()).build();
    TemplateSchemaArtifact template = TemplateSchemaArtifact.builder().withName("Study")
      .withElementSchema(element).build();
    String elementKey = template.getUi().order().get(0);
    String fieldKey = element.getUi().order().get(0);
    ElementInstanceArtifact first = ElementInstanceArtifact.builder().build();
    ElementInstanceArtifact second = ElementInstanceArtifact.builder().build();
    TemplateInstanceArtifact sparse = sparseInstance()
      .withMultiInstanceElementInstances(elementKey, List.of(first, second)).build();

    TemplateInstanceArtifact inflated = InstanceInflater.inflate(template, sparse);

    assertEquals(2, inflated.multiInstanceElementInstances().get(elementKey).size());
    for (ElementInstanceArtifact nested : inflated.multiInstanceElementInstances().get(elementKey))
      assertTrue(nested.singleInstanceFieldInstances().containsKey(fieldKey));
  }

  /**
   * Inflation copies the property IRI each child carries, and invents none.
   *
   * <p>A template built here declares no property IRI for its children — the repository assigns those
   * when the template is uploaded — so there is nothing to copy and the instance's context stays as
   * the caller left it. What inflation must not do is overwrite a binding the instance already has,
   * which is the case this pins.
   */
  @Test public void schemaContextTakesTheBindingsThereAreWithoutOverwritingOne()
  {
    TemplateSchemaArtifact template = twoFieldTemplate();
    String first = template.getUi().order().get(0);
    String second = template.getUi().order().get(1);
    URI custom = URI.create("https://example.org/custom");
    TemplateInstanceArtifact sparse = sparseInstance().withJsonLdContextEntry(first, custom).build();

    TemplateInstanceArtifact inflated = InstanceInflater.inflate(template, sparse);

    assertEquals(custom, inflated.jsonLdContext().get(first));
    assertFalse(inflated.jsonLdContext().containsKey(second));
  }

  @Test public void inflationCanonicalizesKnownChildrenWithoutMutatingSparseInput()
  {
    TemplateSchemaArtifact template = TemplateSchemaArtifact.builder().withName("Study")
      .withFieldSchema(TextField.builder().withName("A").build())
      .withFieldSchema(TextField.builder().withName("B").build())
      .withFieldSchema(TextField.builder().withName("C").build()).build();
    List<String> order = template.getUi().order();
    TemplateInstanceArtifact sparse = sparseInstance()
      .withSingleInstanceFieldInstance(order.get(2), literal("c"))
      .withSingleInstanceFieldInstance(order.get(0), literal("a")).build();
    List<String> sparseOrder = new ArrayList<>(sparse.singleInstanceFieldInstances().keySet());

    TemplateInstanceArtifact inflated = InstanceInflater.inflate(template, sparse);

    assertEquals(order, new ArrayList<>(inflated.singleInstanceFieldInstances().keySet()));
    assertEquals(sparseOrder, new ArrayList<>(sparse.singleInstanceFieldInstances().keySet()));
    assertSame(sparse.singleInstanceFieldInstances().get(order.get(0)),
      inflated.singleInstanceFieldInstances().get(order.get(0)));
  }
}
