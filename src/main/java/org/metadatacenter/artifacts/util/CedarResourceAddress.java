package org.metadatacenter.artifacts.util;

/** Client-side HTTP addressing; never use these helpers to rewrite artifact documents. */
public final class CedarResourceAddress {
  private CedarResourceAddress() {}

  /** Keep the type visible in query parameters and command bodies. Legacy hosts stay absolute. */
  public static String selector(String id) {
    return id.replaceFirst("^https?://repo\\.metadatacenter\\.org[xy]?/(folders|templates|template-elements|template-fields|template-instances)/([0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12})$", "$1/$2");
  }

  /** The collection prefix is already part of the route. */
  public static String pathId(String id) {
    return selector(id).replaceFirst("^(folders|templates|template-elements|template-fields|template-instances)/([0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12})$", "$2");
  }
}
