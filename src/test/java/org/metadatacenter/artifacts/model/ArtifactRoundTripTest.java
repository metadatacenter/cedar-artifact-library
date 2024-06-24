package org.metadatacenter.artifacts.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.metadatacenter.artifacts.model.core.AttributeValueField;
import org.metadatacenter.artifacts.model.core.CheckboxField;
import org.metadatacenter.artifacts.model.core.ControlledTermField;
import org.metadatacenter.artifacts.model.core.ElementSchemaArtifact;
import org.metadatacenter.artifacts.model.core.EmailField;
import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifacts.model.core.ImageField;
import org.metadatacenter.artifacts.model.core.LinkField;
import org.metadatacenter.artifacts.model.core.ListField;
import org.metadatacenter.artifacts.model.core.NumericField;
import org.metadatacenter.artifacts.model.core.PhoneNumberField;
import org.metadatacenter.artifacts.model.core.RadioField;
import org.metadatacenter.artifacts.model.core.RichTextField;
import org.metadatacenter.artifacts.model.core.SectionBreakField;
import org.metadatacenter.artifacts.model.core.TemplateInstanceArtifact;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.artifacts.model.core.TemporalField;
import org.metadatacenter.artifacts.model.core.TextAreaField;
import org.metadatacenter.artifacts.model.core.TextField;
import org.metadatacenter.artifacts.model.core.YouTubeField;
import org.metadatacenter.artifacts.model.core.fields.InputTimeFormat;
import org.metadatacenter.artifacts.model.core.fields.TemporalGranularity;
import org.metadatacenter.artifacts.model.core.fields.XsdNumericDatatype;
import org.metadatacenter.artifacts.model.core.fields.XsdTemporalDatatype;
import org.metadatacenter.artifacts.model.core.fields.constraints.ValueType;
import org.metadatacenter.artifacts.model.reader.ArtifactParseException;
import org.metadatacenter.artifacts.model.reader.JsonSchemaArtifactReader;
import org.metadatacenter.artifacts.model.reader.JsonSchemaArtifactReaderTest;
import org.metadatacenter.artifacts.model.renderer.JsonSchemaArtifactRenderer;
import org.metadatacenter.model.validation.CedarValidator;
import org.metadatacenter.model.validation.ModelValidator;
import org.metadatacenter.model.validation.report.ValidationReport;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.metadatacenter.artifacts.model.core.fields.constraints.ValueConstraintsActionType.DELETE;

public class ArtifactRoundTripTest
{
  private JsonSchemaArtifactReader artifactReader = new JsonSchemaArtifactReader();
  private JsonSchemaArtifactRenderer jsonSchemaArtifactRenderer;
  private ObjectMapper mapper;
  private ModelValidator cedarModelValidator;

  @Before public void setUp()
  {
    artifactReader = new JsonSchemaArtifactReader();
    jsonSchemaArtifactRenderer = new JsonSchemaArtifactRenderer();
    mapper = new ObjectMapper();
    cedarModelValidator = new CedarValidator();
  }

  @Test public void testRoundTripSimpleTemplate()
  {
    TemplateSchemaArtifact originalTemplateSchemaArtifact = TemplateSchemaArtifact.builder()
      .withJsonLdId(URI.create("https://repo.metadatacenter.org/templates/123")).withName("Study").build();

    testRoundTripTemplateSchemaArtifact(originalTemplateSchemaArtifact);
  }

  @Test public void testRoundTripSimpleElementSchemaArtifact()
  {
    ElementSchemaArtifact originalElementSchemaArtifact = ElementSchemaArtifact.builder()
      .withJsonLdId(URI.create("https://repo.metadatacenter.org/template_elements/123")).withName("Study").build();

    testRoundTripElementSchemaArtifact(originalElementSchemaArtifact);
  }

  @Test public void testRoundTripTextField()
  {
    TextField originalFieldSchemaArtifact = TextField.builder()
      .withJsonLdId(URI.create("https://repo.metadatacenter.org/template_fields/123")).withName("Study")
      .withDefaultValue("A default value").withRegex("*").build();

    testRoundTripFieldSchemaArtifact(originalFieldSchemaArtifact);
  }

  @Test public void testRoundTripNumericField()
  {
    String name = "Field name";
    String description = "Field description";
    XsdNumericDatatype numericType = XsdNumericDatatype.DOUBLE;
    Number minValue = 0.0;
    Number maxValue = 100.0;

    NumericField originalFieldSchemaArtifact = NumericField.builder().withName(name)
      .withDescription(description).withNumericType(numericType).withMinValue(minValue).withMaxValue(maxValue).build();

    testRoundTripFieldSchemaArtifact(originalFieldSchemaArtifact);
  }

