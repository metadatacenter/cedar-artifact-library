package org.metadatacenter.artifacts.model.core;

import java.net.URI;
import java.util.Optional;

public class ValueConstraintsAction
{
    private final URI termUri;
    private final Optional<URI> sourceUri;
    private final String source;
    private final ValueType type;
    private final ValueConstraintsActionType action;
    private final Optional<Integer> to;

    public ValueConstraintsAction(URI termUri, Optional<URI> sourceUri, String source, ValueType type,
      ValueConstraintsActionType actionType, Optional<Integer> to)
    {
        this.termUri = termUri;
        this.sourceUri = sourceUri;
        this.source = source;
        this.type = type;
        this.action = actionType;
        this.to = to;
    }

    private ValueConstraintsAction(Builder builder)
    {
        this.termUri = builder.termUri;
        this.sourceUri = builder.sourceUri;
        this.source = builder.source;
        this.type = builder.type;
        this.action = builder.action;
        this.to = builder.to;
    }

    public URI getTermUri()
    {
        return termUri;
    }

    public Optional<URI> getSourceUri()
    {
        return sourceUri;
    }

    public String getSource()
    {
        return source;
    }

    public ValueType getType()
    {
        return type;
    }

    public ValueConstraintsActionType getAction()
    {
        return action;
    }

    public Optional<Integer> getTo()
    {
        return to;
    }

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
            return new ValueConstraintsAction(this);
        }
    }
}
