
package org.javacc.java;

import org.javacc.parser.CodeGeneratorSettings;

import java.io.IOException;

class TokenCodeGenerator implements org.javacc.parser.TokenCodeGenerator {

  /**
   * The Token class generator.
   */
  @Override
  public boolean generateCodeForToken(CodeGeneratorSettings settings) {
    try {
      JavaHelperFiles.generateSimple("/templates/Token.template", "Token.java", settings);
    } catch (IOException e) {
      return false;
    }
    return true;
  }
}
