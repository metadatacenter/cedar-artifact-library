package org.metadatacenter.artifacts.model.core.fields;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.regex.Pattern;

import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateStringFieldNotEmpty;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateStringFieldNotNull;

public record EmailDefaultValue(@JsonValue String value) implements DefaultValue<String>
{
  // The WHATWG HTML5 <input type=email> grammar: a local part, an '@', and a dot-separated
  // domain, with no spaces. Deliberately lenient — it accepts every address a browser would and
  // rejects only clearly malformed values.
  private static final Pattern EMAIL_PATTERN = Pattern.compile(
    "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?"
      + "(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$");

  public EmailDefaultValue {
    validateStringFieldNotNull(this, value, "value");
    validateStringFieldNotEmpty(this, value, "value");
    if (!EMAIL_PATTERN.matcher(value).matches())
      throw new IllegalStateException("default value " + value + " is not a valid e-mail address in " + this);
  }

  @Override public DefaultValueType getValueType()
  {
    return DefaultValueType.EMAIL;
  }
}