  @Test public void testRoundTripTemporalField()
  {
    String name = "Field name";
    String description = "Field description";
    XsdTemporalDatatype temporalType = XsdTemporalDatatype.TIME;
    TemporalGranularity temporalGranularity = TemporalGranularity.SECOND;
    InputTimeFormat inputTimeFormat = InputTimeFormat.TWENTY_FOUR_HOUR;
    boolean timeZoneEnabled = false;

    TemporalField originalFieldSchemaArtifact = TemporalField.builder().withName(name)
      .withDescription(description).withTemporalType(temporalType).withTemporalGranularity(temporalGranularity)
      .withInputTimeFormat(inputTimeFormat).withTimeZoneEnabled(timeZoneEnabled).build();

    testRoundTripFieldSchemaArtifact(originalFieldSchemaArtifact);
  }

  @Test public void testRoundTripControlledTermField()
  {
    String name = "Field name";
    String description = "Field description";
    URI ontologyUri = URI.create("https://data.bioontology.org/ontologies/DOID");
    String ontologyAcronym = "DOID";
    String ontologyName = "Human Disease Ontology";
    URI branchUri = URI.create("http://purl.bioontology.org/ontology/SNOMEDCT/64572001");
    String branchAcronym = "SNOMEDCT";
    String branchName = "Disease";
    String branchSource = "SNOMEDCT";
    Integer branchMaxDepth = 3;
    URI classUri = URI.create("http://purl.bioontology.org/ontology/LNC/LA19711-3");
    String classSource = "LOINC";
    String classLabel = "Human";
    String classPrefLabel = "Homo Spiens";
    ValueType classValueType = ValueType.ONTOLOGY_CLASS;
    URI valueSetUri = URI.create(
      "https://cadsr.nci.nih.gov/metadata/CADSR-VS/77d61de250089d223d7153a4283e738043a15707");
    String valueSetCollection = "CADSR-VS";
    String valueSetName = "Stable Disease";
    Integer valueSetNumberOfTerms = 1;
    URI actionTermUri = URI.create("http://purl.obolibrary.org/obo/NCBITaxon_51291");
    URI actionSourceUri = URI.create("https://data.bioontology.org/ontologies/DOID");
    String actionSource = "DOID";
    ValueType actionValueType = ValueType.ONTOLOGY_CLASS;
    Integer actionTo = 0;

    ControlledTermField originalFieldSchemaArtifact = ControlledTermField.builder().withName(name)
      .withDescription(description).withOntologyValueConstraint(ontologyUri, ontologyAcronym, ontologyName)
      .withBranchValueConstraint(branchUri, branchSource, branchAcronym, branchName, branchMaxDepth)
      .withClassValueConstraint(classUri, classSource, classLabel, classPrefLabel, classValueType)
      .withValueSetValueConstraint(valueSetUri, valueSetCollection, valueSetName, valueSetNumberOfTerms)
      .withValueConstraintsAction(actionTermUri, actionSource, actionValueType, DELETE, actionSourceUri, actionTo)
      .build();

    testRoundTripFieldSchemaArtifact(originalFieldSchemaArtifact);
  }

  @Test public void testRoundTripRadioField()
  {
    String name = "Field name";
    String description = "Field description";

    RadioField originalFieldSchemaArtifact = RadioField.builder().withName(name)
      .withDescription(description).withOption("Choice 1").withOption("Choice 2").withOption("Choice 3", true).build();

    testRoundTripFieldSchemaArtifact(originalFieldSchemaArtifact);
  }

  @Test public void testCreateListField()
  {
    String name = "Field name";
    String description = "Field description";

    ListField originalFieldSchemaArtifact = ListField.builder().withName(name)
      .withDescription(description).withOption("Choice 1").withOption("Choice 2").withOption("Choice 3", true).build();

    testRoundTripFieldSchemaArtifact(originalFieldSchemaArtifact);
  }

  @Test public void testRoundTripCheckboxField()
  {
    String name = "Field name";
    String description = "Field description";

    CheckboxField originalFieldSchemaArtifact = CheckboxField.builder().withName(name)
      .withDescription(description).withOption("Choice 1", false).withOption("Choice 2", false)
      .withOption("Choice 3", true).build();

    testRoundTripFieldSchemaArtifact(originalFieldSchemaArtifact);
  }

  @Test public void testRoundTripPhoneNumberField()
  {
    String name = "Field name";
    String description = "Field description";
    Integer minLength = 0;
    Integer maxLength = 10;

    PhoneNumberField originalFieldSchemaArtifact = PhoneNumberField.builder().withName(name)
      .withDescription(description).withMinLength(minLength).withMaxLength(maxLength).build();

    testRoundTripFieldSchemaArtifact(originalFieldSchemaArtifact);
  }

