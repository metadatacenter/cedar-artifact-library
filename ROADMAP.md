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

- **The library should generate empty JSON instance placeholders.** A canonical CEDAR
  *JSON* instance must carry an entry for every field its template defines, even unset
  ones (rendered as an empty placeholder, e.g. `{"@value": null}`), because the template's
  JSON Schema marks those properties `required`. This is an old design decision we are not
  fond of, but it stays until the next model/library iteration. The *YAML* instance form is
  the opposite — and is correct as-is: it is sparse, omitting unset fields entirely (no
  `null`, no `{}`, no `[]`). The consequence is an asymmetry: rendering a sparse instance
  model to JSON yields an *incomplete* JSON instance, and a YAML→JSON translation that is to
  produce a valid CEDAR instance must re-add the empty placeholders — which requires the
  template (to know which fields exist). That placeholder generation belongs **in the
  library**: complete the currently-stubbed `InstanceFixer(template, instance)` so the
  library can inflate a sparse instance to the full JSON form. Today this inflation is done
  outside the library, MCP-side (`cedar-artifact-mcp`'s `InstanceInflater`), as an interim
  stand-in; fold it into the library and have the instance JSON renderer / YAML→JSON path use
  it. Painful but necessary while the all-fields-present JSON rule stands.

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
