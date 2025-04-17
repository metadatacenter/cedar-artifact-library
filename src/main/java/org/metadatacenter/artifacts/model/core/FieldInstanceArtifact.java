package org.metadatacenter.artifacts.model.core;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateListFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateOptionalFieldNotNull;
import static org.metadatacenter.model.ModelNodeNames.*;

public sealed interface FieldInstanceArtifact extends ChildInstanceArtifact permits TextFieldInstance,
    TextAreaFieldInstance, TemporalFieldInstance, NumericFieldInstance,
    EmailFieldInstance, CheckboxFieldInstance, ListFieldInstance, PhoneNumberFieldInstance, RadioFieldInstance,
    ControlledTermFieldInstance, LinkFieldInstance, RorFieldInstance, OrcidFieldInstance,
    FieldInstanceArtifactRecord {
  static FieldInstanceArtifact create(List<URI> jsonLdTypes, Optional<URI> jsonLdId,
                                      Optional<String> jsonLdValue, Optional<String> label, Optional<String> notation
      , Optional<String> preferredLabel,
                                      Optional<String> language) {
    return new FieldInstanceArtifactRecord(jsonLdTypes, jsonLdId, jsonLdValue, label, notation,
        preferredLabel, language);
  }

  List<URI> jsonLdTypes();

  Optional<URI> jsonLdId();

  Optional<String> jsonLdValue();

  Optional<String> label();

  Optional<String> notation();

  Optional<String> preferredLabel();

  Optional<String> language();

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
                                   Optional<String> language)
    implements FieldInstanceArtifact {
  public FieldInstanceArtifactRecord {
    validateListFieldNotNull(this, jsonLdTypes, JSON_LD_TYPE);
    validateOptionalFieldNotNull(this, jsonLdValue, JSON_LD_VALUE);
    validateOptionalFieldNotNull(this, jsonLdId, JSON_LD_ID);
    validateOptionalFieldNotNull(this, label, RDFS_LABEL);
    validateOptionalFieldNotNull(this, language, JSON_LD_LANGUAGE);
    validateOptionalFieldNotNull(this, notation, SKOS_NOTATION);
    validateOptionalFieldNotNull(this, preferredLabel, SKOS_PREFLABEL);
  }
}

