package org.metadatacenter.artifacts.model;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class BatteryNumbersTest
{
  @Test void aWidenedWholeNumberIsTheSame()
  {
    assertNull(BatteryNumbers.difference(0, 0L));
    assertNull(BatteryNumbers.difference(7L, BigInteger.valueOf(7)));
    assertNull(BatteryNumbers.difference(0.5, 0.5));
  }

  @Test void aRespelledOrRoundedNumberDiffers()
  {
    assertEquals("number spelling differs: original=1 <Integer>, regenerated=1.0 <Double>", BatteryNumbers.difference(1, 1.0));
    assertEquals("value differs: original=9007199254740993, regenerated=9007199254740992",
      BatteryNumbers.difference(9007199254740993L, 9007199254740992L));
  }
}
