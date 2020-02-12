
package org.javacc.java;

import org.javacc.parser.CodeGeneratorSettings;
import org.javacc.parser.Context;

import java.io.IOException;

class TokenCodeGenerator implements org.javacc.parser.TokenCodeGenerator {

  private final Context context;

  TokenCodeGenerator(Context context) {
    this.context = context;
  }

  /**
   * The Token class generator.
   */
  @Override
  public boolean generateCodeForToken(CodeGeneratorSettings settings) {
    try {
      JavaHelperFiles.generateSimple("/templates/Token.template", "Token.java", settings, context);
    } catch (IOException e) {
      return false;
    }
    return true;
  }
}
