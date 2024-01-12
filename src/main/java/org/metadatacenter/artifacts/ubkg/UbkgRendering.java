package org.metadatacenter.artifacts.ubkg;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * A UBKG rendering consists of a list of nodes and edges.
 * <p></p>
 * A node is represented as a map of node URIs to a map containing a label and definition for each node:
 * <pre>node URI -> ("node_label" -> node label, "node_definition" -> node definition)</pre>
 * <p></p>
 * An edge is represented as a map of subject URIs to a map of predicates to object URIs:
 * <pre>subject URI->(predicate -> object URI)</pre>
 */
public interface UbkgRendering
{
  String NODE_LABEL = "node_label";
  String NODE_DEFINITION = "node_definition";

  static UbkgRendering create(LinkedHashMap<URI, Map<String, String>> nodes, LinkedHashMap<URI, Map<String, Set<URI>>> edges)
  {
    return new UbkgRenderingRecord(nodes, edges);
  }

  // node URI -> ("node_label" -> node label, "node_definition" -> node definition)
  LinkedHashMap<URI, Map<String, String>> nodes();

  // subject URI->(predicate -> object URI)
  LinkedHashMap<URI, Map<String, Set<URI>>> edges();

  default List<URI> nodeIds() { return new ArrayList<>(nodes().keySet()); }

  default List<URI> edgeIds() { return new ArrayList<>(edges().keySet()); }

  default Map<String, String> getNode(URI nodeUri)
  {
    if (nodes().containsKey(nodeUri))
      return nodes().get(nodeUri);
    else
      throw new IllegalArgumentException("Invalid node URI " + nodeUri);
  }

  default Map<String, Set<URI>> getEdge(URI subjectUri)
  {
    if (edges().containsKey(subjectUri))
      return edges().get(subjectUri);
    else
      throw new IllegalArgumentException("Invalid subject URI " + subjectUri);
  }

  static Builder builder()
  {
    return new Builder();
  }

  class Builder
  {
    private final LinkedHashMap<URI, Map<String, String>> nodes = new LinkedHashMap<>();
    private final LinkedHashMap<URI, Map<String, Set<URI>>> edges = new LinkedHashMap<>();

    private Builder()
    {
    }

    public Builder withNode(URI nodeUri, String nodeLabel, String nodeDefinition)
    {
      Map<String, String> node = new HashMap<>();

      node.put(NODE_LABEL, nodeLabel);
      node.put(NODE_DEFINITION, nodeDefinition);

      nodes.put(nodeUri, node);

      return this;
    }

    public Builder withEdge(URI subjectUri, String predicate, URI objectUri)
    {
      if (edges.containsKey(subjectUri)) {
        Map<String, Set<URI>> edge = edges.get(subjectUri);

        if (edge.containsKey(predicate))
          edge.get(predicate).add(objectUri);
        else {
          Set<URI> predicateValues = new HashSet<>();
          predicateValues.add(objectUri);
          edge.put(predicate, predicateValues);
        }
      } else {
        Map<String, Set<URI>> edge = new HashMap<>();
        Set<URI> predicateValues = new HashSet<>();
        predicateValues.add(objectUri);
        edge.put(predicate, predicateValues);

        edges.put(subjectUri, edge);
      }
      return this;
    }

    public UbkgRendering build()
    {
      return new UbkgRenderingRecord(nodes, edges);
    }
  }
}

record UbkgRenderingRecord(LinkedHashMap<URI, Map<String, String>> nodes, LinkedHashMap<URI, Map<String, Set<URI>>> edges)
  implements UbkgRendering
{

}
