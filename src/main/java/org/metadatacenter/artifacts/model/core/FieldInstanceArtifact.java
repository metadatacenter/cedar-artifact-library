package org.metadatacenter.artifacts.model.core;

import org.metadatacenter.artifacts.model.core.builders.CheckboxFieldInstanceBuilder;
import org.metadatacenter.artifacts.model.core.builders.ControlledTermFieldInstanceBuilder;
import org.metadatacenter.artifacts.model.core.builders.EmailFieldInstanceBuilder;
import org.metadatacenter.artifacts.model.core.builders.LinkFieldInstanceBuilder;
import org.metadatacenter.artifacts.model.core.builders.ListFieldInstanceBuilder;
import org.metadatacenter.artifacts.model.core.builders.NumericFieldInstanceBuilder;
import org.metadatacenter.artifacts.model.core.builders.PhoneNumberFieldInstanceBuilder;
import org.metadatacenter.artifacts.model.core.builders.RadioFieldInstanceBuilder;
import org.metadatacenter.artifacts.model.core.builders.TemporalFieldInstanceBuilder;
import org.metadatacenter.artifacts.model.core.builders.TextFieldInstanceBuilder;
import org.metadatacenter.artifacts.model.core.builders.TextAreaFieldInstanceBuilder;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateListFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateMapFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateOptionalFieldNotNull;
import static org.metadatacenter.model.ModelNodeNames.JSON_LD_CONTEXT;
import static org.metadatacenter.model.ModelNodeNames.JSON_LD_ID;
import static org.metadatacenter.model.ModelNodeNames.JSON_LD_LANGUAGE;
import static org.metadatacenter.model.ModelNodeNames.JSON_LD_TYPE;
import static org.metadatacenter.model.ModelNodeNames.JSON_LD_VALUE;
import static org.metadatacenter.model.ModelNodeNames.OSLC_MODIFIED_BY;
import static org.metadatacenter.model.ModelNodeNames.PAV_CREATED_BY;
import static org.metadatacenter.model.ModelNodeNames.PAV_CREATED_ON;
import static org.metadatacenter.model.ModelNodeNames.PAV_LAST_UPDATED_ON;
import static org.metadatacenter.model.ModelNodeNames.RDFS_LABEL;
import static org.metadatacenter.model.ModelNodeNames.SKOS_NOTATION;
import static org.metadatacenter.model.ModelNodeNames.SKOS_PREFLABEL;

public sealed interface FieldInstanceArtifact extends ChildInstanceArtifact permits TextFieldInstance,
  TextAreaFieldInstance, TemporalFieldInstance, NumericFieldInstance, ControlledTermFieldInstance, LinkFieldInstance,
  EmailFieldInstance, CheckboxFieldInstance, ListFieldInstance, PhoneNumberFieldInstance, RadioFieldInstance,
  FieldInstanceArtifactRecord
{
  static FieldInstanceArtifact create(List<URI> jsonLdTypes, Optional<URI> jsonLdId,
    Optional<String> jsonLdValue, Optional<String> label, Optional<String> notation, Optional<String> prefLabel,
    Optional<String> language)
  {
    return new FieldInstanceArtifactRecord(jsonLdTypes, jsonLdId, jsonLdValue, label, notation,
      prefLabel, language);
  }

  List<URI> jsonLdTypes();

  Optional<URI> jsonLdId();

  Optional<String> jsonLdValue();

  Optional<String> label();

  Optional<String> notation();

  Optional<String> prefLabel();

  Optional<String> language();

  @Override default void accept(InstanceArtifactVisitor visitor, String path)
  {
    visitor.visitFieldInstanceArtifact(this, path);
  }

  @Override default void accept(InstanceArtifactVisitor visitor, String path, String specificationPath)
  {
    visitor.visitAttributeValueFieldInstanceArtifact(this, path, specificationPath);
  }
}

record FieldInstanceArtifactRecord(List<URI> jsonLdTypes, Optional<URI> jsonLdId, Optional<String> jsonLdValue,
                                   Optional<String> label, Optional<String> notation, Optional<String> prefLabel,
                                   Optional<String> language)
  implements FieldInstanceArtifact
{
  public FieldInstanceArtifactRecord
  {
    validateListFieldNotNull(this, jsonLdTypes, JSON_LD_TYPE);
    validateOptionalFieldNotNull(this, jsonLdValue, JSON_LD_VALUE);
    validateOptionalFieldNotNull(this, jsonLdId, JSON_LD_ID);
    validateOptionalFieldNotNull(this, label, RDFS_LABEL);
    validateOptionalFieldNotNull(this, language, JSON_LD_LANGUAGE);
    validateOptionalFieldNotNull(this, notation, SKOS_NOTATION);
    validateOptionalFieldNotNull(this, prefLabel, SKOS_PREFLABEL);
  }
}

