# Design

The architectural principles behind `cedar-artifact-library`. Read these before making
significant changes — they're the answers to questions that aren't obvious from the code,
and they encode decisions that have been validated by ~10 years of consumers.

## Principle 1: The model is the product

This is a library, not an application. Its job is to give downstream consumers a typed,
validated, immutable representation of CEDAR templates, elements, fields, and instances.
The value the library delivers is *type safety at compile time*. Everything else —
internal verbosity, builder boilerplate, the F-bound — is a cost paid by us so consumers
get clean call sites.

Implications:
- Optimize for the consumer's experience, not ours.
- Verbosity in service of types is fine. Verbosity for no reason is not.
- Anything user-visible (builders, records, sealed interface members) is sacred. Internal
  organization is freely refactorable.

## Principle 2: F-bounded builders are the house pattern

Every field-schema builder uses the [F-bounded polymorphism](https://en.wikipedia.org/wiki/Bounded_quantification#F-bounded_quantification)
pattern:

```java
abstract class FieldSchemaArtifactBuilder<SELF extends FieldSchemaArtifactBuilder<SELF>>

final class TextFieldBuilder extends FieldSchemaArtifactBuilder<TextFieldBuilder>
```

The base class's `with*` setters return `SELF`, so fluent chains preserve the most-specific
type. Without this, `TextField.builder().withName(...).withMinLength(...)` would fail to
compile after the base-class setter; with it, the IDE autocompletes the subclass methods
correctly.

The pattern is intricate but mechanical. New field types must follow it exactly:

```java
public final class FooField implements FieldSchemaArtifact {
  // ... record-style accessors ...

  public static FooFieldBuilder builder() { return new FooFieldBuilder(); }
  public static FooFieldBuilder builder(FooField existing) { return new FooFieldBuilder(existing); }

  public static final class FooFieldBuilder
      extends FieldSchemaArtifactBuilder<FooFieldBuilder> {
    // ... type-specific with*() methods that return FooFieldBuilder ...
    public FooField build() { ... }
  }
}
```

## Principle 3: Records for data, sealed interfaces for closed hierarchies

All artifacts are immutable records (Java 17+). All hierarchies that need exhaustive
treatment (`LiteralFieldInstance`, `IriFieldInstance`, every value-constraint type) are
sealed.

This gives consumers:
- Auto equality / hashCode / toString from records.
- Compile-time exhaustiveness from sealed switches.
- No accidental mutation.

When you add a field type or value constraint, the compiler will tell you what else needs
updating — because sealed dispatch sites won't compile until you handle the new member.
Honor that constraint; don't shortcut it with `default:` arms that silently swallow.

## Principle 4: Validate at the setter, not at build()

Builder setters throw immediately on invalid input:

```java
public SELF withName(String name) {
  if (name == null) throw new IllegalArgumentException("null name passed to builder");
  this.name = name;
  return self();
}
```

By the time `build()` is called, the builder state is trusted. The reasoning: fail at the
point of error so the stack trace points at the offending call site, not at a downstream
assembly step.

Per-tool client-side validation (the kind we use in MCP servers) is the equivalent of this
principle at the I/O layer. Both flow from the same instinct: invariants belong at the
boundary they protect.

## Principle 5: Per-format reader / renderer pairs

Each wire format (JSON, YAML, Excel, UBKG, REDCap) has its own reader + renderer pair.
They share the model layer but nothing else.

```
JsonArtifactReader      ↔  model  ↔  JsonArtifactRenderer
YamlArtifactReader      ↔  model  ↔  YamlArtifactRenderer
                                  ↔  ExcelArtifactRenderer (no reader; one-way)
                                  ↔  UbkgArtifactRenderer (one-way)
```

Adding a new format = adding a new pair. **Do not** try to abstract over them with a
generic serialization layer — the formats are too different (JSON Schema's nesting vs.
YAML's flat configuration block vs. Excel's grid model) for any abstraction not to leak.

## Principle 6: Round-trip is the test contract

Every artifact shape must satisfy:

```java
assertEquals(original, reader.read(renderer.render(original)));
```

A failure to round-trip is a renderer or reader bug, not a "feature." Two such bugs are
currently documented as `@Disabled` regressions in `YamlAsymmetryProbeTest`:
- YAML round-trip drops `width`/`height` on YouTube/Image fields.
- YAML round-trip drops `valueRecommendation` on controlled-term fields.

Both have the test in place; removing the `@Disabled` annotation and watching them go
green is how the fix is verified.

## Principle 7: Model first, I/O second

The `model.core` package depends on nothing format-specific. The `model.reader.*` and
`model.renderer.*` packages depend on the model. **I/O-bound utilities**
(`TerminologyServerClient`, `ExcelArtifactRenderer`, `UbkgArtifactRenderer`,
`redcap.*`, `ss.*`, `tools.*`) depend on heavy externals (Apache POI, HTTP clients,
filesystem) and are candidates for extraction into separate repos eventually.

When adding code, ask: does this *need* to live in the core library, or is it a downstream
concern? If a downstream consumer who only wants JSON/YAML would pay an unnecessary cost
(POI on their classpath, terminology-server config they don't need), the code probably
belongs elsewhere.

## Principle 8: Builder validation > checked exceptions

This library throws `IllegalArgumentException` (unchecked) from setters and `ArtifactParseException`
(also unchecked) from readers. We deliberately don't use checked exceptions:

- Checked exceptions in Java's collection-stream contexts are painful.
- Most parse errors are unrecoverable at the call site anyway.
- The caller chooses how to handle by where they place try/catch.

`ArtifactParseException` carries `(parseErrorMessage, fieldKey, path)` so consumers can
display useful errors. The fields are part of the public contract — see
`ArtifactParseExceptionTest`.

## Principle 9: Adding a new field type — worked example

When CEDAR adds a new field type (call it `FooField`), the changes are:

1. **Create the record + sealed interface member**:
   ```java
   public sealed interface FooField extends FieldSchemaArtifact { /* accessors */ }
   ```

2. **Create the builder**:
   ```java
   public static final class FooFieldBuilder extends FieldSchemaArtifactBuilder<FooFieldBuilder> {
     public FooField build() { /* ... */ }
   }
   ```

3. **Wire into `FieldSchemaArtifact.create(...)`** — the switch expression dispatcher.
   The compiler will force you to add an arm here (sealed switches are exhaustive).

4. **Wire into the renderer's `renderFieldTypeName(FieldInputType)`** — the switch that
   maps input type to wire-format type string.

5. **Wire into the reader's field-input-type discrimination** — the inverse map.

6. **Add unit tests** in `FieldSchemaArtifactBuilderTest` (create + copy-builder).

7. **Add round-trip tests** in `YamlArtifactRoundTripTest` and `JsonArtifactRoundTripTest`.

If any of these steps is missing, the compiler or tests will tell you. The sealed
hierarchy is the safety net.

## Principle 10: Public API stability

Once a field type, builder method, or sealed-interface member is published, it stays.
Deprecate before removing; never silently break a downstream consumer. If the model needs
breaking changes, that's a major-version bump, and a strong candidate for a greenfield
new library rather than evolving in-place.

Internal cleanup (refactoring private helpers, consolidating duplicate code, regenerating
boilerplate via codegen) is fine — but the public builder methods, record accessors, and
sealed-interface members are sacred. Treat them like protocol.

## Principle 11: Comments describe code, not process

Comments must reference code-level facts only. Never reference:
- PR numbers
- Sessions, conversations, "this batch"
- Authors or tools that produced the change
- Anything that only makes sense in the conversational context that produced the file

PR descriptions and commit messages are the right place for that context. Comments are a
permanent artifact; conversational context decays.

## What's *not* settled (open questions)

These are deferred decisions; resolve before acting on them:

- **Whether `FieldSchemaArtifact` should be sealed.** Currently it's a non-sealed interface
  for historical reasons. Making it sealed would let the `FieldSchemaArtifact.create`
  dispatcher be exhaustive at compile time — but it requires deciding whether any
  downstream consumer subclasses it (we believe not).
- **Whether to consolidate the seven "linked-style" field types** (RorField, OrcidField,
  PfasField, RridField, NihGrantIdField, PubMedField, DoiField) into a single
  parameterized field. Pure-internal change with zero wire-format impact, but real
  refactoring work.
- **Whether to generate the builder boilerplate.** ~95% of each field's builder is
  mechanical. An annotation processor or small generator could maintain them all from a
  record-of-records definition.
- **The shape of the next CEDAR model.** Templates and elements are planned to merge;
  what the merged type is called, whether the instance side also merges, and how the wire
  format reflects the merge are all undecided.

When you tackle any of these, this section gets shorter.
