package org.metadatacenter.artifacts.model.core;

import org.metadatacenter.artifacts.model.core.ui.Ui;

import java.net.URI;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateMapFieldContainsAll;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateMapFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateOptionalFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateStringFieldNotEmpty;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateStringFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateUiFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateUriListFieldContains;
import static org.metadatacenter.model.ModelNodeNames.BIBO_STATUS;
import static org.metadatacenter.model.ModelNodeNames.JSON_LD_CONTEXT;
import static org.metadatacenter.model.ModelNodeNames.JSON_LD_ID;
import static org.metadatacenter.model.ModelNodeNames.JSON_LD_TYPE;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_DESCRIPTION;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_TITLE;
import static org.metadatacenter.model.ModelNodeNames.PARENT_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS;
import static org.metadatacenter.model.ModelNodeNames.PAV_DERIVED_FROM;
import static org.metadatacenter.model.ModelNodeNames.PAV_PREVIOUS_VERSION;
import static org.metadatacenter.model.ModelNodeNames.PAV_VERSION;
import static org.metadatacenter.model.ModelNodeNames.SCHEMA_ORG_DESCRIPTION;
import static org.metadatacenter.model.ModelNodeNames.SCHEMA_ORG_NAME;
import static org.metadatacenter.model.ModelNodeNames.UI;

/**
 * Shared compact-constructor invariants for parent-schema records (template + element).
 * <p>
 * Both records previously open-coded the same ~15 line validation block, with subtle drift:
 * {@code TemplateSchemaArtifactRecord} validated version/status/previousVersion/derivedFrom
 * while {@code ElementSchemaArtifactRecord} did not, despite carrying the same fields.
 * This helper covers the shared invariants, plus the "drop children not present in the UI order"
 * cleanup logic which is identical between the two records.
 */
final class ParentSchemaArtifactInvariants
{
  private ParentSchemaArtifactInvariants() {}

  /**
   * Validate the invariants shared by template-schema and element-schema records.
   * <p>
   * Throws {@link IllegalStateException} on violation.
   */
  static void validate(Object self,
                       String internalName,
                       String internalDescription,
                       String name,
                       String description,
                       LinkedHashMap<String, URI> jsonLdContext,
                       List<URI> jsonLdTypes,
                       URI requiredJsonLdTypeIri,
                       Optional<URI> jsonLdId,
                       Optional<URI> instanceJsonLdType,
                       Optional<Version> version,
                       Optional<Status> status,
                       Optional<URI> previousVersion,
                       Optional<URI> derivedFrom,
                       Map<String, ?> fieldSchemas,
                       Map<String, ?> elementSchemas,
                       Optional<String> language,
                       Ui ui,
                       Optional<Annotations> annotations)
  {
    validateStringFieldNotNull(self, internalName, JSON_SCHEMA_TITLE);
    validateStringFieldNotNull(self, internalDescription, JSON_SCHEMA_DESCRIPTION);
    validateStringFieldNotEmpty(self, name, SCHEMA_ORG_NAME);
    validateStringFieldNotNull(self, description, SCHEMA_ORG_DESCRIPTION);
    validateOptionalFieldNotNull(self, version, PAV_VERSION);
    validateOptionalFieldNotNull(self, status, BIBO_STATUS);
    validateOptionalFieldNotNull(self, previousVersion, PAV_PREVIOUS_VERSION);
    validateOptionalFieldNotNull(self, derivedFrom, PAV_DERIVED_FROM);
    validateMapFieldContainsAll(self, jsonLdContext, JSON_LD_CONTEXT, PARENT_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS);
    validateUriListFieldContains(self, jsonLdTypes, JSON_LD_TYPE, requiredJsonLdTypeIri);
    validateOptionalFieldNotNull(self, jsonLdId, JSON_LD_ID);
    validateOptionalFieldNotNull(self, instanceJsonLdType, "instanceJsonLdType");
    validateMapFieldNotNull(self, fieldSchemas, "fieldSchemas");
    validateMapFieldNotNull(self, elementSchemas, "elementSchemas");
    validateOptionalFieldNotNull(self, language, "language");
    validateUiFieldNotNull(self, ui, UI);
    validateOptionalFieldNotNull(self, annotations, "annotations");
  }

  /**
   * Range-check minItems and maxItems for records that have them (element schema artifacts).
   * Throws {@link IllegalStateException} on violation.
   */
  static void validateItemBounds(Object self, String name, Optional<Integer> minItems, Optional<Integer> maxItems)
  {
    String kind = self.getClass().getSimpleName();

    if (minItems.isPresent() && minItems.get() < 0)
      throw new IllegalStateException("minItems must be zero or greater in " + kind + " " + name);

    if (maxItems.isPresent() && maxItems.get() < 1)
      throw new IllegalStateException("maxItems must be one or greater in " + kind + " " + name);

    if (minItems.isPresent() && maxItems.isPresent() && (minItems.get() > maxItems.get()))
      throw new IllegalStateException("minItems must be less than or equal to maxItems in " + kind + " " + name);
  }

  /**
   * Return a copy of {@code children} with any entry whose key is absent from the UI {@code order}
   * list removed. The input map is not modified.
   * <p>
   * This is a silent cleanup — the original behavior in both records. Order/child drift is not
   * flagged as an error because callers (readers, builders) may legitimately produce intermediate
   * states where they diverge.
   */
  static <T extends SchemaArtifact> LinkedHashMap<String, T> prunedToOrder(LinkedHashMap<String, T> children,
                                                                           List<String> uiOrder)
  {
    Set<String> order = new HashSet<>(uiOrder);
    LinkedHashMap<String, T> pruned = new LinkedHashMap<>(children);
    pruned.keySet().removeIf(childKey -> !order.contains(childKey));
    return pruned;
  }
}