  @Test public void testRoundTripEmailField()
  {
    String name = "Field name";
    String description = "Field description";
    Integer minLength = 0;
    Integer maxLength = 10;

    EmailField originalFieldSchemaArtifact = EmailField.builder().withName(name)
      .withDescription(description).withMinLength(minLength).withMaxLength(maxLength).build();

    testRoundTripFieldSchemaArtifact(originalFieldSchemaArtifact);
  }

  @Test public void testRoundTripLinkField()
  {
    String name = "Field name";
    String description = "Field description";
    URI defaultURI = URI.create("https://example.com/Study");

    LinkField originalFieldSchemaArtifact = LinkField.builder().withName(name)
      .withDescription(description).withDefaultValue(defaultURI).build();

    testRoundTripFieldSchemaArtifact(originalFieldSchemaArtifact);
  }

  @Test public void testRoundTripTextAreaField()
  {
    String name = "Field name";
    String description = "Field description";
    Integer minLength = 0;
    Integer maxLength = 10;

    TextAreaField originalFieldSchemaArtifact = TextAreaField.builder().withName(name)
      .withDescription(description).withMinLength(minLength).withMaxLength(maxLength).build();

    testRoundTripFieldSchemaArtifact(originalFieldSchemaArtifact);
  }

  // Can add attribute-value to literalFieldUIContent.json in meta model but then it complains about missing
  // valueConstraints
  @Ignore // Fix standalone attribute-value field
  @Test public void testRoundTripAttributeValueField()
  {
    String name = "Field name";
    String description = "Field description";

    AttributeValueField originalFieldSchemaArtifact = AttributeValueField.builder().withName(name)
      .withDescription(description).build();

    testRoundTripFieldSchemaArtifact(originalFieldSchemaArtifact);
  }

  @Test public void testRoundTripSectionBreakField()
  {
    String name = "Field name";
    String description = "Field description";
    String content = "Content";

    SectionBreakField originalFieldSchemaArtifact = SectionBreakField.builder().withName(name)
      .withDescription(description).withContent(content).build();

    testRoundTripFieldSchemaArtifact(originalFieldSchemaArtifact);
  }

  @Test public void testRoundTripImageField()
  {
    String name = "Field name";
    String description = "Field description";
    String content = "Content";

    ImageField originalFieldSchemaArtifact = ImageField.builder().withName(name)
      .withDescription(description).withContent(content).build();

    testRoundTripFieldSchemaArtifact(originalFieldSchemaArtifact);
  }

  @Test public void testRoundTripYouTubeField()
  {
    String name = "Field name";
    String description = "Field description";
    String content = "Content";

    YouTubeField originalFieldSchemaArtifact = YouTubeField.builder().withName(name)
      .withDescription(description).withContent(content).build();

    testRoundTripFieldSchemaArtifact(originalFieldSchemaArtifact);
  }

  @Test public void testRoundTripRichTextField()
  {
    String name = "Field name";
    String description = "Field description";
    String content = "Content";

    RichTextField originalFieldSchemaArtifact = RichTextField.builder().withName(name)
      .withDescription(description).withContent(content).build();

    testRoundTripFieldSchemaArtifact(originalFieldSchemaArtifact);
  }

  @Test public void testRoundTripTemplateWithAttributeValueField()
  {
    testTemplateSchemaArtifactRoundTripFromFile("templates/SimpleTemplateWithAttributeValues.json");
  }

  @Test public void testRoundTripHuBMAPAntibodiesTemplate()
  {
    testTemplateSchemaArtifactRoundTripFromFile("templates/AntibodiesV3.0.0.json");
  }

  @Test public void testRoundTripHuBMAPATACseqTemplate()
  {
    testTemplateSchemaArtifactRoundTripFromFile("templates/ATACseqV3.0.0.json");
  }

  @Test public void testRoundTripHuBMAP10XMultiomeTemplate()
  {
    testTemplateSchemaArtifactRoundTripFromFile("templates/10XMultiomeV2.0.0.json");
  }

  @Test public void testRoundTripHuBMAPAutoFlouresenceTemplate()
  {
    testTemplateSchemaArtifactRoundTripFromFile("templates/Auto-flouresenceV2.1.0.json");
  }

  @Test public void testRoundTripHuBMAPCODEXTemplate()
  {
    testTemplateSchemaArtifactRoundTripFromFile("templates/CODEXV2.0.0.json");
  }

  @Test public void testRoundTripHuBMAPCellDIVETemplate()
  {
    testTemplateSchemaArtifactRoundTripFromFile("templates/CellDIVEV2.1.0.json");
  }

