package org.metadatacenter.artifacts.model.tools;

import org.metadatacenter.artifacts.model.core.ChildSchemaArtifact;
import org.metadatacenter.artifacts.model.core.ElementInstanceArtifact;
import org.metadatacenter.artifacts.model.core.ElementSchemaArtifact;
import org.metadatacenter.artifacts.model.core.FieldInstanceArtifact;
import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifacts.model.core.ParentInstanceArtifact;
import org.metadatacenter.artifacts.model.core.ParentSchemaArtifact;
import org.metadatacenter.artifacts.model.core.TemplateInstanceArtifact;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Reconstructs a complete template instance from a (possibly sparse) instance and its template.
 *
 * <p>A sparse instance omits fields with no value entirely (no {@code value: null}, no {@code {}}) —
 * the lean form a YAML serialization produces. But the CEDAR <em>JSON</em> instance must mirror the
 * template — every non-static, non-attribute-value field present, even when empty, because the
 * template's JSON Schema marks those properties required. That "all fields present" rule is a JSON-
 * serialization concern, not a property of the model. This class bridges the two — it re-adds the
 * missing empty slots (recursing into elements) while preserving every value the sparse instance
 * already carries, so downstream serialization and validation operate on a complete instance.
 *
 * <p>Inflation also <em>canonicalizes child order</em>: children are emitted in the schema's
 * display order ({@code _ui.order}), whatever order the incoming instance carried — order never
 * affects validity, but serializing instances the way their template orders its fields keeps
 * them readable and diffable. Present values are preserved untouched; keys the schema doesn't
 * know are never touched and stay ahead of the schema-ordered children.
 */
public final class InstanceInflater
{
  private InstanceInflater() {}

  /** The per-builder operations {@link #fillParent} needs; bridges the two builder types. */
  private record Ops(
      BiConsumer<String, FieldInstanceArtifact> putSingleField,
      BiConsumer<String, List<FieldInstanceArtifact>> putMultiField,
      BiConsumer<String, ElementInstanceArtifact> putSingleElement,
      BiConsumer<String, List<ElementInstanceArtifact>> putMultiElement,
      BiConsumer<String, LinkedHashMap<String, FieldInstanceArtifact>> putAttrGroup,
      Consumer<String> removeSingleField,
      Consumer<String> removeMultiField,
      Consumer<String> removeSingleElement,
      Consumer<String> removeMultiElement,
      Consumer<String> removeAttrGroup) {}

  private static Ops opsFor(TemplateInstanceArtifact.Builder builder)
  {
    return new Ops(
        builder::withSingleInstanceFieldInstance, builder::withMultiInstanceFieldInstances,
        builder::withSingleInstanceElementInstance, builder::withMultiInstanceElementInstances,
        builder::withAttributeValueFieldGroup,
        builder::withoutSingleInstanceFieldInstance, builder::withoutMultiInstanceFieldInstances,
        builder::withoutSingleInstanceElementInstance, builder::withoutMultiInstanceElementInstances,
        builder::withoutAttributeValueFieldGroup);
  }

  private static Ops opsFor(ElementInstanceArtifact.Builder builder)
  {
    return new Ops(
        builder::withSingleInstanceFieldInstance, builder::withMultiInstanceFieldInstances,
        builder::withSingleInstanceElementInstance, builder::withMultiInstanceElementInstances,
        builder::withAttributeValueFieldGroup,
        builder::withoutSingleInstanceFieldInstance, builder::withoutMultiInstanceFieldInstances,
        builder::withoutSingleInstanceElementInstance, builder::withoutMultiInstanceElementInstances,
        builder::withoutAttributeValueFieldGroup);
  }

  /** Inflate a (possibly sparse) template instance to a complete one against its template. */
  public static TemplateInstanceArtifact inflate(TemplateSchemaArtifact template, TemplateInstanceArtifact sparse)
  {
    TemplateInstanceArtifact.Builder builder = TemplateInstanceArtifact.builder(sparse);
    ensureContext(template.getChildPropertyUris(), sparse.jsonLdContext(), builder::withJsonLdContextEntry);
    fillParent(template, sparse, opsFor(builder));
    return builder.build();
  }

