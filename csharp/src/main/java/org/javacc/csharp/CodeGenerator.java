
package org.javacc.csharp;

import org.javacc.parser.CodeGeneratorSettings;
import org.javacc.parser.JavaCCContext;
import org.javacc.parser.JavaCCGlobals;
import org.javacc.parser.TokenizerData;
import org.javacc.utils.CodeBuilder.GenericCodeBuilder;

import java.io.File;

public class CodeGenerator implements org.javacc.parser.CodeGenerator {

  /**
   * The name of the C# code generator.
   */
  @Override
  public String getName() {
    return "C#";
  }

  /**
   * Generate any other support files you need.
   */
  @Override
  public boolean generateHelpers(JavaCCContext context, CodeGeneratorSettings settings, TokenizerData tokenizerData) {
    File directory = new File((String) settings.get("OUTPUT_DIRECTORY"));
    try {
      try (GenericCodeBuilder builder = GenericCodeBuilder.of(context, settings)) {
        builder.setFile(new File(directory, "CharStream.cs"));
        builder.addTools(JavaCCGlobals.toolName).printTemplate("/templates/csharp/CharStream.template");
      }

      try (GenericCodeBuilder builder = GenericCodeBuilder.of(context, settings)) {
        builder.setFile(new File(directory, "TokenMgrError.cs"));
        builder.addTools(JavaCCGlobals.toolName).printTemplate("/templates/csharp/TokenMgrError.template");
      }

      try (GenericCodeBuilder builder = GenericCodeBuilder.of(context, settings)) {
        builder.setFile(new File(directory, "ParseException.cs"));
        builder.addTools(JavaCCGlobals.toolName).printTemplate("/templates/csharp/ParseException.template");
      }

      try (GenericCodeBuilder builder = GenericCodeBuilder.of(context, settings)) {
        builder.addTools(JavaCCGlobals.toolName);

        if ((Boolean) settings.get("JAVA_UNICODE_ESCAPE")) {
          builder.setFile(new File(directory, "JavaCharStream.cs"));
          builder.printTemplate("/templates/csharp/JavaCharStream.template");
        } else {
          builder.setFile(new File(directory, "CharStream.cs"));
          builder.printTemplate("/templates/csharp/CharStream.template");
        }
      }
    } catch (Exception e) {
      return false;
    }

    return true;
  }

  /**
   * The Token class generator.
   */
  @Override
  public TokenCodeGenerator getTokenCodeGenerator(JavaCCContext context) {
    return new TokenCodeGenerator(context);
  }

  /**
   * The TokenManager class generator.
   */
  @Override
  public TokenManagerCodeGenerator getTokenManagerCodeGenerator(JavaCCContext context) {
    return new TokenManagerCodeGenerator(context);
  }

  /**
   * The Parser class generator.
   */
  @Override
  public ParserCodeGenerator getParserCodeGenerator(JavaCCContext context) {
    return new ParserCodeGenerator(context);
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
