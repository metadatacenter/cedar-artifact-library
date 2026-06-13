package org.metadatacenter.artifacts.model.core;

/**
 * Marker for field builders whose schema-level default value is a plain {@code String}
 * passed through to {@link org.metadatacenter.artifacts.model.core.fields.constraints.TextValueConstraints}.
 *
 * <p>Compile-time guard against the kind of regression that previously left
 * {@link TextAreaField.TextAreaFieldBuilder} without a {@code withDefaultValue} method
 * while every sibling builder had one. Any new field type sharing the same default
 * shape must extend the {@code permits} clause and implement {@link #withDefaultValue}.
 *
 * <p>Static field builders (page-break, section-break, image, rich-text, YouTube) are
 * deliberately excluded — they carry no instance value and therefore no schema-level
 * default. Numeric / temporal / IRI / controlled-term builders use differently shaped
 * defaults ({@code Number}, IRI {@code URI}, or {@code URI} + label) and are also
 * outside this interface.
 *
 * <p>The return type is the bare {@link FieldSchemaArtifactBuilder} wildcard rather
 * than each concrete {@code SELF} because the interface is shared across heterogeneous
 * builder types; concrete implementations narrow it to their own {@code SELF} via
 * Java's covariant return rules.
 */
public sealed interface LiteralDefaultableFieldBuilder
    permits TextField.TextFieldBuilder,
            TextAreaField.TextAreaFieldBuilder,
            PhoneNumberField.PhoneNumberFieldBuilder,
            EmailField.EmailFieldBuilder,
            RadioField.RadioFieldBuilder,
            CheckboxField.CheckboxFieldBuilder,
            ListField.ListFieldBuilder
{
  FieldSchemaArtifactBuilder<?> withDefaultValue(String defaultValue);
}
