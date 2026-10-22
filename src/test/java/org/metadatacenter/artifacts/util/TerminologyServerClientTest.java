package org.metadatacenter.artifacts.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.core.ControlledTermField;
import org.metadatacenter.artifacts.model.core.fields.constraints.ControlledTermValueConstraints;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The client against a local stand-in for the terminology server, which answers each request with the
 * next queued response and records what it was asked.
 */
@Tag("integration")
class TerminologyServerClientTest
{
  private static final ObjectMapper MAPPER = new ObjectMapper();

  private final Queue<String[]> responses = new ArrayDeque<>();
  private final List<String> paths = new ArrayList<>();
  private final List<String> authorizations = new ArrayList<>();
  private final List<JsonNode> payloads = new ArrayList<>();
  private HttpServer server;

  @BeforeEach void startServer() throws IOException
  {
    server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 0);
    server.createContext("/", exchange -> {
      paths.add(exchange.getRequestMethod() + " " + exchange.getRequestURI().getPath());
      authorizations.add(exchange.getRequestHeaders().getFirst("Authorization"));
      payloads.add(MAPPER.readTree(exchange.getRequestBody()));
      String[] response = responses.remove();
      byte[] body = response[1].getBytes(StandardCharsets.UTF_8);
      exchange.sendResponseHeaders(Integer.parseInt(response[0]), body.length);
      exchange.getResponseBody().write(body);
      exchange.close();
    });
    server.start();
  }

  @AfterEach void stopServer()
  {
    server.stop(0);
  }

  @Test void everySpellingOfTheEndpointReachesIntegratedRetrieve()
  {
    String base = "http://127.0.0.1:" + server.getAddress().getPort();
    for (String endpoint : List.of(base, base + "/", base + "/integrated-search", base + "/integrated-retrieve/")) {
      respond(200, page(1, 0, null));
      assertEquals(List.of(), client(endpoint).getValuesFromTerminologyServer(diseaseBranch()), endpoint);
    }

    assertEquals(List.of("POST /integrated-retrieve", "POST /integrated-retrieve", "POST /integrated-retrieve",
      "POST /integrated-retrieve"), paths);
    assertEquals("apiKey secret", authorizations.get(0));
    JsonNode payload = payloads.get(0);
    assertEquals(1, payload.get("page").asInt());
    assertEquals(1000, payload.get("pageSize").asInt());
    assertEquals("http://purl.obolibrary.org/obo/DOID_4", payload.at("/valueConstraints/branches/0/uri").asText());
    assertEquals("DOID", payload.at("/valueConstraints/branches/0/acronym").asText());
  }

  @Test void pagesUntilAShortPageOrTheReportedTotal()
  {
    // A full page with no total asks for the next one; the short page after it ends the walk.
    respond(200, page(1, 1000, null));
    respond(200, page(1001, 2, null));
    List<TerminologyValue> values = client().getValuesFromTerminologyServer(diseaseBranch());
    assertEquals(1002, values.size());
    assertEquals(URI.create("http://purl.obolibrary.org/obo/DOID_1"), values.get(0).uri());
    assertEquals(URI.create("http://purl.obolibrary.org/obo/DOID_1002"), values.get(1001).uri());
    assertEquals(List.of(1, 2), payloads.stream().map(payload -> payload.get("page").asInt()).toList());

    // A full page that reaches the reported total is the last one.
    payloads.clear();
    respond(200, page(1, 1000, 1000));
    assertEquals(1000, client().getValuesFromTerminologyServer(diseaseBranch()).size());
    assertEquals(1, payloads.size());
  }

  @Test void valuesAreDistinguishedByIriNotByLabel()
  {
    respond(200, "{\"collection\": ["
      + "{\"@id\": \"http://purl.obolibrary.org/obo/DOID_1\", \"prefLabel\": \"Lung disease\"},"
      + "{\"@id\": \"http://purl.obolibrary.org/obo/DOID_2\", \"prefLabel\": \"Lung disease\"},"
      + "{\"@id\": \"http://purl.obolibrary.org/obo/DOID_1\", \"prefLabel\": \"Lung disease\"}]}");

    assertEquals(List.of(
        new TerminologyValue(URI.create("http://purl.obolibrary.org/obo/DOID_1"), "Lung disease"),
        new TerminologyValue(URI.create("http://purl.obolibrary.org/obo/DOID_2"), "Lung disease")),
      client().getValuesFromTerminologyServer(diseaseBranch()));
  }

  @Test void anErrorStatusOrAnIncompleteValueIsRefused()
  {
    respond(500, "{}");
    RuntimeException status = assertThrows(RuntimeException.class,
      () -> client().getValuesFromTerminologyServer(diseaseBranch()));
    assertTrue(status.getMessage().contains("Response code: 500"), status.getMessage());

    respond(200, "{\"collection\": [{\"@id\": \"http://purl.obolibrary.org/obo/DOID_1\"}]}");
    RuntimeException incomplete = assertThrows(RuntimeException.class,
      () -> client().getValuesFromTerminologyServer(diseaseBranch()));
    assertTrue(incomplete.getMessage().contains("value returned with no prefLabel"), incomplete.getMessage());
  }

  private TerminologyServerClient client()
  {
    return client("http://127.0.0.1:" + server.getAddress().getPort());
  }

  private static TerminologyServerClient client(String endpoint)
  {
    return new TerminologyServerClient(endpoint, "secret");
  }

  private static ControlledTermValueConstraints diseaseBranch()
  {
    return ControlledTermField.builder().withName("Disease")
      .withBranchValueConstraint(URI.create("http://purl.obolibrary.org/obo/DOID_4"), "DOID", "DOID", "disease", 0)
      .build().valueConstraints().orElseThrow().asControlledTermValueConstraints();
  }

  private void respond(int status, String body)
  {
    responses.add(new String[] { String.valueOf(status), body });
  }

  /** A page of {@code count} values numbered from {@code first}, with the total the server reports. */
  private static String page(int first, int count, Integer totalCount)
  {
    StringBuilder collection = new StringBuilder();
    for (int i = first; i < first + count; i++) {
      if (collection.length() > 0)
        collection.append(',');
      collection.append("{\"@id\": \"http://purl.obolibrary.org/obo/DOID_").append(i)
        .append("\", \"prefLabel\": \"Disease ").append(i).append("\"}");
    }
    return "{" + (totalCount == null ? "" : "\"totalCount\": " + totalCount + ", ") + "\"collection\": ["
      + collection + "]}";
  }
}
