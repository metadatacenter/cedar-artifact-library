package org.metadatacenter.artifacts.model.reader;

public class ArtifactParseException extends RuntimeException
{
  private final String parseErrorMessage;
  private final String fieldKey;
  private final String path;

  public ArtifactParseException(String parseErrorMessage, String fieldKey, String path)
  {
    super(parseErrorMessage + " for field " + fieldKey + " at " + path);
    this.parseErrorMessage = parseErrorMessage;
    this.fieldKey = fieldKey;
    this.path = path;
  }

  public String getParseErrorMessage()
  {
    return parseErrorMessage;
  }

  public String getFieldKey()
  {
    return fieldKey;
  }

  public String getPath()
  {
    return path;
  }
}
