package org.metadatacenter.artifacts.model.core;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record Version(int major, int minor, int patch)
{
  private static final String VERSION_REGEX = "(\\d+)\\.(\\d+)\\.(\\d+)";

  public Version
  {
    if (major < 0)
      throw new IllegalArgumentException("major must be 0 or greater");

    if (minor < 0)
      throw new IllegalArgumentException("minor must be 0 or greater");

    if (patch < 0)
      throw new IllegalArgumentException("patch must be 0 or greater");
  }

  public static Version fromString(String versionText)
  {
    Matcher m = Pattern.compile(VERSION_REGEX).matcher(versionText);

    if (!m.matches())
      throw new IllegalArgumentException("Invalid version string " + versionText);

    int major = Integer.parseInt(m.group(1));
    int minor = Integer.parseInt(m.group(2));
    int patch = Integer.parseInt(m.group(3));

    return new Version(major, minor, patch);
  }

  public static boolean isValidVersion(String versionText)
  {
    Matcher m = Pattern.compile(VERSION_REGEX).matcher(versionText);

    return m.matches();
  }

  @Override public String toString()
  {
    return major + "." + minor + "." + patch;
  }
}
