package org.metadatacenter.artifacts.model.renderer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import org.metadatacenter.artifacts.model.core.ElementInstanceArtifact;
import org.metadatacenter.artifacts.model.core.ElementSchemaArtifact;
import org.metadatacenter.artifacts.model.core.FieldInstanceArtifact;
import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifacts.model.core.JsonLdArtifact;
import org.metadatacenter.artifacts.model.core.MonitoredArtifact;
import org.metadatacenter.artifacts.model.core.ParentInstanceArtifact;
import org.metadatacenter.artifacts.model.core.SchemaArtifact;
import org.metadatacenter.artifacts.model.core.TemplateInstanceArtifact;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.model.ModelNodeNames;

import java.net.URI;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.metadatacenter.model.ModelNodeNames.BIBO_STATUS;
import static org.metadatacenter.model.ModelNodeNames.FIELD_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS;
import static org.metadatacenter.model.ModelNodeNames.INSTANCE_ARTIFACT_CONTEXT_PREFIX_MAPPINGS;
import static org.metadatacenter.model.ModelNodeNames.JSON_LD_CONTEXT;
import static org.metadatacenter.model.ModelNodeNames.JSON_LD_ID;
import static org.metadatacenter.model.ModelNodeNames.JSON_LD_LANGUAGE;
import static org.metadatacenter.model.ModelNodeNames.JSON_LD_TYPE;
import static org.metadatacenter.model.ModelNodeNames.JSON_LD_VALUE;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_ADDITIONAL_PROPERTIES;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_ARRAY;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_DESCRIPTION;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_FORMAT;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_FORMAT_DATE_TIME;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_FORMAT_URI;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_ITEMS;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_MAX_ITEMS;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_MIN_ITEMS;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_MIN_LENGTH;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_NULL;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_OBJECT;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_ONE_OF;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_PROPERTIES;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_REQUIRED;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_SCHEMA;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_STRING;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_TITLE;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_TYPE;
import static org.metadatacenter.model.ModelNodeNames.OSLC;
import static org.metadatacenter.model.ModelNodeNames.OSLC_MODIFIED_BY;
import static org.metadatacenter.model.ModelNodeNames.PARENT_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS;
import static org.metadatacenter.model.ModelNodeNames.PAV;
import static org.metadatacenter.model.ModelNodeNames.PAV_CREATED_BY;
import static org.metadatacenter.model.ModelNodeNames.PAV_CREATED_ON;
import static org.metadatacenter.model.ModelNodeNames.PAV_DERIVED_FROM;
import static org.metadatacenter.model.ModelNodeNames.PAV_LAST_UPDATED_ON;
import static org.metadatacenter.model.ModelNodeNames.PAV_PREVIOUS_VERSION;
import static org.metadatacenter.model.ModelNodeNames.PAV_VERSION;
import static org.metadatacenter.model.ModelNodeNames.RDFS;
import static org.metadatacenter.model.ModelNodeNames.RDFS_LABEL;
import static org.metadatacenter.model.ModelNodeNames.SCHEMA;
import static org.metadatacenter.model.ModelNodeNames.SCHEMA_IS_BASED_ON;
import static org.metadatacenter.model.ModelNodeNames.SCHEMA_ORG_DESCRIPTION;
import static org.metadatacenter.model.ModelNodeNames.SCHEMA_ORG_IDENTIFIER;
import static org.metadatacenter.model.ModelNodeNames.SCHEMA_ORG_NAME;
import static org.metadatacenter.model.ModelNodeNames.SCHEMA_ORG_SCHEMA_VERSION;
import static org.metadatacenter.model.ModelNodeNames.SKOS;
import static org.metadatacenter.model.ModelNodeNames.SKOS_ALTLABEL;
import static org.metadatacenter.model.ModelNodeNames.SKOS_NOTATION;
import static org.metadatacenter.model.ModelNodeNames.SKOS_PREFLABEL;
import static org.metadatacenter.model.ModelNodeNames.STATIC_FIELD_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS;
import static org.metadatacenter.model.ModelNodeNames.UI;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS;
import static org.metadatacenter.model.ModelNodeNames.XSD;
import static org.metadatacenter.model.ModelNodeNames.XSD_DATETIME;
import static org.metadatacenter.model.ModelNodeValues.OSLC_IRI;
import static org.metadatacenter.model.ModelNodeValues.PAV_IRI;
import static org.metadatacenter.model.ModelNodeValues.RDFS_IRI;
import static org.metadatacenter.model.ModelNodeValues.SCHEMA_IRI;
import static org.metadatacenter.model.ModelNodeValues.SKOS_IRI;
import static org.metadatacenter.model.ModelNodeValues.XSD_IRI;
import static org.metadatacenter.model.ModelNodeValues.XSD_STRING;

public class JsonSchemaArtifactRenderer implements ArtifactRenderer<ObjectNode>
{
  private final ObjectMapper mapper;
  private final DateTimeFormatter datetimeFormatter;

  public JsonSchemaArtifactRenderer()
  {
    this.mapper = new ObjectMapper();
    mapper.registerModule(new Jdk8Module());
    mapper.setSerializationInclusion(JsonInclude.Include.NON_ABSENT);
    datetimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
  }

  /**
   * Generate a JSON Schema specification for a template schema artifact
   * <p></p>
   * An example template schema artifact specification could look as follows:
   * <pre>
   * {
   *   "$schema": "http://json-schema.org/draft-04/schema#",
   *   "type": "object",
   *   "title": "Study template schema", "description": "Study template schema generated by the CEDAR Artifact Library",
   *   "schema:name": "Study", "schema:description": "Study template",
   *   "schema:schemaVersion": "1.6.0", "schema:identifier": "id1212132",
   *   "pav:version": "0.0.1", "bibo:status": "bibo:draft",
   *   "pav:previousVersion": "https://repo.metadatacenter.org/templates/54343",
   *   "pav:derivedFrom": "https://repo.metadatacenter.org/templates/232323",
   *   "@id": "https://repo.metadatacenter.org/templates/474378",
   *   "pav:createdOn": "2023-07-28T11:10:41-07:00", "pav:createdBy": "https://metadatacenter.org/users/656433",
   *   "pav:lastUpdatedOn": "2023-07-28T11:10:41-07:00", "oslc:modifiedBy": "https://metadatacenter.org/users/524332",
   *   "@context": { ... },
   *   "properties": { ... },
   *   "required": [ "@context", "@id",
   *                 "schema:isBasedOn", "schema:name", "schema:description",
   *                 "pav:createdOn", "pav:createdBy", "pav:lastUpdatedOn", "oslc:modifiedBy",
   *                 "Child Name 1", ... "Child Name n"],
   *   "additionalProperties": false,
   *   "_ui": { ... }
   *  }
   * </pre>
   */
  public ObjectNode renderTemplateSchemaArtifact(TemplateSchemaArtifact templateSchemaArtifact)
  {
    ObjectNode rendering = renderSchemaArtifact(templateSchemaArtifact);

    rendering.set(JSON_LD_CONTEXT, renderParentSchemaArtifactContextJsonLdSpecification());

    rendering.put(JSON_SCHEMA_PROPERTIES,
      renderTemplateSchemaArtifactPropertiesJsonSchemaSpecification(templateSchemaArtifact));

    // TODO Put this list in ModelNodeNames
    rendering.put(JSON_SCHEMA_REQUIRED, mapper.createArrayNode());
    rendering.withArray(JSON_SCHEMA_REQUIRED).add(JSON_LD_CONTEXT);
    rendering.withArray(JSON_SCHEMA_REQUIRED).add(JSON_LD_ID);
    rendering.withArray(JSON_SCHEMA_REQUIRED).add(SCHEMA_IS_BASED_ON);
    rendering.withArray(JSON_SCHEMA_REQUIRED).add(SCHEMA_ORG_NAME);
    rendering.withArray(JSON_SCHEMA_REQUIRED).add(SCHEMA_ORG_DESCRIPTION);
    rendering.withArray(JSON_SCHEMA_REQUIRED).add(PAV_CREATED_ON);
    rendering.withArray(JSON_SCHEMA_REQUIRED).add(PAV_CREATED_BY);
    rendering.withArray(JSON_SCHEMA_REQUIRED).add(PAV_LAST_UPDATED_ON);
    rendering.withArray(JSON_SCHEMA_REQUIRED).add(OSLC_MODIFIED_BY);

    for (String childName : templateSchemaArtifact.getChildNames())
      rendering.withArray(JSON_SCHEMA_REQUIRED).add(childName);

    if (templateSchemaArtifact.hasAttributeValueField())
      rendering.put(JSON_SCHEMA_ADDITIONAL_PROPERTIES, renderAdditionalPropertiesForAttributeValueFieldJsonSchemaSpecification());
    else
      rendering.put(JSON_SCHEMA_ADDITIONAL_PROPERTIES, false);

    rendering.put(UI, mapper.valueToTree(templateSchemaArtifact.templateUi()));

    // TODO Think about moving this to renderParentSchemaArtifactPropertiesJsonSchemaSpecification above
    for (String childName : templateSchemaArtifact.templateUi().order()) {
      if (templateSchemaArtifact.isField(childName)) {
        FieldSchemaArtifact childSchemaArtifact = templateSchemaArtifact.getFieldSchemaArtifact(childName);

        if (childSchemaArtifact.isMultiple())
          rendering.withObject("/" + JSON_SCHEMA_PROPERTIES).put(childName,
            renderJsonSchemaArrayWrapperSpecification(renderFieldSchemaArtifact(childSchemaArtifact), childSchemaArtifact.minItems(),
              childSchemaArtifact.maxItems()));
        else
          rendering.withObject("/" + JSON_SCHEMA_PROPERTIES)
            .put(childName, renderFieldSchemaArtifact(childSchemaArtifact));

      } else if (templateSchemaArtifact.isElement(childName)) {
        ElementSchemaArtifact childSchemaArtifact = templateSchemaArtifact.getElementSchemaArtifact(childName);

        if (childSchemaArtifact.isMultiple())
          rendering.withObject("/" + JSON_SCHEMA_PROPERTIES).put(childName,
            renderJsonSchemaArrayWrapperSpecification(renderElementSchemaArtifact(childSchemaArtifact),
              childSchemaArtifact.minItems(), childSchemaArtifact.maxItems()));
        else
          rendering.withObject("/" + JSON_SCHEMA_PROPERTIES)
            .put(childName, renderElementSchemaArtifact(childSchemaArtifact));

      } else // TODO Use typesafe switch on ChildSchemaArtifact when available
        throw new IllegalStateException("Order child " + childName + " is not a field or an element");
    }
    return rendering;
  }

