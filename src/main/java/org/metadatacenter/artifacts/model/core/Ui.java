package org.metadatacenter.artifacts.model.core;

import com.fasterxml.jackson.annotation.JsonIgnore;

public sealed interface Ui permits TemplateUi, ElementUi, FieldUi
{}