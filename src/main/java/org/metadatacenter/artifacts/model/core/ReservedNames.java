package org.metadatacenter.artifacts.model.core;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.metadatacenter.artifacts.model.yaml.YamlConstants.ANNOTATIONS;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.CHILDREN;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.CREATED_BY;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.CREATED_ON;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.DERIVED_FROM;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.DESCRIPTION;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.ID;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.IS_BASED_ON;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.MODIFIED_BY;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.MODIFIED_ON;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.NAME;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.TYPE;
import static org.metadatacenter.model.ModelNodeNames.ELEMENT_INSTANCE_ARTIFACT_KEYWORDS;
import static org.metadatacenter.model.ModelNodeNames.FIELD_INSTANCE_ARTIFACT_KEYWORDS;
import static org.metadatacenter.model.ModelNodeNames.TEMPLATE_INSTANCE_ARTIFACT_KEYWORDS;

/**
 * The names a child of a template or element may not take.
 *
 * Every child's key, and every attribute name a form-filler invents for an attribute-value field,
 * becomes a property of an instance's JSON object beside the properties CEDAR writes there itself.
 * Such a name therefore may not be a JSON-LD keyword, a CEDAR instance property or an object
 * internal that JavaScript consumers of the same document cannot hold as an ordinary key.
 *
 * An attribute-value field's own key has one more constraint. The YAML form writes it beside its
 * parent's metadata rather than under {@code children}, so it may not be one of the metadata keys
 * that parent's YAML mapping carries. Elements reserve the union of their nested and standalone metadata keys, so the same
 * element instance remains writable in either form.
 *
 * The TypeScript model library's {@code ReservedNames} answers the same two questions with the
 * same sets, and the editors ask whichever library they are built on.
 */
public final class ReservedNames
{
  /** The artifact an attribute-value field is a child of, which decides the YAML keys beside it. */
  public enum AttributeValueFieldParent { TEMPLATE, ELEMENT }

  private static final Set<String> INSTANCE_PROPERTIES = Stream.of(TEMPLATE_INSTANCE_ARTIFACT_KEYWORDS,
      ELEMENT_INSTANCE_ARTIFACT_KEYWORDS, FIELD_INSTANCE_ARTIFACT_KEYWORDS)
    .flatMap(Set::stream).collect(Collectors.toUnmodifiableSet());

  private static final Set<String> OBJECT_INTERNALS = Set.of("__proto__", "constructor", "prototype");

  /** The metadata keys of a template instance's YAML mapping. */
  public static final Set<String> TEMPLATE_INSTANCE_YAML_KEYS = Set.of(TYPE, NAME, DESCRIPTION, ID, IS_BASED_ON,
    DERIVED_FROM, CREATED_BY, MODIFIED_BY, CREATED_ON, MODIFIED_ON, CHILDREN, ANNOTATIONS);

  /** The metadata keys of an element instance's YAML mapping when it is written inside its parent. */
  public static final Set<String> NESTED_ELEMENT_INSTANCE_YAML_KEYS = Set.of(TYPE, ID, CHILDREN);

  /** The metadata keys of an element instance's YAML mapping when it is written on its own. */
  public static final Set<String> STANDALONE_ELEMENT_INSTANCE_YAML_KEYS = Set.of(TYPE, NAME, DESCRIPTION, ID,
    CREATED_BY, MODIFIED_BY, CREATED_ON, MODIFIED_ON, CHILDREN);

  private ReservedNames() {}

  /** Whether no child, and no attribute a form-filler invents, may take this name. */
  public static boolean isReservedName(String name)
  {
    return name.startsWith("@") || INSTANCE_PROPERTIES.contains(name) || OBJECT_INTERNALS.contains(name);
  }

  /** Whether an attribute-value field that is a child of {@code parent} may not take this name. */
  public static boolean isReservedAttributeValueFieldName(String name, AttributeValueFieldParent parent)
  {
    return isReservedName(name) || yamlKeys(parent).contains(name);
  }

  private static Set<String> yamlKeys(AttributeValueFieldParent parent)
  {
    return parent == AttributeValueFieldParent.TEMPLATE ? TEMPLATE_INSTANCE_YAML_KEYS
      : STANDALONE_ELEMENT_INSTANCE_YAML_KEYS;
  }

  /** Refuse an attribute-value field name {@link #isReservedAttributeValueFieldName} rejects. */
  static void requireAttributeValueFieldName(Object self, String name, AttributeValueFieldParent parent)
  {
    if (isReservedAttributeValueFieldName(name, parent))
      throw new IllegalStateException("attribute-value field name \"" + name + "\" in "
        + self.getClass().getSimpleName() + " is reserved for CEDAR instance metadata");
  }
}
