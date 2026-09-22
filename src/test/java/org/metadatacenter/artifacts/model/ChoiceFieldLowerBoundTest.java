package org.metadatacenter.artifacts.model;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.core.CheckboxField;
import org.metadatacenter.artifacts.model.core.ChildSchemaArtifact;
import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifacts.model.core.ListField;
import org.metadatacenter.artifacts.model.core.TemplateInstanceArtifact;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.artifacts.model.core.TextField;
import org.metadatacenter.artifacts.model.reader.YamlArtifactReader;
import org.metadatacenter.artifacts.model.renderer.JsonArtifactRenderer;
import org.metadatacenter.artifacts.model.renderer.YamlArtifactRenderer;
import org.metadatacenter.artifacts.model.tools.InstanceInflater;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A field that is multiple because of what it is takes no occurrence it was not given.
 *
 * <p>A child an author marked multiple takes one occurrence when its template states no lower
 * bound, because that is what the system stores and what the meta-schema demands. A checkbox and a
 * multi-select list are different: their multiplicity comes from the field type and from
 * {@code multipleChoice} rather than from anyone's declaration, and holding nothing is a state a
 * reader can mean — nothing was ticked. Giving one of those an occupant invents a selection nobody
 * made, and the occupant reaches a host as a null entry in the value array, which is not a thing
 * any consumer should be handed.
 *
 * <p>An explicitly stated bound is still honoured, here as everywhere: the default only decides
 * what an unstated bound means.
 *
 * <p>The rendered template has to say the same thing. A JSON Schema lower bound is what a stored
 * document is held to, so a template demanding one occurrence of a field an instance of it leaves
 * empty is a template no instance of it satisfies.
 */
public class ChoiceFieldLowerBoundTest
{
  private static TemplateInstanceArtifact.Builder sparse()
  {
    return TemplateInstanceArtifact.builder().withName("Instance")
      .withIsBasedOn(URI.create("https://repo.metadatacenter.org/templates/t1"));
  }

  @Test public void anUntickedCheckboxHoldsNothing()
  {
    CheckboxField checkbox = CheckboxField.builder().withName("Options")
      .withOption("A").withOption("B").build();
    TemplateSchemaArtifact template = TemplateSchemaArtifact.builder().withName("Study")
      .withFieldSchema(checkbox).build();

    TemplateInstanceArtifact inflated = InstanceInflater.inflate(template, sparse().build());

    assertTrue(inflated.multiInstanceFieldInstances().containsKey("Options"),
      "the array itself must be there, whatever it holds");
    assertEquals(0, inflated.multiInstanceFieldInstances().get("Options").size(),
      "nothing was ticked, so the list holds nothing");
  }

  @Test public void aMultiSelectListHoldsNothingUntilSomethingIsChosen()
  {
    ListField list = ListField.builder().withName("Kinds")
      .withOption("A").withOption("B").withMultipleChoice(true).build();
    TemplateSchemaArtifact template = TemplateSchemaArtifact.builder().withName("Study")
      .withFieldSchema(list).build();

    TemplateInstanceArtifact inflated = InstanceInflater.inflate(template, sparse().build());

    assertEquals(0, inflated.multiInstanceFieldInstances().get("Kinds").size());
  }

  @Test public void aCheckboxStillHonoursABoundItsTemplateStates()
  {
    CheckboxField checkbox = CheckboxField.builder().withName("Options")
      .withOption("A").withMinItems(2).build();
    TemplateSchemaArtifact template = TemplateSchemaArtifact.builder().withName("Study")
      .withFieldSchema(checkbox).build();

    TemplateInstanceArtifact inflated = InstanceInflater.inflate(template, sparse().build());

    assertEquals(2, inflated.multiInstanceFieldInstances().get("Options").size(),
      "a stated bound decides, here as everywhere");
  }

  @Test public void anAuthorDeclaredMultipleChildStillTakesOne()
  {
    TextField aliases = TextField.builder().withName("Aliases").withIsMultiple(true).build();
    TemplateSchemaArtifact template = TemplateSchemaArtifact.builder().withName("Study")
      .withFieldSchema(aliases).build();

    TemplateInstanceArtifact inflated = InstanceInflater.inflate(template, sparse().build());

    assertEquals(ChildSchemaArtifact.DEFAULT_MIN_ITEMS,
      inflated.multiInstanceFieldInstances().get("Aliases").size(),
      "the rule for a child someone marked multiple is unchanged");
  }

  @Test public void aSingleChoiceListIsNotMultipleAtAll()
  {
    ListField list = ListField.builder().withName("Kind").withOption("A").build();
    TemplateSchemaArtifact template = TemplateSchemaArtifact.builder().withName("Study")
      .withFieldSchema(list).build();

    TemplateInstanceArtifact inflated = InstanceInflater.inflate(template, sparse().build());

    assertTrue(inflated.singleInstanceFieldInstances().containsKey("Kind"),
      "a single-choice list is one field, not a list of them");
  }

  private static ObjectNode renderedProperty(TemplateSchemaArtifact template, String key)
  {
    return (ObjectNode) new JsonArtifactRenderer().renderTemplateSchemaArtifact(template)
      .get("properties").get(key);
  }

  private static TemplateSchemaArtifact templateHolding(FieldSchemaArtifact field)
  {
    return TemplateSchemaArtifact.builder().withName("Study").withFieldSchema(field).build();
  }

  @Test public void aRenderedCheckboxDemandsNoOccupant()
  {
    CheckboxField checkbox = CheckboxField.builder().withName("Options").withOption("A").build();

    assertEquals(0, renderedProperty(templateHolding(checkbox), "Options").get("minItems").asInt(),
      "a template that states no bound must not demand a selection nobody made");
  }

  @Test public void aRenderedMultiSelectListDemandsNoOccupant()
  {
    ListField list = ListField.builder().withName("Kinds").withOption("A").withMultipleChoice(true).build();

    assertEquals(0, renderedProperty(templateHolding(list), "Kinds").get("minItems").asInt());
  }

  @Test public void aRenderedAuthorDeclaredMultipleChildDemandsOne()
  {
    TextField aliases = TextField.builder().withName("Aliases").withIsMultiple(true).build();

    assertEquals(ChildSchemaArtifact.DEFAULT_MIN_ITEMS,
      renderedProperty(templateHolding(aliases), "Aliases").get("minItems").asInt(),
      "the rule for a child someone marked multiple is unchanged");
  }

  @Test public void aRenderedTemplateDemandsWhatFillingItProduces()
  {
    CheckboxField checkbox = CheckboxField.builder().withName("Options").withOption("A").build();
    TemplateSchemaArtifact template = templateHolding(checkbox);

    int demanded = renderedProperty(template, "Options").get("minItems").asInt();
    int produced = InstanceInflater.inflate(template, sparse().build())
      .multiInstanceFieldInstances().get("Options").size();

    assertTrue(produced >= demanded,
      "an instance the library fills must satisfy the template the library writes");
  }

  @Test public void aCheckboxBoundOfOneSurvivesYaml()
  {
    CheckboxField checkbox = CheckboxField.builder().withName("Options")
      .withOption("A").withMinItems(1).build();

    LinkedHashMap<String, Object> yaml =
      new YamlArtifactRenderer(false).renderTemplateSchemaArtifact(templateHolding(checkbox));
    TemplateSchemaArtifact read = new YamlArtifactReader().readTemplateSchemaArtifact(yaml);

    assertEquals(Optional.of(1), read.getFieldSchemaArtifact("Options").minItems(),
      "a bound an author chose is the author's even when it matches another kind's default: " + yaml);
  }
}
