package org.metadatacenter.artifacts.model.core;

import org.metadatacenter.artifacts.model.core.fields.XsdNumericDatatype;
import org.metadatacenter.artifacts.model.core.fields.XsdTemporalDatatype;
import org.metadatacenter.artifacts.model.core.ui.Ui;

import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class ValidationHelper
{
  public static void validateStringFieldNotNull(Object obj, String field, String fieldKey)
  {
    if (field == null)
      throw new IllegalStateException("field " + fieldKey + " is null in " + obj);
  }

  public static void validateIntegerFieldNotNull(Object obj, Integer field, String fieldKey)
  {
    if (field == null)
      throw new IllegalStateException("field " + fieldKey + " is null in " + obj);
  }

  public static void validateNumberFieldNotNull(Object obj, Number field, String fieldKey)
  {
    if (field == null)
      throw new IllegalStateException("field " + fieldKey + " is null in " + obj);
  }

  public static void validateStringFieldNotEmpty(Object obj, String field, String fieldKey)
  {
    validateStringFieldNotNull(obj, field, fieldKey);

    if (field.isEmpty())
      throw new IllegalStateException("field " + fieldKey + " is empty in " + obj);
  }

  public static void validateStringFieldEquals(Object obj, String field, String fieldKey, String fieldValue)
  {
    validateStringFieldNotNull(obj, field, fieldKey);

    if (!field.equals(fieldValue))
      throw new IllegalStateException("field " + fieldKey + " must equal " + fieldValue + " in " + obj);
  }

  public static void validateUriFieldNotNull(Object obj, URI field, String fieldKey)
  {
    if (field == null)
      throw new IllegalStateException("URI field " + fieldKey + " is null in " + obj);
  }

  public static void validateUriFieldEquals(Object obj, URI field, String fieldKey, URI value)
  {
    validateUriFieldNotNull(obj, field, fieldKey);

    if (!field.equals(value))
      throw new IllegalStateException("URI field " + fieldKey + " must equal " + value + " in " + obj);
  }

  public static void validateUriListFieldContains(Object obj, List<URI> uriListField, String fieldKey, URI value)
  {
    validateListFieldNotNull(obj, uriListField, fieldKey);

    if (!uriListField.contains(value))
      throw new IllegalStateException("URI list field " + fieldKey + " must contain " + value + " in " + obj);
  }

  public static void validateUriListFieldContainsOneOf(Object obj, List<URI> uriListField, String fieldKey, Set<URI> values)
  {
    validateListFieldNotNull(obj, uriListField, fieldKey);

    if (uriListField.stream().noneMatch(values::contains))
      throw new IllegalStateException("URI list field " + fieldKey + " must contain at least one of " + values +
        " in " + obj + "; missing: " + uriListField.stream().filter(e -> !values.contains(e)).toList());
  }

  public static void validateUriListFieldContainsAllOf(Object obj, List<URI> uriListField, String fieldKey, Set<URI> values)
  {
    validateListFieldNotNull(obj, uriListField, fieldKey);

    if (!uriListField.containsAll(values))
      throw new IllegalStateException("URI list field " + fieldKey + " must contain all values " + values + " in " +
        obj + "; missing: " + uriListField.stream().filter(e -> !values.contains(e)).toList());
  }

  public static <K, V> void validateMapFieldContainsAll(Object obj, Map<K, V> field, String fieldKey, Map<K, V> values)
  {
    validateMapFieldNotNull(obj, field, fieldKey);

    if (!field.entrySet().containsAll(values.entrySet()))
      throw new IllegalStateException("Map field " + fieldKey + " must contain all entries " + values + " in " +
        obj + "; missing: " + values.entrySet().stream().filter(e -> !field.entrySet().contains(e)).toList());
  }

  public static void validateXsdTemporalDatatypeFieldNotNull(Object obj, XsdTemporalDatatype field, String fieldKey)
  {
    if (field == null)
      throw new IllegalStateException("Temporal type field " + fieldKey + " is null in " + obj);
  }

  public static void validateXsdNumericDatatypeFieldNotNull(Object obj, XsdNumericDatatype field, String fieldKey)
  {
    if (field == null)
      throw new IllegalStateException("Numeric type field " + fieldKey + " is null in " + obj);
  }

  public static void validateVersionFieldNotNull(Object obj, Version field, String fieldKey)
  {
    if (field == null)
      throw new IllegalStateException("Version field " + fieldKey + " is null in " + obj);
  }

  public static <T> void validateOptionalFieldNotNull(Object obj, Optional<T> field, String fieldKey)
  {
    if (field == null)
      throw new IllegalStateException("Optional field " + fieldKey + " is null in " + obj);
  }

  public static <T> void validateOptionalFieldNotEmpty(Object obj, Optional<T> field, String fieldKey)
  {
    validateOptionalFieldNotNull(obj, field, fieldKey);

    if (field.isEmpty())
      throw new IllegalStateException("Required Optional field " + fieldKey + " is empty in " + obj);
  }

  public static void validateUiFieldNotNull(Object obj, Ui field, String fieldKey)
  {
    if (field == null)
      throw new IllegalStateException("UI field " + fieldKey + " is null in " + obj);
  }

  public static <K, V> void validateMapFieldNotNull(Object obj, Map<K, V> field, String fieldKey)
  {
    if (field == null)
      throw new IllegalStateException("Map field " + fieldKey + " is null in " + obj);

    if (field.values().stream().anyMatch(e -> e == null))
      throw new IllegalStateException("Map field " + fieldKey + " contains null values in " + obj);
  }

  public static <T> void validateListFieldNotNull(Object obj, List<T> field, String fieldKey)
  {
    if (field == null)
      throw new IllegalStateException("List field " + fieldKey + " is null in " + obj);

    if (field.stream().anyMatch(e -> e == null))
      throw new IllegalStateException("List field " + fieldKey + " contains null entries in " + obj);
  }

  public static <T> void validateSetFieldNotNull(Object obj, Set<T> field, String fieldKey)
  {
    if (field == null)
      throw new IllegalStateException("Set field " + fieldKey + " is null in " + obj);

    if (field.stream().anyMatch(e -> e == null))
      throw new IllegalStateException("Set field " + fieldKey + " contains null entries in " + obj);
  }

  public static <T> void validateListFieldDoesNotHaveDuplicates(Object obj, List<T> field, String fieldKey)
  {
    validateListFieldNotNull(obj, field, fieldKey);

    if (hasDuplicates(field))
      throw new IllegalStateException("List field " + fieldKey + " has duplicates in " + obj);
  }

  private static <T> boolean hasDuplicates(List<T> list)
  {
    Set<T> elementsSoFar = new HashSet<>();

    for (T element : list) {
      if (elementsSoFar.contains(element))
        return true;
      else
        elementsSoFar.add(element);
    }
    return false;
  }
}
