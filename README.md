CEDAR Artifact Library
======================

This library various tools to work with CEDAR model artifacts (templates, elements, fields, and template instances).

Primarily, the library provides a Java API to work with CEDAR artifacts.

The library uses this API to support the conversion of artifacts to and from various serializations.

Currently, it supports (1) the reading of artifacts from their JSON Schema and JSON-LD serializations, 
(2) the writing of JSON Schema and YAML serializations of templates, elements and fields, and 
(3) the writing of Excel, TSV, CSV, YAML, and UBKG serializations of templates.

## Reading Schema Artifacts

The library provides a class to convert the [Jackson Library](https://github.com/FasterXML/jackson) `ObjectNode` class representation of a JSON object containing a JSON Schema serialization of CEDAR artifacts to a Java representation of those artifacts. 

A class called `JsonSchemaArtifactReader` provides methods to generate Java representations of templates, elements and fields from their JSON Schema representation.

For example, assuming we used the Jackson Library to read a JSON document containing a JSON Schema representation of a CEDAR template, we can generate a Java representation as follows:

```java
// Obtain an instance of a Jackson Library ObjectNode class
// containing a JSON Schema representation of a CEDAR template
ObjectNode objectNode = ...
// Generate an instance of the JsonSchemaArtifactReader class
JsonSchemaArtifactReader artifactReader = new JsonSchemaArtifactReader();
// Generate a Java representation of the CEDAR template 
TemplateSchemaArtifact templateSchemaArtifact 
  = artifactReader.readTemplateSchemaArtifact(objectNode);
```

The `TemplateSchemaArtifact` contains a full representation of a CEDAR template.

The `JsonSchemaArtifactReader` class also provides methods to read CEDAR element, field and instance artifacts.

## Serializing Schema Artifacts

Currently, the following serializations are supported: JSON Schema, YAML, Excel, CSV, TSV, and UBKG.

### Serializing to JSON Schema 

A class called `JsonSchemaArtifactRenderer` provides methods to serialize CEDAR schema artifacts to JSON Schema.

Again, the `ObjectNode` class from the Jackson Library is used to represent JSON documents.

For example, we can generate a JSON Schema serialization for a CEDAR template as follows:

```java
// Obtain instance of TemplateSchemaArtifact class
TemplateSchemaArtifact templateSchemaArtifact = ...
// Generate a Jackson Library ObjectNode instance containing a JSON Schema representation of the template
ObjectNode rendering 
  = jsonSchemaArtifactRenderer.renderTemplateSchemaArtifact(templateSchemaArtifact);
```

### Serializing to YAML

A class called `YamlArtifactRenderer` provides methods to serialize CEDAR schema artifacts to YAML.

For example, we can generate a YAML serialization of a CEDAR template as follows:

```java
// Set to 'false' for a complete YAML representation of an artifact, 'true' for a condensed representation
boolean isCompact = false;
// Create the renderer
YamlArtifactRenderer yamlArtifactRenderer = new YamlArtifactRenderer(isCompact);
// Generate a map containing a YAML representation of the template
LinkedHashMap<String, Object> yamlRendering 
  = yamlArtifactRenderer.renderTemplateSchemaArtifact(templateSchemaArtifact);
```

This map can be written to a file using the Jackson Library as follows:

```java
YAMLFactory yamlFactory = new YAMLFactory()
          disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER).
          enable(YAMLGenerator.Feature.MINIMIZE_QUOTES).
          enable(YAMLGenerator.Feature.INDENT_ARRAYS_WITH_INDICATOR).
          disable(YAMLGenerator.Feature.SPLIT_LINES);
ObjectMapper mapper = new ObjectMapper(yamlFactory);

LinkedHashMap<String, Object> yamlRendering 
  = yamlRenderer.renderTemplateSchemaArtifact(templateSchemaArtifact);

mapper.writeValue([file], yamlRendering);
```

### Serializing Templates to Excel

A class called `ExcelArtifactRenderer` provides methods to serialize CEDAR templates to Excel.

Only top-level fields in templates will be serialized. All nested elements will be ignored.

The [Apache POI](https://poi.apache.org/) library `Workbook` class is used to store the generated Excel representation.

Using the `ExcelArtifactRendered`, we can generate a YAML serialization of a CEDAR template as follows:

```java
// Pass a CEDAR terminology server endpoint (e.g., https://terminology.metadatacenter.org/bioportal/integrated-search/) with a CEDAR API key
ExcelArtifactRenderer renderer
  = new ExcelArtifactRenderer(terminologyServerIntegratedSearchEndpoint, terminologyServerAPIKey);

// Generate an Apache POI Workbook rendering, starting at column 0 and row 0
Workbook workbook = renderer.render(templateSchemaArtifact, 0, 0);
```

### Serializing Templates to TSV

A utility class is provided that can take the above Excel rendering and generate a TSV from it.

For example, to generate a TSV from the first sheeet of the above workbook:

```java
StringBuffer tsvBuffer = SpreadSheetUtil.convertSheetToTsv(workbook.getSheetAt(0));
```

This string buffer can be written to a file as follows:

```java
    try (BufferedWriter writer = new BufferedWriter(new FileWriter([file]))) {
      writer.write(tsvBuffer.toString());
    } catch (IOException e) {
      ...
    }
```

### Serializing Templates to CSV

Again, a utility class is provided that can take the above Excel rendering and generate a CSV from it.

For example, to generate a CSV from the first sheeet of the above workbook:

```java
StringBuffer csvBuffer = SpreadSheetUtil.convertSheetToCsv(workbook.getSheetAt(0));
```
## Creating Templates

CEDAR templates are represented using the `TemplateSchemaArtifact` class. 
This class provides a complete specification of a CEDAR template. 

A companion builder class can be used to create a template. 

For example, to create a minimal template with a name and description, we can use the library as follows:

```java
TemplateSchemaArtifact templateSchemaArtifact = TemplateSchemaArtifact.builder()
  .withName("Study")
  .withDescription("A template describing a study")
  .build();
```

## Creating Elements

CEDAR elements are represented using the `ElementSchemaArtifact` class. 

Again, a companion builder class can be used to create an element. 

For example, to create a minimal element with a name and description, we can use the library as follows:

```java
ElementSchemaArtifact elementSchemaArtifact = ElementSchemaArtifact.builder()
  .withName("Address")
  .withDescription("An element describing an address")
  .build();
```

This element can be added as a child to a template as follows:

```java
TemplateSchemaArtifact templateSchemaArtifact = TemplateSchemaArtifact.builder()
  .withName("Study")
  .withDescription("A template describing a study")
  .withElementSchema(elementSchemaArtifact)
  .build();
```

## Creating Fields

Currently, CEDAR provides the following types of fields: text, temporal, numeric, controlled term, text area, phone number, email, radio, list, link, image, YouTube, section break, rich text, and attribute-value.

A class called `FieldSchemaArtifact` represents all of these field types.

Since each field has specific characteristics, a custom builder is provided to contruct each field type.

### Creating Text Fields

A class called `TextFieldBuilder` can be used to create a CEDAR text field.

With this class we can create a text field representing a study name with a minimum length of 2 and a maximum length of 10 as follows:

```java
FieldSchemaArtifact fieldSchemaArtifact = FieldSchemaArtifact.textFieldBuilder().
      withName("Study ID").
      withDescription("Field representing the ID of a study).
      withMinLength(2).
      withMaxLength(10).
      build();
```

This field can be added as a child to a template as follows:

```java
TemplateSchemaArtifact templateSchemaArtifact = TemplateSchemaArtifact.builder()
  .withName("Study")
  .withDescription("A template describing a study")
  .withFieldSchema(fieldSchemaArtifact);
  .build();
```

### Creating Numeric Fields

A class called `NumericFieldBuilder` can be used to create a CEDAR numeric fields.

In CEDAR, numeric fields can be one of XML Schema Datatypes decimal, integer, long, byte, short, int, float and double. An enumeration called `NumericType` can be used to specify this type on field creation. Numeric fields also allow the optional specification of minimum and maximum values, and of a default value.

An example numeric field representing the percentage of a treatment completed and with a default of 0% could be created as follows:

```java
    FieldSchemaArtifact fieldSchemaArtifact = FieldSchemaArtifact.numericFieldBuilder().
      withName("Treatment Completed %").
      withDescription("Please enter the percentage of the treatment that has been completed").
      withNumericType(NumericType.INTEGER).
      withMinValue(0).
      withMaxValue(100).
      withDefaultValue(0).
      build();
```

### Creating Temporal Fields

A class called `TemporalFieldBuilder` can be used to create a CEDAR temporal fields.

In CEDAR, temporal fields can represent a time value, a date value, and a datetime value. An enumerated type called `TemporalType` can be used to specify this type when creating a temporal field. Similarly, the desired granularity and whether a 12- or 24-hour presentation is desired can be opitionally be specified; an enumeration called `TemporalGranularity` can be used to specify the format, and an enumeration called `InputTimeFormat` for the latter. Finally, a temporal field may optionally be configured to display time zone information.

An example temporal field representing the time of a patient visit recorded with the accuracy of minutes and presented in 24-hour format with time zone information displayed could be created as follows:

```java
    FieldSchemaArtifact fieldSchemaArtifact = FieldSchemaArtifact.temporalFieldBuilder().
      withName("Patient Visit Time").
      withTemporalType(TemporalType.DATETIME).
      withTemporalGranularity(TemporalGranularity.MINUTES).
      withInputTimeFormat(InputTimeFormat.TWENTY_FOUR_HOURS).
      withTimeZoneEnabled(true).
      build();
```

### Creating Controlled Term Fields

A class called `ControlledTermFieldBuilder` can be used to create a CEDAR controlled term fields.

Controlled term fields can have four possible value types: classes, ontologies, ontology branches, and value sets.

The builder class has methods `withClassValueConstraint`, `withOntologyValueConstraint`, `withBranchValueConstraint`,  and `withValueSetValueConstraint` to allow specification of these types.

Here, for example, is the builder being used to create a field with all four types:

```java
    String name = "Disease";
    String description = "Please enter a disease";

    URI ontologyUri = URI.create("https://data.bioontology.org/ontologies/DOID");
    String ontologyAcronym = "DOID";
    String ontologyName = "Human Disease Ontology";
    URI branchUri = URI.create("http://purl.bioontology.org/ontology/SNOMEDCT/64572001");

    String branchAcronym = "SNOMEDCT";
    String branchName = "Disease";
    String branchSource = "SNOMEDCT";

    URI classUri = URI.create("http://purl.bioontology.org/ontology/LNC/LA19711-3");
    String classSource = "LOINC";
    String classLabel= "Human";
    String classPrefLabel = "Human";
    ValueType classValueType = ValueType.ONTOLOGY_CLASS;

    URI valueSetUri = URI.create("https://cadsr.nci.nih.gov/metadata/CADSR-VS/77d61de250089d223d7153a4283e738043a15707");
    String valueSetCollection = "CADSR-VS";
    String valueSetName = "Stable Disease";

    FieldSchemaArtifact fieldSchemaArtifact = FieldSchemaArtifact.controlledTermFieldBuilder().
      withName("Disease").
      withOntologyValueConstraint(ontologyUri, ontologyAcronym, ontologyName).
      withBranchValueConstraint(branchUri, branchSource, branchAcronym, branchName, 3).
      withClassValueConstraint(classUri, classSource, classLabel, classPrefLabel, classValueType).
      withValueSetValueConstraint(valueSetUri, valueSetCollection, valueSetName).
      build();
```

### Creating Radio Fields

A class called `RadioFieldBuilder` can be used to create a CEDAR radio field.

A list of options can be supplied when creating a radio field. Whether an option is selected by default can also be indicated.

For example, we can create a radio field representing a question with options Yes/No/Maybe, with Maybe selected by default as follows:

```java
    FieldSchemaArtifact fieldSchemaArtifact = FieldSchemaArtifact.radioFieldBuilder().
      withName("Covid-19 Status").
      withDescription("Have you had Covid-19?").
      withOption("Yes").
      withOption("No").
      withOption("Maybe", true).
      build();
```

### Creating List Fields

A class called `ListFieldBuilder` can be used to create a CEDAR list field.

A list of options can be supplied when creation a list field. Whether an option is selected by default can also be indicated.

Using this class, we can create a list field representing a question with options Moderna/Pfizer/None, with None selected by default as follows:

```java
    FieldSchemaArtifact fieldSchemaArtifact = FieldSchemaArtifact.listFieldBuilder().
      withName("Covid-19 Vaccine").
      withDescription("Which vaccine provider did you use?").
      withOption("Moderna").
      withOption("Pfizer").
      withOption("None", true).
      build();
```

### Creating Checkbox Fields

A class called `CheckboxFieldBuilder` can be used to create a CEDAR checkbox field.

When creating a checkbox field, a list of options can be supplied. Whether an option is selected by default can also be indicated.

For example, we can create a checkbox field representing a question with options Yes/No/Don't Know, with Don't Know salected by default as follows:

```java
    FieldSchemaArtifact fieldSchemaArtifact = FieldSchemaArtifact.checkboxFieldBuilder().
      withName("DTAP Status").
      withDescription("Are you up-to-date on your DTAP vaccination?").
      withOption("Yes").
      withOption("No").
      withOption("Don't Know", true).
      build();
```

### Creating Link Fields

A class called `LinkFieldBuilder` can be used to create a CEDAR link field.

Using this class, we can create a link field as follows:

```java
    FieldSchemaArtifact fieldSchemaArtifact = FieldSchemaArtifact.linkFieldBuilder().
      withName("Institution Home Page").
      withDescription("Please enter your institution's home page").
      withDefaultValue(URI.create("https://stanford.edu"), "Stanford")
      build();
```

A default value can be specified using the `withDefaultValue` builder method. This method must be supplied with the URI of the default value and a user-friendly label for it.

### Creating Phone Number Fields

A class called `PhoneNumberFieldBuilder` can be used to create a CEDAR phone number field.

Using this class, we can create a phone number field as follows:

```java
    FieldSchemaArtifact fieldSchemaArtifact = FieldSchemaArtifact.phoneNumberFieldBuilder().
      withName("Phone Number").
      withDescription("Please enter your phone number").
      build();
```

### Creating Email Fields

A class called `EmailFieldBuilder` can be used to create a CEDAR email field.

Using this class, we can create an email field as follows:

```java
    FieldSchemaArtifact fieldSchemaArtifact = FieldSchemaArtifact.emailFieldBuilder().
      withName("Email Address").
      withDescription("Please enter your email address").
      build();
```

### Creating Text Area Fields

A class called `TextAreaFieldBuilder` can be used to create a CEDAR text area fields.

For example, to create a text area field representing a study description with a minimum lengtth of 20 and a maximum length of 10000 we can do the following:

```java
FieldSchemaArtifact fieldSchemaArtifact = FieldSchemaArtifact.textAreaFieldBuilder().
      withName("Study Description").
      withDescription("Field representing the description of a study).
      withMinLength(20).
      withMaxLength(1000).
      build();
```

### Creating Attribute-Value Fields

A class called `AttributeValueTextBuilder` can be used to create a CEDAR attribute-value fields.

Using this class, we can create an atribute-value field as follows:

```java
FieldSchemaArtifact fieldSchemaArtifact = FieldSchemaArtifact.attributeValueFieldBuilder().
      withName("Additional Patient Characteristics").
      build();
```

### Creating Section Break Fields

A class called `SectionBreakFieldBuilder` can be used to create a CEDAR section break fields.

Using this class, we can create a section break field as follows:

```java
FieldSchemaArtifact fieldSchemaArtifact = FieldSchemaArtifact.sectionBreakFieldBuilder().
      withName("Patient Details Section").
      withContent("This section of the form contains details about a patient").
      build();
```

### Creating Image Fields

A class called `ImageFieldBuilder` can be used to create a CEDAR image fields.

Using this class, we can create an image field as follows:

```java
FieldSchemaArtifact fieldSchemaArtifact = FieldSchemaArtifact.imageFieldBuilder().
      withName("Patient Picture").
      withContent("link to image").
      build();
```

### Creating Image Fields

A class called `YouTubeBuilder` can be used to create a CEDAR YouTube fields.

Using this class, we can create a YouTube field as follows:

```java
FieldSchemaArtifact fieldSchemaArtifact = FieldSchemaArtifact.youTubeFieldBuilder().
      withName("Patient Video").
      withContent("link to video").
      build();
```

### Creating Rich Text Fields

A class called `YouRichTextBuilder` can be used to create a CEDAR rich text fields.

Using this class, we can create a rich text field as follows:

```java
FieldSchemaArtifact fieldSchemaArtifact = FieldSchemaArtifact.richTextFieldBuilder().
      withName("Patient Video").
      withContent("<http> ....").
      build();
```

## Generating Excel, TSV, and YAML Serializations of CEDAR Templates

To generate an Excel spreadsheet from a CEDAR template stored in a file:

```
    mvn exec:java@template2excel 
      -Dexec.args="-f <input_template_filename> 
                   -e <output_Excel_filename> 
                   -s https://terminology.metadatacenter.org/bioportal/integrated-search/ 
                   -k <CEDAR API key>"
```

To generate an Excel spreadsheet from a CEDAR template stored on the main CEDAR system:

```
    mvn exec:java@template2excel 
      -Dexec.args="-i <template_iri> 
                   -e <output_Excel_filename> 
                   -s https://terminology.metadatacenter.org/bioportal/integrated-search/ 
                   -r https://resource.metadatacenter.org/templates/ 
                   -k <CEDAR API key>"
```

e.g.,

```
    mvn exec:java@template2excel 
      -Dexec.args="-i https://repo.metadatacenter.org/templates/22bc762a-5020-419d-b170-24253ed9e8d9 
                   -e Sample.xlsx 
                   -s https://terminology.metadatacenter.org/bioportal/integrated-search/ 
                   -r https://resource.metadatacenter.org/templates/ 
                   -k <CEDAR API key>"
```

This will retrieve a JSON Schema-based template from CEDAR and convert it into an Excel file.

To generate a TSV file from a CEDAR template stored in a file:

    mvn exec:java@template2tsv
      -Dexec.args="-f <input_template_filename> 
                   -t <output_TSV_filename> 
                   -s https://terminology.metadatacenter.org/bioportal/integrated-search/ 
                   -k <CEDAR API key>"

To generate a TSV file from a CEDAR template stored on the main CEDAR system:

```
    mvn exec:java@template2tsv 
      -Dexec.args="-i <artifact_iri> 
                   -t <output_TSV_filename> 
                   -s https://terminology.metadatacenter.org/bioportal/integrated-search/ 
                   -r https://resource.metadatacenter.org/templates/ 
                   -k <CEDAR API key>"
```

To generate a YAML file from a CEDAR template stored in a file:

    mvn exec:java@artifact2yaml 
      -Dexec.args="-tsf <input_artifact_filename> -y <output_YAML_filename>"

This will read a JSON-Schema-based template and convert it into a YAML file. 
If the optional -c argument is present then a compact YAML rendering of the artifact is generated; otherwise the full form is produced.

If the `-y` option is omitted the YAML is written to the console.

Other file-based options are `-esf` for element schema artifacts, `-fsf` for field schema artifacts, and `-tif` for template instance artifacts.

To generate a YAML file from a CEDAR template stored on the main CEDAR system:

```
    mvn exec:java@artifact2yaml 
      -Dexec.args="-tsi <artifact_iri> 
                   -y <output_YAML_filename> 
                   -r https://resource.metadatacenter.org
                   -k <CEDAR API key>"

Other IRI-based options are `-esi` for element schema artifacts, `-fsi` for field schema artifacts, and `-tii` for template instance artifacts.

Again, the -c argument is optional. If the `-y` option is omitted the YAML is written to the console.


```
## Building the Library

To build the code in this repository you must have the following items installed:

+ [Java 17](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
+ A tool for checking out a [Git](http://git-scm.com/) repository.
+ Apache's [Maven](http://maven.apache.org/index.html).

First build the [CEDAR parent project](https://github.com/metadatacenter/cedar-parent) and the [CEDAR Model Library](https://github.com/metadatacenter/cedar-model-library).

Get a copy of the latest code:

    git clone https://github.com/metadatacenter/cedar-artifact-library.git

Change into the cedar-artifact-library directory:

    cd cedar-artifact-library 

Then build it with Maven:

    mvn clean install


