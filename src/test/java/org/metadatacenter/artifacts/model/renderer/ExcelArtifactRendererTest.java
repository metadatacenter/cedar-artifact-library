package org.metadatacenter.artifacts.model.renderer;

import com.sun.net.httpserver.HttpServer;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataValidationConstraint.OperatorType;
import org.apache.poi.ss.usermodel.DataValidationConstraint.ValidationType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.core.AttributeValueField;
import org.metadatacenter.artifacts.model.core.CheckboxField;
import org.metadatacenter.artifacts.model.core.ControlledTermField;
import org.metadatacenter.artifacts.model.core.DoiField;
import org.metadatacenter.artifacts.model.core.ElementSchemaArtifact;
import org.metadatacenter.artifacts.model.core.EmailField;
import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifacts.model.core.ImageField;
import org.metadatacenter.artifacts.model.core.LinkField;
import org.metadatacenter.artifacts.model.core.ListField;
import org.metadatacenter.artifacts.model.core.NihGrantIdField;
import org.metadatacenter.artifacts.model.core.NumericField;
import org.metadatacenter.artifacts.model.core.OrcidField;
import org.metadatacenter.artifacts.model.core.PageBreakField;
import org.metadatacenter.artifacts.model.core.PfasField;
import org.metadatacenter.artifacts.model.core.PhoneNumberField;
import org.metadatacenter.artifacts.model.core.PubMedField;
import org.metadatacenter.artifacts.model.core.RadioField;
import org.metadatacenter.artifacts.model.core.RichTextField;
import org.metadatacenter.artifacts.model.core.RorField;
import org.metadatacenter.artifacts.model.core.RridField;
import org.metadatacenter.artifacts.model.core.SectionBreakField;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.artifacts.model.core.TemporalField;
import org.metadatacenter.artifacts.model.core.TextAreaField;
import org.metadatacenter.artifacts.model.core.TextField;
import org.metadatacenter.artifacts.model.core.Version;
import org.metadatacenter.artifacts.model.core.YouTubeField;
import org.metadatacenter.artifacts.model.core.fields.InputTimeFormat;
import org.metadatacenter.artifacts.model.core.fields.TemporalGranularity;
import org.metadatacenter.artifacts.model.core.fields.XsdNumericDatatype;
import org.metadatacenter.artifacts.model.core.fields.XsdTemporalDatatype;
import org.metadatacenter.artifacts.ss.SpreadSheetUtil;
import org.metadatacenter.artifacts.util.TerminologyServerClient;
import org.metadatacenter.model.validation.NumericPolicy;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import static org.junit.jupiter.api.Assertions.*;

@Tag("integration")
class ExcelArtifactRendererTest {
  private static final URI TEMPLATE_ID = URI.create("https://repo.metadatacenter.org/templates/study");
  private static final String EOL = System.lineSeparator();

  @Test void exactDefaultsSurviveWorkbookRoundTrip() throws Exception {
    for (String value : new String[]{"1.5", "0.1", "9223372036854775807", "0.10000000000000001", "1e400"}) {
      var field = NumericField.builder().withName("Number").withNumericType(XsdNumericDatatype.DECIMAL)
          .withDefaultValue(new BigDecimal(value)).build();
      var template = TemplateSchemaArtifact.builder().withName("Numbers").withJsonLdId(java.net.URI.create("urn:test:numbers")).withFieldSchema(field).build();
      try (var workbook = new ExcelArtifactRenderer(null).render(template, 0, 0)) {
        var bytes = new ByteArrayOutputStream();
        workbook.write(bytes);
        try (var reloaded = WorkbookFactory.create(new ByteArrayInputStream(bytes.toByteArray()))) {
          var cell = reloaded.getSheetAt(0).getRow(1).getCell(0);
          if (value.equals("1.5") || value.equals("0.1")) {
            assertEquals(CellType.NUMERIC, cell.getCellType());
            assertEquals(0, new BigDecimal(value).compareTo(BigDecimal.valueOf(cell.getNumericCellValue())));
          } else {
            assertEquals(CellType.STRING, cell.getCellType());
            assertEquals(0, new BigDecimal(value).compareTo(new BigDecimal(cell.getStringCellValue())));
          }
        }
      }
    }
  }

