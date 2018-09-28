
package org.javacc.java;

import org.javacc.java.JavaFiles.JavaResourceTemplateLocations;
import org.javacc.parser.CodeGeneratorSettings;

public class TokenCodeGenerator implements org.javacc.parser.TokenCodeGenerator {

  /**
   * The Token class generator.
   */
  @Override
  public boolean generateCodeForToken(CodeGeneratorSettings settings) {
    try {
      JavaResourceTemplateLocations templateLoc = JavaFiles.RESOURCES_JAVA_CLASSIC;
      JavaFiles.gen_Token(templateLoc);
    } catch (Exception e) {
      return false;
    }

    return true;
  }
}
