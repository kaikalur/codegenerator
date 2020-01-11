package org.javacc.csharp;

import org.javacc.parser.CodeGeneratorSettings;
import org.javacc.parser.JavaCCGlobals;
import org.javacc.utils.CodeGenBuilder;

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
      CodeGenBuilder.generateTemplate("/templates/csharp/Token.template", "Token.cs", JavaCCGlobals.toolName, settings);
    }
    catch(IOException e)
    {
      return false;
    }

    return true;
  }
}
