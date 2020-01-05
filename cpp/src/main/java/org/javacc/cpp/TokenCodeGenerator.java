package org.javacc.cpp;

import java.io.IOException;
import org.javacc.parser.CodeGeneratorSettings;
import org.javacc.parser.JavaCCGlobals;
import org.javacc.utils.TemplateGenerator;

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
      TemplateGenerator.generateTemplate("/templates/cpp/Token.h.template", "Token.h", JavaCCGlobals.toolName, settings);
      TemplateGenerator.generateTemplate("/templates/cpp/Token.cc.template", "Token.cc", JavaCCGlobals.toolName, settings);
}
    catch(IOException e)
    {
      return false;
    }

    return true;
  }
}
