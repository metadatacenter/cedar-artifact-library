package org.metadatacenter.artifacts.model.core;

import java.net.URI;
import java.util.Optional;

import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateOptionalFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateStringFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateUriFieldNotNull;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_ACTION_TO;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_SOURCE;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_SOURCE_URI;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_TERM_URI;

public record ControlledTermValueConstraintsAction(URI termUri, Optional<URI> sourceUri, String source,
                                                   ValueType valueType, ValueConstraintsActionType actionType,
                                                   Optional<Integer> to)
{
    public ControlledTermValueConstraintsAction
    {
        validateUriFieldNotNull(this, termUri, VALUE_CONSTRAINTS_TERM_URI);
        validateOptionalFieldNotNull(this, sourceUri, VALUE_CONSTRAINTS_SOURCE_URI);
        validateStringFieldNotNull(this, source, VALUE_CONSTRAINTS_SOURCE);
        validateOptionalFieldNotNull(this, to, VALUE_CONSTRAINTS_ACTION_TO);
    }

    public static class Builder
    {
        private URI termUri;
        private Optional<URI> sourceUri = Optional.empty();
        private String source;
        private ValueType valueType;
        private ValueConstraintsActionType actionType;
        private Optional<Integer> to = Optional.empty();

        public Builder withTermUri(URI termUri)
        {
            this.termUri = termUri;
            return this;
        }

        public Builder withSourceUri(URI sourceUri)
        {
            this.sourceUri = Optional.ofNullable(sourceUri);
            return this;
        }

        public Builder withSource(String source)
        {
            this.source = source;
            return this;
        }

        public Builder withValueType(ValueType valueType)
        {
            this.valueType = valueType;
            return this;
        }

        public Builder withActionType(ValueConstraintsActionType actionType)
        {
            this.actionType = actionType;
            return this;
        }

        public Builder withTo(int to)
        {
            this.to = Optional.of(to);
            return this;
        }

        public ControlledTermValueConstraintsAction build()
        {
            return new ControlledTermValueConstraintsAction(termUri, sourceUri, source, valueType, actionType, to);
        }
    }
}