  @Test void theHeaderRowNamesEachFieldInOrderFromTheGivenCell() throws Exception {
    var template = template()
        .withFieldSchema(TextField.builder().withName("Title").withDescription("The study title")
            .withRequiredValue(true).build())
        .withFieldSchema(TextField.builder().withName("Keyword").withDescription("A keyword")
            .withIsMultiple(true).build())
        .withElementSchema(ElementSchemaArtifact.builder().withName("Address")
            .withFieldSchema(TextField.builder().withName("Street").build()).build())
        .withFieldSchema(NumericField.builder().withName("Count").withNumericType(XsdNumericDatatype.INT).build())
        .build();
    try (var workbook = reopen(new ExcelArtifactRenderer(null).render(template, 2, 1))) {
      assertEquals("Study", workbook.getSheetName(0));
      Row header = workbook.getSheetAt(0).getRow(1);
      // Only the template's own fields become columns; an element's fields do not appear.
      assertEquals(List.of("Title", "Keyword", "Count"), cellTexts(header, 2, 5));
      assertEquals(2, header.getFirstCellNum());
      assertEquals(5, header.getLastCellNum());
      // The header comment carries the description. A required field says so; a multiple-value
      // field carries no mark.
      assertEquals("(Required) The study title", header.getCell(2).getCellComment().getString().getString());
      assertEquals("A keyword", header.getCell(3).getCellComment().getString().getString());
      assertEquals("", header.getCell(4).getCellComment().getString().getString());
      assertEquals("CEDAR Metadata Validator", header.getCell(2).getCellComment().getAuthor());
      CellStyle style = header.getCell(2).getCellStyle();
      assertEquals(HorizontalAlignment.CENTER, style.getAlignment());
      assertEquals(FillPatternType.SOLID_FOREGROUND, style.getFillPattern());
      assertEquals(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(), style.getFillForegroundColor());
      // Validations start on the row after the header.
      var validation = validationsByColumn(workbook.getSheetAt(0)).get(4);
      var region = validation.getRegions().getCellRangeAddress(0);
      assertEquals(2, region.getFirstRow());
      assertEquals(1000, region.getLastRow());
    }
  }

