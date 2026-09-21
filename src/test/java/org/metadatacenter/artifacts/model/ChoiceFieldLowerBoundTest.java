package org.metadatacenter.artifacts.model;

import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.core.CheckboxField;
import org.metadatacenter.artifacts.model.core.ChildSchemaArtifact;
import org.metadatacenter.artifacts.model.core.ListField;
import org.metadatacenter.artifacts.model.core.TemplateInstanceArtifact;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.artifacts.model.core.TextField;
import org.metadatacenter.artifacts.model.tools.InstanceInflater;

import java.net.URI;

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
}
