
package org.javacc.java;

import org.javacc.parser.JavaCCGlobals;
import org.javacc.parser.JavaCCParserConstants;
import org.javacc.parser.Options;
import org.javacc.parser.Token;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

abstract class JavaUtil {

  private static final Pattern PACKAGE_PATTERN = Pattern.compile("package[^a-z]+([^;]+)", Pattern.CASE_INSENSITIVE);

  private JavaUtil() {}


  /**
   * Parses the package from the insertion points.
   *
   * @return
   */
  public static String parsePackage() {
    Token t = null;
    StringWriter writer = new StringWriter();
    try (PrintWriter printer = new PrintWriter(writer)) {
      if (JavaCCGlobals.cu_to_insertion_point_1.size() != 0
          && JavaCCGlobals.cu_to_insertion_point_1.get(0).kind == JavaCCParserConstants.PACKAGE) {
        for (int i = 1; i < JavaCCGlobals.cu_to_insertion_point_1.size(); i++) {
          if (JavaCCGlobals.cu_to_insertion_point_1.get(i).kind == JavaCCParserConstants.SEMICOLON) {
            JavaUtil.printTokenSetup(JavaCCGlobals.cu_to_insertion_point_1.get(0));
            for (int j = 0; j <= i; j++) {
              t = JavaCCGlobals.cu_to_insertion_point_1.get(j);
              JavaUtil.printToken(t, printer, true);
            }
            JavaUtil.printTrailingComments(t, printer, true);
            printer.println("");
            printer.println("");
            break;
          }
        }
      }
    }

    String text = writer.toString();
    if (text == null)
      return "";

    Matcher matcher = PACKAGE_PATTERN.matcher(text);
    return matcher.find() ? matcher.group(1) : "";
  }


  public static String getStatic() {
    return (Options.getStatic() ? "static " : "");
  }

  public static String getBooleanType() {
    return "boolean";
  }

  private static void printTokenSetup(Token t) {
    Token tt = t;
    while (tt.specialToken != null)
      tt = tt.specialToken;
    JavaCCGlobals.cline = tt.beginLine;
    JavaCCGlobals.ccol = tt.beginColumn;
  }

  private static void printToken(Token t, java.io.PrintWriter ostr, boolean escape) {
    Token tt = t.specialToken;
    if (tt != null) {
      while (tt.specialToken != null)
        tt = tt.specialToken;
      while (tt != null) {
        ostr.append(JavaCCGlobals.printTokenOnly(tt, escape));
        tt = tt.next;
      }
    }
    ostr.append(JavaCCGlobals.printTokenOnly(t, escape));
  }

  public static void printTrailingComments(Token t, java.io.PrintWriter ostr, boolean escape) {
    if (t.next == null)
      return;

    printLeadingComments(t.next, escape);
  }


  private static String printLeadingComments(Token t, boolean escape) {
    String retval = "";
    if (t.specialToken == null)
      return retval;
    Token tt = t.specialToken;
    while (tt.specialToken != null)
      tt = tt.specialToken;
    while (tt != null) {
      retval += JavaCCGlobals.printTokenOnly(tt, escape);
      tt = tt.next;
    }
    if (JavaCCGlobals.ccol != 1 && JavaCCGlobals.cline != t.beginLine) {
      retval += "\n";
      JavaCCGlobals.cline++;
      JavaCCGlobals.ccol = 1;
    }
    return retval;
  }
}
