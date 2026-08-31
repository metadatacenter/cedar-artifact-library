package org.metadatacenter.artifacts.model.core;

import java.util.Optional;

/**
 * The properties common to every schema artifact, whether it is a template, a template element or a field.
 *
 * <p>A schema artifact carries two names and two descriptions, and they denote different things. {@link #name()}
 * and {@link #description()} belong to the artifact: an author supplies them and a user reads them.
 * {@link #internalName()} and {@link #internalDescription()} describe the JSON Schema that constrains instances
 * of the artifact, not the artifact itself. A caller that wants what an author wrote reads the first pair.
 *
 * <p>The distinction survives into the JSON serialization, where the first pair renders as {@code schema:name}
 * and {@code schema:description} and the second as the JSON Schema keywords {@code title} and
 * {@code description}. Consume the model rather than those keys.
 *
 * <p>The remaining properties are optional. An identifier is an author-supplied string, distinct from the
 * artifact's {@link JsonLdArtifact#jsonLdId() IRI} and unconstrained by CEDAR. A language names the natural
 * language of the artifact's text. Annotations hold IRI-keyed metadata that the model does not otherwise
 * account for.
 */
public interface SchemaArtifact extends Artifact, JsonLdArtifact, VersionedArtifact, MonitoredArtifact
{
  /**
   * @return The artifact's own name, the one an author supplies. Renders as {@code schema:name}.
   */
  String name();

  /**
   * @return The artifact's own description, the one an author supplies. Renders as {@code schema:description}.
   */
  String description();

  Optional<String> identifier();

  Optional<String> language();

  Optional<Annotations> annotations();

  /**
   * @return The name of the JSON Schema constraining instances of this artifact, not of the artifact itself.
   * Renders as the JSON Schema keyword {@code title}. For the artifact's own name, see {@link #name()}.
   */
  String internalName();

  /**
   * @return The description of the JSON Schema constraining instances of this artifact, not of the artifact
   * itself. Renders as the JSON Schema keyword {@code description}. For the artifact's own description, see
   * {@link #description()}.
   */
  String internalDescription();
}
