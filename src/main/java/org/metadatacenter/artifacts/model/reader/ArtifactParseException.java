package org.metadatacenter.artifacts.model.reader;

public class ArtifactParseException extends RuntimeException
{
  private final String parseErrorMessage;
  private final String fieldName;
  private final String path;

  public ArtifactParseException(String parseErrorMessage, String fieldName, String path)
  {
    super(parseErrorMessage + " for field " + fieldName + " at " + path);
    this.parseErrorMessage = parseErrorMessage;
    this.fieldName = fieldName;
    this.path = path;
  }

  public String getParseErrorMessage()
  {
    return parseErrorMessage;
  }

  public String getFieldName()
  {
    return fieldName;
  }

  public String getPath()
  {
    return path;
  }
}
