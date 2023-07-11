package org.metadatacenter.artifacts.model.core;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ValidationHelper
{
  public static void validateStringFieldNotNull(Object obj, String field, String fieldName)
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

  public static void validateURIFieldNotNull(Object obj, URI field, String fieldName)
  {
    if (field == null)
      throw new IllegalStateException("URI field " + fieldName + " is null in " + obj);
  }

  public static void validateURIFieldEquals(Object obj, URI field, String fieldName, String fieldValue)
  {
    validateURIFieldNotNull(obj, field, fieldName);

    if (!field.toString().equals(fieldValue))
      throw new IllegalStateException("URI field " + fieldName + " must equal " + fieldValue + " in " + obj);
  }

  public static void validateVersionFieldNotNull(Object obj, Version field, String fieldName)
  {
    if (field == null)
      throw new IllegalStateException("Version field " + fieldName + " is null in " + obj);
  }

  public static <T> void validateOptionalFieldNotNull(Object obj, Optional<T> field, String fieldName)
  {
    if (field == null)
      throw new IllegalStateException("Optional field " + fieldName + " is null in " + obj);
  }

  public static <T> void validateOptionalFieldNotEmpty(Object obj, Optional<T> field, String fieldName)
  {
    validateOptionalFieldNotNull(obj, field, fieldName);

    if (field.isEmpty())
      throw new IllegalStateException("Required Optional field " + fieldName + " is empty in " + obj);
  }

  public static void validateUIFieldNotNull(Object obj, UI field, String fieldName)
  {
    if (field == null)
      throw new IllegalStateException("UI field " + fieldName + " is null in " + obj);
  }

  public static <K, V> void validateMapFieldNotNull(Object obj, Map<K, V> field, String fieldName)
  {
    if (field == null)
      throw new IllegalStateException("Map field " + fieldName + " is null in " + obj);
  }

  public static <T> void validateListFieldNotNull(Object obj, List<T> field, String fieldName)
  {
    if (field == null)
      throw new IllegalStateException("List field " + fieldName + " is null in " + obj);
  }
}
