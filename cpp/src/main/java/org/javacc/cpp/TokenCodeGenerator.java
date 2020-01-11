package org.javacc.cpp;

import org.javacc.parser.CodeGeneratorSettings;
import org.javacc.parser.JavaCCGlobals;
import org.javacc.parser.Options;
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
      String[] parameters = new String[] {Options.USEROPTION__STATIC, Options.USEROPTION__SUPPORT_CLASS_VISIBILITY_PUBLIC, Options.USEROPTION__CPP_TOKEN_INCLUDES, Options.USEROPTION__TOKEN_EXTENDS};

      CodeGenBuilder.generateTemplate("/templates/cpp/Token.h.template", "Token.h", JavaCCGlobals.toolName, settings, parameters);
      CodeGenBuilder.generateTemplate("/templates/cpp/Token.cc.template", "Token.cc", JavaCCGlobals.toolName, settings, parameters);
}
    catch(IOException e)
    {
      return false;
    }

    return true;
  }
}
