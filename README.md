CEDAR Artifact Library
======================

Contains various tools to work with CEDAR model artifacts (templates, elements, fields, template instances).

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

    mvn exec:java -Dexec.args="<Input_template_filename.json> <Output_Excel_filename.xlsx> https://terminology.metadatacenter.org/bioportal/integrated-search/ <CEDAR API key>"

This will read a JSON-Schema-based template and convert it into an Excel file.

