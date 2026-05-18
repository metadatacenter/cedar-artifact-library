package org.metadatacenter.artifacts.model.core;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateListFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateMapFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateOptionalFieldNotNull;
import static org.metadatacenter.model.ModelNodeNames.JSON_LD_CONTEXT;
import static org.metadatacenter.model.ModelNodeNames.JSON_LD_ID;
import static org.metadatacenter.model.ModelNodeNames.JSON_LD_TYPE;
import static org.metadatacenter.model.ModelNodeNames.OSLC_MODIFIED_BY;
import static org.metadatacenter.model.ModelNodeNames.PAV_CREATED_BY;
import static org.metadatacenter.model.ModelNodeNames.PAV_CREATED_ON;
import static org.metadatacenter.model.ModelNodeNames.PAV_LAST_UPDATED_ON;
import static org.metadatacenter.model.ModelNodeNames.SCHEMA_ORG_DESCRIPTION;
import static org.metadatacenter.model.ModelNodeNames.SCHEMA_ORG_NAME;

/**
 * Shared compact-constructor invariants for instance-artifact records (template + element).
 * <p>
 * Both records previously open-coded the same ~14 line validation block on the same
 * field names. This helper covers those shared invariants; record-specific fields
 * ({@code isBasedOn}, {@code derivedFrom}, {@code annotations} on the template flavor)
 * stay inline in the respective compact constructor.
 */
final class InstanceArtifactInvariants
{
  private InstanceArtifactInvariants() {}

  /**
   * Validate the invariants shared by template-instance and element-instance records.
   * <p>
   * Throws {@link IllegalStateException} on violation.
   */
  static void validate(Object self,
                       LinkedHashMap<String, URI> jsonLdContext,
                       List<URI> jsonLdTypes,
                       Optional<URI> jsonLdId,
                       Optional<String> name,
                       Optional<String> description,
                       Optional<URI> createdBy,
                       Optional<URI> modifiedBy,
                       Optional<OffsetDateTime> createdOn,
                       Optional<OffsetDateTime> lastUpdatedOn,
                       List<String> childKeys,
                       Map<String, ?> singleInstanceFieldInstances,
                       Map<String, ?> multiInstanceFieldInstances,
                       Map<String, ?> singleInstanceElementInstances,
                       Map<String, ?> multiInstanceElementInstances,
                       Map<String, ?> attributeValueFieldInstanceGroups)
  {
    validateMapFieldNotNull(self, jsonLdContext, JSON_LD_CONTEXT);
    validateListFieldNotNull(self, jsonLdTypes, JSON_LD_TYPE);
    validateOptionalFieldNotNull(self, jsonLdId, JSON_LD_ID);
    validateOptionalFieldNotNull(self, name, SCHEMA_ORG_NAME);
    validateOptionalFieldNotNull(self, description, SCHEMA_ORG_DESCRIPTION);
    validateOptionalFieldNotNull(self, createdBy, PAV_CREATED_BY);
    validateOptionalFieldNotNull(self, modifiedBy, OSLC_MODIFIED_BY);
    validateOptionalFieldNotNull(self, createdOn, PAV_CREATED_ON);
    validateOptionalFieldNotNull(self, lastUpdatedOn, PAV_LAST_UPDATED_ON);
    validateListFieldNotNull(self, childKeys, "childKeys");
    validateMapFieldNotNull(self, singleInstanceFieldInstances, "singleInstanceFieldInstances");
    validateMapFieldNotNull(self, multiInstanceFieldInstances, "multiInstanceFieldInstances");
    validateMapFieldNotNull(self, singleInstanceElementInstances, "singleInstanceElementInstances");
    validateMapFieldNotNull(self, multiInstanceElementInstances, "multiInstanceElementInstances");
    validateMapFieldNotNull(self, attributeValueFieldInstanceGroups, "attributeValueFieldInstanceGroups");
  }
}
