
package org.javacc.cpp;

import org.javacc.parser.CodeGeneratorSettings;
import org.javacc.parser.JavaCCGlobals;
import org.javacc.parser.Options;
import org.javacc.parser.TokenManagerCodeGenerator;
import org.javacc.utils.CodeGenBuilder;

public class CodeGenerator implements org.javacc.parser.CodeGenerator {

  public static final boolean IS_DEBUG = true;

  /**
   * The name of the C# code generator.
   */
  @Override
  public String getName() {
    return "C++";
  }

  /**
   * Generate any other support files you need.
   */
  @Override
  public boolean generateHelpers(CodeGeneratorSettings settings) {
    try {
      String[] parameters = new String[] {Options.USEROPTION__STATIC, Options.USEROPTION__SUPPORT_CLASS_VISIBILITY_PUBLIC};
      CodeGenBuilder.generateTemplate("/templates/cpp/CharStream.h.template", "CharStream.h",
          JavaCCGlobals.toolName, settings, parameters);
      CodeGenBuilder.generateTemplate("/templates/cpp/CharStream.cc.template", "CharStream.cc",
          JavaCCGlobals.toolName, settings, parameters);

      parameters = new String[] {Options.USEROPTION__STATIC, Options.USEROPTION__SUPPORT_CLASS_VISIBILITY_PUBLIC};
      CodeGenBuilder.generateTemplate("/templates/cpp/TokenMgrError.h.template", "TokenMgrError.h",
          JavaCCGlobals.toolName, settings, parameters);
      CodeGenBuilder.generateTemplate("/templates/cpp/TokenMgrError.cc.template", "TokenMgrError.cc",
          JavaCCGlobals.toolName, settings, parameters);

      parameters = new String[] {Options.USEROPTION__STATIC, Options.USEROPTION__SUPPORT_CLASS_VISIBILITY_PUBLIC};
      CodeGenBuilder.generateTemplate("/templates/cpp/ParseException.h.template", "ParseException.h",
          JavaCCGlobals.toolName, settings, parameters);
      CodeGenBuilder.generateTemplate("/templates/cpp/ParseException.cc.template", "ParseException.cc",
          JavaCCGlobals.toolName, settings, parameters);

      parameters = new String[] {Options.USEROPTION__STATIC, Options.USEROPTION__SUPPORT_CLASS_VISIBILITY_PUBLIC};
      CodeGenBuilder.generateTemplate("/templates/cpp/TokenManager.h.template", "TokenManager.h",
          JavaCCGlobals.toolName, settings, parameters);

      parameters = new String[] {Options.USEROPTION__STATIC, Options.USEROPTION__SUPPORT_CLASS_VISIBILITY_PUBLIC};
      CodeGenBuilder.generateTemplate("/templates/cpp/JavaCC.h.template", "JavaCC.h", JavaCCGlobals.toolName,
          settings, parameters);

      parameters = new String[] {Options.USEROPTION__STATIC, Options.USEROPTION__SUPPORT_CLASS_VISIBILITY_PUBLIC, Options.USEROPTION__BUILD_PARSER, Options.USEROPTION__BUILD_TOKEN_MANAGER};
      CodeGenBuilder.generateTemplate("/templates/cpp/ErrorHandler.h.template", "ErrorHandler.h",
          JavaCCGlobals.toolName, settings, parameters);

      OtherFilesGenCPP.start();
    } catch (Exception e) {
      return false;
    }

    return true;
  }

  /**
   * The Token class generator.
   */
  @Override
  public TokenCodeGenerator getTokenCodeGenerator() {
    return new TokenCodeGenerator();
  }

  /**
   * The TokenManager class generator.
   */
  @Override
  public TokenManagerCodeGenerator getTokenManagerCodeGenerator() {
    return new org.javacc.cpp.TokenManagerCodeGenerator();
  }

  /**
   * The Parser class generator.
   */
  @Override
  public ParserCodeGenerator getParserCodeGenerator() {
    return new ParserCodeGenerator();
  }

  /**
   * TODO(sreeni): Fix this when we do tree annotations in the parser code
   * generator. The JJTree preprocesor.
   */
  @Override
  public org.javacc.jjtree.DefaultJJTreeVisitor getJJTreeCodeGenerator() {
    return new JJTreeCodeGenerator();
  }

}
