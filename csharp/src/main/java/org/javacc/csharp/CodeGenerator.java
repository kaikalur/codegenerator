package org.javacc.csharp;

import org.javacc.parser.CodeGeneratorSettings;
import org.javacc.parser.JavaCCGlobals;
import org.javacc.utils.TemplateGenerator;

public class CodeGenerator implements org.javacc.parser.CodeGenerator
{
  /**
   * The name of the C# code generator.
   */
  @Override
  public String getName() 
  {
    return "C#";
  }

  /**
   * Generate any other support files you need.
   */
  @Override
  public boolean generateHelpers(CodeGeneratorSettings settings)
  {
    try
    {
      TemplateGenerator.generateTemplate("/templates/csharp/CharStream.template", "CharStream.cs", JavaCCGlobals.toolName, settings);
      TemplateGenerator.generateTemplate("/templates/csharp/TokenMgrError.template", "TokenMgrError.cs", JavaCCGlobals.toolName, settings);
      TemplateGenerator.generateTemplate("/templates/csharp/ParseException.template", "ParseException.cs", JavaCCGlobals.toolName, settings);
      if ((Boolean)settings.get("JAVA_UNICODE_ESCAPE")) {
        TemplateGenerator.generateTemplate("/templates/csharp/JavaCharStream.template", "JavaCharStream.cs", JavaCCGlobals.toolName, settings);
      } else {
        TemplateGenerator.generateTemplate("/templates/csharp/CharStream.template", "CharStream.cs", JavaCCGlobals.toolName, settings);
      }
    }
    catch(Exception e)
    {
      return false;
    }

    return true;
  }

  /**
   * The Token class generator.
   */
  @Override
  public TokenCodeGenerator getTokenCodeGenerator()
  {
    return new TokenCodeGenerator();
  }

  /**
   * The TokenManager class generator.
   */
  @Override
  public TokenManagerCodeGenerator getTokenManagerCodeGenerator()
  {
    return new TokenManagerCodeGenerator();
  }

  /**
   * The Parser class generator.
   */
  @Override
  public ParserCodeGenerator getParserCodeGenerator()
  {
    return new ParserCodeGenerator();
  }
  
  /**
   * TODO(sreeni): Fix this when we do tree annotations in the parser code generator.
   * The JJTree preprocesor.
   */
  @Override
  public org.javacc.jjtree.DefaultJJTreeVisitor getJJTreeCodeGenerator()
  {
    return new JJTreeCodeGenerator();
  }

}
