package org.metadatacenter.artifacts.model.core;

public sealed interface IriFieldInstance permits ControlledTermFieldInstance, LinkFieldInstance, OrcidFieldInstance,
    RorFieldInstance, PfasFieldInstance, RridFieldInstance, PubMedFieldInstance, NihGrantIdFieldInstance, DoiFieldInstance
{}
