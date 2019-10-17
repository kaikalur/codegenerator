package org.javacc.java;

import org.javacc.java.JavaGlobals.JavaTemplates;
import org.javacc.parser.CodeGeneratorSettings;
import org.javacc.parser.Options;

public class CodeGenerator implements org.javacc.parser.CodeGenerator
{
  /**
   * The name of the Java code generator.
   */
  @Override
  public String getName() 
  {
    return "Java";
  }

  /**
   * Generate any other support files you need.
   */
  @Override
  public boolean generateHelpers(CodeGeneratorSettings settings)
  {
    JavaTemplates templates = JavaGlobals.getTemplates();
    try
    {
      JavaGlobals.generateSimple("/templates/TokenMgrError.template", JavaGlobals.getTokenMgrErrorClass() + ".java", "/* JavaCC generated file. */", settings);
      JavaGlobals.generateSimple(templates.getParseExceptionTemplateResourceUrl(), "ParseException.java", "/* JavaCC generated file. */", settings);
      
      JavaGlobals.gen_Constants();
      
      if (Options.isGenerateBoilerplateCode()) {
        JavaGlobals.gen_Token();
        if (Options.getUserTokenManager()) {
          // CBA -- I think that Token managers are unique so will always be generated
          JavaGlobals.gen_TokenManager();
        }
        
        if (Options.getUserCharStream()) {
          JavaGlobals.generateSimple("/templates/CharStream.template", "CharStream.java", "/* JavaCC generated file. */", settings);
        } else if (Options.getJavaUnicodeEscape()) {
          JavaGlobals.generateSimple(templates.getJavaCharStreamTemplateResourceUrl(), "JavaCharStream.java", "/* JavaCC generated file. */", settings);
        } else {
          JavaGlobals.generateSimple(templates.getSimpleCharStreamTemplateResourceUrl(), "SimpleCharStream.java", "/* JavaCC generated file. */", settings);
        }
        
        if (JavaGlobals.isJavaModern()) {
          JavaGlobals.genMiscFile("Provider.java", "/templates/gwt/Provider.template");
          JavaGlobals.genMiscFile("StringProvider.java", "/templates/gwt/StringProvider.template");
          // This provides a bridge to standard Java readers.
          JavaGlobals.genMiscFile("StreamProvider.java", "/templates/gwt/StreamProvider.template");
        }
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
  public org.javacc.parser.TokenManagerCodeGenerator getTokenManagerCodeGenerator()
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