  /** Inflate a (possibly sparse) element instance to a complete one against its element schema. */
  public static ElementInstanceArtifact inflateElement(ElementSchemaArtifact schema, ElementInstanceArtifact sparse)
  {
    ElementInstanceArtifact.Builder builder = ElementInstanceArtifact.builder(sparse);
    ensureContext(schema.getChildPropertyUris(), sparse.jsonLdContext(), builder::withJsonLdContextEntry);
    fillParent(schema, sparse, opsFor(builder));
    return builder.build();
  }

  /** An all-empty element instance matching the element schema — every regular child present, value-less. */
  public static ElementInstanceArtifact emptyElement(ElementSchemaArtifact schema)
  {
    ElementInstanceArtifact.Builder builder = ElementInstanceArtifact.builder();
    for (Map.Entry<String, URI> entry : schema.getChildPropertyUris().entrySet())
      builder.withJsonLdContextEntry(entry.getKey(), entry.getValue());
    fillParent(schema, null, opsFor(builder));
    return builder.build();
  }

  /**
   * How many occurrences the child's JSON Schema will demand.
   *
   * A template always states a lower bound for a multi-instance child, and states
   * {@link ChildSchemaArtifact#DEFAULT_MIN_ITEMS} when the artifact names none, so an empty array
   * fails the template unless the schema asked for zero. Completing an instance has to satisfy
   * that bound the same way it fills a missing single field.
   */
  private static int lowerBound(ChildSchemaArtifact child)
  {
    return child.minItems().orElse(ChildSchemaArtifact.DEFAULT_MIN_ITEMS);
  }

  private static void ensureContext(Map<String, URI> required, Map<String, URI> existing,
      BiConsumer<String, URI> put)
  {
    for (Map.Entry<String, URI> entry : required.entrySet())
      if (!existing.containsKey(entry.getKey()))
        put.accept(entry.getKey(), entry.getValue());
  }

  /**
   * Hand an attribute-value field's key back before its group claims it.
   *
   * An instance is read without its template, so an empty array names no kind. {@code
   * JsonArtifactReader} records one as an empty multi-instance field, which is one of the three
   * things it could be, and an attribute-value field that names no attribute is written as
   * exactly that empty array. The schema is in hand here and says which of the three it is, so
   * the reader's provisional choice is withdrawn — the builders reject a key that is already
   * registered, and the group cannot take a key the misreading still holds.
   *
   * Only an empty list is withdrawn. A child carrying values is a disagreement between the
   * schema and the instance about what that child is, and is reported rather than discarded.
   *
   * @throws IllegalArgumentException if the instance holds the key as anything but an empty
   *   multi-instance field.
   */
  private static void releaseAttributeValueChildKey(ParentInstanceArtifact existing, String childKey, Ops ops)
  {
    if (existing == null || !existing.childKeys().contains(childKey))
      return;

    List<FieldInstanceArtifact> misread = existing.multiInstanceFieldInstances().get(childKey);

    if (misread == null || !misread.isEmpty())
      throw new IllegalArgumentException("child " + childKey
          + " is an attribute-value field in the schema, but the instance holds it as something"
          + " other than a list of attribute names");

    ops.removeMultiField().accept(childKey);
  }

  /**
   * Describe how the instance holds a child, for an error that names the disagreement.
   *
   * @return the slot the instance uses, or null when it does not hold the child at all
   */
  private static String slotHolding(ParentInstanceArtifact existing, String childKey)
  {
    if (existing == null)
      return null;
    if (existing.singleInstanceFieldInstances().containsKey(childKey))
      return "a single field";
    if (existing.multiInstanceFieldInstances().containsKey(childKey))
      return "a list of fields";
    if (existing.singleInstanceElementInstances().containsKey(childKey))
      return "a single element";
    if (existing.multiInstanceElementInstances().containsKey(childKey))
      return "a list of elements";
    if (existing.attributeValueFieldInstanceGroups().containsKey(childKey))
      return "an attribute-value group";
    return existing.childKeys().contains(childKey) ? "something unrecognised" : null;
  }

