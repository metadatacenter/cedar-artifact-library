package org.metadatacenter.artifacts.model.core.fields.constraints;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.metadatacenter.model.validation.ConstraintLinksValidation;
import java.util.LinkedHashMap;
import java.util.Map;

/** Declared public destinations; never part of identity, membership or version selection. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ConstraintLinks(String landingPage, String icon) {
  private static final ObjectMapper JSON = new ObjectMapper();
  public ConstraintLinks {
    ConstraintLinksValidation.validate(JSON.valueToTree(asMap(landingPage, icon)));
  }
  public static ConstraintLinks read(JsonNode value) {
    ConstraintLinksValidation.validate(value);
    return new ConstraintLinks(value.has("landingPage") ? value.get("landingPage").textValue() : null,
        value.has("icon") ? value.get("icon").textValue() : null);
  }
  public Map<String, String> asMap() { return asMap(landingPage, icon); }
  private static Map<String, String> asMap(String page, String icon) {
    Map<String, String> result = new LinkedHashMap<>();
    if (page != null) result.put("landingPage", page);
    if (icon != null) result.put("icon", icon);
    return result;
  }
}