  @Test public void testRoundTripHuBMAPConfocalTemplate()
  {
    testTemplateSchemaArtifactRoundTripFromFile("templates/ConfocalV2.1.0.json");
  }

  @Test public void testRoundTripHuBMAPContributorTemplate()
  {
    testTemplateSchemaArtifactRoundTripFromFile("templates/ContributorV2.0.0.json");
  }

  @Test public void testRoundTripHuBMAPCyCIFTemplate()
  {
    testTemplateSchemaArtifactRoundTripFromFile("templates/CyCIFV2.1.0.json");
  }

  @Test public void testRoundTripHuBMAPDESITemplate()
  {
    testTemplateSchemaArtifactRoundTripFromFile("templates/DESIV2.0.0.json");
  }

  @Test public void testRoundTripHuBMAPEnhancedSRSTemplate()
  {
    testTemplateSchemaArtifactRoundTripFromFile("templates/EnhancedSRSV2.1.0.json");
  }

  @Test public void testRoundTripHuBMAPGeoMxNGSTemplate()
  {
    testTemplateSchemaArtifactRoundTripFromFile("templates/GeoMxNGSV2.0.0.json");
  }

  @Test public void testRoundTripHuBMAPGeoMxnCounterTemplate()
  {
    testTemplateSchemaArtifactRoundTripFromFile("templates/GeoMxnCounterV2.0.0.json");
  }

  @Test public void testRoundTripHuBMAPHiFiSlideTemplate()
  {
    testTemplateSchemaArtifactRoundTripFromFile("templates/HiFi-SlideV2.0.0.json");
  }

  @Test public void testRoundTripHuBMAPHistologyTemplate()
  {
    testTemplateSchemaArtifactRoundTripFromFile("templates/HistologyV2.2.0.json");
  }

  @Test public void testRoundTripHuBMAPICMC2DTemplate()
  {
    testTemplateSchemaArtifactRoundTripFromFile("templates/ICMC2DV2.0.0.json");
  }

  @Test public void testRoundTripHuBMAPLCMSTemplate()
  {
    testTemplateSchemaArtifactRoundTripFromFile("templates/LC-MSV4.0.0.json");
  }

  @Test public void testRoundTripHuBMAPLightSheetTemplate()
  {
    testTemplateSchemaArtifactRoundTripFromFile("templates/LightSheetV3.1.0.json");
  }

  @Test public void testRoundTripHuBMAPMALDITemplate()
  {
    testTemplateSchemaArtifactRoundTripFromFile("templates/MALDIV2.0.0.json");
  }

  @Test public void testRoundTripHuBMAPMIBITemplate()
  {
    testTemplateSchemaArtifactRoundTripFromFile("templates/MIBIV2.0.0.json");
  }

  @Test public void testRoundTripHuBMAPNanoSplitsTemplate()
  {
    testTemplateSchemaArtifactRoundTripFromFile("templates/NanoSplitsV2.0.0.json");
  }

  @Test public void testRoundTripHuBMAPPhenoCyclerTemplate()
  {
    testTemplateSchemaArtifactRoundTripFromFile("templates/PhenoCyclerV2.2.0.json");
  }

  @Test public void testRoundTripHuBMAPRNAseqTemplate()
  {
    testTemplateSchemaArtifactRoundTripFromFile("templates/RNAseqV5.0.0.json");
  }

  @Test public void testRoundTripHuBMAPRNAseqWithProbesTemplate()
  {
    testTemplateSchemaArtifactRoundTripFromFile("templates/RNAseqWithProbes5.0.0.json");
  }

  @Test public void testRoundTripHuBMAPSIMSTemplate()
  {
    testTemplateSchemaArtifactRoundTripFromFile("templates/SIMSV2.0.0.json");
  }

  @Test public void testRoundTripHuBMAPSampleBlockTemplate()
  {
    testTemplateSchemaArtifactRoundTripFromFile("templates/SampleBlockV2.1.0.json");
  }

  @Test public void testRoundTripHuBMAPSampleSectionTemplate()
  {
    testTemplateSchemaArtifactRoundTripFromFile("templates/SampleSectionV2.1.0.json");
  }

  @Test public void testRoundTripHuBMAPSampleSuspensionTemplate()
  {
    testTemplateSchemaArtifactRoundTripFromFile("templates/SampleSuspensionV2.1.0.json");
  }

  @Test public void testRoundTripHuBMAPSecondHarmonicGenerationTemplate()
  {
    testTemplateSchemaArtifactRoundTripFromFile("templates/SecondHarmonicGenerationV2.1.0.json");
  }