  /**
   * Generate a JSON Schema specification for an element schema artifact
   * <p></p>
   * An example JSON Schema artifact specification could look as follows:
   * <pre>
   * {
   *   "$schema": "http://json-schema.org/draft-04/schema#",
   *   "type": "object",
   *   "title": "Address element schema", "description: "Address element schema generated by the CEDAR Template Editor 2.6.19",
   *   "schema:name": "Address", "schema:description": "Address element",
   *   "schema:schemaVersion": "1.6.0", "schema:identifier": "id999434",
   *   "pav:version": "0.0.1", "bibo:status": "bibo:draft",
   *   "pav:previousVersion": "https://repo.metadatacenter.org/template-elements/54343",
   *   "pav:derivedFrom": "https://repo.metadatacenter.org/template-elements/232323",
   *   "@type": "https://schema.metadatacenter.org/core/TemplateElement",
   *   "@id": "https://repo.metadatacenter.org/templates-elements/474378",
   *   "pav:createdOn": "2023-07-28T11:10:41-07:00", "pav:createdBy": "https://metadatacenter.org/users/656433",
   *   "pav:lastUpdatedOn": "2023-07-28T11:10:41-07:00", "oslc:modifiedBy": "https://metadatacenter.org/users/524332",
   *   "@context": { ... },
   *   "properties": { ... },
   *   "required": [ "@context", "@id", "Child Name 1", ... "Child Name n"],
   *   "additionalProperties": false,
   *   "_ui": { ... }
   *  }
   * </pre>
   */
  public ObjectNode renderElementSchemaArtifact(ElementSchemaArtifact elementSchemaArtifact)
  {
    ObjectNode rendering = renderSchemaArtifact(elementSchemaArtifact);

    rendering.put(JSON_LD_CONTEXT, renderParentSchemaArtifactContextJsonLdSpecification());

    rendering.put(JSON_SCHEMA_PROPERTIES,
      renderElementSchemaArtifactPropertiesJsonSchemaSpecification(elementSchemaArtifact));

    // TODO Put this list in ModelNodeNames
    rendering.put(JSON_SCHEMA_REQUIRED, mapper.createArrayNode());
    rendering.withArray(JSON_SCHEMA_REQUIRED).add(JSON_LD_CONTEXT);
    rendering.withArray(JSON_SCHEMA_REQUIRED).add(JSON_LD_ID);

    for (String childName : elementSchemaArtifact.getChildNames())
      rendering.withArray(JSON_SCHEMA_REQUIRED).add(childName);

    if (elementSchemaArtifact.hasAttributeValueField())
      rendering.put(JSON_SCHEMA_ADDITIONAL_PROPERTIES,
        renderAdditionalPropertiesForAttributeValueFieldJsonSchemaSpecification());
    else
      rendering.put(JSON_SCHEMA_ADDITIONAL_PROPERTIES, false);

    rendering.put(UI, mapper.valueToTree(elementSchemaArtifact.elementUi()));

    for (String childName : elementSchemaArtifact.elementUi().order()) {
      if (elementSchemaArtifact.isField(childName)) {
        FieldSchemaArtifact childSchemaArtifact = elementSchemaArtifact.getFieldSchemaArtifact(childName);

        if (childSchemaArtifact.isMultiple())
          rendering.withObject("/" + JSON_SCHEMA_PROPERTIES).put(childName,
            renderJsonSchemaArrayWrapperSpecification(renderFieldSchemaArtifact(childSchemaArtifact),
              childSchemaArtifact.minItems(), childSchemaArtifact.maxItems()));
        else
          rendering.withObject("/" + JSON_SCHEMA_PROPERTIES)
            .put(childName, renderFieldSchemaArtifact(childSchemaArtifact));

      } else if (elementSchemaArtifact.isElement(childName)) {
        ElementSchemaArtifact childSchemaArtifact = elementSchemaArtifact.getElementSchemaArtifact(childName);

        if (childSchemaArtifact.isMultiple())
          rendering.withObject("/" + JSON_SCHEMA_PROPERTIES).put(childName,
            renderJsonSchemaArrayWrapperSpecification(renderElementSchemaArtifact(childSchemaArtifact),
              childSchemaArtifact.minItems(), childSchemaArtifact.maxItems()));
        else
          rendering.withObject("/" + JSON_SCHEMA_PROPERTIES)
            .put(childName, renderElementSchemaArtifact(childSchemaArtifact));

      } else // TODO Use typesafe switch on ChildSchemaArtifact when available
        throw new IllegalStateException("Order child " + childName + " is not a field or an element");
    }
    return rendering;
  }

  /**
   * Generate a JSON Schema specification for a field schema artifact
   * <p></p>
   * An example JSON Schema field artifact could look as follows:
   * <pre>
   * {
   *   "$schema": "http://json-schema.org/draft-04/schema#",
   *   "type": "object",
   *   "title": "Disease field schema", "description": "Disease field schema generated by the CEDAR Template Editor 2.6.19",
   *   "schema:name": "Disease", "schema:description": "Please enter a disease",
   *   "schema:schemaVersion": "1.6.0", "schema:identifier": "id5666",
   *   "pav:version": "0.0.1", "bibo:status": "bibo:draft",
   *   "pav:previousVersion": "https://repo.metadatacenter.org/templates/435454",
   *   "pav:derivedFrom": "https://repo.metadatacenter.org/templates/893443",
   *   "@type": "https://schema.metadatacenter.org/core/Template",
   *   "@id": "https://repo.metadatacenter.org/templates/127666",
   *   "pav:createdOn": "2023-07-28T11:10:41-07:00", "pav:createdBy": "https://metadatacenter.org/users/656433",
   *   "pav:lastUpdatedOn": "2023-07-28T11:10:41-07:00", "oslc:modifiedBy": "https://metadatacenter.org/users/524332",
   *   "@context": { ... },
   *   "properties": { ... },
   *   "required": [ "@id" ],
   *   "additionalProperties": false,
   *   "skos:prefLabel": "Condition", "skos:altLabel": [ "Problem", "Illness" ],
   *   "_valueConstraints": { ... },
   *   "_ui": { ... }
   *  }
   * </pre>
   */
  public ObjectNode renderFieldSchemaArtifact(FieldSchemaArtifact fieldSchemaArtifact)
  {
    ObjectNode rendering = renderSchemaArtifact(fieldSchemaArtifact);

    if (fieldSchemaArtifact.isStatic())
      rendering.put(JSON_LD_CONTEXT, renderStaticFieldSchemaArtifactContextPrefixesJsonLdSpecification());
    else
      rendering.put(JSON_LD_CONTEXT, renderFieldSchemaArtifactContextPrefixesJsonLdSpecification());

    // Static fields have no value constraints field.
    if (!fieldSchemaArtifact.isStatic()) {

      if (fieldSchemaArtifact.hasIRIValue()) {
        rendering.put(JSON_SCHEMA_PROPERTIES, renderIRIFieldArtifactPropertiesJsonSchemaSpecification());
        rendering.put(JSON_SCHEMA_REQUIRED, mapper.createArrayNode());
        rendering.withArray(JSON_SCHEMA_REQUIRED).add(JSON_LD_ID);
      } else {
        rendering.put(JSON_SCHEMA_PROPERTIES, renderLiteralFieldArtifactPropertiesJsonSchemaSpecification());
        // Non-IRI fields may have en empty object as a value so there are no required fields
      }

      if (fieldSchemaArtifact.valueConstraints().isPresent())
        rendering.put(VALUE_CONSTRAINTS, mapper.valueToTree(fieldSchemaArtifact.valueConstraints().get()));
    }

    if (fieldSchemaArtifact.skosPrefLabel().isPresent())
      rendering.put(SKOS_PREFLABEL, fieldSchemaArtifact.skosPrefLabel().get());

    if (!fieldSchemaArtifact.skosAlternateLabels().isEmpty()) {
      rendering.put(SKOS_ALTLABEL, mapper.createArrayNode());
      for (String skosAlternateLabel : fieldSchemaArtifact.skosAlternateLabels())
        rendering.withArray(SKOS_ALTLABEL).add(skosAlternateLabel);
    }

    rendering.put(JSON_SCHEMA_ADDITIONAL_PROPERTIES, false);

    rendering.put(UI, mapper.valueToTree(fieldSchemaArtifact.fieldUi()));

    return rendering;
  }