  @Test void eachColumnValidatesTheValuesItsFieldAdmits() throws Exception {
    var template = template()
        .withFieldSchema(TextField.builder().withName("Code").withMinLength(2).withMaxLength(10).build())
        .withFieldSchema(TextField.builder().withName("Short").withMaxLength(3).build())
        .withFieldSchema(NumericField.builder().withName("Count").withNumericType(XsdNumericDatatype.INT)
            .withMinValue(1).withMaxValue(9).build())
        .withFieldSchema(NumericField.builder().withName("Total").withNumericType(XsdNumericDatatype.LONG).build())
        .withFieldSchema(NumericField.builder().withName("Weight").withNumericType(XsdNumericDatatype.DECIMAL)
            .withMinValue(new BigDecimal("0.5")).build())
        .withFieldSchema(NumericField.builder().withName("Ceiling").withNumericType(XsdNumericDatatype.FLOAT)
            .withMaxValue(new BigDecimal("2.5")).build())
        .withFieldSchema(NumericField.builder().withName("Depth").withNumericType(XsdNumericDatatype.DOUBLE).build())
        .withFieldSchema(TemporalField.builder().withName("When").withTemporalType(XsdTemporalDatatype.DATE)
            .withTemporalGranularity(TemporalGranularity.DAY).build())
        .withFieldSchema(TemporalField.builder().withName("At").withTemporalType(XsdTemporalDatatype.TIME)
            .withTemporalGranularity(TemporalGranularity.MINUTE).build())
        .withFieldSchema(TextField.builder().withName("Free").build())
        .withFieldSchema(TextAreaField.builder().withName("Notes").build())
        .withFieldSchema(EmailField.builder().withName("Email").build())
        .withFieldSchema(PhoneNumberField.builder().withName("Phone").build())
        .withFieldSchema(LinkField.builder().withName("Homepage").build())
        .withFieldSchema(ControlledTermField.builder().withName("Unconstrained term").build())
        .build();
    try (var workbook = render(template)) {
      var validations = validationsByColumn(workbook.getSheetAt(0));

      assertValidation(validations.get(0), ValidationType.TEXT_LENGTH, OperatorType.BETWEEN, "2", "10",
          "Value should have a minimum of 2 characters and a maximum of 10");
      assertValidation(validations.get(1), ValidationType.TEXT_LENGTH, OperatorType.LESS_OR_EQUAL, "3", "",
          "Value should have a maximum of 3 characters");
      assertValidation(validations.get(2), ValidationType.INTEGER, OperatorType.BETWEEN, "1", "9",
          "Value should be between 1 and 9");
      assertValidation(validations.get(3), ValidationType.INTEGER, OperatorType.BETWEEN,
          String.valueOf(Integer.MIN_VALUE), String.valueOf(Integer.MAX_VALUE), "Value should be a number");
      // A bound admits a value equal to it, and the message says so.
      assertValidation(validations.get(4), ValidationType.DECIMAL, OperatorType.GREATER_OR_EQUAL, "0.5", "",
          "Value should be at least 0.5");
      assertValidation(validations.get(5), ValidationType.DECIMAL, OperatorType.LESS_OR_EQUAL, "2.5", "",
          "Value should be at most 2.5");
      assertValidation(validations.get(6), ValidationType.DECIMAL, OperatorType.BETWEEN,
          String.valueOf(-Float.MAX_VALUE), String.valueOf(Float.MAX_VALUE), "Value should be a number");
      assertValidation(validations.get(7), ValidationType.DATE, OperatorType.BETWEEN, "Date(1, 1, 1)",
          "Date(9999,12,31)", "yyyy/m/d");
      assertValidation(validations.get(8), ValidationType.TIME, OperatorType.BETWEEN, "TIME(0,0,0)",
          "TIME(23,59,59)", "hh:mm");
      // Free text, a text area, an email, a phone number, a link and a controlled term with no
      // value space have no rule a spreadsheet can check.
      assertEquals(Set.of(0, 1, 2, 3, 4, 5, 6, 7, 8), validations.keySet());
    }
  }

  @Test void aTextFieldsMinimumLengthAdmitsAValueOfThatLength() throws Exception {
    // The default has exactly the minimum length, so the column's own rule must admit it.
    var template = template()
        .withFieldSchema(TextField.builder().withName("Code").withMinLength(5).withDefaultValue("ABCDE").build())
        .build();
    try (var workbook = render(template)) {
      assertValidation(validationsByColumn(workbook.getSheetAt(0)).get(0), ValidationType.TEXT_LENGTH,
          OperatorType.GREATER_OR_EQUAL, "5", "", "Value should have a minimum of 5 characters");
      assertEquals("ABCDE", workbook.getSheetAt(0).getRow(1).getCell(0).getStringCellValue());
    }
  }

