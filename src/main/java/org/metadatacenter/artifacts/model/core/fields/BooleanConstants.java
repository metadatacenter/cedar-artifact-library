package org.metadatacenter.artifacts.model.core.fields;

/**
 * Wire-format names specific to the boolean field type and its value constraints.
 * <p>
 * The general model node names live in {@code org.metadatacenter.model.ModelNodeNames}; these are
 * the boolean-specific additions the core model library does not yet declare. They are gathered
 * here so the field input type, the value-constraints reader/renderer pairs, and the tests all
 * refer to a single definition.
 */
public final class BooleanConstants
{
  private BooleanConstants() {}

  /** The {@code _ui.inputType} wire value for a boolean field. */
  public static final String FIELD_INPUT_TYPE_BOOLEAN = "boolean";

  /** The {@code _valueConstraints.nullEnabled} key. */
  public static final String VALUE_CONSTRAINTS_NULL_ENABLED = "nullEnabled";

  /** The {@code _valueConstraints.labels} key holding the display labels map. */
  public static final String VALUE_CONSTRAINTS_LABELS = "labels";

  /** The {@code labels} map key for the true value's display label. */
  public static final String VALUE_CONSTRAINTS_LABEL_TRUE = "true";

  /** The {@code labels} map key for the false value's display label. */
  public static final String VALUE_CONSTRAINTS_LABEL_FALSE = "false";

  /** The {@code labels} map key for the null value's display label. */
  public static final String VALUE_CONSTRAINTS_LABEL_NULL = "null";
}