  /**
   * Generate a template instance artifact
   * <p></p>
   * An example template instance artifact could look as follows:
   * <pre>
   *   {
   *   "@context": {
   *     "xsd": "http://www.w3.org/2001/XMLSchema#",
   *     "pav": "http://purl.org/pav/",
   *     "schema": "http://schema.org/",
   *     "oslc": "http://open-services.net/ns/core#",
   *     "skos": "http://www.w3.org/2004/02/skos/core#",
   *     "schema:name": {"@type": "xsd:string" },
   *     "schema:description": { "@type": "xsd:string" },
   *     "pav:derivedFrom": { "@type": "@id" },
   *     "pav:createdOn": { "@type": "xsd:dateTime" },
   *     "pav:createdBy": { "@type": "@id" },
   *     "pav:lastUpdatedOn": { "@type": "xsd:dateTime" },
   *     "oslc:modifiedBy": { "@type": "@id" },
   *     "schema:isBasedOn": { "@type": "@id" },
   *     "skos:notation": { "@type": "xsd:string" },
   *     "rdfs:label": { "@type": "xsd:string" }
   *   },
   *   "@id": "https://repo.metadatacenter.org/template-instances/66776767"
   *   "schema:isBasedOn": "https://repo.metadatacenter.org/templates/5454545",
   *   "schema:name": "Study metadata", "schema:description": "",
   *   "pav:createdOn": "2023-08-01T11:03:05-07:00",
   *   "pav:createdBy": "https://metadatacenter.org/users/344343",
   *   "pav:lastUpdatedOn": "2023-08-01T11:03:05-07:00",
   *   "oslc:modifiedBy": "https://metadatacenter.org/users/5666565"
   * }
   * </pre>
   */
  public ObjectNode renderTemplateInstanceArtifact(TemplateInstanceArtifact templateInstanceArtifact)
  {
    ObjectNode rendering = renderParentInstanceArtifact(templateInstanceArtifact);

    rendering.put(JSON_LD_CONTEXT, renderTemplateInstanceArtifactContextJsonLdSpecification());

    for (var propertyMapping : templateInstanceArtifact.jsonLdContext().entrySet()) {
      String fieldName = propertyMapping.getKey();
      URI propertyUri = propertyMapping.getValue();
      rendering.withObject("/" + JSON_LD_CONTEXT).put(fieldName, propertyUri.toString());
    }

    rendering.put(SCHEMA_IS_BASED_ON, templateInstanceArtifact.isBasedOn().toString());

    if (templateInstanceArtifact.jsonLdId().isPresent())
      rendering.put(JSON_LD_ID, templateInstanceArtifact.jsonLdId().get().toString());
    else
      rendering.putNull(JSON_LD_ID);

    if (templateInstanceArtifact.name().isPresent())
      rendering.put(SCHEMA_ORG_NAME, templateInstanceArtifact.name().get());

    if (templateInstanceArtifact.description().isPresent())
      rendering.put(SCHEMA_ORG_DESCRIPTION, templateInstanceArtifact.description().get());

    if (templateInstanceArtifact.createdBy().isPresent())
      rendering.put(PAV_CREATED_BY, templateInstanceArtifact.createdBy().get().toString());
    else
      rendering.putNull(PAV_CREATED_BY);

    if (templateInstanceArtifact.modifiedBy().isPresent())
      rendering.put(OSLC_MODIFIED_BY, templateInstanceArtifact.modifiedBy().get().toString());
    else
      rendering.putNull(OSLC_MODIFIED_BY);

    if (templateInstanceArtifact.createdOn().isPresent())
      rendering.put(PAV_CREATED_ON, renderOffsetDateTime(templateInstanceArtifact.createdOn().get()));
    else
      rendering.putNull(PAV_CREATED_ON);

    if (templateInstanceArtifact.lastUpdatedOn().isPresent())
      rendering.put(PAV_LAST_UPDATED_ON, renderOffsetDateTime(templateInstanceArtifact.lastUpdatedOn().get()));
    else
      rendering.putNull(PAV_LAST_UPDATED_ON);

    return rendering;
  }

  public ObjectNode renderElementInstanceArtifact(ElementInstanceArtifact elementInstanceArtifact)
  {
    ObjectNode rendering = renderParentInstanceArtifact(elementInstanceArtifact);

    if (!elementInstanceArtifact.jsonLdContext().isEmpty()) {
      rendering.put(JSON_LD_CONTEXT, mapper.createObjectNode());

      for (var propertyMapping : elementInstanceArtifact.jsonLdContext().entrySet()) {
        String fieldName = propertyMapping.getKey();
        URI propertyUri = propertyMapping.getValue();
        rendering.withObject("/" + JSON_LD_CONTEXT).put(fieldName, propertyUri.toString());
      }
    }

    if (elementInstanceArtifact.createdBy().isPresent())
      rendering.put(PAV_CREATED_BY, elementInstanceArtifact.createdBy().get().toString());

    if (elementInstanceArtifact.modifiedBy().isPresent())
      rendering.put(OSLC_MODIFIED_BY, elementInstanceArtifact.modifiedBy().get().toString());

    if (elementInstanceArtifact.createdOn().isPresent())
      rendering.put(PAV_CREATED_ON, renderOffsetDateTime(elementInstanceArtifact.createdOn().get()));

    if (elementInstanceArtifact.lastUpdatedOn().isPresent())
      rendering.put(PAV_LAST_UPDATED_ON, renderOffsetDateTime(elementInstanceArtifact.lastUpdatedOn().get()));

    return rendering;
  }

  private ObjectNode renderFieldInstanceArtifact(FieldInstanceArtifact fieldInstanceArtifact)
  {
    ObjectNode objectNode = mapper.createObjectNode();

    // If JSON-LD @value is present, assume it is a non-IRI field
    if (fieldInstanceArtifact.jsonLdValue().isPresent()) {
      if (fieldInstanceArtifact.jsonLdValue().get() != null)
        objectNode.put(JSON_LD_VALUE, fieldInstanceArtifact.jsonLdValue().get());
      else
        objectNode.putNull(JSON_LD_VALUE);

      if (fieldInstanceArtifact.jsonLdTypes().size() == 1) {
        objectNode.put(JSON_LD_TYPE, fieldInstanceArtifact.jsonLdTypes().get(0).toString());
      } else if (fieldInstanceArtifact.jsonLdTypes().size() > 1) {
        ArrayNode jsonLdTypesArrayNode = mapper.createArrayNode();

        for (int i = 0; i < fieldInstanceArtifact.jsonLdTypes().size(); i++)
          jsonLdTypesArrayNode.add(fieldInstanceArtifact.jsonLdTypes().get(i).toString());

        objectNode.put(JSON_LD_TYPE, jsonLdTypesArrayNode);
      }

      if (fieldInstanceArtifact.language().isPresent())
        objectNode.put(JSON_LD_LANGUAGE, fieldInstanceArtifact.language().get());

      if (fieldInstanceArtifact.notation().isPresent())
        objectNode.put(SKOS_NOTATION, fieldInstanceArtifact.notation().get());

      if (fieldInstanceArtifact.prefLabel().isPresent())
        objectNode.put(SKOS_PREFLABEL, fieldInstanceArtifact.prefLabel().get());

    } else if (fieldInstanceArtifact.jsonLdId().isPresent()) {
      if (fieldInstanceArtifact.jsonLdId().get() != null)
        objectNode.put(JSON_LD_ID, fieldInstanceArtifact.jsonLdId().get().toString());

      if (fieldInstanceArtifact.label().isPresent())
        objectNode.put(RDFS_LABEL, fieldInstanceArtifact.label().get());
    }

    return objectNode;
  }

  private ObjectNode renderParentInstanceArtifact(ParentInstanceArtifact parentInstanceArtifact)
  {
    ObjectNode rendering = mapper.createObjectNode();

    if (parentInstanceArtifact.jsonLdId().isPresent())
      rendering.put(JSON_LD_ID, parentInstanceArtifact.jsonLdId().get().toString());

    if (parentInstanceArtifact.name().isPresent())
      rendering.put(SCHEMA_ORG_NAME, parentInstanceArtifact.name().get());

    if (parentInstanceArtifact.description().isPresent())
      rendering.put(SCHEMA_ORG_DESCRIPTION, parentInstanceArtifact.description().get());

    for (String childName: parentInstanceArtifact.childNames()) {

      if (parentInstanceArtifact.singleInstanceFieldInstances().containsKey(childName)) {
        FieldInstanceArtifact fieldInstanceArtifact = parentInstanceArtifact.singleInstanceFieldInstances().get(childName);

        rendering.put(childName, renderFieldInstanceArtifact(fieldInstanceArtifact));
      } else if (parentInstanceArtifact.multiInstanceFieldInstances().containsKey(childName)) {
        List<FieldInstanceArtifact> fieldInstanceArtifacts = parentInstanceArtifact.multiInstanceFieldInstances()
          .get(childName);

        rendering.put(childName, renderFieldInstanceArtifacts(fieldInstanceArtifacts));
      } else if (parentInstanceArtifact.singleInstanceElementInstances().containsKey(childName)) {
        ElementInstanceArtifact elementInstanceArtifact = parentInstanceArtifact.singleInstanceElementInstances().get(childName);

        rendering.put(childName, renderElementInstanceArtifact(elementInstanceArtifact));
      } else if (parentInstanceArtifact.multiInstanceElementInstances().containsKey(childName)) {
        List<ElementInstanceArtifact> elementInstanceArtifacts = parentInstanceArtifact.multiInstanceElementInstances().get(childName);

        rendering.put(childName, renderElementInstanceArtifacts(elementInstanceArtifacts));
      } else if (parentInstanceArtifact.attributeValueFieldInstanceGroups().containsKey(childName)) {
        Map<String, FieldInstanceArtifact> attributeValueFieldInstances = parentInstanceArtifact.attributeValueFieldInstanceGroups()
          .get(childName);

        Set<String> attributeValueInstanceFieldNames = attributeValueFieldInstances.keySet();
        ArrayNode attributeValueFieldInstanceNamesNode = mapper.createArrayNode();

        for (String attributeValueFieldInstanceName : attributeValueInstanceFieldNames)
          attributeValueFieldInstanceNamesNode.add(attributeValueFieldInstanceName);

        rendering.put(childName, attributeValueFieldInstanceNamesNode);

      } else if (parentInstanceArtifact.attributeValueFieldInstanceGroups().values().stream()
        .anyMatch(instancesForAttributeValueField -> instancesForAttributeValueField.containsKey(childName))) {

        for (var attributeValueFieldInstancesGroupEntry : parentInstanceArtifact.attributeValueFieldInstanceGroups().entrySet()) {
          Map<String, FieldInstanceArtifact> attributeValueFieldInstances = attributeValueFieldInstancesGroupEntry.getValue();

          if (attributeValueFieldInstances.containsKey(childName))
            rendering.put(childName, renderFieldInstanceArtifact(attributeValueFieldInstances.get(childName)));
        }
      } else throw new RuntimeException("unknown child " + childName + " in parent instance artifact");
    }

    return rendering;
  }

  private ArrayNode renderFieldInstanceArtifacts(List<FieldInstanceArtifact> fieldInstanceArtifacts)
  {
    ArrayNode arrayNode = mapper.createArrayNode();

    for (FieldInstanceArtifact fieldInstanceArtifact : fieldInstanceArtifacts)
      arrayNode.add(renderFieldInstanceArtifact(fieldInstanceArtifact));

    return arrayNode;
  }

  private ArrayNode renderElementInstanceArtifacts(List<ElementInstanceArtifact> elementInstanceArtifacts)
  {
    ArrayNode arrayNode = mapper.createArrayNode();

    for (ElementInstanceArtifact elementInstanceArtifact : elementInstanceArtifacts)
      arrayNode.add(renderElementInstanceArtifact(elementInstanceArtifact));

    return arrayNode;
  }