  @Test public void testRoundTripHuBMAPSnareSeq2Template()
  {
    testTemplateSchemaArtifactRoundTripFromFile("templates/SnareSeq2V2.0.0.json");
  }

  @Test public void testRoundTripHuBMAPThickSectionMultiphotonMxIFTemplate()
  {
    testTemplateSchemaArtifactRoundTripFromFile("templates/ThickSectionMultiphotonMxIFV2.1.0.json");
  }

  @Test public void testRoundTripHuBMAPVisiumNoProbesTemplate()
  {
    testTemplateSchemaArtifactRoundTripFromFile("templates/VisiumNoProbesV3.0.0.json");
  }

  @Test public void testRoundTripHuBMAPVisiumTemplate()
  {
    testTemplateSchemaArtifactRoundTripFromFile("templates/VisiumV2.0.0.json");
  }

  @Test public void testRoundTripHuBMAPVisiumWithProbesTemplate()
  {
    testTemplateSchemaArtifactRoundTripFromFile("templates/VisiumWithProbesV3.0.0.json");
  }

  @Test public void testRoundTripSimpleInstanceWithAttributeValues()
  {
    testTemplateInstanceArtifactRoundTripFromFile("instances/SimpleInstanceWithAttributeValues.json");
  }

  @Test
  public void testRADxInstanceAgainstTemplate() {

    ObjectNode templateNode = getJSONFileContentAsObjectNode("templates/RADxCLIGeneratedTemplate.json");
    ObjectNode instanceNode = getJSONFileContentAsObjectNode("instances/RADxCLIGeneratedInstance.json");

    assertTrue(validateJsonSchema(templateNode, instanceNode));
  }

  @Test
  public void testRADx2InstanceAgainstTemplate() {

    ObjectNode templateNode = getJSONFileContentAsObjectNode("templates/RADx2.0CLIGeneratedTemplate.json");
    ObjectNode instanceNode = getJSONFileContentAsObjectNode("instances/RADx2.0CLIGeneratedInstance.json");

    assertTrue(validateJsonSchema(templateNode, instanceNode));
  }

  @Test public void testRoundTripInstanceWithNestedAttributeValues()
  {
    testTemplateInstanceArtifactRoundTripFromFile("instances/InstanceWithNestedAttributeValues.json");
  }

  @Test public void testRoundTripTemplate001()
  {
    testTemplateSchemaArtifactRoundTripFromFile("templates/template-001.json");
  }

  @Test public void testRoundTripTemplate002()
  {
    testTemplateSchemaArtifactRoundTripFromFile("templates/template-002.json");
  }

  @Ignore // TODO Static fields don't render _content as null if it is missing
  @Test public void testRoundTripTemplate004()
  {
    testTemplateSchemaArtifactRoundTripFromFile("templates/template-004.json");
  }

  @Test public void testRoundTripTemplate005()
  {
    testTemplateSchemaArtifactRoundTripFromFile("templates/template-005.json");
  }

  @Test public void testRoundTripTemplate006()
  {
    testTemplateSchemaArtifactRoundTripFromFile("templates/template-006.json");
  }

  @Test public void testRoundTripTemplate007()
  {
    testTemplateSchemaArtifactRoundTripFromFile("templates/template-007.json");
  }

  @Test public void testRoundTripTemplate008()
  {
    testTemplateSchemaArtifactRoundTripFromFile("templates/template-008.json");
  }

  @Ignore // TODO YouTube fields not read/rendered correctly
  @Test public void testRoundTripTemplate009()
  {
    testTemplateSchemaArtifactRoundTripFromFile("templates/template-009.json");
  }

  @Test public void testRoundTripTemplate010()
  {
    testTemplateSchemaArtifactRoundTripFromFile("templates/template-010.json");
  }

  @Test public void testRoundTripTemplate011()
  {
    testTemplateSchemaArtifactRoundTripFromFile("templates/template-011.json");
  }

  @Test public void testRoundTripTemplate012()
  {
    testTemplateSchemaArtifactRoundTripFromFile("templates/template-012.json");
  }

  @Test public void testRoundTripTemplate013()
  {
    testTemplateSchemaArtifactRoundTripFromFile("templates/template-013.json");
  }

  @Test public void testRoundTripTemplate014()
  {
    testTemplateSchemaArtifactRoundTripFromFile("templates/template-014.json");
  }

  @Test public void testRoundTripTemplate015()
  {
    testTemplateSchemaArtifactRoundTripFromFile("templates/template-015.json");
  }

  @Test public void testRoundTripTemplate016()
  {
    testTemplateSchemaArtifactRoundTripFromFile("templates/template-016.json");
  }

  @Test public void testRoundTripTemplate017()
  {
    testTemplateSchemaArtifactRoundTripFromFile("templates/template-017.json");
  }

