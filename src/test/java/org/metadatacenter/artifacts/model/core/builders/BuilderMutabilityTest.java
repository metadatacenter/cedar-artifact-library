package org.metadatacenter.artifacts.model.core.builders;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.core.ElementInstanceArtifact;
import org.metadatacenter.artifacts.model.core.ElementSchemaArtifact;
import org.metadatacenter.artifacts.model.core.RadioField;
import org.metadatacenter.artifacts.model.core.TemplateInstanceArtifact;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.artifacts.model.core.TextField;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Builders must stay mutable on every path to their list-valued state: a fresh builder,
 * a builder cloned from an existing artifact, and a builder whose list was set by a bulk
 * setter must all accept subsequent appends. The copy-fidelity tests can't catch
 * violations (an immutable seed clones perfectly — it only breaks when appended to), so
 * this suite appends after each seeding path.
 */
public class BuilderMutabilityTest
{
  private static final URI EXTRA_TYPE = URI.create("https://example.org/types/Extra");

  @Test public void freshBuildersAcceptAppendedJsonLdTypes()
  {
    Assertions.assertTrue(TemplateSchemaArtifact.builder()
      .withName("T").withJsonLdType(EXTRA_TYPE).build().jsonLdTypes().contains(EXTRA_TYPE));
    Assertions.assertTrue(ElementSchemaArtifact.builder()
      .withName("E").withJsonLdType(EXTRA_TYPE).build().jsonLdTypes().contains(EXTRA_TYPE));
    Assertions.assertTrue(TemplateInstanceArtifact.builder()
      .withName("I").withIsBasedOn(URI.create("https://repo.metadatacenter.org/templates/x"))
      .withJsonLdType(EXTRA_TYPE).build().jsonLdTypes().contains(EXTRA_TYPE));
    Assertions.assertTrue(ElementInstanceArtifact.builder()
      .withJsonLdType(EXTRA_TYPE).build().jsonLdTypes().contains(EXTRA_TYPE));
  }

  @Test public void bulkSettersLeaveJsonLdTypesAppendable()
  {
    // Schema artifacts must keep their canonical type IRI in the list, so the bulk set
    // restates it before the append.
    URI templateType = URI.create("https://schema.metadatacenter.org/core/Template");

    Assertions.assertTrue(TemplateSchemaArtifact.builder()
      .withName("T").withJsonLdTypes(List.of(templateType)).withJsonLdType(EXTRA_TYPE)
      .build().jsonLdTypes().containsAll(List.of(templateType, EXTRA_TYPE)));
    Assertions.assertTrue(ElementInstanceArtifact.builder()
      .withJsonLdTypes(List.of(URI.create("https://example.org/types/First")))
      .withJsonLdType(EXTRA_TYPE)
      .build().jsonLdTypes().contains(EXTRA_TYPE));
  }

  @Test public void bulkSettersDoNotAliasTheCallersList()
  {
    List<URI> callers = new ArrayList<>(
      List.of(URI.create("https://schema.metadatacenter.org/core/TemplateElement")));
    ElementSchemaArtifact element = ElementSchemaArtifact.builder()
      .withName("E").withJsonLdTypes(callers).withJsonLdType(EXTRA_TYPE).build();

    Assertions.assertEquals(1, callers.size(),
      "appending inside the builder must not mutate the caller's list");
    Assertions.assertTrue(element.jsonLdTypes().contains(EXTRA_TYPE));
  }

  @Test public void clonedBuildersAcceptAppendedJsonLdTypes()
  {
    TemplateSchemaArtifact template = TemplateSchemaArtifact.builder().withName("T").build();
    Assertions.assertTrue(TemplateSchemaArtifact.builder(template)
      .withJsonLdType(EXTRA_TYPE).build().jsonLdTypes().contains(EXTRA_TYPE));

    ElementInstanceArtifact entry = ElementInstanceArtifact.builder().build();
    Assertions.assertTrue(ElementInstanceArtifact.builder(entry)
      .withJsonLdType(EXTRA_TYPE).build().jsonLdTypes().contains(EXTRA_TYPE));
  }

  @Test public void clonedChoiceFieldBuildersAccumulateOptions()
  {
    RadioField original = RadioField.builder().withName("Sex").withOption("Male").build();

    RadioField extended = RadioField.builder(original).withOption("Female").build();

    Assertions.assertEquals(2, extended.valueConstraints().orElseThrow()
      .asTextValueConstraints().literals().size());
  }

  @Test public void clonedFieldBuildersAcceptAppendedJsonLdTypes()
  {
    TextField original = TextField.builder().withName("Note").build();
    Assertions.assertTrue(TextField.builder(original)
      .withJsonLdType(EXTRA_TYPE).build().jsonLdTypes().contains(EXTRA_TYPE));
  }
}
