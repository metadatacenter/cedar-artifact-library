CEDAR Artifact Library
======================

Contains various tools to work with CEDAR model artifacts (templates, elements, fields, and template instances).

Primarily, the library provides an API to programatically work with CEDAR artifacts.

The library uses this API to support the conversion of artifacts to and from various serializations.

Currently it supports (1) the reading of artifacts from their JSON Schema and JSON-LD serializations, and (2) the writing of Excel, TSV, CSV, and YAML serializations.

## Reading Schema Artifacts

The library provides a class to convert the [Jackson Library](https://github.com/FasterXML/jackson) `ObjectNode` class representation of a JSON object containing a JSON Schema serialization of CEDAR artifacts to a Java representation of those artifacts. 

A class called `JsonSchemaArtifactReader` provides methods to generate Java representations of templates, elements and fields from their JSON Schema representation.

For example, assuming we used the Jackson Library to read a JSON document containing a JSON Schema representation of a CEDAR template, we can generate a Java representation as follows:

```java
// Generate an instance of the JsonSchemaArtifactReader class
JsonSchemaArtifactReader artifactReader = new JsonSchemaArtifactReader();
// Generate a Java representation JSON in Jackson Library ObjectNode instance
TemplateSchemaArtifact templateSchemaArtifact 
  = artifactReader.readTemplateSchemaArtifact(objectNode);
```

The `TemplateSchemaArtifact` contains a full representation of a CEDAR template.

The `JsonSchemaArtifactReader` class also provides methods to read CEDAR element and field artifacts.

## Serializing Schema Artifacts

Currently, four serializations are supported: JSON Schema, YAML, Excel, CSV, and TSV.

### Serializing to JSON Schema 

A class called `JsonSchemaArtifactRenderer` provides methods to serialize CEDAR schema artifacts to JSON Schema.

Again, the `ObjectNode` class from the Jackson Library is used to represent JSON documents.

For example, we can generate a JSON Schema serialization for a CEDAR template as follows:

```java
// Obtain instance of TemplateSchemaArtifact class
TemplateSchemaArtifact templateSchemaArtifact = ...
// Generate a Jackson Library ObjectNode instance containing a JSON Schema representation on the template
ObjectNode rendering 
  = jsonSchemaArtifactRenderer.renderTemplateSchemaArtifact(templateSchemaArtifact);
```

### Serializing to YAML

A class called `YamlArtifactRenderer` provides methods to serialize CEDAR schema artifacts to YAML.

For example, we can generate a YAML serialization of a CEDAR template as follows:

```java
// Set to true for a complete YAML representation of an artifact, false for a condensed representation
boolean isExanded = true;
// Create the renderer
YamlArtifactRenderer yamlArtifactRenderer = new YamlArtifactRenderer(isExpanded);
// Generate a map containing a YAML representation of the template
LinkedHashMap<String, Object> yamlRendering 
  = yamlArtifactRenderer.renderTemplateSchemaArtifact(templateSchemaArtifact);
```

This map can be written to a file using the Jackson Library as follows:

```java
YAMLFactory yamlFactory = new YAMLFactory()
  .disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)
  .enable(YAMLGenerator.Feature.MINIMIZE_QUOTES)
  .disable(YAMLGenerator.Feature.SPLIT_LINES);
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

### Serializing to TSV

A utility class is provided that can take the above Excel rendering and generate a TSV from it.

For example, to generate a TSV from the first sheeet of the above workbook:

```java
StringBuffer tsvBuffer = SpreadSheetUtil.convertSheetToTsv(workbook.getSheetAt(0));
```

### Serializing to CSV

Again, a utility class is provided that can take the above Excel rendering and generate a CSV from it.

For example, to generate a CSV from the first sheeet of the above workbook:

```java
StringBuffer tsvBuffer = SpreadSheetUtil.convertSheetToCsv(workbook.getSheetAt(0));
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

To generate an Excel spreadsheet from a CEDAR template:

    mvn exec:java@template2excel -Dexec.args="<input_template_filename.json> <output_Excel_filename.xlsx> https://terminology.metadatacenter.org/bioportal/integrated-search/ <CEDAR API key>"

This will read a JSON-Schema-based template and convert it into an Excel file.

To generate a TSV from a CEDAR template:

    mvn exec:java@template2tsv -Dexec.args="<input_template_filename.json> <output_TSV_filename.tsv> https://terminology.metadatacenter.org/bioportal/integrated-search/ <CEDAR API key>"

To generate a YAML from a CEDAR template:

    mvn exec:java@template2yaml -Dexec.args="<input_template_filename.json> <output_YAML_filename.tsv>"

This will read a JSON-Schema-based template and convert it into a YAML file.

## Reading Schema Artifacts from their JSON Schema Serializations

