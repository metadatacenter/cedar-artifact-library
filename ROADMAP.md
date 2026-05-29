# Roadmap

Library-side work items. Things that surface as quirks in downstream
consumers (`cedar-artifact-mcp`, server tooling, etc.) but whose actual fix
lives here.

For architectural ground-rules see [DESIGN.md](./DESIGN.md); for contributor
conventions see [CLAUDE.md](./CLAUDE.md).

## Next

- **Reader vs. builder defaulting for `version` / `status`** — The Java
  builder path defaults a freshly-built template/element/field to
  `version: 0.0.1` and `status: draft` (see
  `TemplateSchemaArtifact.java:83-84`, already flagged with a `// TODO`).
  The YAML reader path does not: a compact YAML document with no `version:`
  and no `status:` parses to a model whose `version()` and `status()`
  accessors return `Optional.empty()`. So a model built via
  `TemplateSchemaArtifact.builder().withName(...).build()` and a model read
  via `YamlArtifactReader.readTemplateSchemaArtifact(...)` are not
  equivalent for the same logical input. Resolve in either direction:
  default in the reader to match the builder, or strip the builder defaults
  to match the reader. Same resolution should propagate to
  `ElementSchemaArtifact` and `FieldSchemaArtifact`.

## Known limitations

- **`ImageFieldUiBuilder` can't set `width` / `height`** — the YAML reader/renderer pair
  now preserves `width`/`height` on static fields end-to-end (a model that carries them —
  e.g. read from JSON — survives a YAML round trip), and `YouTubeField.Builder` exposes
  `withWidth` / `withHeight`. But `ImageField`'s UI builder still hardcodes both to
  `Optional.empty()`, so an image field's dimensions can't be set through the Java builder
  API. Add `withWidth` / `withHeight` to `StaticFieldUi.ImageFieldUiBuilder` (and the
  `ImageField.Builder` passthroughs) to close the gap.

## Out of scope

I/O-bound features (Excel rendering, REDCap export, terminology lookups,
etc.) belong in separate repos — see DESIGN.md "Model first, I/O second".
The library stays focused on builders, readers, renderers, and the in-memory
model.