  @Test public void testRoundTripTemplate018()
  {
    testTemplateSchemaArtifactRoundTripFromFile("templates/template-018.json");
  }

  @Test public void testRoundTripTemplate019()
  {
    testTemplateSchemaArtifactRoundTripFromFile("templates/template-019.json");
  }

  @Test public void testRoundTripTemplate020()
  {
    testTemplateSchemaArtifactRoundTripFromFile("templates/template-020.json");
  }

  @Test public void testRoundTripTemplate021()
  {
    testTemplateSchemaArtifactRoundTripFromFile("templates/template-021.json");
  }

  @Test public void testRoundTripTemplate022()
  {
    testTemplateSchemaArtifactRoundTripFromFile("templates/template-022.json");
  }

  @Test public void testRoundTripTemplate023()
  {
    testTemplateSchemaArtifactRoundTripFromFile("templates/template-023.json");
  }

  @Test public void testRoundTripTemplate24()
  {
    testTemplateSchemaArtifactRoundTripFromFile("templates/template-024.json");
  }

  @Test public void testRoundTripTemplate025()
  {
    testTemplateSchemaArtifactRoundTripFromFile("templates/template-025.json");
  }

  @Test public void testRoundTripTemplate026()
  {
    testTemplateSchemaArtifactRoundTripFromFile("templates/template-026.json");
  }

  @Test public void testRoundTripTemplate027()
  {
    testTemplateSchemaArtifactRoundTripFromFile("templates/template-027.json");
  }

  @Test public void testRoundTripTemplate028()
  {
    testTemplateSchemaArtifactRoundTripFromFile("templates/template-028.json");
  }

  @Test public void testRoundTripTemplate030()
  {
    testTemplateSchemaArtifactRoundTripFromFile("templates/template-030.json");
  }

  @Test public void testRoundTripTemplate101()
  {
    testTemplateSchemaArtifactRoundTripFromFile("templates/template-101.json");
  }

  @Test public void testRoundTripRADxCLIGeneratedTemplate()
  {
    testTemplateSchemaArtifactRoundTripFromFile("templates/RADxCLIGeneratedTemplate.json");
  }

  @Test public void testRoundTripRADx2CLIGeneratedTemplate()
  {
    testTemplateSchemaArtifactRoundTripFromFile("templates/RADx2.0CLIGeneratedTemplate.json");
  }

  @Test public void testRoundTripSimpleInstance()
  {
    testTemplateInstanceArtifactRoundTripFromFile("instances/SimpleInstance.json");
  }

  @Test public void testRoundTripSimpleInstanceWithNesting()
  {
    testTemplateInstanceArtifactRoundTripFromFile("instances/SimpleInstanceWithNesting.json");
  }

  @Test public void testRoundTripRADxCLIGeneratedInstance()
  {
    testTemplateInstanceArtifactRoundTripFromFile("instances/RADxCLIGeneratedInstance.json");
  }

  @Test public void testRoundTripRADx2CLIGeneratedInstance()
  {
    testTemplateInstanceArtifactRoundTripFromFile("instances/RADx2.0CLIGeneratedInstance.json");
  }

  @Test public void testRoundTripElement001()
  {
    testElementSchemaArtifactRoundTripFromFile("elements/element-001.json");
  }

  @Test public void testRoundTripElement002()
  {
    testElementSchemaArtifactRoundTripFromFile("elements/element-002.json");
  }

  @Test public void testRoundTripElement003()
  {
    testElementSchemaArtifactRoundTripFromFile("elements/element-003.json");
  }

  @Test public void testRoundTripElement004()
  {
      testElementSchemaArtifactRoundTripFromFile("elements/element-004.json");
  }

  @Test public void testRoundTripField001()
  {
    testFieldSchemaArtifactRoundTripFromFile("fields/field-001.json");
  }

  @Test public void testRoundTripField002()
  {
    testFieldSchemaArtifactRoundTripFromFile("fields/field-002.json");
  }

  @Test public void testRoundTripField003()
  {
    testFieldSchemaArtifactRoundTripFromFile("fields/field-003.json");
  }

  @Test public void testRoundTripField004()
  {
    testFieldSchemaArtifactRoundTripFromFile("fields/field-004.json");
  }

  @Test public void testRoundTripSimpleTemplateWithType()
  {
    testTemplateSchemaArtifactRoundTripFromFile("templates/SimpleTemplateWithType.json");
  }

  @Test(expected = ArtifactParseException.class)
  public void testRoundTripNoValuePresentTemplate()
  {
    testElementSchemaArtifactRoundTripFromFile("elements/NoNumericTypePresentElement.json");
  }

