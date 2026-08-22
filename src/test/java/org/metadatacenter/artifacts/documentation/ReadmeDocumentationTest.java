package org.metadatacenter.artifacts.documentation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ReadmeDocumentationTest
{
  private static final String COMPILE_MARKER = "<!-- readme-test: compile -->";
  private static final String FIELD_TYPES_START = "<!-- field-types:start -->";
  private static final String FIELD_TYPES_END = "<!-- field-types:end -->";
  private static final Pattern MARKED_JAVA_FENCE = Pattern.compile(
    Pattern.quote(COMPILE_MARKER) + "\\s*```java\\R(.*?)\\R```", Pattern.DOTALL);
  private static final Pattern FIELD_TYPE_NAME = Pattern.compile("`([A-Za-z][A-Za-z0-9]*Field)`");

  @TempDir Path compilationDirectory;

  @Test public void documentedFieldTypesMatchSealedHierarchy() throws Exception
  {
    String readme = readme();
    int start = readme.indexOf(FIELD_TYPES_START);
    int end = readme.indexOf(FIELD_TYPES_END);
    assertTrue(start >= 0 && end > start, "README field-type markers are missing or out of order");

    Set<String> documentedTypes = new TreeSet<>();
    Matcher documentedTypeMatcher = FIELD_TYPE_NAME.matcher(readme.substring(start, end));
    while (documentedTypeMatcher.find())
      documentedTypes.add(documentedTypeMatcher.group(1));

    Set<String> permittedTypes = Arrays.stream(FieldSchemaArtifact.class.getPermittedSubclasses())
      .map(Class::getSimpleName).collect(Collectors.toCollection(TreeSet::new));

    assertEquals(permittedTypes, documentedTypes);
  }

  @Test public void markedJavaSnippetsCompileAndRun() throws Exception
  {
    Matcher snippetMatcher = MARKED_JAVA_FENCE.matcher(readme());
    int snippetNumber = 0;
    while (snippetMatcher.find()) {
      snippetNumber++;
      compileAndRun("ReadmeSnippet" + snippetNumber, snippetMatcher.group(1));
    }
    assertTrue(snippetNumber > 0, "README has no executable Java snippets");
  }

  private void compileAndRun(String className, String snippet) throws Exception
  {
    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    assertNotNull(compiler, "README snippets require a JDK compiler");

    String source = """
      import org.metadatacenter.artifacts.model.core.*;
      import org.metadatacenter.artifacts.model.core.fields.*;
      import org.metadatacenter.artifacts.model.core.fields.constraints.*;

      import java.net.URI;

      public final class %s
      {
        public static void run() throws Exception
        {
      %s    }
      }
      """.formatted(className, snippet.indent(4));

    Path sourceFile = compilationDirectory.resolve(className + ".java");
    Files.writeString(sourceFile, source, StandardCharsets.UTF_8);

    DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
    try (StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null,
      StandardCharsets.UTF_8)) {
      Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjects(sourceFile.toFile());
      List<String> options = List.of("-classpath", System.getProperty("java.class.path"), "-d",
        compilationDirectory.toString());
      boolean compiled = compiler.getTask(null, fileManager, diagnostics, options, null, compilationUnits).call();
      String diagnosticText = diagnostics.getDiagnostics().stream().map(Object::toString)
        .collect(Collectors.joining(System.lineSeparator()));
      assertTrue(compiled, () -> "README snippet failed to compile:\n" + diagnosticText + "\n" + source);
    }

    try (URLClassLoader classLoader = new URLClassLoader(new URL[] {compilationDirectory.toUri().toURL()},
      getClass().getClassLoader())) {
      try {
        Class.forName(className, true, classLoader).getMethod("run").invoke(null);
      } catch (InvocationTargetException e) {
        throw new AssertionError("README snippet failed while running: " + className, e.getCause());
      }
    }
  }

  private static String readme() throws Exception
  {
    Path testClasses = Path.of(ReadmeDocumentationTest.class.getProtectionDomain().getCodeSource().getLocation().toURI());
    return Files.readString(testClasses.resolve("../..").normalize().resolve("README.md"));
  }
}
