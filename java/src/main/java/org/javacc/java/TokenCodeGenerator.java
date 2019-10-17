package org.javacc.java;

import org.javacc.parser.CodeGeneratorSettings;

import java.io.IOException;

public class TokenCodeGenerator implements org.javacc.parser.TokenCodeGenerator
{
  /**
   * The Token class generator.
   */
  @Override
  public boolean generateCodeForToken(CodeGeneratorSettings settings)
  {
    try
    {
      JavaGlobals.generateSimple("/templates/Token.template", "Token.java", "/* JavaCC generated file. */", settings);
    }
    catch(IOException e)
    {
      return false;
    }

    return true;
  }
}