  private void testTemplateSchemaArtifactRoundTripFromFile(String fileName)
  {
    ObjectNode originalRendering = getJSONFileContentAsObjectNode(fileName);

    assertTrue(validateJsonSchema(originalRendering));

    assertTrue(validateTemplateSchemaArtifact(originalRendering));

    TemplateSchemaArtifact originalTemplateSchemaArtifact = artifactReader.readTemplateSchemaArtifact(
      originalRendering);

    ObjectNode finalRendering = jsonSchemaArtifactRenderer.renderTemplateSchemaArtifact(originalTemplateSchemaArtifact);

    assertTrue(validateJsonSchema(finalRendering));

    assertTrue(validateTemplateSchemaArtifact(finalRendering));

    TemplateSchemaArtifact finalTemplateSchemaArtifact = artifactReader.readTemplateSchemaArtifact(finalRendering);

    assertEquals(originalTemplateSchemaArtifact, finalTemplateSchemaArtifact);
  }

  private void testElementSchemaArtifactRoundTripFromFile(String fileName)
  {
    ObjectNode originalRendering = getJSONFileContentAsObjectNode(fileName);

    assertTrue(validateJsonSchema(originalRendering));

    assertTrue(validateElementSchemaArtifact(originalRendering));

    ElementSchemaArtifact originalElementSchemaArtifact = artifactReader.readElementSchemaArtifact(
      originalRendering);

    ObjectNode finalRendering = jsonSchemaArtifactRenderer.renderElementSchemaArtifact(originalElementSchemaArtifact);

    assertTrue(validateJsonSchema(finalRendering));

    assertTrue(validateElementSchemaArtifact(finalRendering));

    ElementSchemaArtifact finalElementSchemaArtifact = artifactReader.readElementSchemaArtifact(finalRendering);

    assertEquals(originalElementSchemaArtifact, finalElementSchemaArtifact);
  }

  private void testFieldSchemaArtifactRoundTripFromFile(String fileName)
  {
    ObjectNode originalRendering = getJSONFileContentAsObjectNode(fileName);

    assertTrue(validateJsonSchema(originalRendering));

    assertTrue(validateFieldSchemaArtifact(originalRendering));

    FieldSchemaArtifact originalFieldSchemaArtifact = artifactReader.readFieldSchemaArtifact(
      originalRendering);

    ObjectNode finalRendering = jsonSchemaArtifactRenderer.renderFieldSchemaArtifact(originalFieldSchemaArtifact);

    assertTrue(validateJsonSchema(finalRendering));

    assertTrue(validateFieldSchemaArtifact(finalRendering));

    FieldSchemaArtifact finalFieldSchemaArtifact = artifactReader.readFieldSchemaArtifact(finalRendering);

    assertEquals(originalFieldSchemaArtifact, finalFieldSchemaArtifact);
  }

  private void testTemplateInstanceArtifactRoundTripFromFile(String fileName)
  {
    ObjectNode originalRendering = getJSONFileContentAsObjectNode(fileName);

    TemplateInstanceArtifact originalTemplateInstanceArtifact = artifactReader.readTemplateInstanceArtifact(
      originalRendering);

    ObjectNode finalRendering = jsonSchemaArtifactRenderer.renderTemplateInstanceArtifact(
      originalTemplateInstanceArtifact);

    TemplateInstanceArtifact finalTemplateInstanceArtifact = artifactReader.readTemplateInstanceArtifact(
      finalRendering);

    assertEquals(originalTemplateInstanceArtifact.jsonLdContext(), finalTemplateInstanceArtifact.jsonLdContext());
    assertEquals(originalTemplateInstanceArtifact.childNames(), finalTemplateInstanceArtifact.childNames());
    assertEquals(originalTemplateInstanceArtifact.singleInstanceFieldInstances(), finalTemplateInstanceArtifact.singleInstanceFieldInstances());
    assertEquals(originalTemplateInstanceArtifact.singleInstanceElementInstances(), finalTemplateInstanceArtifact.singleInstanceElementInstances());
    assertEquals(originalTemplateInstanceArtifact.multiInstanceFieldInstances(), finalTemplateInstanceArtifact.multiInstanceFieldInstances());
    assertEquals(originalTemplateInstanceArtifact.multiInstanceElementInstances(), finalTemplateInstanceArtifact.multiInstanceElementInstances());

    assertEquals(originalTemplateInstanceArtifact, finalTemplateInstanceArtifact);
  }

