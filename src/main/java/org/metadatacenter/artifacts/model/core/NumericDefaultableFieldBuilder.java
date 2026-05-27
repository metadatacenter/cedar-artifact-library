package org.metadatacenter.artifacts.model.core;

/**
 * Marker for field builders whose schema-level default value is a {@link Number}
 * passed through to
 * {@link org.metadatacenter.artifacts.model.core.fields.constraints.NumericValueConstraints}.
 *
 * <p>Compile-time guard parallel to {@link LiteralDefaultableFieldBuilder} — any new
 * field type sharing the numeric-default shape must extend the {@code permits} clause
 * and implement {@link #withDefaultValue}.
 *
 * <p>Currently only {@link NumericField.NumericFieldBuilder} fits; a future
 * narrower-range field type (e.g. an integer-only field) would join here.
 */
public sealed interface NumericDefaultableFieldBuilder
    permits NumericField.NumericFieldBuilder
{
  FieldSchemaArtifactBuilder<?> withDefaultValue(Number defaultValue);
}
