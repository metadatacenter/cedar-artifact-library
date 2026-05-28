# For Claude (or any new contributor)

Start here:

1. **[DESIGN.md](./DESIGN.md)** — the architectural principles. Read this before adding
   a field type, a value-constraint kind, a new reader/renderer, or any change to a
   builder API. It captures decisions that aren't obvious from the code.
2. **[ROADMAP.md](./ROADMAP.md)** — what's open, what's known-broken, what's out of scope.
3. **README.md** (in the project root if present) for end-user information.

## Conventions you must respect

These have bitten contributors before:

- **Push to `develop`, never `main`.** The release process owns `main`; PRs that target
  `main` directly will be rejected.
- **Comments describe code-level facts only.** Do not reference PR numbers, session
  context, "this PR", "the work in this batch", or anything else that won't make sense
  to a future reader who lacks the authoring context. PR descriptions and commit messages
  are the right place for that.
- **Spotless must pass.** Run `mvn spotless:apply` before committing if your editor
  doesn't auto-format.
- **Tests must pass and not skip silently.** `mvn test` must end clean. The two known
  exceptions are documented in `YamlAsymmetryProbeTest` (`@Disabled` regressions for
  the two YAML round-trip bugs); those are the only acceptable skips.

## Patterns to mirror

When adding a new field type, follow the pattern in `RorField` (the smallest non-trivial
example). Build a builder that extends the F-bounded `FieldSchemaArtifactBuilder<SELF>`,
register it in the sealed hierarchy, and add per-type tests both at the unit level
(`FieldSchemaArtifactBuilderTest`) and at the JSON/YAML round-trip level. DESIGN.md
section "Adding a new field type" walks through this.

## What's not in scope

If you're tempted to:
- Add an I/O-bound feature (Excel rendering, REDCap export, terminology lookups, etc.):
  consider whether it belongs in a separate repo. See DESIGN.md section "Model first,
  I/O second."
- Change the public builder API: don't, unless coordinated with a model-version bump.
- Add comments referring to "this session" or "this PR": don't. See above.
