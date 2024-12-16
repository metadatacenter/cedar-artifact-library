package org.metadatacenter.artifacts.model.core;

public sealed interface LiteralFieldInstance permits TextFieldInstance, TextAreaFieldInstance, NumericFieldInstance,
    PhoneNumberFieldInstance, TemporalFieldInstance, RadioFieldInstance, EmailFieldInstance, CheckboxFieldInstance,
    ListFieldInstance {
}
