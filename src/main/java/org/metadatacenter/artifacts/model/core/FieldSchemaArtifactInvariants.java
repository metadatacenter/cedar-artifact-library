package org.metadatacenter.artifacts.model.core;

import org.metadatacenter.artifacts.model.core.fields.constraints.ValueConstraints;
import org.metadatacenter.artifacts.model.core.ui.FieldUi;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateListFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateMapFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateOptionalFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateUiFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateUriListFieldContainsOneOf;
import static org.metadatacenter.model.ModelNodeNames.FIELD_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS;
import static org.metadatacenter.model.ModelNodeNames.FIELD_SCHEMA_ARTIFACT_TYPE_IRI;
import static org.metadatacenter.model.ModelNodeNames.JSON_LD_CONTEXT;
import static org.metadatacenter.model.ModelNodeNames.JSON_LD_TYPE;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_MAX_ITEMS;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_MIN_ITEMS;
import static org.metadatacenter.model.ModelNodeNames.SKOS_ALTLABEL;
import static org.metadatacenter.model.ModelNodeNames.SKOS_PREFLABEL;
import static org.metadatacenter.model.ModelNodeNames.STATIC_FIELD_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS;
import static org.metadatacenter.model.ModelNodeNames.STATIC_FIELD_SCHEMA_ARTIFACT_TYPE_IRI;
import static org.metadatacenter.model.ModelNodeNames.UI;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS;

/**
 * Shared compact-constructor invariants for every field-schema record.
 * <p>
 * Each field-record's compact constructor was previously a ~25-line copy of the same null checks,
 * min/maxItems range check, and JSON-LD context normalization. Centralizing them here
 * eliminates the duplication (and the typos and misattributed error messages that came with it).
 */
final class FieldSchemaArtifactInvariants
{
  private static final Set<URI> ALLOWED_TYPE_IRIS = Set.of(
    URI.create(FIELD_SCHEMA_ARTIFACT_TYPE_IRI),
    URI.create(STATIC_FIELD_SCHEMA_ARTIFACT_TYPE_IRI));

  private FieldSchemaArtifactInvariants() {}

  /**
   * Validate the field-schema-artifact invariants shared by every field type.
   * <p>
   * Throws {@link IllegalStateException} with a message that names the offending field type
   * (derived from {@code self.getClass().getSimpleName()}) and field name.
   */
  static void validate(Object self,
                       String name,
                       LinkedHashMap<String, URI> jsonLdContext,
                       List<URI> jsonLdTypes,
                       Optional<String> preferredLabel,
                       List<String> alternateLabels,
                       Optional<Integer> minItems,
                       Optional<Integer> maxItems,
                       Optional<URI> propertyUri,
                       Optional<String> language,
                       FieldUi fieldUi,
                       Optional<ValueConstraints> valueConstraints,
                       Optional<Annotations> annotations)
  {
    validateMapFieldNotNull(self, jsonLdContext, JSON_LD_CONTEXT);
    validateUriListFieldContainsOneOf(self, jsonLdTypes, JSON_LD_TYPE, ALLOWED_TYPE_IRIS);
    validateOptionalFieldNotNull(self, preferredLabel, SKOS_PREFLABEL);
    validateListFieldNotNull(self, alternateLabels, SKOS_ALTLABEL);
    validateOptionalFieldNotNull(self, minItems, JSON_SCHEMA_MIN_ITEMS);
    validateOptionalFieldNotNull(self, maxItems, JSON_SCHEMA_MAX_ITEMS);
    validateOptionalFieldNotNull(self, propertyUri, "propertyUri");
    validateOptionalFieldNotNull(self, language, "language");
    validateUiFieldNotNull(self, fieldUi, UI);
    validateOptionalFieldNotNull(self, valueConstraints, VALUE_CONSTRAINTS);
    validateOptionalFieldNotNull(self, annotations, "annotations");

    String kind = self.getClass().getSimpleName();

    if (minItems.isPresent() && minItems.get() < 0)
      throw new IllegalStateException("minItems must be zero or greater in " + kind + " " + name);

    if (maxItems.isPresent() && maxItems.get() < 1)
      throw new IllegalStateException("maxItems must be one or greater in " + kind + " " + name);

    if (minItems.isPresent() && maxItems.isPresent() && (minItems.get() > maxItems.get()))
      throw new IllegalStateException("minItems must be less than or equal to maxItems in " + kind + " " + name);
  }

  /**
   * Variant for the static-content field records (section break, page break, image, YouTube, rich text)
   * which do not carry isMultiple / minItems / maxItems / propertyUri.
   */
  static void validateStatic(Object self,
                             LinkedHashMap<String, URI> jsonLdContext,
                             List<URI> jsonLdTypes,
                             Optional<String> preferredLabel,
                             List<String> alternateLabels,
                             Optional<String> language,
                             FieldUi fieldUi,
                             Optional<ValueConstraints> valueConstraints,
                             Optional<Annotations> annotations)
  {
    validateMapFieldNotNull(self, jsonLdContext, JSON_LD_CONTEXT);
    validateUriListFieldContainsOneOf(self, jsonLdTypes, JSON_LD_TYPE, ALLOWED_TYPE_IRIS);
    validateOptionalFieldNotNull(self, preferredLabel, SKOS_PREFLABEL);
    validateListFieldNotNull(self, alternateLabels, SKOS_ALTLABEL);
    validateOptionalFieldNotNull(self, language, "language");
    validateUiFieldNotNull(self, fieldUi, UI);
    validateOptionalFieldNotNull(self, valueConstraints, VALUE_CONSTRAINTS);
    validateOptionalFieldNotNull(self, annotations, "annotations");
  }

  /**
   * The canonical JSON-LD @context prefix mapping that every field record uses,
   * selected by whether the field is static (e.g. section break, image) or not.
   */
  static LinkedHashMap<String, URI> canonicalContext(FieldUi fieldUi)
  {
    return new LinkedHashMap<>(fieldUi.isStatic()
      ? STATIC_FIELD_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS
      : FIELD_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS);
  }

  /**
   * Static-only context. Used by records (page break, section break, image, etc.) that
   * by construction always serialize with the static prefix mapping.
   */
  static LinkedHashMap<String, URI> staticContext()
  {
    return new LinkedHashMap<>(STATIC_FIELD_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS);
  }
}