  /**
   * Generate a base schema artifact rendering. In addition to core artifact fields (@type, @id, pav:createdOn,
   * pav:createdBy, pav:lastUpdatedOn, and oslc:modifiedBy), it will have JSON Schema fields (@schema, type,
   * title, description), Schema.org fields (schema:name, schema:description, schema:schemaVersion,
   * schema:identifier), version fields (pav:version, bibo:status, pav:previousVersion), and a derivation field
   * (pav:derivedFrom).
   * <p></p>
   * An example artifact could look as follows:
   * <pre>
   * {
   *   "$schema": "http://json-schema.org/draft-04/schema#",
   *   "type": "object",
   *   "title": "Study template schema", "description": "Bare template schema generated by the CEDAR Template Editor 2.6.19",
   *   "schema:name": "Study", "schema:description": "",
   *   "schema:schemaVersion": "1.6.0", "schema:identifier": "id4343",
   *   "pav:version": "0.0.1", "bibo:status": "bibo:draft",
   *   "pav:previousVersion": "https://repo.metadatacenter.org/templates/435454",
   *   "pav:derivedFrom": "https://repo.metadatacenter.org/templates/893443",
   *   "@type": "https://schema.metadatacenter.org/core/Template",
   *   "@id": "https://repo.metadatacenter.org/templates/127666",
   *   "pav:createdOn": "2023-07-28T11:10:41-07:00", "pav:createdBy": "https://metadatacenter.org/users/656433",
   *   "pav:lastUpdatedOn": "2023-07-28T11:10:41-07:00", "oslc:modifiedBy": "https://metadatacenter.org/users/524332"
   *  }
   * </pre>
   */
  private ObjectNode renderSchemaArtifact(SchemaArtifact schemaArtifact)
  {
    ObjectNode rendering = renderJsonLdArtifact(schemaArtifact);

    rendering.put(JSON_SCHEMA_SCHEMA, schemaArtifact.jsonSchemaSchemaUri().toString());
    rendering.put(JSON_SCHEMA_TYPE, schemaArtifact.jsonSchemaType());
    rendering.put(JSON_SCHEMA_TITLE, schemaArtifact.jsonSchemaTitle());
    rendering.put(JSON_SCHEMA_DESCRIPTION, schemaArtifact.jsonSchemaDescription());
    rendering.put(SCHEMA_ORG_NAME, schemaArtifact.name());
    rendering.put(SCHEMA_ORG_DESCRIPTION, schemaArtifact.description());

    rendering.put(SCHEMA_ORG_SCHEMA_VERSION, schemaArtifact.modelVersion().toString());

    if (schemaArtifact.identifier().isPresent()) {
      String identifier = schemaArtifact.identifier().get();
      if (!identifier.isEmpty())
        rendering.put(SCHEMA_ORG_IDENTIFIER, identifier);
    }

    if (schemaArtifact.version().isPresent())
      rendering.put(PAV_VERSION, schemaArtifact.version().get().toString());

    if (schemaArtifact.status().isPresent())
      rendering.put(BIBO_STATUS, schemaArtifact.status().get().toString());

    if (schemaArtifact.previousVersion().isPresent()) {
      String previousVersion = schemaArtifact.previousVersion().get().toString();
      if (!previousVersion.isEmpty())
        rendering.put(PAV_PREVIOUS_VERSION, previousVersion);
    }

    if (schemaArtifact.derivedFrom().isPresent()) {
      String derivedFrom = schemaArtifact.derivedFrom().get().toString();
      if (!derivedFrom.isEmpty())
        rendering.put(PAV_DERIVED_FROM, derivedFrom);
    }

    return rendering;
  }

  /**
   * Generate a base JSON-LD artifact rendering with core fields, which are @type, and @id
   * <p></p>
   * An example artifact could look as follows:
   * <pre>
   * {
   *   "@type": "https://schema.metadatacenter.org/core/Template",
   *   "@id": "https://repo.metadatacenter.org/templates/6d21a997-b884-4779-966a-aa71632f3232",
   *  }
   * </pre>
   */
  private ObjectNode renderJsonLdArtifact(JsonLdArtifact jsonLdArtifact)
  {
    ObjectNode rendering = renderMonitoredArtifact(jsonLdArtifact);

    if (jsonLdArtifact.jsonLdTypes().size() == 1) {
      rendering.put(JSON_LD_TYPE, jsonLdArtifact.jsonLdTypes().get(0).toString());
    } else if (jsonLdArtifact.jsonLdTypes().size() > 1) {
      rendering.put(JSON_LD_TYPE, mapper.createArrayNode());
      for (URI jsonLdType : jsonLdArtifact.jsonLdTypes())
        rendering.withArray(JSON_LD_TYPE).add(jsonLdType.toString());
    }

    if (jsonLdArtifact.jsonLdId().isPresent()) {
      rendering.put(JSON_LD_ID, jsonLdArtifact.jsonLdId().get().toString());
    } else {
      rendering.putNull(JSON_LD_ID);
    }

    return rendering;
  }

  /**
   * Generate a monitored artifact rendering, which will have fields pav:createdOn, pav:createdBy, pav:lastUpdatedOn,
   * and oslc:modifiedBy
   * <p></p>
   * An example could look as follows:
   * <pre>
   * {
   *   "pav:createdOn": "2023-07-28T11:10:41-07:00", "pav:createdBy": "https://metadatacenter.org/users/656433",
   *   "pav:lastUpdatedOn": "2023-07-28T11:10:41-07:00", "oslc:modifiedBy": "https://metadatacenter.org/users/524332"
   *  }
   * </pre>
   */
  private ObjectNode renderMonitoredArtifact(MonitoredArtifact monitoredArtifact)
  {
    ObjectNode rendering = mapper.createObjectNode();

    if (monitoredArtifact.createdBy().isPresent()) {
      rendering.put(PAV_CREATED_BY, monitoredArtifact.createdBy().get().toString());
    } else {
      rendering.putNull(PAV_CREATED_BY);
    }

    if (monitoredArtifact.modifiedBy().isPresent()) {
      rendering.put(OSLC_MODIFIED_BY, monitoredArtifact.modifiedBy().get().toString());
    } else {
      rendering.putNull(OSLC_MODIFIED_BY);
    }

    if (monitoredArtifact.createdOn().isPresent()) {
      rendering.put(PAV_CREATED_ON, renderOffsetDateTime(monitoredArtifact.createdOn().get()));
    } else {
      rendering.putNull(PAV_CREATED_ON);
    }

    if (monitoredArtifact.lastUpdatedOn().isPresent()) {
      rendering.put(PAV_LAST_UPDATED_ON, renderOffsetDateTime(monitoredArtifact.lastUpdatedOn().get()));
    } else {
      rendering.putNull(PAV_LAST_UPDATED_ON);
    }

    return rendering;
  }

  /**
   * Generate a JSON Schema specification for an additionalProperties field for a template or element containing
   * an attribute-value field
   * <p>
   * The specification is defined as follows:
   * <pre>
   * {
   *   "type": "object",
   *   "properties": {
   *     "@value": { "type": [ "string", "null" ] },
   *     "@type": { "type": "string", "format": "uri" }
   *   },
   *   "required": [ "@value" ],
   *   "additionalProperties": false
   * }
   * </pre>
   * The additional properties are of type object and contain two main properties:
   * - "@value": A string or null value.
   * - "@type": A string representing a URI.
   * <p>
   * The "@value" property is required, while the "@type" property is optional.
   * The "@value" property can hold a string value or be null, while the "@type" property must be a string in URI format.
   * <p>
   * Note that no other additional properties are allowed due to "additionalProperties" being set to false.
   */
  private ObjectNode renderAdditionalPropertiesForAttributeValueFieldJsonSchemaSpecification()
  {
    ObjectNode rendering = mapper.createObjectNode();

    rendering.put(JSON_SCHEMA_TYPE, JSON_SCHEMA_OBJECT);

    rendering.put(JSON_SCHEMA_PROPERTIES, mapper.createObjectNode());
    rendering.withObject("/" + JSON_SCHEMA_PROPERTIES).put(JSON_LD_VALUE, renderStringOrNullJsonSchemaTypeSpecification());
    rendering.withObject("/" + JSON_SCHEMA_PROPERTIES).put(JSON_LD_TYPE, renderUriJsonSchemaTypeSpecification());
    rendering.put(JSON_SCHEMA_REQUIRED, mapper.createArrayNode());
    rendering.withArray(JSON_SCHEMA_REQUIRED).add(JSON_LD_VALUE);
    rendering.put(JSON_SCHEMA_ADDITIONAL_PROPERTIES, false);

    return rendering;
  }

