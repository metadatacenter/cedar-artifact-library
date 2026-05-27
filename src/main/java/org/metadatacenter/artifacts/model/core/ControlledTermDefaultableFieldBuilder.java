package org.metadatacenter.artifacts.model.core;

import java.net.URI;

/**
 * Marker for field builders whose schema-level default value is a {@link URI} plus a
 * human-readable label, passed through to
 * {@link org.metadatacenter.artifacts.model.core.fields.constraints.ControlledTermValueConstraints}.
 *
 * <p>Compile-time guard parallel to {@link LiteralDefaultableFieldBuilder}. Only
 * {@link ControlledTermField.ControlledTermFieldBuilder} fits today; if the model ever
 * adds another constraint-bound IRI-with-label field shape (e.g. value-set-only), it
 * would join here.
 */
public sealed interface ControlledTermDefaultableFieldBuilder
    permits ControlledTermField.ControlledTermFieldBuilder
{
  FieldSchemaArtifactBuilder<?> withDefaultValue(URI uri, String label);
}
