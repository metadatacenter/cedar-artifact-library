package org.metadatacenter.artifacts.model.core;

import java.util.Set;

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
 * The sets are the validation library's {@link org.metadatacenter.model.validation.ReservedNames},
 * which the validator asks too, so what this library refuses to read the validator refuses to pass.
 * The TypeScript model library's {@code ReservedNames} answers the same two questions with the
 * same sets, and the editors ask whichever library they are built on.
 */
public final class ReservedNames
{
  /** The artifact an attribute-value field is a child of, which decides the YAML keys beside it. */
  public enum AttributeValueFieldParent { TEMPLATE, ELEMENT }

  /** The metadata keys of a template instance's YAML mapping. */
  public static final Set<String> TEMPLATE_INSTANCE_YAML_KEYS =
    org.metadatacenter.model.validation.ReservedNames.TEMPLATE_INSTANCE_YAML_KEYS;

  /** The metadata keys of an element instance's YAML mapping when it is written inside its parent. */
  public static final Set<String> NESTED_ELEMENT_INSTANCE_YAML_KEYS =
    org.metadatacenter.model.validation.ReservedNames.NESTED_ELEMENT_INSTANCE_YAML_KEYS;

  /** The metadata keys of an element instance's YAML mapping when it is written on its own. */
  public static final Set<String> STANDALONE_ELEMENT_INSTANCE_YAML_KEYS =
    org.metadatacenter.model.validation.ReservedNames.STANDALONE_ELEMENT_INSTANCE_YAML_KEYS;

  private ReservedNames() {}

  /** Whether no child, and no attribute a form-filler invents, may take this name. */
  public static boolean isReservedName(String name)
  {
    return org.metadatacenter.model.validation.ReservedNames.isReservedName(name);
  }

  /** Whether an attribute-value field that is a child of {@code parent} may not take this name. */
  public static boolean isReservedAttributeValueFieldName(String name, AttributeValueFieldParent parent)
  {
    return org.metadatacenter.model.validation.ReservedNames.isReservedAttributeValueFieldName(name,
      parent == AttributeValueFieldParent.TEMPLATE ? org.metadatacenter.model.validation.ReservedNames.Parent.TEMPLATE
        : org.metadatacenter.model.validation.ReservedNames.Parent.ELEMENT);
  }

  /** Refuse an attribute-value field name {@link #isReservedAttributeValueFieldName} rejects. */
  static void requireAttributeValueFieldName(Object self, String name, AttributeValueFieldParent parent)
  {
    if (isReservedAttributeValueFieldName(name, parent))
      throw new IllegalStateException("attribute-value field name \"" + name + "\" in "
        + self.getClass().getSimpleName() + " is reserved for CEDAR instance metadata");
  }
}
