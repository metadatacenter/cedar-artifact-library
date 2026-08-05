package org.metadatacenter.artifacts.model.tools;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.metadatacenter.artifacts.model.core.Artifact;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.artifacts.model.renderer.ArtifactRenderException;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class YamlSerializerTest
{
  @TempDir Path temporaryDirectory;

  @Test public void rejectsNullArtifact()
  {
    ArtifactRenderException exception = assertThrows(ArtifactRenderException.class,
      () -> YamlSerializer.getYAML(null, false, false));

    assertEquals("Cannot render a null artifact as YAML", exception.getMessage());
  }

  @Test public void rejectsUnsupportedArtifactType()
  {
    Artifact unsupportedArtifact = new UnsupportedArtifact();

    ArtifactRenderException exception = assertThrows(ArtifactRenderException.class,
      () -> YamlSerializer.getYAML(unsupportedArtifact, false, false));

    assertTrue(exception.getMessage().contains(UnsupportedArtifact.class.getName()));
  }

  @Test public void saveYamlPropagatesFileWriteFailureWithContext()
  {
    TemplateSchemaArtifact artifact = TemplateSchemaArtifact.builder().withName("Study").build();

    UncheckedIOException exception = assertThrows(UncheckedIOException.class,
      () -> YamlSerializer.saveYAML(artifact, false, false, temporaryDirectory));

    assertTrue(exception.getMessage().contains(temporaryDirectory.toAbsolutePath().toString()));
    assertInstanceOf(IOException.class, exception.getCause());
  }

  @Test public void saveYamlWritesTheRenderedContent() throws IOException
  {
    TemplateSchemaArtifact artifact = TemplateSchemaArtifact.builder().withName("Study").build();
    Path outputFile = temporaryDirectory.resolve("study.yaml");

    YamlSerializer.saveYAML(artifact, false, false, outputFile);

    assertEquals(YamlSerializer.getYAML(artifact, false, false), Files.readString(outputFile));
  }

  private static final class UnsupportedArtifact implements Artifact
  {
  }
}
