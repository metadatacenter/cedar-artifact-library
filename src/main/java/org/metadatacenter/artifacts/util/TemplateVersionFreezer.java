package org.metadatacenter.artifacts.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.metadatacenter.artifacts.model.core.fields.constraints.VersionSpec;

import java.net.URI;
import java.util.Optional;
import java.util.function.Function;

/**
 * Freeze-on-publish over a whole template/element document (VERSIONING-DESIGN §7). Walks the template
 * JSON, finds every field's {@code _valueConstraints}, and stamps each <b>unpinned</b> controlled-term
 * entry with its current version triple — so a published template locks its vocabulary state instead
 * of drifting with "latest".
 *
 * The walk is deliberately <b>surgical</b>: it only <i>adds</i> a {@code version} object to entries
 * that lack one and that the resolver can resolve. It never reshapes, reorders, or removes anything
 * else, so a published document is byte-identical to before except for the added pins — critical for
 * a publish path that must not disturb existing content. An entry already carrying a {@code version},
 * or one the resolver cannot resolve, is left untouched (idempotent, and safely a no-op when the
 * resolver serves nothing — e.g. the terminology local store is off).
 *
 * Mutates the given node in place. The per-entry freeze rule and the injected resolver mirror
 * {@link ControlledTermVersionFreezer}; this variant operates on raw JSON, which is the form a
 * template takes in the publish pipeline.
 */
public final class TemplateVersionFreezer {

  private static final String VALUE_CONSTRAINTS = "_valueConstraints";
  private static final String VERSION = "version";

  /** Freezes every unpinned controlled-term constraint in {@code document}, in place. */
  public static void freeze(JsonNode document, ControlledTermVersionFreezer.VersionResolver resolver) {
    if (document == null) {
      return;
    }
    if (document.isObject()) {
      JsonNode vc = document.get(VALUE_CONSTRAINTS);
      if (vc != null && vc.isObject()) {
        freezeConstraints((ObjectNode) vc, resolver);
      }
      for (JsonNode child : document) {
        freeze(child, resolver); // nested fields live under "properties", elements nest arbitrarily deep
      }
    } else if (document.isArray()) {
      for (JsonNode child : document) {
        freeze(child, resolver);
      }
    }
  }

  private static void freezeConstraints(ObjectNode valueConstraints,
                                        ControlledTermVersionFreezer.VersionResolver resolver) {
    freezeEntries(valueConstraints.get("ontologies"),
        e -> textValue(e, "acronym").flatMap(resolver::currentVersionByAcronym));
    freezeEntries(valueConstraints.get("branches"),
        e -> textValue(e, "acronym").flatMap(resolver::currentVersionByAcronym));
    freezeEntries(valueConstraints.get("classes"),
        e -> textValue(e, "uri").map(URI::create).flatMap(resolver::currentVersionByClassUri));
    freezeEntries(valueConstraints.get("valueSets"),
        e -> textValue(e, "vsCollection").flatMap(resolver::currentVersionByValueSetCollection));
  }

  private static void freezeEntries(JsonNode entries, Function<ObjectNode, Optional<VersionSpec>> resolve) {
    if (entries == null || !entries.isArray()) {
      return;
    }
    for (JsonNode entry : entries) {
      if (entry.isObject() && !entry.has(VERSION)) { // absent version ⇒ latest ⇒ eligible to freeze
        resolve.apply((ObjectNode) entry)
            .ifPresent(v -> ((ObjectNode) entry).set(VERSION, versionNode((ObjectNode) entry, v)));
      }
    }
  }

  /** Serializes a triple as {@code {id, effectiveDate?, declaredVersion?}}, omitting absent parts. */
  private static ObjectNode versionNode(ObjectNode parent, VersionSpec spec) {
    ObjectNode version = parent.objectNode();
    version.put("id", spec.id());
    spec.effectiveDate().ifPresent(d -> version.put("effectiveDate", d));
    spec.declaredVersion().ifPresent(d -> version.put("declaredVersion", d));
    return version;
  }

  private static Optional<String> textValue(ObjectNode entry, String field) {
    JsonNode node = entry.get(field);
    return node != null && node.isTextual() && !node.asText().isBlank()
        ? Optional.of(node.asText()) : Optional.empty();
  }

  private TemplateVersionFreezer() {}
}
