package org.metadatacenter.artifacts.model.core;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Version
{
  private final int major;
  private final int minor;
  private final int patch;

  public Version(int major, int minor, int patch)
  {
    if (major < 0)
      throw new IllegalArgumentException("major must be 0 or greater");

    if (minor < 0)
      throw new IllegalArgumentException("minor must be 0 or greater");

    if (patch < 0)
      throw new IllegalArgumentException("patch must be 0 or greater");

    this.major = major;
    this.minor = minor;
    this.patch = patch;
  }

  public int getMinor()
  {
    return minor;
  }

  public int getPatch()
  {
    return patch;
  }

  public int getMajor()
  {
    return major;
  }

  public static Version fromString(String versionText) {

    Matcher m = Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+)").matcher(versionText);

    if (!m.matches())
      throw new IllegalArgumentException("Invalid version string " + versionText);

    int major = Integer.parseInt(m.group(1));
    int minor = Integer.parseInt(m.group(2));
    int patch = Integer.parseInt(m.group(3));

    return new Version(major, minor, patch);
  }

  @Override public String toString()
  {
    return major + "." +  minor + "." + patch;
  }

  @Override public boolean equals(Object o)
  {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    Version version = (Version)o;
    return major == version.major && minor == version.minor && patch == version.patch;
  }

  @Override public int hashCode()
  {
    return Objects.hash(major, minor, patch);
  }
}