  @Test void aFieldWithOptionsGetsASheetOfThemAndAColumnThatOffersThem() throws Exception {
    var template = template()
        .withFieldSchema(ListField.builder().withName("Colour").withOption("Red").withOption("Green").build())
        .withFieldSchema(RadioField.builder().withName("Size").withOption("Small").withOption("Large", true).build())
        // A sheet named after the template already exists, so this field's sheet takes a numbered name.
        .withFieldSchema(CheckboxField.builder().withName("Study").withOption("Yes").build())
        .build();
    try (var workbook = render(template)) {
      assertEquals(List.of("Study", "Colour", "Size", "Sheet0", ".metadata"), sheetNames(workbook));
      assertEquals(List.of("Red", "Green"), columnTexts(workbook.getSheet("Colour"), 0));
      assertNull(workbook.getSheet("Colour").getRow(0).getCell(1));
      assertEquals(List.of("Small", "Large"), columnTexts(workbook.getSheet("Size"), 0));
      assertEquals(List.of("Yes"), columnTexts(workbook.getSheet("Sheet0"), 0));

      var validations = validationsByColumn(workbook.getSheetAt(0));
      assertConstraint(validations.get(0), ValidationType.LIST, OperatorType.BETWEEN, "'Colour'!$A$1:$A$2", null);
      assertConstraint(validations.get(1), ValidationType.LIST, OperatorType.BETWEEN, "'Size'!$A$1:$A$2", null);
      assertConstraint(validations.get(2), ValidationType.LIST, OperatorType.BETWEEN, "'Sheet0'!$A$1:$A$1", null);
      assertTrue(validations.get(0).getShowErrorBox());
    }
  }

