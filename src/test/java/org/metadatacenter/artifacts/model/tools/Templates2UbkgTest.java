package org.metadatacenter.artifacts.model.tools;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Templates2UbkgTest
{
  @TempDir Path directory;

  @Test public void readsEveryJsonFileInTheDirectoryAndNothingElse() throws IOException
  {
    Files.writeString(directory.resolve("study.json"), "{\"schema:name\": \"Study\"}");
    Files.writeString(directory.resolve("sample.json"), "{\"schema:name\": \"Sample\"}");
    Files.writeString(directory.resolve("notes.txt"), "{\"schema:name\": \"Notes\"}");
    Path nested = Files.createDirectory(directory.resolve("nested.json"));
    Files.writeString(nested.resolve("inner.json"), "{\"schema:name\": \"Inner\"}");

    Set<String> names = Templates2Ubkg.readJsonFromFilesInDirectory(directory.toString()).stream()
      .map((ObjectNode node) -> node.get("schema:name").asText()).collect(Collectors.toSet());

    assertEquals(Set.of("Study", "Sample"), names);
  }

  @Test public void refusesAPathThatIsNotADirectoryOrAFileThatIsNotAJsonObject() throws IOException
  {
    Path file = Files.writeString(directory.resolve("study.json"), "[\"not\", \"an\", \"object\"]");
    RuntimeException notADirectory = assertThrows(RuntimeException.class,
      () -> Templates2Ubkg.readJsonFromFilesInDirectory(file.toString()));
    assertEquals("Provided path " + file + " is not a directory", notADirectory.getMessage());

    RuntimeException notAnObject = assertThrows(RuntimeException.class,
      () -> Templates2Ubkg.readJsonFromFilesInDirectory(directory.toString()));
    assertEquals("Expecting JSON object in file study.json", notAnObject.getMessage());

    Files.writeString(file, "{\"schema:name\": ");
    RuntimeException malformed = assertThrows(RuntimeException.class,
      () -> Templates2Ubkg.readJsonFromFilesInDirectory(directory.toString()));
    assertTrue(malformed.getMessage().startsWith("Error reading JSON file study.json"), malformed.getMessage());
  }
}
