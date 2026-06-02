package org.metadatacenter.artifacts.model.core;

import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.core.fields.EmailDefaultValue;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link EmailDefaultValue} validates its value against the WHATWG HTML5 e-mail grammar:
 * lenient enough to accept any address a browser would, strict enough to reject clearly
 * malformed values.
 */
public class EmailDefaultValueTest
{
  @Test public void acceptsTypicalAddress()
  {
    assertDoesNotThrow(() -> new EmailDefaultValue("user@example.com"));
  }

  @Test public void acceptsAddressWithPlusTagAndSubdomain()
  {
    assertDoesNotThrow(() -> new EmailDefaultValue("first.last+tag@mail.example.co.uk"));
  }

  @Test public void acceptsSingleLabelDomain()
  {
    // The HTML5 grammar does not require a dot in the domain.
    assertDoesNotThrow(() -> new EmailDefaultValue("root@localhost"));
  }

  @Test public void rejectsValueWithoutAtSign()
  {
    IllegalStateException ex =
      assertThrows(IllegalStateException.class, () -> new EmailDefaultValue("not-an-email"));
    assertTrue(ex.getMessage().contains("not a valid e-mail address"), ex.getMessage());
  }

  @Test public void rejectsValueWithSpace()
  {
    assertThrows(IllegalStateException.class, () -> new EmailDefaultValue("has space@example.com"));
  }

  @Test public void rejectsEmptyLocalPart()
  {
    assertThrows(IllegalStateException.class, () -> new EmailDefaultValue("@example.com"));
  }

  @Test public void rejectsEmptyDomain()
  {
    assertThrows(IllegalStateException.class, () -> new EmailDefaultValue("user@"));
  }
}