  /**
   * Render a JSON Schema properties specification in a template schema artifact. First, this specification defines
   * the top-level fields that should be present in a template instance.
   * <p></p>
   * Defined as follows:
   * <pre>
   *   {
   *     "@context": {
   *       "type": "object",
   *       "properties": {
   *         "schema": { "type": "string", "format": "uri", "enum": ["http://schema.org/"] },
   *         "oslc": { "type": "string", "format": "uri", "enum": ["http://open-services.net/ns/core#"] },
   *         "pav": { "type": "string", "format": "uri", "enum": ["http://purl.org/pav/"] },
   *         "rdfs": { "type": "string", "format": "uri", "enum": ["http://www.w3.org/2000/01/rdf-schema#"] },
   *         "xsd": { "type": "string", "format": "uri", "enum": ["http://www.w3.org/2001/XMLSchema#"] },
   *         "skos": { "type": "string", "format": "uri", "enum": ["http://www.w3.org/2004/02/skos/core#"] },
   *         "rdfs:label": { "type": "object", "properties": { "@type": { "type": "string", "enum": ["xsd:string"] }}},
   *         "schema:isBasedOn": { "type": "object", "properties": {"@type": {  "type": "string",  "enum": ["@id"] }}},
   *         "schema:name": { "type": "object", "properties": {"@type": { "type": "string",  "enum": ["xsd:string"] }}},
   *         "schema:description": { "type": "object", "properties": { "@type": {  "type": "string",  "enum": ["xsd:string"] }}},
   *         "pav:derivedFrom": { "type": "object", "properties": { "@type": {  "type": "string",  "enum": ["@id"] }}},
   *         "pav:createdOn": { "type": "object", "properties": { "@type": {  "type": "string",  "enum": ["xsd:dateTime"] }}},
   *         "pav:createdBy": { "type": "object", "properties": { "@type": {  "type": "string",  "enum": ["@id"] }}},
   *         "pav:lastUpdatedOn": { "type": "object", "properties": { "@type": {  "type": "string",  "enum": ["xsd:dateTime"] }}},
   *         "oslc:modifiedBy": { "type": "object", "properties": { "@type": {  "type": "string",  "enum": ["@id"] }} },
   *         "[Child Name 1]": { "enum": [ "[CHILD_PROPERTY_URI_1]"] },
   *         ...
   *         "[Child Name n]": { "enum": [ "[CHILD_PROPERTY_URI_n]"] }
   *       },
   *       "required": [ "xsd", "pav", "schema", "oslc", "schema:isBasedOn", "schema:name", "schema:description",
   *                     "pav:createdOn", "pav:createdBy", "pav:lastUpdatedOn", "oslc:modifiedBy",
   *                     "[Child Name 1]", ... "[Child Name n]" ],
   *       "additionalProperties": false
   *     },
   *     "@id": { "type": [ "string", "null" ], "format": "uri" },
   *     "@type": { "oneOf": [ { "type": "string","format": "uri" },
   *                           { "type": "array", "minItems": 1, 'items": { "type": "string","format": "uri" }, "uniqueItems": true } ]
   *              },
   *     "schema:isBasedOn": { "type": "string", "format": "uri" },
   *     "schema:name": { "type": "string","minLength": 1 },
   *     "schema:description": { "type": "string" },
   *     "pav:derivedFrom": { "type": "string", "format": "uri" },
   *     "pav:createdOn": { "type": [ "string", "null" ], "format": "date-time" },
   *     "pav:createdBy": { "type": [ "string", "null" ], "format": "uri" },
   *     "pav:lastUpdatedOn": {  "type": [ "string", "null" ], "format": "date-time" },
   *     "oslc:modifiedBy": { "type": [ "string", "null" ], "format": "uri" }
   *     "[Child Name 1]": { [Child JSON Schema 1] },
   *     ...
   *     "[Child Name n]": { [Child JSON Schema n] }
   *   }
   * </pre>
   * A conforming instance should look as follows:
   * <pre>
   * {
   *   "@context": {
   *     "schema": "https://schema.org/",
   *     "oslc": "http://open-services.net/ns/core#",
   *     "pav": "http://purl.org/pav/",
   *     "rdfs": "http://www.w3.org/2000/01/rdf-schema#",
   *     "xsd": "http://www.w3.org/2001/XMLSchema#",
   *     "skos": "http://www.w3.org/2004/02/skos/core#",
   *     "rdfs:label": { "@type": "xsd:string" },
   *     "schema:isBasedOn": { "@type": "@id" },
   *     "schema:name": { "@type": "xsd:string" },
   *     "schema:description": { "@type": "xsd:string" },
   *     "pav:derivedFrom": { "@type": "@id" },
   *     "pav:createdOn": {  "@type": "xsd:dateTime" },
   *     "pav:createdBy": { "@type": "@id" },
   *     "oslc:modifiedBy": { "@type": "@id" },
   *     "Child Name 1": "https://myschema.org/property/p1"
   *     ...
   *     "Child Name n": "https://myschema.org/property/pn"
   *   },
   *   "@type": "https://www.example.com/Study",
   *   "@id": "https://repo.metadatacenter.org/template-instances/474378",
   *   "schema:isBasedOn": "",
   *   "schema:name": "Study SDY232", "schema:description": "Metadata for SDY232 study",
   *   "pav:createdOn": "2023-07-28T11:10:41-07:00", "pav:createdBy": "https://metadatacenter.org/users/656433",
   *   "pav:lastUpdatedOn": "2023-07-28T11:10:41-07:00", "oslc:modifiedBy": "https://metadatacenter.org/users/524332",
   *   "Child Name 1": { [Child JSON-LD 1] },
   *   ...
   *   "Child Name n": { [Child JSON-LD n] }
   * }
   * </pre>
   */
  private ObjectNode renderTemplateSchemaArtifactPropertiesJsonSchemaSpecification(TemplateSchemaArtifact templateSchemaArtifact)
  {
    ObjectNode rendering = mapper.createObjectNode();

    rendering.put(JSON_LD_CONTEXT, renderTemplateSchemaArtifactContextPropertiesJsonSchemaSpecification(templateSchemaArtifact));
    rendering.put(JSON_LD_TYPE, renderUriOrUriArrayJsonSchemaSpecification(1, true));
    rendering.put(JSON_LD_ID, renderUriOrNullJsonSchemaTypeSpecification());
    rendering.put(PAV_DERIVED_FROM, renderUriJsonSchemaTypeSpecification());
    rendering.put(SCHEMA_IS_BASED_ON, renderUriJsonSchemaTypeSpecification());
    rendering.put(SCHEMA_ORG_NAME, renderStringJsonSchemaTypeSpecification(1));
    rendering.put(SCHEMA_ORG_DESCRIPTION, renderStringJsonSchemaTypeSpecification(0));
    rendering.put(PAV_CREATED_ON, renderDateTimeOrNullJsonSchemaTypeSpecification());
    rendering.put(PAV_CREATED_BY, renderUriOrNullJsonSchemaTypeSpecification());
    rendering.put(PAV_LAST_UPDATED_ON, renderDateTimeOrNullJsonSchemaTypeSpecification());
    rendering.put(OSLC_MODIFIED_BY, renderUriOrNullJsonSchemaTypeSpecification());

    return rendering;
  }

  /**
   * Render a JSON Schema properties specification in an element schema artifact. First, this specification defines
   * the top-level fields that should be present in an element instance.
   * <p></p>
   * Defined as follows:
   * <pre>
   *   {
   *     "@context": {
   *       "type": "object",
   *       "properties": {
   *         "rdfs:label": { "type": "object", "properties": { "@type": { "type": "string", "enum": ["xsd:string"] }}},
   *         "[Child Name 1]": { "enum": [ "[CHILD_PROPERTY_URI_1]"] },
   *         ...
   *         "[Child Name n]": { "enum": [ "[CHILD_PROPERTY_URI_n]"] }
   *       },
   *       "required": [ "[Child Name 1]", ... "[Child Name n]" ],
   *       "additionalProperties": false
   *     },
   *     "@id": { "type": "string", "format": "uri" },
   *     "@type": { "oneOf": [ { "type": "string","format": "uri" },
   *                           { "type": "array", "minItems": 1, 'items": { "type": "string","format": "uri" }, "uniqueItems": true } ]
   *              },
   *     "[Child Name 1]": { [Child JSON Schema 1] },
   *     ...
   *     "[Child Name n]": { [Child JSON Schema n] }
   *   }
   * </pre>
   * A conforming element instance should look as follows:
   * <pre>
   * {
   *   "@context": {
   *     "schema": "https://schema.org/",
   *     "oslc": "http://open-services.net/ns/core#",
   *     "pav": "http://purl.org/pav/",
   *     "rdfs": "http://www.w3.org/2000/01/rdf-schema#",
   *     "xsd": "http://www.w3.org/2001/XMLSchema#",
   *     "skos": "http://www.w3.org/2004/02/skos/core#",
   *     "rdfs:label": { "@type": "xsd:string" },
   *     "Child Name 1": "https://myschema.org/property/p1"
   *     ...
   *     "Child Name n": "https://myschema.org/property/pn"
   *   },
   *   "@type": "https://www.example.com/Study",
   *   "@id": "https://repo.metadatacenter.org/template-instances/474378",
   *   "Child Name 1": { [Child JSON-LD 1] },
   *   ...
   *   "Child Name n": { [Child JSON-LD n] }
   * }
   * </pre>
   */
  private ObjectNode renderElementSchemaArtifactPropertiesJsonSchemaSpecification(ElementSchemaArtifact elementSchemaArtifact)
  {
    ObjectNode rendering = mapper.createObjectNode();

    rendering.put(JSON_LD_CONTEXT, renderElementSchemaArtifactContextPropertiesJsonSchemaSpecification(elementSchemaArtifact));
    rendering.put(JSON_LD_TYPE, renderUriOrUriArrayJsonSchemaSpecification(1, true));
    rendering.put(JSON_LD_ID, renderUriJsonSchemaTypeSpecification());

    return rendering;
  }

