package org.metadatacenter.artifacts.model.core;

import org.metadatacenter.artifacts.model.core.fields.constraints.ValueConstraints;
import org.metadatacenter.artifacts.model.core.ui.FieldUi;
import org.metadatacenter.artifacts.model.core.ui.StaticFieldUi;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

import static org.metadatacenter.model.ModelNodeNames.STATIC_FIELD_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS;
import static org.metadatacenter.model.ModelNodeNames.STATIC_FIELD_SCHEMA_ARTIFACT_TYPE_URI;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_OBJECT;

public sealed interface PageBreakField extends FieldSchemaArtifact
{
  static PageBreakField create(LinkedHashMap<String, URI> jsonLdContext, List<URI> jsonLdTypes, Optional<URI> jsonLdId,
    String name, String description, Optional<String> identifier, Optional<Version> version, Optional<Status> status,
    Optional<URI> previousVersion, Optional<URI> derivedFrom, Optional<URI> createdBy, Optional<URI> modifiedBy,
    Optional<OffsetDateTime> createdOn, Optional<OffsetDateTime> lastUpdatedOn, Optional<String> preferredLabel,
    List<String> alternateLabels, Optional<String> language, FieldUi fieldUi,
    Optional<ValueConstraints> valueConstraints, Optional<Annotations> annotations, String internalName,
    String internalDescription)
  {
    return new PageBreakFieldRecord(jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, version,
      status, previousVersion, derivedFrom, createdBy, modifiedBy, createdOn, lastUpdatedOn, preferredLabel,
      alternateLabels, language, fieldUi, valueConstraints, annotations, internalName, internalDescription);
  }

  default boolean isMultiple() {return false;}

  default Optional<Integer> minItems() {return Optional.empty();}

  default Optional<Integer> maxItems() {return Optional.empty();}

  default Optional<URI> propertyUri() {return Optional.empty();}

  static PageBreakFieldBuilder builder() {return new PageBreakFieldBuilder();}

  static PageBreakFieldBuilder builder(PageBreakField pageBreakField)
  {
    return new PageBreakFieldBuilder(pageBreakField);
  }

  final class PageBreakFieldBuilder extends FieldSchemaArtifactBuilder<PageBreakField.PageBreakFieldBuilder>
  {
    private final StaticFieldUi.PageBreakFieldUiBuilder fieldUiBuilder;

    public PageBreakFieldBuilder()
    {
      super(JSON_SCHEMA_OBJECT, STATIC_FIELD_SCHEMA_ARTIFACT_TYPE_URI);
      withJsonLdContext(new LinkedHashMap<>(STATIC_FIELD_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS));
      this.fieldUiBuilder = StaticFieldUi.pageBreakFieldUiBuilder();
    }

    public PageBreakFieldBuilder(PageBreakField pageBreakField)
    {
      super(pageBreakField);
      this.fieldUiBuilder = StaticFieldUi.pageBreakFieldUiBuilder(pageBreakField.fieldUi().asStaticFieldUi());
    }

    @Override public PageBreakFieldBuilder withRequiredValue(boolean requiredValue)
    {
      return this;
    }

    @Override public PageBreakFieldBuilder withRecommendedValue(boolean recommendedValue)
    {
      return this;
    }

    @Override public PageBreakFieldBuilder withHidden(boolean hidden)
    {
      fieldUiBuilder.withHidden(hidden);
      return this;
    }

    @Override public PageBreakFieldBuilder withValueRecommendationEnabled(boolean valueRecommendationEnabled)
    {
      fieldUiBuilder.withValueRecommendationEnabled(valueRecommendationEnabled);
      return this;
    }

    @Override public PageBreakFieldBuilder withContinuePreviousLine(boolean continuePreviousLine)
    {
      fieldUiBuilder.withContinuePreviousLine(continuePreviousLine);
      return this;
    }

    public PageBreakFieldBuilder withContent(String content)
    {
      fieldUiBuilder.withContent(content);
      return this;
    }

    public PageBreakField build()
    {
      withFieldUi(fieldUiBuilder.build());
      return create(jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, version, status,
        previousVersion, derivedFrom, createdBy, modifiedBy, createdOn, lastUpdatedOn, preferredLabel, alternateLabels,
        language, fieldUi, valueConstraints, annotations, internalName, internalDescription);
    }
  }
}

record PageBreakFieldRecord(LinkedHashMap<String, URI> jsonLdContext, List<URI> jsonLdTypes, Optional<URI> jsonLdId,
                            String name, String description, Optional<String> identifier, Optional<Version> version,
                            Optional<Status> status, Optional<URI> previousVersion, Optional<URI> derivedFrom,
                            Optional<URI> createdBy, Optional<URI> modifiedBy, Optional<OffsetDateTime> createdOn,
                            Optional<OffsetDateTime> lastUpdatedOn, Optional<String> preferredLabel,
                            List<String> alternateLabels, Optional<String> language, FieldUi fieldUi,
                            Optional<ValueConstraints> valueConstraints, Optional<Annotations> annotations,
                            String internalName, String internalDescription) implements PageBreakField
{
  public PageBreakFieldRecord
  {
    FieldSchemaArtifactInvariants.validateStatic(this, jsonLdContext, jsonLdTypes,
      preferredLabel, alternateLabels, language, fieldUi, valueConstraints, annotations);
    jsonLdContext = FieldSchemaArtifactInvariants.staticContext();
    jsonLdTypes = List.copyOf(jsonLdTypes);
  }
}
