package org.metadatacenter.artifacts.model.core;

import java.net.URI;

/**
 * Marker for field builders whose schema-level default value is a bare {@link URI}
 * passed through to
 * {@link org.metadatacenter.artifacts.model.core.fields.constraints.LinkValueConstraints}.
 *
 * <p>Compile-time guard parallel to {@link LiteralDefaultableFieldBuilder} for the IRI
 * field family: link, ROR, ORCID, PFAS, RRID, PubMed, NIH-grant-ID, DOI. Any new IRI
 * field type must extend the {@code permits} clause and implement {@link #withDefaultValue}.
 *
 * <p>The schema-side IRI default carries no label slot — that's a deliberate library
 * decision distinct from the instance side, where {@code rdfs:label} accompanies the
 * {@code @id}. {@link ControlledTermField}'s default uses a separate two-arg shape
 * ({@code URI} + label) and is captured by {@link ControlledTermDefaultableFieldBuilder}.
 */
public sealed interface IriDefaultableFieldBuilder
    permits LinkField.LinkFieldBuilder,
            RorField.RorFieldBuilder,
            OrcidField.OrcidFieldBuilder,
            PfasField.PfasFieldBuilder,
            RridField.RridFieldBuilder,
            PubMedField.PubMedFieldBuilder,
            NihGrantIdField.NihGrantIdFieldBuilder,
            DoiField.DoiFieldBuilder
{
  FieldSchemaArtifactBuilder<?> withDefaultValue(URI defaultValue);
}
