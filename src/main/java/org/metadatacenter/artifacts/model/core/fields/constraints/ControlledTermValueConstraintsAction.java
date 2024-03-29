package org.metadatacenter.artifacts.model.core.fields.constraints;

import java.net.URI;
import java.util.Optional;

import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateOptionalFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateStringFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateUriFieldNotNull;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_ACTION_TO;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_SOURCE;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_SOURCE_URI;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_TERM_URI;

public record ControlledTermValueConstraintsAction(URI termUri, String source,
                                                   ValueType type, ValueConstraintsActionType action,
                                                   Optional<URI> sourceUri, Optional<Integer> to)
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
        private String source;
        private ValueType type;
        private ValueConstraintsActionType action;
        private Optional<URI> sourceUri = Optional.empty();
        private Optional<Integer> to = Optional.empty();

        public Builder withTermUri(URI termUri)
        {
            this.termUri = termUri;
            return this;
        }

        public Builder withSource(String source)
        {
            this.source = source;
            return this;
        }

        public Builder withType(ValueType type)
        {
            this.type = type;
            return this;
        }

        public Builder withAction(ValueConstraintsActionType action)
        {
            this.action = action;
            return this;
        }

        public Builder withSourceUri(URI sourceUri)
        {
            this.sourceUri = Optional.ofNullable(sourceUri);
            return this;
        }

        public Builder withTo(int to)
        {
            this.to = Optional.of(to);
            return this;
        }

        public ControlledTermValueConstraintsAction build()
        {
            return new ControlledTermValueConstraintsAction(termUri, source, type, action, sourceUri, to);
        }
    }
}
