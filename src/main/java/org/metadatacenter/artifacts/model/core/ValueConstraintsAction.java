package org.metadatacenter.artifacts.model.core;

import java.net.URI;
import java.util.Optional;

public record ValueConstraintsAction(URI termUri, Optional<URI> sourceUri, String source,
                                     ValueType type, ValueConstraintsActionType action, Optional<Integer> to)
{
    public static class Builder
    {
        private URI termUri;
        private Optional<URI> sourceUri;
        private String source;
        private ValueType type;
        private ValueConstraintsActionType action;
        private Optional<Integer> to = Optional.empty();

        public Builder termUri(URI termUri)
        {
            this.termUri = termUri;
            return this;
        }

        public Builder sourceUri(URI sourceUri)
        {
            this.sourceUri = Optional.ofNullable(sourceUri);
            return this;
        }

        public Builder source(String source)
        {
            this.source = source;
            return this;
        }

        public Builder type(ValueType type)
        {
            this.type = type;
            return this;
        }

        public Builder action(ValueConstraintsActionType action)
        {
            this.action = action;
            return this;
        }

        public Builder to(int to)
        {
            this.to = Optional.of(to);
            return this;
        }

        public ValueConstraintsAction build()
        {
            return new ValueConstraintsAction(termUri, sourceUri, source, type, action, to);
        }
    }
}
