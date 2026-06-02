# Roadmap

Library-side work items. Things that surface as quirks in downstream
consumers (`cedar-artifact-mcp`, server tooling, etc.) but whose actual fix
lives here.

For architectural ground-rules see [DESIGN.md](./DESIGN.md); for contributor
conventions see [CLAUDE.md](./CLAUDE.md).

## Next

- **Reader vs. builder defaulting for `version` / `status`** — *resolved.* The readers now
  default a **top-level** artifact's `version`/`status` to `0.0.1` / `draft` when absent, matching
  the builder, so a built model and a read model agree for the same logical input. The shared
  default is `Version.DEFAULT` (and `Status.DRAFT`), used by the template/element/field builders
  and by `YamlArtifactReader` / `JsonArtifactReader`. Nested children that omit `version`/`status`
  are left untouched (no defaulting), preserving lossless round-tripping of real templates whose
  child fields carry neither.

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
  library**: add a template-driven inflater that takes a (possibly sparse) instance plus its
  template and fills the missing empty placeholders, recursing into elements. The traversal
  must be driven by the *template* structure, not by visiting the instance — a visitor over a
  sparse instance never reaches the fields that are absent, which are exactly the ones to add.
  (An earlier instance-visitor stub, `InstanceFixer`, was removed for this reason; it could
  only touch fields already present.) A tested stand-in already exists outside the library,
  MCP-side (`cedar-artifact-mcp`'s `InstanceInflater` / `EmptyFieldInstances`); fold it into
  the library and have the instance JSON renderer / YAML→JSON path use it. Painful but
  necessary while the all-fields-present JSON rule stands.

- **The public reader/renderer API leaks the parse-library tree type** *(major-version change)*.
  `JsonArtifactReader` / `JsonArtifactRenderer` take and return Jackson's `ObjectNode`, and
  `YamlArtifactReader` / `YamlArtifactRenderer` take and return SnakeYAML's
  `LinkedHashMap<String, Object>` — so the underlying parse library is part of the public contract
  (it even leaks into the shared `ArtifactReader<N>` type parameter). This couples clients to
  Jackson/SnakeYAML and forces them to obtain or build those tree types just to call the library.
  The fix is to move the public boundary to the **wire format itself — `String`**:
  `readTemplate(String)` / `renderTemplate(artifact) → String` (and the element/field/instance
  counterparts), parsing/serializing internally. This hides both libraries, unifies JSON and YAML
  behind one symmetric `read(String) → Artifact` / `render(Artifact) → String` contract, and matches
  what callers actually hold (text from a file or HTTP body). The internals are unchanged — the
  String methods just prepend a parse and append a serialize. A bespoke `JsonNode`-style abstraction
  interface is **not** the answer: it trades one library coupling for a hand-rolled tree API plus
  adapters that callers must still populate. Migrate additively to avoid a hard break: add the
  String methods, mark the node-typed ones `@Deprecated(forRemoval = true)` delegating to them, and
  remove them at the next major version. Two clean-ups fall out: the keyed/tree-returning render
  overloads (e.g. `renderElementSchemaArtifact(key, artifact)`) are internal child-composition
  helpers and can become package-private, and the `ArtifactReader<N>` type parameter disappears.
  Caveat: a caller that wants the rendered artifact as a *tree* (to embed in a larger document or
  validate without re-parsing) would lose direct access; if that need is real, keep a single,
  explicitly parse-library-typed, opt-in "advanced" method so the coupling exists only where it is
  consciously chosen.

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
