package org.metadatacenter.artifacts.model.core;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.Optional;

import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateListFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateAtMostOneFieldInstanceType;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateOptionalFieldNotNull;
import static org.metadatacenter.model.ModelNodeNames.*;

public sealed interface FieldInstanceArtifact extends ChildInstanceArtifact permits TextFieldInstance,
    TextAreaFieldInstance, TemporalFieldInstance, NumericFieldInstance,
    EmailFieldInstance, CheckboxFieldInstance, ListFieldInstance, PhoneNumberFieldInstance, RadioFieldInstance,
    ControlledTermFieldInstance, LinkFieldInstance, RorFieldInstance, OrcidFieldInstance, PfasFieldInstance,
    RridFieldInstance, PubMedFieldInstance, NihGrantIdFieldInstance, DoiFieldInstance, FieldInstanceArtifactRecord {
  static FieldInstanceArtifact create(List<URI> jsonLdTypes, Optional<URI> jsonLdId,
                                      Optional<String> jsonLdValue, Optional<String> label, Optional<String> notation,
                                      Optional<String> preferredLabel, Optional<String> language) {
    return new FieldInstanceArtifactRecord(jsonLdTypes, jsonLdId, jsonLdValue, label, notation,
        preferredLabel, language, true, jsonLdId.map(URI::toString), StatedNulls.NONE);
  }

  /**
   * As above, saying whether the document wrote an {@code @value} key for this field.
   *
   * Only a reader working without the template calls this. An unfilled field is written {@code {}} on a
   * controlled-term or link field and {@code {"@value": null}} on a literal one, and each is refused
   * where the other belongs: a controlled-term field's sub-schema allows no {@code @value}, and a
   * literal field's requires one. Nothing in the instance says which kind the field is, so the shape
   * the document chose is the only evidence there is, and it is kept rather than normalised.
   */
  static FieldInstanceArtifact create(List<URI> jsonLdTypes, Optional<URI> jsonLdId,
                                      Optional<String> jsonLdValue, Optional<String> label, Optional<String> notation,
                                      Optional<String> preferredLabel, Optional<String> language,
                                      boolean carriesValueKey) {
    return new FieldInstanceArtifactRecord(jsonLdTypes, jsonLdId, jsonLdValue, label, notation,
        preferredLabel, language, carriesValueKey, jsonLdId.map(URI::toString), StatedNulls.NONE);
  }

  /** Construct a field preserving its exact RDF IRI spelling, including valid Unicode separators. */
  static FieldInstanceArtifact createWithIri(List<URI> jsonLdTypes, Optional<String> jsonLdIdIri,
      Optional<String> jsonLdValue, Optional<String> label, Optional<String> notation,
      Optional<String> preferredLabel, Optional<String> language, boolean carriesValueKey) {
    return createWithIri(jsonLdTypes, jsonLdIdIri, jsonLdValue, label, notation, preferredLabel, language,
        carriesValueKey, Set.of());
  }

  /** As above, recording the optional value keys the document stated as null. */
  static FieldInstanceArtifact createWithIri(List<URI> jsonLdTypes, Optional<String> jsonLdIdIri,
      Optional<String> jsonLdValue, Optional<String> label, Optional<String> notation,
      Optional<String> preferredLabel, Optional<String> language, boolean carriesValueKey, Set<String> statedNulls) {
    Optional<URI> transportId = jsonLdIdIri.map(value -> {
      if (value.isEmpty()) throw new IllegalArgumentException("An empty string is not a field IRI");
      try { return org.metadatacenter.model.validation.IriReference.toUri(value); }
      catch (java.net.URISyntaxException e) { throw new IllegalArgumentException("Invalid field IRI", e); }
    });
    return new FieldInstanceArtifactRecord(jsonLdTypes, transportId, jsonLdValue, label, notation,
        preferredLabel, language, carriesValueKey, jsonLdIdIri, StatedNulls.of(statedNulls));
  }

  /** Exact RDF identifier. Use this for identity and serialization; jsonLdId() is a URI view. */
  default Optional<String> jsonLdIdIri() { return jsonLdId().map(URI::toString); }

  List<URI> jsonLdTypes();

  Optional<URI> jsonLdId();

  Optional<String> jsonLdValue();

  Optional<String> label();

  Optional<String> notation();

  Optional<String> preferredLabel();

  Optional<String> language();

  /**
   * Whether the document this was read from wrote an {@code @value} key, null or otherwise.
   *
   * True for everything built rather than read: a built instance is one of the typed kinds above, and
   * each renders the shape its kind calls for. Only the untyped record answers false, and only when the
   * document it came from wrote {@code {}}.
   */
  default boolean carriesValueKey() {
    return true;
  }

  /**
   * The optional value keys the document this was read from stated as null, and those the writer keeps.
   * None for anything built rather than read. See {@link StatedNulls}.
   */
  @JsonIgnore
  default StatedNulls statedNulls() {
    return StatedNulls.NONE;
  }

  /** This value, keeping each stated null among {@code required}, the keys its stored value schema requires. */
  default FieldInstanceArtifact keepingStatedNulls(Collection<String> required) {
    return this;
  }

  @Override
  default void accept(InstanceArtifactVisitor visitor, String path) {
    visitor.visitFieldInstanceArtifact(this, path);
  }

  @Override
  default void accept(InstanceArtifactVisitor visitor, String path, String specificationPath) {
    visitor.visitAttributeValueFieldInstanceArtifact(this, path, specificationPath);
  }
}

record FieldInstanceArtifactRecord(List<URI> jsonLdTypes, Optional<URI> jsonLdId, Optional<String> jsonLdValue,
                                   Optional<String> label, Optional<String> notation, Optional<String> preferredLabel,
                                   Optional<String> language, boolean carriesValueKey, Optional<String> jsonLdIdIri,
                                   @JsonIgnore StatedNulls statedNulls)
    implements FieldInstanceArtifact {
  public FieldInstanceArtifactRecord {
    validateListFieldNotNull(this, jsonLdTypes, JSON_LD_TYPE);
    validateAtMostOneFieldInstanceType(this, jsonLdTypes);
    validateOptionalFieldNotNull(this, jsonLdValue, JSON_LD_VALUE);
    validateOptionalFieldNotNull(this, jsonLdId, JSON_LD_ID);
    validateOptionalFieldNotNull(this, jsonLdIdIri, JSON_LD_ID);
    validateOptionalFieldNotNull(this, label, RDFS_LABEL);
    validateOptionalFieldNotNull(this, language, JSON_LD_LANGUAGE);
    validateOptionalFieldNotNull(this, notation, SKOS_NOTATION);
    validateOptionalFieldNotNull(this, preferredLabel, SKOS_PREFLABEL);
    java.util.Objects.requireNonNull(statedNulls, "statedNulls");
    jsonLdTypes = List.copyOf(jsonLdTypes);
  }

  @Override public FieldInstanceArtifact keepingStatedNulls(Collection<String> required) {
    StatedNulls kept = statedNulls.keeping(required);
    return kept == statedNulls ? this : new FieldInstanceArtifactRecord(jsonLdTypes, jsonLdId, jsonLdValue, label,
        notation, preferredLabel, language, carriesValueKey, jsonLdIdIri, kept);
  }
}
