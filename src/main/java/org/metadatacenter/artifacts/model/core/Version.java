package org.metadatacenter.artifacts.model.core;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record Version(int major, int minor, int patch)
{
  private static final String VERSION_REGEX = "(0|[1-9]\\d*)\\.(0|[1-9]\\d*)\\.(0|[1-9]\\d*)";

  /** The default version assigned to a freshly created artifact: {@code 0.0.1}. */
  public static final Version DEFAULT = new Version(0, 0, 1);

  public Version
  {
    if (major < 0)
      throw new IllegalArgumentException("major must be 0 or greater");

    if (minor < 0)
      throw new IllegalArgumentException("minor must be 0 or greater");

    if (patch < 0)
      throw new IllegalArgumentException("patch must be 0 or greater");

    if (major == 0 && minor == 0 && patch == 0)
      throw new IllegalArgumentException("0.0.0 is not a version");
  }

  public static Version fromString(String versionText)
  {
    if (!isValidVersion(versionText))
      throw new IllegalArgumentException("Invalid version string " + versionText);

    Matcher m = Pattern.compile(VERSION_REGEX).matcher(versionText);
    m.matches();

    int major = Integer.parseInt(m.group(1));
    int minor = Integer.parseInt(m.group(2));
    int patch = Integer.parseInt(m.group(3));

    return new Version(major, minor, patch);
  }

  /**
   * Whether the text is three numbers without leading zeros, each of which fits a part of this
   * record, and not {@code 0.0.0}. A part too large for an int used to pass and then throw from
   * {@link Integer#parseInt}, outside the readers' contract of refusing an artifact with an
   * {@code ArtifactParseException}. A leading zero used to be read away, so a reader wrote back a
   * different version from the one stored.
   */
  public static boolean isValidVersion(String versionText)
  {
    Matcher m = Pattern.compile(VERSION_REGEX).matcher(versionText);

    if (!m.matches())
      return false;
    for (int group = 1; group <= 3; group++) {
      String part = m.group(group);
      if (part.length() > 10 || Long.parseLong(part) > Integer.MAX_VALUE)
        return false;
    }
    return !versionText.equals("0.0.0");
  }

  @Override public String toString()
  {
    return major + "." + minor + "." + patch;
  }
}
