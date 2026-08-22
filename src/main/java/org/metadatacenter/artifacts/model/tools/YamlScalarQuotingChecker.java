package org.metadatacenter.artifacts.model.tools;

import com.fasterxml.jackson.dataformat.yaml.util.StringQuotingChecker;

import java.util.regex.Pattern;

/**
 * Decides, per string, whether the YAML writer has to quote it. Jackson's default checker asks what
 * YAML 1.1 would resolve — which is what SnakeYAML reads — and leaves the rest plain. That is not
 * enough for two kinds of string.
 *
 * <p>A spelling some reader resolves to a number, a boolean or null. Jackson quotes the 1.1 spellings
 * and its own numeric-looking check covers plain decimals, but a signed hexadecimal, a binary
 * integer, a digit-separated number, an exponent form, an infinity and a sexagesimal all went out
 * bare, and a reader on the YAML 1.2 core schema gives back a number where a string went in.
 *
 * <p>A string carrying a character a plain scalar cannot hold back intact: a tab or carriage return,
 * a C0 or C1 control, DEL, a no-break or otherwise exotic Unicode space, a line or paragraph
 * separator. SnakeYAML writes those raw into a plain scalar and then refuses to read the document,
 * or folds the character into a space. Quoted, they are escaped and survive. Whitespace a reader
 * strips — at either end of a scalar, or before a line break — is quoted for the same reason.
 *
 * <p>Neither rule loosens anything: a string the default checker quotes is still quoted.
 */
public final class YamlScalarQuotingChecker extends StringQuotingChecker.Default
{
  /** Spellings a reader resolves to something other than a string, beyond what the default catches. */
  private static final Pattern[] RESOLVED_BY_SOME_READER = {
    Pattern.compile("[-+]?0[xX][0-9a-fA-F_]+"),                       // hexadecimal, signed or not
    Pattern.compile("[-+]?0[bB][01_]+"),                              // binary
    Pattern.compile("[-+]?0[oO]?[0-7_]+"),                            // octal, both spellings
    Pattern.compile("[-+]?[0-9][0-9_]*"),                             // digit separators
    Pattern.compile("[-+]?[0-9_]*\\.?[0-9_]+[eE][-+]?[0-9]+"),        // exponent notation
    Pattern.compile("[-+]?\\.(inf|Inf|INF|nan|NaN|NAN)"),             // infinities and not-a-number
    Pattern.compile("[-+]?[0-9][0-9_]*(:[0-5]?[0-9])+(\\.[0-9_]*)?"), // sexagesimal
    Pattern.compile("~|<<|=")                                         // null, the merge key, the value key
  };

  @Override public boolean needToQuoteValue(String value)
  {
    return super.needToQuoteValue(value) || resolvedBySomeReader(value) || plainCannotCarry(value);
  }

  @Override public boolean needToQuoteName(String name)
  {
    return super.needToQuoteName(name) || resolvedBySomeReader(name) || plainCannotCarry(name);
  }

  private static boolean resolvedBySomeReader(String text)
  {
    for (Pattern pattern : RESOLVED_BY_SOME_READER) {
      if (pattern.matcher(text).matches())
        return true;
    }
    return false;
  }

  private static boolean plainCannotCarry(String text)
  {
    for (int i = 0; i < text.length(); i++) {
      char c = text.charAt(i);
      boolean control = c == '\t' || c == '\r' || (c < 0x20 && c != '\n') || (c >= 0x7f && c <= 0xa0);
      boolean exoticSpace = c == 0x1680 || (c >= 0x2000 && c <= 0x200a) || c == 0x2028 || c == 0x2029
        || c == 0x202f || c == 0x205f || c == 0x3000 || c == 0xfeff;
      if (control || exoticSpace)
        return true;
    }
    return text.startsWith(" ") || text.endsWith(" ") || text.contains(" \n");
  }
}
