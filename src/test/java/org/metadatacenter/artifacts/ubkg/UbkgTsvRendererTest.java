package org.metadatacenter.artifacts.ubkg;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UbkgTsvRendererTest
{
  private static final URI STUDY = URI.create("https://repo.metadatacenter.org/templates/study");
  private static final URI TITLE = URI.create("https://repo.metadatacenter.org/template-fields/title");
  private static final URI COUNT = URI.create("https://repo.metadatacenter.org/template-fields/count");
  private static final URI STRING = URI.create("http://www.w3.org/2001/XMLSchema#string");

  @Test public void eachNodeIsARowUnderTheUbkgNodeColumns()
  {
    UbkgRendering rendering = UbkgRendering.builder().withNode(STUDY, "Study", "1.2.3")
      .withNode(TITLE, "Title", "The study title").build();

    // The namespace, synonym and cross-reference columns are written empty.
    assertEquals("node_id\tnode_namespace\tnode_label\tnode_definition\tnode_synonyms\tnode_dbxrefs\n"
        + STUDY + "\t\tStudy\t1.2.3\t\t\n"
        + TITLE + "\t\tTitle\tThe study title\t\t\n",
      new UbkgTsvRenderer(rendering).renderNodes().toString());
  }

  @Test public void aTabOrLineBreakInALabelOrDefinitionBecomesASpace()
  {
    UbkgRendering rendering = UbkgRendering.builder()
      .withNode(TITLE, "Study\ttitle", "The title\r\nof the study,\nin\t\tfull\rform").build();

    // A tab would start a new column and a line break a new row. A run of them, such as a Windows line
    // break, becomes one space.
    assertEquals("node_id\tnode_namespace\tnode_label\tnode_definition\tnode_synonyms\tnode_dbxrefs\n"
        + TITLE + "\t\tStudy title\tThe title of the study, in full form\t\t\n",
      new UbkgTsvRenderer(rendering).renderNodes().toString());
  }

  @Test public void eachObjectOfAnEdgeIsARowOfItsOwn()
  {
    UbkgRendering rendering = UbkgRendering.builder()
      .withEdge(STUDY, "has_field", TITLE)
      .withEdge(STUDY, "has_field", COUNT)
      .withEdge(STUDY, "has_field", TITLE)
      .withEdge(TITLE, "has_datatype", STRING).build();

    List<String> lines = List.of(new UbkgTsvRenderer(rendering).renderEdges().toString().split("\n", -1));

    assertEquals("subject\tpredicate\tobject", lines.get(0));
    // An object repeated for one subject and predicate is written once; the order within a subject is
    // not fixed.
    assertEquals(5, lines.size());
    assertEquals("", lines.get(4));
    assertEquals(Set.of(STUDY + "\thas_field\t" + TITLE, STUDY + "\thas_field\t" + COUNT,
      TITLE + "\thas_datatype\t" + STRING), Set.copyOf(lines.subList(1, 4)));
  }
}
