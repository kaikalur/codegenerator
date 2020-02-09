
package org.javacc.cpp;

import org.javacc.jjtree.DefaultJJTreeVisitor;
import org.javacc.parser.CodeGenerator;
import org.javacc.parser.CodeGeneratorSettings;
import org.javacc.parser.JavaCCContext;
import org.javacc.parser.JavaCCGlobals;
import org.javacc.parser.Options;
import org.javacc.parser.TokenizerData;

import java.io.File;

public class CppCodeGenerator implements CodeGenerator {

  static final boolean IS_DEBUG = true;

  /**
   * The name of the C# code generator.
   */
  @Override
  public final String getName() {
    return "C++";
  }

  /**
   * Generate any other support files you need.
   */
  @Override
  public final boolean generateHelpers(JavaCCContext context, CodeGeneratorSettings settings, TokenizerData tokenizerData) {
    try {
      try (CppCodeBuilder builder = CppCodeBuilder.of(context, settings)) {
        builder.setFile(new File((String) settings.get("OUTPUT_DIRECTORY"), "CharStream.cc"));
        builder.addTools(JavaCCGlobals.toolName);
        builder.addOption(Options.USEROPTION__STATIC, Options.USEROPTION__SUPPORT_CLASS_VISIBILITY_PUBLIC);

        builder.printTemplate("/templates/cpp/CharStream.cc.template");
        builder.switchToIncludeFile();
        builder.printTemplate("/templates/cpp/CharStream.h.template");
      }

      try (CppCodeBuilder builder = CppCodeBuilder.of(context, settings)) {
        builder.setFile(new File((String) settings.get("OUTPUT_DIRECTORY"), "TokenMgrError.cc"));
        builder.addTools(JavaCCGlobals.toolName);
        builder.addOption(Options.USEROPTION__STATIC, Options.USEROPTION__SUPPORT_CLASS_VISIBILITY_PUBLIC);

        builder.printTemplate("/templates/cpp/TokenMgrError.cc.template");
        builder.switchToIncludeFile();
        builder.printTemplate("/templates/cpp/TokenMgrError.h.template");
      }

      try (CppCodeBuilder builder = CppCodeBuilder.of(context, settings)) {
        builder.setFile(new File((String) settings.get("OUTPUT_DIRECTORY"), "ParseException.cc"));
        builder.addTools(JavaCCGlobals.toolName);
        builder.addOption(Options.USEROPTION__STATIC, Options.USEROPTION__SUPPORT_CLASS_VISIBILITY_PUBLIC);

        builder.printTemplate("/templates/cpp/ParseException.cc.template");
        builder.switchToIncludeFile();
        builder.printTemplate("/templates/cpp/ParseException.h.template");
      }

      try (CppCodeBuilder builder = CppCodeBuilder.ofHeader(context, settings)) {
        builder.setFile(new File((String) settings.get("OUTPUT_DIRECTORY"), "TokenManager.h"));
        builder.addTools(JavaCCGlobals.toolName);
        builder.addOption(Options.USEROPTION__STATIC, Options.USEROPTION__SUPPORT_CLASS_VISIBILITY_PUBLIC);

        builder.printTemplate("/templates/cpp/TokenManager.h.template");
      }

      try (CppCodeBuilder builder = CppCodeBuilder.ofHeader(context, settings)) {
        builder.setFile(new File((String) settings.get("OUTPUT_DIRECTORY"), "JavaCC.h"));
        builder.addTools(JavaCCGlobals.toolName);
        builder.addOption(Options.USEROPTION__STATIC, Options.USEROPTION__SUPPORT_CLASS_VISIBILITY_PUBLIC);

        builder.printTemplate("/templates/cpp/JavaCC.h.template");
      }

      try (CppCodeBuilder builder = CppCodeBuilder.ofHeader(context, settings)) {
        builder.setFile(new File((String) settings.get("OUTPUT_DIRECTORY"), "ErrorHandler.h"));
        builder.addTools(JavaCCGlobals.toolName);
        builder.addOption(Options.USEROPTION__STATIC, Options.USEROPTION__SUPPORT_CLASS_VISIBILITY_PUBLIC,
            Options.USEROPTION__BUILD_PARSER, Options.USEROPTION__BUILD_TOKEN_MANAGER);

        builder.printTemplate("/templates/cpp/ErrorHandler.h.template");
      }

      OtherFilesGenCPP.start(context, tokenizerData);
    } catch (Exception e) {
      return false;
    }

    return true;
  }

  /**
   * The Token class generator.
   */
  @Override
  public final TokenCodeGenerator getTokenCodeGenerator(JavaCCContext context) {
    return new TokenCodeGenerator(context);
  }

  /**
   * The TokenManager class generator.
   */
  @Override
  public final TokenManagerCodeGenerator getTokenManagerCodeGenerator(JavaCCContext context) {
    return new TokenManagerCodeGenerator(context);
  }

  /**
   * The Parser class generator.
   */
  @Override
  public final ParserCodeGenerator getParserCodeGenerator(JavaCCContext context) {
    return new ParserCodeGenerator(context);
  }

  /**
   * TODO(sreeni): Fix this when we do tree annotations in the parser code
   * generator. The JJTree preprocesor.
   */
  @Override
  public final DefaultJJTreeVisitor getJJTreeCodeGenerator() {
    return new JJTreeCodeGenerator();
  }

}
