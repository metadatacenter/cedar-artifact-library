package org.metadatacenter.artifacts.model.renderer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.core.NumericField;
import org.metadatacenter.artifacts.model.core.fields.XsdNumericDatatype;
import org.metadatacenter.artifacts.model.tools.YamlSerializer;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class Java17DecimalFixtureTest {
  @Test void decimalFixturesMatchTheCanonicalWriterAndPreserveTheDouble() throws Exception {
    var cases = new ObjectMapper().readTree(getClass().getResourceAsStream("/java17-decimal.json"));
    for (var entry : cases) {
      double positive = Double.longBitsToDouble(Long.parseUnsignedLong(entry.get("bits").asText(), 16));
      for (int sign : new int[]{1, -1}) {
        double value = positive * sign;
        String expected = new BigDecimal(entry.get("decimal").asText()).multiply(BigDecimal.valueOf(sign))
            .stripTrailingZeros().toPlainString();
        assertEquals(expected, new BigDecimal(Double.toString(value)).stripTrailingZeros().toPlainString());
        var field = NumericField.builder().withName("Decimal").withNumericType(XsdNumericDatatype.DOUBLE)
            .withMinValue(value).build();
        assertTrue(YamlSerializer.getYAML(field, false, true).contains("minValue: " + expected + "\n"), entry.toString());
        assertEquals(value == 0 ? 0 : value, Double.parseDouble(expected));
      }
    }
  }
}
