package org.metadatacenter.artifacts.model.core;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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

  /**
   * Check that {@code childKeys} and the child-instance maps agree, in both directions:
   * <ul>
   *   <li><b>No extras in the maps:</b> every key of every child-instance map must be listed in
   *       {@code childKeys}.</li>
   *   <li><b>Every childKey backed:</b> every entry in {@code childKeys} must be accounted for by a
   *       child-instance map key, or by an inner field-instance name of an attribute-value group.
   *       An attribute-value group contributes both its group name (a top-level map key) and each
   *       of its inner field-instance names to {@code childKeys}; the inner names are not separate
   *       top-level map keys.</li>
   * </ul>
   * The builders maintain this by construction; this guards records assembled by other means.
   *
   * @throws IllegalStateException on any mismatch
   */
  static void validateChildKeyConsistency(Object self,
                                          List<String> childKeys,
                                          Map<String, ?> singleInstanceFieldInstances,
                                          Map<String, ?> multiInstanceFieldInstances,
                                          Map<String, ?> singleInstanceElementInstances,
                                          Map<String, ?> multiInstanceElementInstances,
                                          Map<String, ? extends Map<String, ?>> attributeValueFieldInstanceGroups)
  {
    Set<String> declared = new HashSet<>(childKeys);

    checkKeysDeclared(self, declared, singleInstanceFieldInstances.keySet(), "singleInstanceFieldInstances");
    checkKeysDeclared(self, declared, multiInstanceFieldInstances.keySet(), "multiInstanceFieldInstances");
    checkKeysDeclared(self, declared, singleInstanceElementInstances.keySet(), "singleInstanceElementInstances");
    checkKeysDeclared(self, declared, multiInstanceElementInstances.keySet(), "multiInstanceElementInstances");
    checkKeysDeclared(self, declared, attributeValueFieldInstanceGroups.keySet(), "attributeValueFieldInstanceGroups");
    for (Map.Entry<String, ? extends Map<String, ?>> group : attributeValueFieldInstanceGroups.entrySet())
      checkKeysDeclared(self, declared, group.getValue().keySet(), "attribute-value group " + group.getKey());

    Set<String> accountedFor = new HashSet<>();
    accountedFor.addAll(singleInstanceFieldInstances.keySet());
    accountedFor.addAll(multiInstanceFieldInstances.keySet());
    accountedFor.addAll(singleInstanceElementInstances.keySet());
    accountedFor.addAll(multiInstanceElementInstances.keySet());
    accountedFor.addAll(attributeValueFieldInstanceGroups.keySet());
    for (Map<String, ?> attributeValueGroup : attributeValueFieldInstanceGroups.values())
      accountedFor.addAll(attributeValueGroup.keySet());

    for (String childKey : childKeys)
      if (!accountedFor.contains(childKey))
        throw new IllegalStateException("childKey " + childKey + " in " + self.getClass().getSimpleName()
          + " has no corresponding child instance");
  }

  private static void checkKeysDeclared(Object self, Set<String> declaredChildKeys, Set<String> mapKeys,
                                        String mapName)
  {
    for (String key : mapKeys)
      if (!declaredChildKeys.contains(key))
        throw new IllegalStateException("child instance " + key + " in " + mapName + " of "
          + self.getClass().getSimpleName() + " is not declared in childKeys");
  }
}