  /**
   * Generate a JSON Schema specification for a @context properties specification in a template schema artifact
   * <p></p>
   * Defined as follows:
   * <pre>
   *   {
   *     "properties": {
   *         "type": "object",
   *         "rdfs": { "type": "string", "format": "uri", "enum": ["http://www.w3.org/2000/01/rdf-schema#"] },
   *         "xsd": { "type": "string", "format": "uri", "enum": ["http://www.w3.org/2001/XMLSchema#"] },
   *         "pav": { "type": "string", "format": "uri", "enum": ["http://purl.org/pav/"] },
   *         "schema": { "type": "string", "format": "uri", "enum": ["http://schema.org/"] },
   *         "oslc": { "type": "string", "format": "uri", "enum": ["http://open-services.net/ns/core#"] },
   *         "skos": { "type": "string", "format": "uri", "enum": ["http://www.w3.org/2004/02/skos/core#"] },
   *         "rdfs:label": { "type": "object", "properties": { "@type": { "type": "string", "enum": ["xsd:string"] }}},
   *         "schema:isBasedOn": { "type": "object", "properties": {"@type": {  "type": "string",  "enum": ["@id"] }}},
   *         "schema:name": { "type": "object", "properties": {"@type": { "type": "string",  "enum": ["xsd:string"] }}},
   *         "schema:description": { "type": "object", "properties": { "@type": {  "type": "string",  "enum": ["xsd:string"] }}},
   *         "pav:derivedFrom": { "type": "object", "properties": { "@type": {  "type": "string",  "enum": ["@id"] }}},
   *         "pav:createdOn": { "type": "object", "properties": { "@type": {  "type": "string",  "enum": ["xsd:dateTime"] }}},
   *         "pav:createdBy": { "type": "object", "properties": { "@type": {  "type": "string",  "enum": ["@id"] }}},
   *         "pav:lastUpdatedOn": { "type": "object", "properties": { "@type": {  "type": "string",  "enum": ["xsd:dateTime"] }}},
   *         "oslc:modifiedBy": { "type": "object", "properties": { "@type": {  "type": "string",  "enum": ["@id"] }} },
   *         "skos:notation": { "type": "object", "properties": {"@type": { "type": "string",  "enum": ["xsd:string"] }}},
   *         "<Child Name 1>": { "enum": [ "<PROPERTY URI 1>"] },
   *         ...
   *         "<Child Name n>": { "enum": [ "<PROPERTY URI n>"] }
   *     }
   *   }
   * </pre>
   * A conforming template instance should look as follows:
   * <pre>
   *   {
   *     "rdfs": "http://www.w3.org/2000/01/rdf-schema#",
   *     "xsd": "http://www.w3.org/2001/XMLSchema#",
   *     "pav": "http://purl.org/pav/",
   *     "schema": "https://schema.org/",
   *     "oslc": "http://open-services.net/ns/core#",
   *     "skos": "http://www.w3.org/2004/02/skos/core#",
   *     "rdfs:label": { "@type": "xsd:string" },
   *     "schema:isBasedOn": { "@type": "@id" },
   *     "schema:name": { "@type": "xsd:string" },
   *     "schema:description": { "@type": "xsd:string" },
   *     "pav:derivedFrom": { "@type": "@id" },
   *     "pav:createdOn": {  "@type": "xsd:dateTime" },
   *     "pav:createdBy": { "@type": "@id" },
   *     "oslc:modifiedBy": { "@type": "@id" },
   *     "skos:notation": { "@type": "xsd:string" },
   *     "Child Name 1": "https://myschema.org/property/p1"
   *     ...
   *     "Child Name n": "https://myschema.org/property/pn"
   *   }
   * </pre>
   */
  private ObjectNode renderTemplateSchemaArtifactContextPropertiesJsonSchemaSpecification(TemplateSchemaArtifact templateSchemaArtifact)
  {
    ObjectNode rendering = mapper.createObjectNode();

    rendering.put(JSON_SCHEMA_TYPE, JSON_SCHEMA_OBJECT);

    rendering.put(JSON_SCHEMA_PROPERTIES, mapper.createObjectNode());

    rendering.withObject("/" + JSON_SCHEMA_PROPERTIES).put(SCHEMA, renderJsonSchemaUriEnumSpecification(SCHEMA_IRI));
    rendering.withObject("/" + JSON_SCHEMA_PROPERTIES).put(OSLC, renderJsonSchemaUriEnumSpecification(OSLC_IRI));
    rendering.withObject("/" + JSON_SCHEMA_PROPERTIES).put(PAV, renderJsonSchemaUriEnumSpecification(PAV_IRI));

    rendering.withObject("/" + JSON_SCHEMA_PROPERTIES).put(RDFS, renderJsonSchemaUriEnumSpecification(RDFS_IRI));
    rendering.withObject("/" + JSON_SCHEMA_PROPERTIES).put(XSD, renderJsonSchemaUriEnumSpecification(XSD_IRI));
    rendering.withObject("/" + JSON_SCHEMA_PROPERTIES).put(SKOS, renderJsonSchemaUriEnumSpecification(SKOS_IRI));

    rendering.withObject("/" + JSON_SCHEMA_PROPERTIES).put(RDFS_LABEL, renderJsonSchemaJsonLdDatatypeSpecification("xsd:string"));
    rendering.withObject("/" + JSON_SCHEMA_PROPERTIES).put(SCHEMA_IS_BASED_ON, renderJsonSchemaJsonLdDatatypeSpecification(
      JSON_LD_ID));
    rendering.withObject("/" + JSON_SCHEMA_PROPERTIES).put(SCHEMA_ORG_NAME, renderJsonSchemaJsonLdDatatypeSpecification("xsd:string"));
    rendering.withObject("/" + JSON_SCHEMA_PROPERTIES).put(SCHEMA_ORG_DESCRIPTION, renderJsonSchemaJsonLdDatatypeSpecification("xsd:string"));
    rendering.withObject("/" + JSON_SCHEMA_PROPERTIES).put(PAV_DERIVED_FROM, renderJsonSchemaJsonLdDatatypeSpecification(
      JSON_LD_ID));
    rendering.withObject("/" + JSON_SCHEMA_PROPERTIES).put(PAV_CREATED_ON, renderJsonSchemaJsonLdDatatypeSpecification("xsd:dateTime"));
    rendering.withObject("/" + JSON_SCHEMA_PROPERTIES).put(PAV_CREATED_BY, renderJsonSchemaJsonLdDatatypeSpecification(
      JSON_LD_ID));
    rendering.withObject("/" + JSON_SCHEMA_PROPERTIES).put(PAV_LAST_UPDATED_ON, renderJsonSchemaJsonLdDatatypeSpecification("xsd:dateTime"));
    rendering.withObject("/" + JSON_SCHEMA_PROPERTIES).put(OSLC_MODIFIED_BY, renderJsonSchemaJsonLdDatatypeSpecification(
      JSON_LD_ID));
    rendering.withObject("/" + JSON_SCHEMA_PROPERTIES).put(SKOS_NOTATION, renderJsonSchemaJsonLdDatatypeSpecification("xsd:string"));

    for (var entry : templateSchemaArtifact.getChildPropertyUris().entrySet()) {
      String childName = entry.getKey();
      URI propertyUri = entry.getValue();
      rendering.withObject("/" + JSON_SCHEMA_PROPERTIES).put(childName, renderJsonSchemaEnumSpecification(propertyUri.toString()));
    }

    rendering.put(JSON_SCHEMA_REQUIRED, mapper.createArrayNode());
    rendering.withArray(JSON_SCHEMA_REQUIRED).add(XSD);
    rendering.withArray(JSON_SCHEMA_REQUIRED).add(PAV);
    rendering.withArray(JSON_SCHEMA_REQUIRED).add(SCHEMA);
    rendering.withArray(JSON_SCHEMA_REQUIRED).add(OSLC);
    rendering.withArray(JSON_SCHEMA_REQUIRED).add(SCHEMA_IS_BASED_ON);
    rendering.withArray(JSON_SCHEMA_REQUIRED).add(SCHEMA_ORG_NAME);
    rendering.withArray(JSON_SCHEMA_REQUIRED).add(SCHEMA_ORG_DESCRIPTION);
    rendering.withArray(JSON_SCHEMA_REQUIRED).add(PAV_CREATED_ON);
    rendering.withArray(JSON_SCHEMA_REQUIRED).add(PAV_CREATED_BY);
    rendering.withArray(JSON_SCHEMA_REQUIRED).add(PAV_LAST_UPDATED_ON);
    rendering.withArray(JSON_SCHEMA_REQUIRED).add(OSLC_MODIFIED_BY);

    for (String childName : templateSchemaArtifact.getChildPropertyUris().keySet())
      rendering.withArray(JSON_SCHEMA_REQUIRED).add(childName);

    rendering.put(JSON_SCHEMA_ADDITIONAL_PROPERTIES, false);

    return rendering;
  }

  /**
   * Generate a JSON Schema specification for a @context properties specification in an element schema artifact
   * <p></p>
   * Defined as follows:
   * <pre>
   *   {
   *     "properties": {
   *         "type": "object",
   *         "rdfs:label": { "type": "object", "properties": { "@type": { "type": "string", "enum": ["xsd:string"] }}},
   *         "skos:notation": { "type": "object", "properties": {"@type": { "type": "string",  "enum": ["xsd:string"] }}},
   *         "<Child Name 1>": { "enum": [ "<PROPERTY URI 1>"] },
   *         ...
   *         "<Child Name n>": { "enum": [ "<PROPERTY URI n>"] }
   *     }
   *   }
   * </pre>
   * A conforming template instance should look as follows:
   * <pre>
   *   {
   *     "skos:notation": { "@type": "xsd:string" },
   *     "Child Name 1": "https://myschema.org/property/p1"
   *     ...
   *     "Child Name n": "https://myschema.org/property/pn"
   *   }
   * </pre>
   */
  private ObjectNode renderElementSchemaArtifactContextPropertiesJsonSchemaSpecification(ElementSchemaArtifact elementSchemaArtifact)
  {
    ObjectNode rendering = mapper.createObjectNode();

    rendering.put(JSON_SCHEMA_TYPE, JSON_SCHEMA_OBJECT);

    rendering.put(JSON_SCHEMA_PROPERTIES, mapper.createObjectNode());

    rendering.withObject("/" + JSON_SCHEMA_PROPERTIES).put(SKOS_NOTATION, renderJsonSchemaEnumSpecification(XSD_STRING));

    for (var entry : elementSchemaArtifact.getChildPropertyUris().entrySet()) {
      String childName = entry.getKey();
      URI propertyUri = entry.getValue();
      rendering.withObject("/" + JSON_SCHEMA_PROPERTIES).put(childName, renderJsonSchemaEnumSpecification(propertyUri.toString()));
    }

    rendering.put(JSON_SCHEMA_REQUIRED, mapper.createArrayNode());

    for (String childName : elementSchemaArtifact.getChildPropertyUris().keySet())
      rendering.withArray(JSON_SCHEMA_REQUIRED).add(childName);

    rendering.put(JSON_SCHEMA_ADDITIONAL_PROPERTIES, false);

    return rendering;
  }

  /**
   * Generate a JSON Schema specification for a URI-formatted string value with a specific value.
   * <p></p>
   * Defined as follows:
   * <pre>
   * { "type": "string", "format": "uri", "enum": [ "<IRI>" ] }
   * </pre>
   * A conforming value could look as follows:
   * <pre>
   *   "http://purl.org/pav/"
   * </pre>
   */
  private ObjectNode renderJsonSchemaUriEnumSpecification(String uri)
  {
    ObjectNode rendering = mapper.createObjectNode();

    rendering.put(JSON_SCHEMA_TYPE, "string");
    rendering.put(ModelNodeNames.JSON_SCHEMA_FORMAT, ModelNodeNames.JSON_SCHEMA_FORMAT_URI);
    rendering.put(ModelNodeNames.JSON_SCHEMA_ENUM, mapper.createArrayNode());
    rendering.withArray(ModelNodeNames.JSON_SCHEMA_ENUM).add(uri);

    return rendering;
  }

  /**
   * Generate a JSON Schema specification for an enum with a specific value.
   * <p></p>
   * Defined as follows:
   * <pre>
   * { "enum": [ "<IRI>" ] }
   * </pre>
   * A conforming value could look as follows:
   * <pre>
   *   "http://purl.org/pav/"
   * </pre>
   */
  private ObjectNode renderJsonSchemaEnumSpecification(String value)
  {
    ObjectNode rendering = mapper.createObjectNode();

    rendering.put(ModelNodeNames.JSON_SCHEMA_ENUM, mapper.createArrayNode());
    rendering.withArray(ModelNodeNames.JSON_SCHEMA_ENUM).add(value);

    return rendering;
  }

