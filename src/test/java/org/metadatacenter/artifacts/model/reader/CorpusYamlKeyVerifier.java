package org.metadatacenter.artifacts.model.reader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Records, for every YAML document in the shared corpus, the keys this library's YAML reader reads in
 * each mapping of it, and checks that the committed record is current.
 *
 * <p>The YAML reader refuses a key it does not read, so the keys it reads at a place are the YAML form
 * at that place. The TypeScript library's YAML reader refuses keys the same way, and its
 * {@code YamlKeyVocabularyConcordance} spec reads every corpus document with it and compares the keys
 * read with this record, mapping by mapping. Where the two differ, one library accepts a document the
 * other refuses. A reader asks for some keys a document does not hold, a field type's optional
 * settings say, so the record shows what a reader would accept at a place, not only what the corpus
 * states there.
 *
 * <p>Not a unit test, for the reason {@code CorpusFixtureVerifier} gives. Run with:
 *
 * <pre>{@code
 *   mvn test-compile exec:java \
 *       -Dexec.classpathScope=test \
 *       -Dexec.mainClass=org.metadatacenter.artifacts.model.reader.CorpusYamlKeyVerifier \
 *       -Dexec.args=/path/to/cedar-test-artifacts
 * }</pre>
 *
 * <p>With {@code --write} after the corpus path, it writes the record instead of checking it.
 */
public final class CorpusYamlKeyVerifier
{
  static final Path RECORD = Path.of("src/test/resources/concordance/yaml-key-vocabulary.json");

  private static final ObjectMapper MAPPER = new ObjectMapper();

  private CorpusYamlKeyVerifier() {}

  public static void main(String[] args) throws IOException
  {
    if (args.length < 1 || args.length > 2 || (args.length == 2 && !args[1].equals("--write"))) {
      System.err.println("Usage: CorpusYamlKeyVerifier <path to cedar-test-artifacts> [--write]");
      System.exit(2);
    }
    Path artifacts = Path.of(args[0]).resolve("artifacts");
    if (!Files.isDirectory(artifacts)) {
      System.err.println("Not a corpus: " + artifacts.toAbsolutePath());
      System.exit(2);
    }

    String record = record(artifacts);
    if (args.length == 2) {
      Files.writeString(RECORD, record);
      System.out.println("YAML key vocabulary written to " + RECORD);
      return;
    }
    String committed = Files.exists(RECORD) ? Files.readString(RECORD) : "";
    System.out.printf("corpus YAML documents read for their keys: %d   record current: %s%n",
      MAPPER.readTree(record).get("cases").size(), record.equals(committed));
    if (!record.equals(committed)) {
      System.out.println("\nThe YAML reader reads other keys than " + RECORD + " records. Run with --write, and sync"
        + " the record into the TypeScript library with `npm run sync:concordance`.");
      System.exit(1);
    }
  }

  /** The record, one corpus document a line, in path order. */
  static String record(Path artifacts) throws IOException
  {
    List<Path> documents;
    try (Stream<Path> walk = Files.walk(artifacts)) {
      documents = walk.filter(path -> path.toString().endsWith(".yaml")).sorted().toList();
    }
    StringBuilder record = new StringBuilder("{\"cases\": [\n");
    for (int i = 0; i < documents.size(); i++) {
      Path document = documents.get(i);
      ObjectNode entry = MAPPER.createObjectNode();
      entry.put("file", artifacts.relativize(document).toString().replace('\\', '/'));
      boolean compact = document.getFileName().toString().endsWith(".compact.yaml");
      entry.put("compact", compact);
      LinkedHashMap<String, Object> tracked = YamlKeyTracking.track(
        ArtifactSourceParser.yaml(Files.readString(document, StandardCharsets.UTF_8)));
      try {
        read(tracked, new YamlArtifactReader(compact));
        // Each mapping's keys, sorted and separated by a space, by the mapping's JSON Pointer.
        ObjectNode asked = entry.putObject("asked");
        for (Map.Entry<String, Set<String>> mapping : YamlKeyTracking.askedKeys(tracked).entrySet())
          asked.put(mapping.getKey(), String.join(" ", mapping.getValue()));
      } catch (RuntimeException refused) {
        // A document the reader refuses for one of its values is read only as far as that value.
        entry.put("refused", true);
      }
      record.append(MAPPER.writeValueAsString(entry)).append(i + 1 < documents.size() ? ",\n" : "\n");
    }
    return record.append("]}\n").toString();
  }

  private static void read(LinkedHashMap<String, Object> source, YamlArtifactReader reader)
  {
    Object type = YamlKeyTracking.untracked(() -> source.get("type"));
    switch (String.valueOf(type)) {
      case "template" -> reader.readTemplateSchemaArtifact(source);
      case "element" -> reader.readElementSchemaArtifact(source);
      case "instance" -> reader.readTemplateInstanceArtifact(source);
      case "element-instance" -> reader.readElementInstanceArtifact(source);
      default -> reader.readFieldSchemaArtifact(source);
    }
  }
}