  private void testRoundTripTemplateSchemaArtifact(TemplateSchemaArtifact originalTemplateSchemaArtifact)
  {
    ObjectNode originalRendering = jsonSchemaArtifactRenderer.renderTemplateSchemaArtifact(
      originalTemplateSchemaArtifact);

    assertTrue(validateJsonSchema(originalRendering));

    assertTrue(validateTemplateSchemaArtifact(originalRendering));

    TemplateSchemaArtifact finalTemplateSchemaArtifact = artifactReader.readTemplateSchemaArtifact(originalRendering);

    ObjectNode finalRendering = jsonSchemaArtifactRenderer.renderTemplateSchemaArtifact(finalTemplateSchemaArtifact);

    assertTrue(validateJsonSchema(finalRendering));

    assertTrue(validateTemplateSchemaArtifact(finalRendering));

    assertEquals(originalTemplateSchemaArtifact, finalTemplateSchemaArtifact);
  }

  private void testRoundTripElementSchemaArtifact(ElementSchemaArtifact originalElementSchemaArtifact)
  {
    ObjectNode originalRendering = jsonSchemaArtifactRenderer.renderElementSchemaArtifact(originalElementSchemaArtifact);

    assertTrue(validateJsonSchema(originalRendering));

    assertTrue(validateElementSchemaArtifact(originalRendering));

    ElementSchemaArtifact finalElementSchemaArtifact = artifactReader.readElementSchemaArtifact(originalRendering);

    ObjectNode finalRendering = jsonSchemaArtifactRenderer.renderElementSchemaArtifact(finalElementSchemaArtifact);

    assertTrue(validateJsonSchema(finalRendering));

    assertTrue(validateElementSchemaArtifact(finalRendering));

    assertEquals(originalElementSchemaArtifact, finalElementSchemaArtifact);
  }

  private void testRoundTripFieldSchemaArtifact(FieldSchemaArtifact originalFieldSchemaArtifact)
  {
    ObjectNode originalRendering = jsonSchemaArtifactRenderer.renderFieldSchemaArtifact(originalFieldSchemaArtifact);

    assertTrue(validateJsonSchema(originalRendering));

    assertTrue(validateFieldSchemaArtifact(originalRendering));

    FieldSchemaArtifact finalFieldSchemaArtifact = artifactReader.readFieldSchemaArtifact(originalRendering);

    ObjectNode finalRendering = jsonSchemaArtifactRenderer.renderFieldSchemaArtifact(finalFieldSchemaArtifact);

    assertTrue(validateJsonSchema(finalRendering));

    assertTrue(validateFieldSchemaArtifact(finalRendering));

    assertEquals(originalFieldSchemaArtifact, finalFieldSchemaArtifact);
  }

  private ObjectNode getJSONFileContentAsObjectNode(String jsonFileName)
  {
    try {
      JsonNode jsonNode = mapper.readTree(
        new File(JsonSchemaArtifactReaderTest.class.getClassLoader().getResource(jsonFileName).getFile()));

      if (jsonNode.isObject())
        return (ObjectNode)jsonNode;
      else
        throw new RuntimeException("Error reading JSON file " + jsonFileName + ": root node is not an ObjectNode");
    } catch (IOException e) {
      throw new RuntimeException("Error reading JSON file " + jsonFileName + ": " + e.getMessage());
    }
  }

  private boolean validateJsonSchema(ObjectNode schemaNode)
  {
    try {
      JsonSchemaFactory.byDefault().getJsonSchema(schemaNode);
      return true;
    } catch (ProcessingException e) {
      return false;
    }
  }

  private boolean validateJsonSchema(ObjectNode schemaNode, ObjectNode instanceNode)
  {
    try {
      JsonSchema schema = JsonSchemaFactory.byDefault().getJsonSchema(schemaNode);
      schema.validate(instanceNode);
      return true;
    } catch (ProcessingException e) {
      return false;
    }
  }

  private boolean validateTemplateSchemaArtifact(ObjectNode schemaNode)
  {
    try {
      ValidationReport validationReport = cedarModelValidator.validateTemplate(schemaNode);
      if (validationReport.getValidationStatus().equals("true"))
        return true;
      else
        return false;
    } catch (Exception e) {
      return false;
    }
  }

  private boolean validateElementSchemaArtifact(ObjectNode schemaNode)
  {
    try {
      ValidationReport validationReport = cedarModelValidator.validateTemplateElement(schemaNode);
      if (validationReport.getValidationStatus().equals("true"))
        return true;
      else
        return false;
    } catch (Exception e) {
      return false;
    }
  }

  private boolean validateFieldSchemaArtifact(ObjectNode schemaNode)
  {
    try {
      ValidationReport validationReport = cedarModelValidator.validateTemplateField(schemaNode);
      if (validationReport.getValidationStatus().equals("true"))
        return true;
      else
        return false;
    } catch (Exception e) {
      return false;
    }
  }

}