  /**
   * Generate a JSON Schema specification for a JSON-LD @type datatype specification
   * <p></p>
   * Defined as follows:
   * <pre>
   * { "type": "object", "properties": { "@type": { "type": "string", "enum": ["<DATATYPE>"] }}},
   * </pre>
   * A conforming value could look as follows:
   * <pre>
   *   { "@type": "xsd:string" }
   * </pre>
   */
  private ObjectNode renderJsonSchemaJsonLdDatatypeSpecification(String datatype)
  {
    ObjectNode rendering = mapper.createObjectNode();

    rendering.put(JSON_SCHEMA_PROPERTIES, mapper.createObjectNode());
    rendering.withObject("/" + JSON_SCHEMA_PROPERTIES).put(JSON_LD_TYPE, mapper.createObjectNode());
    rendering.withObject("/" + JSON_SCHEMA_PROPERTIES)
      .withObject("/" + JSON_LD_TYPE).put(JSON_SCHEMA_TYPE, JSON_SCHEMA_STRING);
    rendering.withObject("/" + JSON_SCHEMA_PROPERTIES)
      .withObject("/" + JSON_LD_TYPE).put(ModelNodeNames.JSON_SCHEMA_ENUM, mapper.createArrayNode());
    rendering.withObject("/" + JSON_SCHEMA_PROPERTIES)
      .withObject( "/" + JSON_LD_TYPE).withArray(ModelNodeNames.JSON_SCHEMA_ENUM).add(datatype);

    return rendering;
  }

  /**
   * Generate a JSON-LD @context for parent schema artifacts (i.e., templates and elements)
   * <p>
   * Defined as follows:
   * <pre>
   *   {
   *     "schema": "http://schema.org/",
   *     "oslc": "http://open-services.net/ns/core#",
   *     "pav": "http://purl.org/pav/",
   *     "bibo": "http://purl.org/ontology/bibo/",
   *     "schema:name": { "@type": "xsd:string" },
   *     "schema:description": { "@type": "xsd:string" },
   *     "pav:createdOn": { "@type": "xsd:dateTime" },
   *     "pav:createdBy": { "@type": "@id" },
   *     "pav:lastUpdatedOn": { "@type": "xsd:dateTime" },
   *     "oslc:modifiedBy": { "@type": "@id" }
   *   }
   * </pre>
   */
  private ObjectNode renderParentSchemaArtifactContextJsonLdSpecification()
  {
    ObjectNode rendering = renderParentSchemaArtifactContextPrefixesJsonLdSpecification();

    rendering.put(SCHEMA_ORG_NAME, renderXsdStringJsonLdSpecification());
    rendering.put(SCHEMA_ORG_DESCRIPTION, renderXsdStringJsonLdSpecification());
    rendering.put(PAV_CREATED_ON, renderXsdDateTimeJsonLdSpecification());
    rendering.put(PAV_CREATED_BY, renderIriJsonLdSpecification());
    rendering.put(PAV_LAST_UPDATED_ON, renderXsdDateTimeJsonLdSpecification());
    rendering.put(OSLC_MODIFIED_BY, renderIriJsonLdSpecification());

    return rendering;
  }

  /**
   * Generate a JSON-LD @context for template instance artifacts
   * <p>
   * Defined as follows:
   * <pre>
   *   {
   *     "schema": "http://schema.org/",
   *     "oslc": "http://open-services.net/ns/core#",
   *     "pav": "http://purl.org/pav/",
   *     "rdfs": "http://www.w3.org/2000/01/rdf-schema#",
   *     "xsd": "http://www.w3.org/2001/XMLSchema#",
   *     "skos": "http://www.w3.org/2004/02/skos/core#",
   *     "oslc": "http://open-services.net/ns/core#",
   *     "schema:name": { "@type": "xsd:string" },
   *     "schema:description": { "@type": "xsd:string" },
   *     "pav:createdOn": { "@type": "xsd:dateTime" },
   *     "pav:createdBy": { "@type": "@id" },
   *     "pav:lastUpdatedOn": { "@type": "xsd:dateTime" },
   *     "oslc:modifiedBy": { "@type": "@id" },
   *     "skos:notation": { "@type": "xsd:string" }
   *     "pav:isDerivedFrom": { "@type": "@id" }
   *     "schema:isBasedOn": { "@type": "@id" }
   *   }
   * </pre>
   */
  private ObjectNode renderTemplateInstanceArtifactContextJsonLdSpecification()
  {
    ObjectNode rendering = mapper.createObjectNode();

    for (var entry: INSTANCE_ARTIFACT_CONTEXT_PREFIX_MAPPINGS.entrySet())
      rendering.put(entry.getKey(), entry.getValue().toString());

    rendering.put(SCHEMA_ORG_NAME, renderXsdStringJsonLdSpecification());
    rendering.put(SCHEMA_ORG_DESCRIPTION, renderXsdStringJsonLdSpecification());

    rendering.put(PAV_CREATED_ON, renderXsdDateTimeJsonLdSpecification());
    rendering.put(PAV_CREATED_BY, renderIriJsonLdSpecification());
    rendering.put(PAV_LAST_UPDATED_ON, renderXsdDateTimeJsonLdSpecification());
    rendering.put(OSLC_MODIFIED_BY, renderIriJsonLdSpecification());

    rendering.put(SKOS_NOTATION, renderXsdStringJsonLdSpecification());

    rendering.put(PAV_DERIVED_FROM, renderIriJsonLdSpecification());
    rendering.put(SCHEMA_IS_BASED_ON, renderIriJsonLdSpecification());

    return rendering;
  }

  /**
   * Generate a JSON-LD @context for a child instance artifacts
   * <p>
   * Defined as follows:
   * <pre>
   *   {
   *   }
   * </pre>
   */
  private ObjectNode renderInstanceArtifactContextJsonLdSpecification()
  {
    ObjectNode rendering = mapper.createObjectNode();

    return rendering;
  }

  /**
   * Generate JSON-LD @context prefix specification for parent schema artifacts
   * <p></p>
   * Defined as follows:
   * <pre>
   *   "@context": {
   *     "schema": "http://schema.org/",
   *     "pav": "http://purl.org/pav/",
   *     "oslc": "http://open-services.net/ns/core#",
   *     "bibo": "http://purl.org/ontology/bibo/",
   *     "xsd": "http://www.w3.org/2001/XMLSchema#"
   *   }
   * </pre>
   */
  private ObjectNode renderParentSchemaArtifactContextPrefixesJsonLdSpecification()
  {
    ObjectNode rendering = mapper.createObjectNode();

    for (var entry: PARENT_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS.entrySet())
      rendering.put(entry.getKey(), entry.getValue().toString());

    return rendering;
  }

  /**
   * Generate JSON-LD @context prefix specification for field schema artifacts
   * <p></p>
   * Defined as follows:
   * <pre>
   *   "@context": {
   *     "schema": "http://schema.org/",
   *     "pav": "http://purl.org/pav/",
   *     "oslc": "http://open-services.net/ns/core#",
   *     "bibo": "http://purl.org/ontology/bibo/",
   *     "xsd": "http://www.w3.org/2001/XMLSchema#",
   *     "skos": "http://www.w3.org/2004/02/skos/core#",
   *     "schema:name": { "@type": "xsd:string" },
   *     "schema:description": { "@type": "xsd:string" },
   *     "pav:createdOn": { "@type": "xsd:dateTime" },
   *     "pav:createdBy": { "@type": "@id" },
   *     "pav:lastUpdatedOn": { "@type": "xsd:dateTime" },
   *     "oslc:modifiedBy": { "@type": "@id" },
   *     "skos:prefLabel": { "@type": "xsd:string" },
   *     "skos:altLabel": { "@type": "xsd:string" }
   *   }
   * </pre>
   */
  private ObjectNode renderFieldSchemaArtifactContextPrefixesJsonLdSpecification()
  {
    ObjectNode rendering = mapper.createObjectNode();

    for (var entry: FIELD_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS.entrySet())
      rendering.put(entry.getKey(), entry.getValue().toString());

    rendering.put(SCHEMA_ORG_NAME, renderXsdStringJsonLdSpecification());
    rendering.put(SCHEMA_ORG_DESCRIPTION, renderXsdStringJsonLdSpecification());
    rendering.put(PAV_CREATED_ON, renderXsdDateTimeJsonLdSpecification());
    rendering.put(PAV_CREATED_BY, renderIriJsonLdSpecification());
    rendering.put(PAV_LAST_UPDATED_ON, renderXsdDateTimeJsonLdSpecification());
    rendering.put(OSLC_MODIFIED_BY, renderIriJsonLdSpecification());
    rendering.put(SKOS_PREFLABEL, renderXsdStringJsonLdSpecification());
    rendering.put(SKOS_ALTLABEL, renderXsdStringJsonLdSpecification());

    return rendering;
  }

  /**
   * Generate JSON-LD @context prefix specification for static field schema artifacts
   * <p></p>
   * Defined as follows:
   * <pre>
   *   "@context": {
   *     "schema": "http://schema.org/",
   *     "pav": "http://purl.org/pav/",
   *     "oslc": "http://open-services.net/ns/core#",
   *     "bibo": "http://purl.org/ontology/bibo/",
   *   }
   * </pre>
   */
  private ObjectNode renderStaticFieldSchemaArtifactContextPrefixesJsonLdSpecification()
  {
    ObjectNode rendering = mapper.createObjectNode();

    for (var entry: STATIC_FIELD_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS.entrySet())
      rendering.put(entry.getKey(), entry.getValue().toString());

    return rendering;
  }

  /**
   * Generate JSON-LD @context prefix specification for instance artifacts
   * <p></p>
   * Defined as follows:
   * <pre>
   *   "@context": {
   *     "schema": "http://schema.org/",
   *     "pav": "http://purl.org/pav/",
   *     "oslc": "http://open-services.net/ns/core#",
   *     "skos": "http://www.w3.org/2004/02/skos/core#",
   *     "rdfs": "http://www.w3.org/2000/01/rdf-schema#",
   *     "xsd": "http://www.w3.org/2001/XMLSchema#"
   *   }
   * </pre>
   */
  private ObjectNode renderInstanceArtifactContextPrefixesJsonLdSpecification()
  {
    ObjectNode rendering = mapper.createObjectNode();

    for (var entry: INSTANCE_ARTIFACT_CONTEXT_PREFIX_MAPPINGS.entrySet())
      rendering.put(entry.getKey(), entry.getValue().toString());

    return rendering;
  }

