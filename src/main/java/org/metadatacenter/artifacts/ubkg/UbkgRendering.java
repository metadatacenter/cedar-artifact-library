package org.metadatacenter.artifacts.ubkg;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface UbkgRendering
{
  String NODE_LABEL = "node_label";
  String NODE_DEFINITION = "node_definition";

  static UbkgRendering create(LinkedHashMap<URI, Map<String, String>> nodes, LinkedHashMap<URI, Map<String, Set<URI>>> edges)
  {
    return new UbkgRenderingRecord(nodes, edges);
  }

  LinkedHashMap<URI, Map<String, String>> nodes();

  LinkedHashMap<URI, Map<String, Set<URI>>> edges();

  default List<URI> nodeIds() { return new ArrayList<>(nodes().keySet()); }

  default List<URI> edgeIds() { return new ArrayList<>(edges().keySet()); }

  default Map<String, String> getNode(URI nodeId)
  {
    if (nodes().containsKey(nodeId))
      return nodes().get(nodeId);
    else
      throw new IllegalArgumentException("Invalid node ID " + nodeId);
  }

  default Map<String, Set<URI>> getEdge(URI edgeId)
  {
    if (edges().containsKey(edgeId))
      return edges().get(edgeId);
    else
      throw new IllegalArgumentException("Invalid edge ID " + edgeId);
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

    public Builder withNode(URI nodeId, String nodeLabel, String nodeDefinition)
    {
      Map<String, String> node = new HashMap<>();

      node.put(NODE_LABEL, nodeLabel);
      node.put(NODE_DEFINITION, nodeDefinition);

      nodes.put(nodeId, node);

      return this;
    }

    public Builder withEdge(URI subject, String predicate, URI object)
    {
      if (edges.containsKey(subject)) {
        Map<String, Set<URI>> edge = edges.get(subject);

        if (edge.containsKey(predicate))
          edge.get(predicate).add(object);
        else {
          Set<URI> predicateValues = new HashSet<>();
          predicateValues.add(object);
          edge.put(predicate, predicateValues);
        }
      } else {
        Map<String, Set<URI>> edge = new HashMap<>();
        Set<URI> predicateValues = new HashSet<>();
        predicateValues.add(object);
        edge.put(predicate, predicateValues);

        edges.put(subject, edge);
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