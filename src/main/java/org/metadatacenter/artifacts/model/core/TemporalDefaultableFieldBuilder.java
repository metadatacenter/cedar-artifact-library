package org.metadatacenter.artifacts.model.core;

/**
 * Marker for field builders whose schema-level default value is a temporal value —
 * an ISO 8601 date/time/dateTime expressed as a {@link String} — passed through to
 * {@link org.metadatacenter.artifacts.model.core.fields.constraints.TemporalValueConstraints}.
 *
 * <p>Compile-time guard parallel to {@link LiteralDefaultableFieldBuilder}. Distinct
 * from the literal family because temporal defaults are semantically date/time
 * values even though their builder signature happens to be {@code String}; mixing
 * them would muddy the type taxonomy.
 */
public sealed interface TemporalDefaultableFieldBuilder
    permits TemporalField.TemporalFieldBuilder
{
  FieldSchemaArtifactBuilder<?> withDefaultValue(String defaultValue);
}