  /**
   * Generate a JSON Schema properties specification for a label-valued field
   * <p></p>
   * Defined as follows:
   * <pre>
   * {
   *   "@type": {
   *     "oneOf": [
   *       { "type": "string", "format": "uri" },
   *       { "type": "array", "minItems": 1, "items": { "type": "string", "format": "uri" },  "uniqueItems": true }
   *     ]
   *   },
   *   "rdfs:label": {  "type": [ "string", "null" ] },
   *   "@value": {  "type": [ "string", "null" ] }
   * }
   * </pre>
   * A conforming value could look like:
   * <pre>
   * {
   *   "@type": "http://www.w3.org/2001/XMLSchema#string",
   *   "@value": "Bob"
   * }
   * </pre>
   */
  private ObjectNode renderLiteralFieldArtifactPropertiesJsonSchemaSpecification()
  {
    ObjectNode rendering = mapper.createObjectNode();

    rendering.put(JSON_LD_TYPE, renderUriOrUriArrayJsonSchemaSpecification(1, true));
    rendering.put(RDFS_LABEL, renderStringOrNullJsonSchemaTypeSpecification());
    rendering.put(JSON_LD_VALUE, renderStringOrNullJsonSchemaTypeSpecification());

    return rendering;
  }


  /**
   * Generate a JSON Schema properties specification for a IRI-valued field
   * <p>
   * Defined as follows:
   * <pre>
   * {
   *   "@type": {
   *     "oneOf": [
   *       { "type": "string", "format": "uri" },
   *       { "type": "array", "minItems": 1, "items": { "type": "string", "format": "uri" },  "uniqueItems": true }
   *     ]
   *   },
   *   "rdfs:label": {  "type": [ "string", "null" ] },
   *   "@id": { "type": "string", "format": "uri" }
   * }
   * </pre>
   * A conforming value could look like:
   * <pre>
   * {
   *   "rsfs:label": "Melanoma",
   *   "@id": "http://purl.bioontology.org/ontology/LNC/LA14279-6"
   * }
   * </pre>
   */
  private ObjectNode renderIRIFieldArtifactPropertiesJsonSchemaSpecification()
  {
    ObjectNode rendering = mapper.createObjectNode();

    rendering.put(JSON_LD_TYPE, renderUriOrUriArrayJsonSchemaSpecification(1, true));
    rendering.put(RDFS_LABEL, renderStringOrNullJsonSchemaTypeSpecification());
    rendering.put(JSON_LD_ID, renderUriJsonSchemaTypeSpecification());

    return rendering;
  }

  /**
   * Generate a JSON Schema type specification for a string value
   * <p>
   * Defined as follows:
   * <pre>
   *   { "type": "string", "minLength": [minLength] }
   * </pre>
   */
  private ObjectNode renderStringJsonSchemaTypeSpecification(int minLength)
  {
    ObjectNode rendering = mapper.createObjectNode();

    rendering.put(JSON_SCHEMA_TYPE, JSON_SCHEMA_STRING);

    if (minLength > 0)
     rendering.put(JSON_SCHEMA_MIN_LENGTH, minLength);

    return rendering;
  }

  /**
   * Generate a JSON Schema type specification for a string or null value
   * <p>
   * Defined as follows:
   * <pre>
   *   { "type": [ "string", "null" ] }
   * </pre>
   */
  private ObjectNode renderStringOrNullJsonSchemaTypeSpecification()
  {
    ObjectNode rendering = mapper.createObjectNode();

    rendering.put(JSON_SCHEMA_TYPE, mapper.createArrayNode());
    rendering.withArray(JSON_SCHEMA_TYPE).add(JSON_SCHEMA_STRING);
    rendering.withArray(JSON_SCHEMA_TYPE).add(JSON_SCHEMA_NULL);

    return rendering;
  }

  /**
   * Generate a JSON Schema type specification for a URI-formatted string or null value
   * <p>
   * Defined as follows:
   * <pre>
   //   { "type": [ "string", "null" ], "format": "uri" },
   * </pre>
   */
  private ObjectNode renderUriOrNullJsonSchemaTypeSpecification()
  {
    ObjectNode rendering = mapper.createObjectNode();

    rendering.put(JSON_SCHEMA_TYPE, mapper.createArrayNode());
    rendering.withArray(JSON_SCHEMA_TYPE).add(JSON_SCHEMA_STRING);
    rendering.withArray(JSON_SCHEMA_TYPE).add(JSON_SCHEMA_NULL);

    rendering.put(JSON_SCHEMA_FORMAT, JSON_SCHEMA_FORMAT_URI);

    return rendering;
  }

  /**
   * Generate a JSON-LD @type specification for an xsd:string
   * <p>
   * Defined as follows:
   * <pre>
   *   { "@type": "xsd:string" }
   * </pre>
   */
  private ObjectNode renderXsdStringJsonLdSpecification()
  {
    ObjectNode rendering = mapper.createObjectNode();

    rendering.put(JSON_LD_TYPE, XSD_STRING);

    return rendering;
  }

  /**
   * Generate a JSON-LD @type specification for an xsd:dateTime
   * <p>
   * Defined as follows:
   * <pre>
   *   { "@type": "xsd:dateTime" }
   * </pre>
   */
  private ObjectNode renderXsdDateTimeJsonLdSpecification()
  {
    ObjectNode rendering = mapper.createObjectNode();

    rendering.put(JSON_LD_TYPE, XSD_DATETIME);

    return rendering;
  }

  /**
   * Generate a JSON-LD @type specification for an IRI
   * <p>
   * Defined as follows:
   * <pre>
   *   { "@type": "@id" }
   * </pre>
   */
  private ObjectNode renderIriJsonLdSpecification()
  {
    ObjectNode rendering = mapper.createObjectNode();

    rendering.put(JSON_LD_TYPE, JSON_LD_ID);

    return rendering;
  }

  /**
   * Generate a JSON Schema type specification for a date-time or null value
   * <p>
   * Defined as follows:
   * <pre>
   *  { "type": [ "string", "null" ], "format": "date-time" },
   * </pre>
   */
  private ObjectNode renderDateTimeOrNullJsonSchemaTypeSpecification()
  {
    ObjectNode rendering = mapper.createObjectNode();

    rendering.put(JSON_SCHEMA_TYPE, mapper.createArrayNode());
    rendering.withArray(JSON_SCHEMA_TYPE).add(JSON_SCHEMA_STRING);
    rendering.withArray(JSON_SCHEMA_TYPE).add(JSON_SCHEMA_NULL);
    rendering.put(JSON_SCHEMA_FORMAT, JSON_SCHEMA_FORMAT_DATE_TIME);

    return rendering;
  }


  /**
   * Generate a JSON Schema type specification for a URI-formatted string
   * <p>
   * Defined as follows:
   * <pre>
   *   { "type": "string", "format": "uri" }
   * </pre>
   */
  private ObjectNode renderUriJsonSchemaTypeSpecification()
  {
    ObjectNode rendering = mapper.createObjectNode();

    rendering.put(JSON_SCHEMA_TYPE, JSON_SCHEMA_STRING);
    rendering.put(ModelNodeNames.JSON_SCHEMA_FORMAT, JSON_SCHEMA_FORMAT_URI);

    return rendering;
  }

  /**
   * Generate a JSON Schema specification for URI-formatted string or URI-formatted string array
   * <p>
   * Defined as follows:
   * <pre>
   * {
   *   "oneOf": [
   *     { "type": "string", "format": "uri" },
   *     { "type": "array", "minItems": 1, "items": { "type": "string", "format": "uri" }, "uniqueItems": true }
   *   ]
   * }
   * </pre>
   */
  private ObjectNode renderUriOrUriArrayJsonSchemaSpecification(int minItems, boolean uniqueItems)
  {
    ObjectNode rendering = mapper.createObjectNode();

    rendering.put(JSON_SCHEMA_ONE_OF, mapper.createArrayNode());
    rendering.withArray(JSON_SCHEMA_ONE_OF).add(renderUriJsonSchemaTypeSpecification());
    rendering.withArray(JSON_SCHEMA_ONE_OF).add(renderUriArrayJsonSchemaTypeSpecification(minItems, uniqueItems));

    return rendering;
  }

  /**
   * Generate a JSON Schema type specification for URI-formatted string array
   * <p>
   * Defined as follows:
   * <pre>
   * { "type": "array", "minItems": [minItems], "items": { "type": "string", "format": "uri" }, "uniqueItems": [uniqueItems] }
   * </pre>
   */
  private ObjectNode renderUriArrayJsonSchemaTypeSpecification(int minItems, boolean uniqueItems)
  {
    ObjectNode rendering = mapper.createObjectNode();

    rendering.put(JSON_SCHEMA_TYPE, ModelNodeNames.JSON_SCHEMA_ARRAY);
    rendering.put(ModelNodeNames.JSON_SCHEMA_MIN_ITEMS, minItems);
    rendering.put(ModelNodeNames.JSON_SCHEMA_ITEMS, mapper.createObjectNode());
    rendering.withObject( "/" + ModelNodeNames.JSON_SCHEMA_ITEMS).put(JSON_SCHEMA_TYPE, JSON_SCHEMA_STRING);
    rendering.withObject( "/" + ModelNodeNames.JSON_SCHEMA_ITEMS).put(ModelNodeNames.JSON_SCHEMA_FORMAT, JSON_SCHEMA_FORMAT_URI);
    rendering.put(ModelNodeNames.JSON_SCHEMA_UNIQUE_ITEMS, uniqueItems);

    return rendering;
  }

  /**
   * Generate a JSON Schema array specification wrapping a supplied JSON Schema Specification
   * <p>
   * Defined as follows:
   * <pre>
   * { "type": "array", "minItems": [minItems], "maxItems": [maxItems], "items": wrappedObjectNode }
   * </pre>
   */
  private ObjectNode renderJsonSchemaArrayWrapperSpecification(ObjectNode wrappedObjectNode, Optional<Integer> minItems,
    Optional<Integer> maxItems)
  {
    ObjectNode wrapperObjectNode = mapper.createObjectNode();

    wrapperObjectNode.put(JSON_SCHEMA_TYPE, JSON_SCHEMA_ARRAY);

    if (minItems.isPresent())
      wrapperObjectNode.put(JSON_SCHEMA_MIN_ITEMS, minItems.get());
    else
      wrapperObjectNode.put(JSON_SCHEMA_MIN_ITEMS, 0);

    if (maxItems.isPresent())
      wrapperObjectNode.put(JSON_SCHEMA_MAX_ITEMS, maxItems.get());

    wrapperObjectNode.put(JSON_SCHEMA_ITEMS, wrappedObjectNode);

    return wrapperObjectNode;
  }

  private String renderOffsetDateTime(OffsetDateTime offsetDateTime)
  {
    return offsetDateTime.format(datetimeFormatter);
  }
}