  @Test void aControlledTermColumnOffersTheValuesTheTerminologyServerReturns() throws Exception {
    var requests = new ArrayList<String>();
    var server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 0);
    server.createContext("/", exchange -> {
      requests.add(exchange.getRequestMethod() + " " + exchange.getRequestURI().getPath());
      byte[] body = ("{\"totalCount\": 3, \"collection\": ["
          + "{\"@id\": \"http://purl.obolibrary.org/obo/DOID_1\", \"prefLabel\": \"Lung disease\"},"
          + "{\"@id\": \"http://purl.obolibrary.org/obo/DOID_2\", \"prefLabel\": \"Liver disease\"},"
          + "{\"@id\": \"http://purl.obolibrary.org/obo/DOID_3\", \"prefLabel\": \"Lung disease\"}]}")
          .getBytes(StandardCharsets.UTF_8);
      exchange.sendResponseHeaders(200, body.length);
      exchange.getResponseBody().write(body);
      exchange.close();
    });
    server.start();
    try {
      var client = new TerminologyServerClient("http://127.0.0.1:" + server.getAddress().getPort(), "key");
      var template = template().withFieldSchema(ControlledTermField.builder().withName("Disease")
          .withBranchValueConstraint(URI.create("http://purl.obolibrary.org/obo/DOID_4"), "DOID", "DOID",
              "disease", 0).build()).build();
      try (var workbook = reopen(new ExcelArtifactRenderer(client).render(template, 0, 0))) {
        assertEquals(List.of("POST /integrated-retrieve"), requests);
        var values = workbook.getSheet("Disease");
        // A dropdown offers labels, so two values sharing a label appear once.
        assertEquals(List.of("Lung disease", "Liver disease"), columnTexts(values, 0));
        assertTrue(Set.of("http://purl.obolibrary.org/obo/DOID_1", "http://purl.obolibrary.org/obo/DOID_3")
            .contains(values.getRow(0).getCell(1).getStringCellValue()));
        assertEquals("http://purl.obolibrary.org/obo/DOID_2", values.getRow(1).getCell(1).getStringCellValue());
        assertConstraint(validationsByColumn(workbook.getSheetAt(0)).get(0), ValidationType.LIST,
            OperatorType.BETWEEN, "'Disease'!$A$1:$A$2", null);
      }
    } finally {
      server.stop(0);
    }
  }

  @Test void aTemporalColumnShowsAndExplainsItsFieldsFormat() throws Exception {
    record Case(XsdTemporalDatatype type, TemporalGranularity granularity, InputTimeFormat timeFormat,
                int validationType, String format) {}
    var cases = List.of(
        new Case(XsdTemporalDatatype.DATETIME, TemporalGranularity.YEAR, null, ValidationType.DATE, "yyyy"),
        new Case(XsdTemporalDatatype.DATETIME, TemporalGranularity.MONTH, null, ValidationType.DATE, "yyyy/m"),
        new Case(XsdTemporalDatatype.DATETIME, TemporalGranularity.DAY, null, ValidationType.DATE, "yyyy/m/d"),
        new Case(XsdTemporalDatatype.DATETIME, TemporalGranularity.HOUR, null, ValidationType.DATE, "yyyy/m/d hh"),
        new Case(XsdTemporalDatatype.DATETIME, TemporalGranularity.MINUTE, InputTimeFormat.TWELVE_HOUR,
            ValidationType.DATE, "yyyy/m/d hh:mm AM/PM"),
        new Case(XsdTemporalDatatype.DATETIME, TemporalGranularity.SECOND, InputTimeFormat.TWENTY_FOUR_HOUR,
            ValidationType.DATE, "yyyy/m/d hh:mm:ss"),
        new Case(XsdTemporalDatatype.DATETIME, TemporalGranularity.DECIMAL_SECOND, null, ValidationType.DATE,
            "yyyy/m/d hh:mm:ss.000"),
        new Case(XsdTemporalDatatype.DATE, TemporalGranularity.YEAR, null, ValidationType.DATE, "yyyy"),
        new Case(XsdTemporalDatatype.DATE, TemporalGranularity.MONTH, null, ValidationType.DATE, "yyyy/m"),
        new Case(XsdTemporalDatatype.DATE, TemporalGranularity.DAY, null, ValidationType.DATE, "yyyy/m/d"),
        new Case(XsdTemporalDatatype.TIME, TemporalGranularity.HOUR, null, ValidationType.TIME, "hh"),
        new Case(XsdTemporalDatatype.TIME, TemporalGranularity.MINUTE, InputTimeFormat.TWELVE_HOUR,
            ValidationType.TIME, "hh:mm AM/PM"),
        new Case(XsdTemporalDatatype.TIME, TemporalGranularity.SECOND, null, ValidationType.TIME, "hh:mm:ss"),
        new Case(XsdTemporalDatatype.TIME, TemporalGranularity.DECIMAL_SECOND, null, ValidationType.TIME,
            "hh:mm:ss.000"));
    for (Case c : cases) {
      var field = TemporalField.builder().withName("When").withTemporalType(c.type())
          .withTemporalGranularity(c.granularity());
      if (c.timeFormat() != null) field.withInputTimeFormat(c.timeFormat());
      try (var workbook = render(template().withFieldSchema(field.build()).build())) {
        var sheet = workbook.getSheetAt(0);
        assertEquals(c.format(), sheet.getColumnStyle(0).getDataFormatString(), c.toString());
        var validation = validationsByColumn(sheet).get(0);
        assertEquals(c.validationType(), validation.getValidationConstraint().getValidationType(), c.toString());
        assertEquals(c.format(), validation.getErrorBoxText(), c.toString());
      }
    }
  }

  @Test void defaultsFillTheFirstRowBelowTheHeader() throws Exception {
    var template = template()
        .withFieldSchema(TextField.builder().withName("Title").withDefaultValue("Untitled").build())
        .withFieldSchema(NumericField.builder().withName("Count").withNumericType(XsdNumericDatatype.INT)
            .withDefaultValue(3).build())
        .withFieldSchema(TemporalField.builder().withName("When").withTemporalType(XsdTemporalDatatype.DATE)
            .withTemporalGranularity(TemporalGranularity.DAY).withDefaultValue("2026-10-21").build())
        .withFieldSchema(ControlledTermField.builder().withName("Disease")
            .withDefaultValue(URI.create("http://purl.obolibrary.org/obo/DOID_1"), "Lung disease").build())
        .withFieldSchema(TextField.builder().withName("Free").build())
        .build();
    try (var workbook = render(template)) {
      Row firstDataRow = workbook.getSheetAt(0).getRow(1);
      assertEquals("Untitled", firstDataRow.getCell(0).getStringCellValue());
      assertEquals(CellType.NUMERIC, firstDataRow.getCell(1).getCellType());
      assertEquals(3, firstDataRow.getCell(1).getNumericCellValue());
      assertEquals("2026-10-21", firstDataRow.getCell(2).getStringCellValue());
      // A controlled-term default shows its label, the text a person picks.
      assertEquals("Lung disease", firstDataRow.getCell(3).getStringCellValue());
      assertNull(firstDataRow.getCell(4));
    }
  }

  @Test void hiddenAndStaticFieldsHideTheirColumns() throws Exception {
    var template = template()
        .withFieldSchema(TextField.builder().withName("Title").build())
        .withFieldSchema(EmailField.builder().withName("Email").withHidden(true).build())
        .withFieldSchema(SectionBreakField.builder().withName("Section").build())
        .withFieldSchema(RichTextField.builder().withName("Instructions").withContent("Fill this in").build())
        .withFieldSchema(ImageField.builder().withName("Logo").withContent("https://example.org/logo.png").build())
        .withFieldSchema(YouTubeField.builder().withName("Video").withContent("dQw4w9WgXcQ").build())
        .withFieldSchema(PageBreakField.builder().withName("Next page").build())
        .build();
    try (var workbook = render(template)) {
      var sheet = workbook.getSheetAt(0);
      assertEquals(List.of(false, true, true, true, true, true, true),
          List.of(sheet.isColumnHidden(0), sheet.isColumnHidden(1), sheet.isColumnHidden(2), sheet.isColumnHidden(3),
              sheet.isColumnHidden(4), sheet.isColumnHidden(5), sheet.isColumnHidden(6)));
      assertTrue(sheet.getDataValidations().isEmpty());
    }
    // The TSV leaves out a hidden column, so there a static field has no column at all.
    assertEquals("Title" + EOL + EOL, tsv(template));
  }

  @Test void anExternalAuthorityFieldIsAFreeTextColumn() throws Exception {
    var template = template()
        .withFieldSchema(OrcidField.builder().withName("ORCID").build())
        .withFieldSchema(RorField.builder().withName("ROR").build())
        .withFieldSchema(PfasField.builder().withName("PFAS").build())
        .withFieldSchema(PubMedField.builder().withName("PubMed").build())
        .withFieldSchema(RridField.builder().withName("RRID").build())
        .withFieldSchema(NihGrantIdField.builder().withName("Grant").build())
        .withFieldSchema(DoiField.builder().withName("DOI").build())
        .build();
    var names = List.of("ORCID", "ROR", "PFAS", "PubMed", "RRID", "Grant", "DOI");
    try (var workbook = render(template)) {
      var sheet = workbook.getSheetAt(0);
      assertEquals(names, cellTexts(sheet.getRow(0), 0, 7));
      for (int column = 0; column < 7; column++) assertFalse(sheet.isColumnHidden(column), names.get(column));
      // Its values are identifiers looked up elsewhere, so the column offers no list and checks nothing.
      assertEquals(List.of("Study", ".metadata"), sheetNames(workbook));
      assertTrue(sheet.getDataValidations().isEmpty());
    }
    assertEquals(String.join("\t", names) + EOL + "\t".repeat(6) + EOL, tsv(template));
  }

  @Test void aLinkOrExternalAuthorityDefaultIsWrittenAsItsIri() throws Exception {
    record Case(String iri, Function<URI, FieldSchemaArtifact> field) {}
    var cases = List.of(
        new Case("https://example.org/protocol",
            iri -> LinkField.builder().withName("Protocol").withDefaultValue(iri).build()),
        new Case("https://orcid.org/0000-0002-1825-0097",
            iri -> OrcidField.builder().withName("ORCID").withDefaultValue(iri).build()),
        new Case("https://ror.org/00f54p054",
            iri -> RorField.builder().withName("ROR").withDefaultValue(iri).build()),
        new Case("https://comptox.epa.gov/dashboard/chemical/details/DTXSID3031864",
            iri -> PfasField.builder().withName("PFAS").withDefaultValue(iri).build()),
        new Case("https://pubmed.ncbi.nlm.nih.gov/26978244",
            iri -> PubMedField.builder().withName("PubMed").withDefaultValue(iri).build()),
        new Case("https://identifiers.org/RRID:AB_2298772",
            iri -> RridField.builder().withName("RRID").withDefaultValue(iri).build()),
        new Case("https://reporter.nih.gov/project-details/10000001",
            iri -> NihGrantIdField.builder().withName("Grant").withDefaultValue(iri).build()),
        new Case("https://doi.org/10.1038/sdata.2016.18",
            iri -> DoiField.builder().withName("DOI").withDefaultValue(iri).build()));
    assertAll(cases.stream().map(c -> () -> {
      var field = c.field().apply(URI.create(c.iri()));
      var template = template().withFieldSchema(field).build();
      try (var workbook = render(template)) {
        var sheet = workbook.getSheetAt(0);
        var cell = sheet.getRow(1).getCell(0);
        // The default is the IRI as text, in a column that checks nothing, so no rule can refuse it.
        assertEquals(CellType.STRING, cell.getCellType(), field.name());
        assertEquals(c.iri(), cell.getStringCellValue(), field.name());
        assertFalse(sheet.isColumnHidden(0), field.name());
        assertTrue(sheet.getDataValidations().isEmpty(), field.name());
      }
      assertEquals(field.name() + EOL + c.iri() + EOL, tsv(template), field.name());
    }));
  }

  @Test void anAttributeValueFieldGetsNoColumn() throws Exception {
    var template = template()
        .withFieldSchema(TextField.builder().withName("Title").build())
        .withFieldSchema(AttributeValueField.builder().withName("Extra").build())
        .withFieldSchema(TextField.builder().withName("Notes").build())
        .build();
    try (var workbook = render(template)) {
      // Its names are chosen when an instance is filled in, so the template has none to head a column with.
      Row header = workbook.getSheetAt(0).getRow(0);
      assertEquals(List.of("Title", "Notes"), cellTexts(header, 0, 2));
      assertEquals(2, header.getLastCellNum());
    }
    assertEquals("Title\tNotes" + EOL + "\t" + EOL, tsv(template));
  }

  @Test void theTsvWritesANumericDefaultAsPlainDecimalText() throws Exception {
    record Case(XsdNumericDatatype type, String defaultValue, String text) {}
    var cases = new ArrayList<Case>();
    for (XsdNumericDatatype type : XsdNumericDatatype.values()) cases.add(new Case(type, "3", "3"));
    for (XsdNumericDatatype type : List.of(XsdNumericDatatype.DECIMAL, XsdNumericDatatype.FLOAT,
        XsdNumericDatatype.DOUBLE)) {
      cases.add(new Case(type, "3.50", "3.5"));
      cases.add(new Case(type, "0.0001", "0.0001"));
    }
    // An integer type admits a whole number written with a fraction, and shows it with no decimal point,
    // also when it is too precise for a numeric cell.
    cases.add(new Case(XsdNumericDatatype.INT, "3.0", "3"));
    cases.add(new Case(XsdNumericDatatype.LONG, "100000000000000", "100000000000000"));
    cases.add(new Case(XsdNumericDatatype.LONG, "12345678901234567.0", "12345678901234567"));
    assertAll(cases.stream().map(c -> () -> {
      var template = template().withFieldSchema(NumericField.builder().withName("Count").withNumericType(c.type())
          .withDefaultValue(NumericPolicy.number(c.defaultValue())).build()).build();
      assertEquals("Count" + EOL + c.text() + EOL, tsv(template), c.type().name() + " " + c.defaultValue());
    }));
  }

  @Test void aMetadataSheetIdentifiesTheTemplateTheWorkbookWasMadeFrom() throws Exception {
    var template = template().withVersion(Version.fromString("1.2.3")).build();
    try (var workbook = render(template)) {
      assertEquals(List.of("Study", ".metadata"), sheetNames(workbook));
      var metadata = workbook.getSheet(".metadata");
      assertEquals(List.of("schema:title", "pav:version", "pav:createdOn", "pav:derivedFrom"),
          cellTexts(metadata.getRow(0), 0, 4));
      assertEquals(List.of("Study", "1.2.3"), cellTexts(metadata.getRow(1), 0, 2));
      assertEquals(TEMPLATE_ID.toString(), metadata.getRow(1).getCell(3).getStringCellValue());
      OffsetDateTime createdOn = OffsetDateTime.parse(metadata.getRow(1).getCell(2).getStringCellValue());
      assertTrue(Duration.between(createdOn, OffsetDateTime.now()).abs().toMinutes() < 5, createdOn.toString());
    }
  }

  @Test void aTemplateWithoutAnIdentifierIsRefused() {
    var template = TemplateSchemaArtifact.builder().withName("Study").build();
    var refusal = assertThrows(RuntimeException.class, () -> new ExcelArtifactRenderer(null).render(template, 0, 0));
    assertEquals("template Study has no field @id", refusal.getMessage());
  }

  private static TemplateSchemaArtifact.Builder template() {
    return TemplateSchemaArtifact.builder().withName("Study").withJsonLdId(TEMPLATE_ID);
  }

  private static XSSFWorkbook render(TemplateSchemaArtifact template) throws IOException {
    return reopen(new ExcelArtifactRenderer(null).render(template, 0, 0));
  }

  /** The TSV that {@code Template2Tsv} writes: the template's sheet, flattened. */
  private static String tsv(TemplateSchemaArtifact template) throws IOException {
    try (var workbook = new ExcelArtifactRenderer(null).render(template, 0, 0)) {
      return SpreadSheetUtil.convertSheetToTsv(workbook.getSheetAt(0)).toString();
    }
  }

  /** Write the workbook and read it back, so the assertions see what a spreadsheet program opens. */
  private static XSSFWorkbook reopen(Workbook workbook) throws IOException {
    var bytes = new ByteArrayOutputStream();
    workbook.write(bytes);
    workbook.close();
    return (XSSFWorkbook) WorkbookFactory.create(new ByteArrayInputStream(bytes.toByteArray()));
  }

  private static Map<Integer, XSSFDataValidation> validationsByColumn(XSSFSheet sheet) {
    var byColumn = new HashMap<Integer, XSSFDataValidation>();
    for (var validation : sheet.getDataValidations())
      for (var region : validation.getRegions().getCellRangeAddresses())
        byColumn.put(region.getFirstColumn(), validation);
    return byColumn;
  }

  private static void assertConstraint(XSSFDataValidation validation, int type, int operator, String formula1,
                                       String formula2) {
    var constraint = validation.getValidationConstraint();
    assertEquals(type, constraint.getValidationType());
    if (type != ValidationType.LIST) assertEquals(operator, constraint.getOperator());
    assertEquals(formula1, constraint.getFormula1());
    assertEquals(formula2, constraint.getFormula2());
  }

  private static void assertValidation(XSSFDataValidation validation, int type, int operator, String formula1,
                                       String formula2, String message) {
    assertConstraint(validation, type, operator, formula1, formula2);
    assertEquals("Validation Error", validation.getErrorBoxTitle());
    assertEquals(message, validation.getErrorBoxText());
    assertTrue(validation.getShowErrorBox());
  }

  private static List<String> cellTexts(Row row, int from, int to) {
    var texts = new ArrayList<String>();
    for (int column = from; column < to; column++) texts.add(row.getCell(column).getStringCellValue());
    return texts;
  }

  private static List<String> columnTexts(XSSFSheet sheet, int column) {
    var texts = new ArrayList<String>();
    for (var row : sheet) texts.add(row.getCell(column).getStringCellValue());
    return texts;
  }

  private static List<String> sheetNames(Workbook workbook) {
    var names = new ArrayList<String>();
    for (int i = 0; i < workbook.getNumberOfSheets(); i++) names.add(workbook.getSheetName(i));
    return names;
  }
}