  /**
   * Refuse a child the instance holds in a different slot from the one the schema declares.
   *
   * The builders reject a key that is already registered, so without this the disagreement
   * surfaced as {@code child X already present in instance} — which says a key collided without
   * saying that the instance and its template disagree about what the child is. Every instance
   * seen with this shape was already invalid against its own template, an array where an object
   * was required, so the useful outcome is a diagnosis rather than a repair.
   *
   * @throws IllegalArgumentException when the instance holds the child in another slot
   */
  private static void requireMatchingSlot(ParentInstanceArtifact existing, String childKey,
      String schemaSlot, String instanceSlot)
  {
    if (instanceSlot == null || instanceSlot.equals(schemaSlot))
      return;
    throw new IllegalArgumentException("the instance and its template disagree about child "
        + childKey + ": the template declares " + schemaSlot + " and the instance holds "
        + instanceSlot);
  }

  /**
   * Walk the schema's children in display order and (re-)emit each one: a child the instance is
   * missing gets the empty slot the JSON form requires; a child it carries is removed and
   * re-added — values untouched, elements recursively inflated — so the children end up in the
   * schema's order regardless of the order the instance arrived in. {@code existing} is null
   * when building a fresh empty element — then every child is added empty.
   */
  private static void fillParent(ParentSchemaArtifact schema, ParentInstanceArtifact existing, Ops ops)
  {
    for (String childKey : schema.getUi().order()) {
      if (schema.isStaticField(childKey))
        continue;

      if (schema.isAttributeValueField(childKey)) {
        if (existing != null && existing.attributeValueFieldInstanceGroups().containsKey(childKey)) {
          LinkedHashMap<String, FieldInstanceArtifact> group =
              new LinkedHashMap<>(existing.attributeValueFieldInstanceGroups().get(childKey));
          ops.removeAttrGroup().accept(childKey);
          ops.putAttrGroup().accept(childKey, group);
        } else {
          releaseAttributeValueChildKey(existing, childKey, ops);
          ops.putAttrGroup().accept(childKey, new LinkedHashMap<>());
        }
        continue;
      }

      if (schema.isField(childKey)) {
        FieldSchemaArtifact field = schema.getFieldSchemaArtifact(childKey);
        String holding = slotHolding(existing, childKey);
        if (field.isMultiple()) {
          requireMatchingSlot(existing, childKey, "a list of fields", holding);
          List<FieldInstanceArtifact> values = new ArrayList<>();
          if (existing != null && existing.multiInstanceFieldInstances().containsKey(childKey)) {
            values.addAll(existing.multiInstanceFieldInstances().get(childKey));
            ops.removeMultiField().accept(childKey);
          }
          while (values.size() < lowerBound(field))
            values.add(EmptyFieldInstances.emptyFor(field));
          ops.putMultiField().accept(childKey, List.copyOf(values));
        } else {
          requireMatchingSlot(existing, childKey, "a single field", holding);
          if (existing != null && existing.singleInstanceFieldInstances().containsKey(childKey)) {
            FieldInstanceArtifact value = existing.singleInstanceFieldInstances().get(childKey);
            ops.removeSingleField().accept(childKey);
            ops.putSingleField().accept(childKey, value);
          } else {
            ops.putSingleField().accept(childKey, EmptyFieldInstances.emptyFor(field));
          }
        }
      } else if (schema.isElement(childKey)) {
        ElementSchemaArtifact element = schema.getElementSchemaArtifact(childKey);
        String holding = slotHolding(existing, childKey);
        if (element.isMultiple()) {
          requireMatchingSlot(existing, childKey, "a list of elements", holding);
          List<ElementInstanceArtifact> inflated = new ArrayList<>();
          if (existing != null && existing.multiInstanceElementInstances().containsKey(childKey)) {
            for (ElementInstanceArtifact e : existing.multiInstanceElementInstances().get(childKey))
              inflated.add(inflateElement(element, e));
            ops.removeMultiElement().accept(childKey);
          }
          while (inflated.size() < lowerBound(element))
            inflated.add(emptyElement(element));
          ops.putMultiElement().accept(childKey, List.copyOf(inflated));
        } else {
          requireMatchingSlot(existing, childKey, "a single element", holding);
          if (existing != null && existing.singleInstanceElementInstances().containsKey(childKey)) {
            ElementInstanceArtifact inflated =
                inflateElement(element, existing.singleInstanceElementInstances().get(childKey));
            ops.removeSingleElement().accept(childKey);
            ops.putSingleElement().accept(childKey, inflated);
          } else {
            ops.putSingleElement().accept(childKey, emptyElement(element));
          }
        }
      }
    }
  }
}
