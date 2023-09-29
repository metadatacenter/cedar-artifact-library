package org.metadatacenter.artifacts.ubkg;

import java.net.URI;

import static org.metadatacenter.artifacts.ubkg.UbkgRendering.NODE_DEFINITION;
import static org.metadatacenter.artifacts.ubkg.UbkgRendering.NODE_LABEL;

public class UbkgTsvRenderer
{
  private final UbkgRendering ubkgRendering;

  public UbkgTsvRenderer(UbkgRendering ubkgRendering)
  {
    this.ubkgRendering = ubkgRendering;
  }

  public StringBuffer renderNodes()
  {
    StringBuffer sb = new StringBuffer();

    sb.append("node_id\tnode_namespace\tnode_label\tnode_definition\tnode_synonyms\tnode_dbxrefs\n");

    for (var node : ubkgRendering.nodes().entrySet()) {
      URI nodeURI = node.getKey();
      var nodeProperties = node.getValue();

      if (!nodeProperties.containsKey(NODE_LABEL))
        throw new RuntimeException("Node " + nodeURI + " does not have an entry for " + NODE_LABEL);

      if (!nodeProperties.containsKey(NODE_DEFINITION))
        throw new RuntimeException("Node " + nodeURI + " does not have an entry for " + NODE_DEFINITION);

      String nodeLabel = nodeProperties.get(NODE_LABEL);
      String nodeDefinition = nodeProperties.get(NODE_DEFINITION);

      sb.append(nodeURI.toString() + "\t\t" + nodeLabel + "\t" + nodeDefinition + "\t\t\n");
    }

    return sb;
  }

  public StringBuffer renderEdges()
  {
    StringBuffer sb = new StringBuffer();

    sb.append("subject\tpredicate\tobject\n");

    for (var edge : ubkgRendering.edges().entrySet()) {
      URI edgeURI = edge.getKey();
      for (var predicateObject : edge.getValue().entrySet()) {
        String predicate = predicateObject.getKey();
        URI object = predicateObject.getValue();

        sb.append(edgeURI.toString() + "\t" + predicate + "\t" + object + "\n");
      }
    }
    return sb;
  }

}
