package org.metadatacenter.artifacts.model.core;

import java.net.URI;

public record ValueSetValueConstraint(String name, String valueSetCollection, URI uri, int numberOfTerms) {
}
