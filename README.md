CEDAR Artifact Library
======================

Contains various tools to work with CEDAR model artifacts (templates, elements, fields, and template instances).

Primarily, the library provides an API to programatically work with CEDAR artifacts.

The library uses this API to support the conversion of artifacts to and from various serializations.

Currently it supports (1) the reading of artifacts from their JSON Schema and JSON-LD serializations, and (2) the writing of Excel, TSV, and YAML serializations.

## Reading Artifacts

The library provides a class to convert the [Jackson library](https://github.com/FasterXML/jackson) ObjectNode class representation of a JSON object to a Java representation of an artifacts. 

A class JsonSchemaArtifactReader provides methods to generate Java representations of templates, elementd and fields from the JSON Schema representation.

For example, assuming we used the Jackson library to read a JSON document containing a JSON Schema representation of a CEDAER template, we can generate a Java representation as follows:

```java

  // Generate an instance of the JsonSchemaArtifactReader class
  JsonSchemaArtifactReader artifactReader = new JsonSchemaArtifactReader();

  // Read an Jackson library ObjectNode instance and generate a Java representation of it
  TemplateSchemaArtifact templateSchemaArtifact = artifactReader.readTemplateSchemaArtifact(objectNode);
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

