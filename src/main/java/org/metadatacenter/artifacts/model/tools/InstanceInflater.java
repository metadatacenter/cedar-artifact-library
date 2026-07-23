package org.metadatacenter.artifacts.model.tools;

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

  private static void ensureContext(Map<String, URI> required, Map<String, URI> existing,
      BiConsumer<String, URI> put)
  {
    for (Map.Entry<String, URI> entry : required.entrySet())
      if (!existing.containsKey(entry.getKey()))
        put.accept(entry.getKey(), entry.getValue());
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
          ops.putAttrGroup().accept(childKey, new LinkedHashMap<>());
        }
        continue;
      }

      if (schema.isField(childKey)) {
        FieldSchemaArtifact field = schema.getFieldSchemaArtifact(childKey);
        if (field.isMultiple()) {
          if (existing != null && existing.multiInstanceFieldInstances().containsKey(childKey)) {
            List<FieldInstanceArtifact> values =
                new ArrayList<>(existing.multiInstanceFieldInstances().get(childKey));
            ops.removeMultiField().accept(childKey);
            ops.putMultiField().accept(childKey, values);
          } else {
            ops.putMultiField().accept(childKey, List.of());
          }
        } else {
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
        if (element.isMultiple()) {
          if (existing != null && existing.multiInstanceElementInstances().containsKey(childKey)) {
            List<ElementInstanceArtifact> inflated = new ArrayList<>();
            for (ElementInstanceArtifact e : existing.multiInstanceElementInstances().get(childKey))
              inflated.add(inflateElement(element, e));
            ops.removeMultiElement().accept(childKey);
            ops.putMultiElement().accept(childKey, inflated);
          } else {
            ops.putMultiElement().accept(childKey, List.of());
          }
        } else {
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
