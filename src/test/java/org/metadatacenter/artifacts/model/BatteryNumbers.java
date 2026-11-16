package org.metadatacenter.artifacts.model;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * How the template battery compares two numbers. A whole number may come back as another integral class,
 * since a reader can widen an Integer to a Long, but it must keep its spelling and its exact value: a
 * writer that starts writing {@code minItems: 1.0}, or rounds a bound above 2^53, changes what the
 * TypeScript library and the stored documents read.
 */
final class BatteryNumbers
{
  private BatteryNumbers() {}

  /** Null when the two numbers are the same, otherwise how they differ. */
  static String difference(Number original, Number regenerated)
  {
    if (isIntegral(original) != isIntegral(regenerated))
      return "number spelling differs: original=" + original + " <" + original.getClass().getSimpleName()
        + ">, regenerated=" + regenerated + " <" + regenerated.getClass().getSimpleName() + ">";
    if (new BigDecimal(original.toString()).compareTo(new BigDecimal(regenerated.toString())) != 0)
      return "value differs: original=" + original + ", regenerated=" + regenerated;
    return null;
  }

  static boolean isIntegral(Number number)
  {
    return number instanceof Integer || number instanceof Long || number instanceof Short || number instanceof Byte
      || number instanceof BigInteger;
  }
}
