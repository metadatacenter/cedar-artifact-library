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
  public static void validateStringFieldNotNull(Object obj, String field, String fieldName)
  {
    if (field == null)
      throw new IllegalStateException("field " + fieldName + " is null in " + obj);
  }

  public static void validateIntegerFieldNotNull(Object obj, Integer field, String fieldName)
  {
    if (field == null)
      throw new IllegalStateException("field " + fieldName + " is null in " + obj);
  }

  public static void validateStringFieldNotEmpty(Object obj, String field, String fieldName)
  {
    validateStringFieldNotNull(obj, field, fieldName);

    if (field.equals(""))
      throw new IllegalStateException("field " + fieldName + " is empty in " + obj);
  }

  public static void validateStringFieldEquals(Object obj, String field, String fieldName, String fieldValue)
  {
    validateStringFieldNotNull(obj, field, fieldName);

    if (!field.equals(fieldValue))
      throw new IllegalStateException("field " + fieldName + " must equal " + fieldValue + " in " + obj);
  }

  public static void validateUriFieldNotNull(Object obj, URI field, String fieldName)
  {
    if (field == null)
      throw new IllegalStateException("URI field " + fieldName + " is null in " + obj);
  }

  public static void validateUriFieldEquals(Object obj, URI field, String fieldName, URI value)
  {
    validateUriFieldNotNull(obj, field, fieldName);

    if (!field.equals(value))
      throw new IllegalStateException("URI field " + fieldName + " must equal " + value + " in " + obj);
  }

  public static void validateUriListFieldContains(Object obj, List<URI> uriListField, String fieldName, URI value)
  {
    validateListFieldNotNull(obj, uriListField, fieldName);

    if (!uriListField.contains(value))
      throw new IllegalStateException("URI list field " + fieldName + " must contain " + value + " in " + obj);
  }

  public static void validateUriListFieldContainsOneOf(Object obj, List<URI> uriListField, String fieldName, Set<URI> values)
  {
    validateListFieldNotNull(obj, uriListField, fieldName);

    if (!uriListField.stream().anyMatch(values::contains))
      throw new IllegalStateException("URI list field " + fieldName + " must contain at least one of " + values + " in " + obj);
  }

  public static void validateUriListFieldContainsAllOf(Object obj, List<URI> uriListField, String fieldName, Set<URI> values)
  {
    validateListFieldNotNull(obj, uriListField, fieldName);

    if (!uriListField.containsAll(values))
      throw new IllegalStateException("URI list field " + fieldName + " must contain all values " + values + " in " + obj);
  }

  public static <K, V> void validateMapFieldContainsAll(Object obj, Map<K, V> field, String fieldName, Map<K, V> values)
  {
    validateMapFieldNotNull(obj, field, fieldName);

    if (!field.entrySet().containsAll(values.entrySet()))
      throw new IllegalStateException("Map field " + fieldName + " must contain all entries " + values + " in " + obj);
  }

  public static void validateXsdTemporalDatatypeFieldNotNull(Object obj, XsdTemporalDatatype field, String fieldName)
  {
    if (field == null)
      throw new IllegalStateException("Temporal type field " + fieldName + " is null in " + obj);
  }

  public static void validateXsdNumericDatatypeFieldNotNull(Object obj, XsdNumericDatatype field, String fieldName)
  {
    if (field == null)
      throw new IllegalStateException("Numeric type field " + fieldName + " is null in " + obj);
  }

  public static void validateVersionFieldNotNull(Object obj, Version field, String fieldName)
  {
    if (field == null)
      throw new IllegalStateException("Version field " + fieldName + " is null in " + obj);
  }

  public static <T> void validateOptionalFieldNotNull(Object obj, Optional<T> field, String fieldName)
  {
    if (field == null || (field.isPresent() && field.get() == null))
      throw new IllegalStateException("Optional field " + fieldName + " is null in " + obj);
  }

  public static <T> void validateOptionalFieldNotEmpty(Object obj, Optional<T> field, String fieldName)
  {
    validateOptionalFieldNotNull(obj, field, fieldName);

    if (field.isEmpty())
      throw new IllegalStateException("Required Optional field " + fieldName + " is empty in " + obj);
  }

  public static void validateUiFieldNotNull(Object obj, Ui field, String fieldName)
  {
    if (field == null)
      throw new IllegalStateException("UI field " + fieldName + " is null in " + obj);
  }

  public static <K, V> void validateMapFieldNotNull(Object obj, Map<K, V> field, String fieldName)
  {
    if (field == null)
      throw new IllegalStateException("Map field " + fieldName + " is null in " + obj);

    if (field.values().stream().anyMatch(e -> e == null))
      throw new IllegalStateException("Map field " + fieldName + " contains null values in " + obj);
  }

  public static <T> void validateListFieldNotNull(Object obj, List<T> field, String fieldName)
  {
    if (field == null)
      throw new IllegalStateException("List field " + fieldName + " is null in " + obj);

    if (field.stream().anyMatch(e -> e == null))
      throw new IllegalStateException("List field " + fieldName + " contains null entries in " + obj);
  }

  public static <T> void validateSetFieldNotNull(Object obj, Set<T> field, String fieldName)
  {
    if (field == null)
      throw new IllegalStateException("Set field " + fieldName + " is null in " + obj);

    if (field.stream().anyMatch(e -> e == null))
      throw new IllegalStateException("Set field " + fieldName + " contains null entries in " + obj);
  }

  public static <T> void validateListFieldDoesNotHaveDuplicates(Object obj, List<T> field, String fieldName)
  {
    validateListFieldNotNull(obj, field, fieldName);

    if (hasDuplicates(field))
      throw new IllegalStateException("List field " + fieldName + " has duplicates in " + obj);
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
