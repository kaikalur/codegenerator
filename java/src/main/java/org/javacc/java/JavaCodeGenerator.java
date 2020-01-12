
package org.javacc.java;

import org.javacc.jjtree.DefaultJJTreeVisitor;
import org.javacc.parser.CodeGenerator;
import org.javacc.parser.CodeGeneratorSettings;
import org.javacc.parser.Options;

public class JavaCodeGenerator implements CodeGenerator {

  /**
   * The name of the Java code generator.
   */
  @Override
  public final String getName() {
    return "Java";
  }

  /**
   * Generate any other support files you need.
   */
  @Override
  public boolean generateHelpers(CodeGeneratorSettings settings) {
    JavaTemplates templates = JavaTemplates.getTemplates();

    try {
      JavaHelperFiles.generateSimple("/templates/TokenMgrError.template",
          JavaTemplates.getTokenMgrErrorClass() + ".java", settings);
      JavaHelperFiles.generateSimple(templates.getParseExceptionTemplateResourceUrl(), "ParseException.java", settings);

      JavaHelperFiles.gen_Constants();

      if (Options.isGenerateBoilerplateCode()) {
        JavaHelperFiles.gen_Token();
        if (Options.getUserTokenManager()) {
          // CBA -- I think that Token managers are unique so will always be
          // generated
          JavaHelperFiles.gen_TokenManager();
        }

        if (Options.getUserCharStream()) {
          JavaHelperFiles.generateSimple("/templates/CharStream.template", "CharStream.java", settings);
        } else if (Options.getJavaUnicodeEscape()) {
          JavaHelperFiles.generateSimple(templates.getJavaCharStreamTemplateResourceUrl(), "JavaCharStream.java",
              settings);
        } else {
          JavaHelperFiles.generateSimple(templates.getSimpleCharStreamTemplateResourceUrl(), "SimpleCharStream.java",
              settings);
        }

        if (JavaTemplates.isJavaModern()) {
          JavaHelperFiles.genMiscFile("Provider.java", "/templates/gwt/Provider.template");
          JavaHelperFiles.genMiscFile("StringProvider.java", "/templates/gwt/StringProvider.template");
          // This provides a bridge to standard Java readers.
          JavaHelperFiles.genMiscFile("StreamProvider.java", "/templates/gwt/StreamProvider.template");
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
  public final TokenCodeGenerator getTokenCodeGenerator() {
    return new TokenCodeGenerator();
  }

  /**
   * The TokenManager class generator.
   */
  @Override
  public final TokenManagerCodeGenerator getTokenManagerCodeGenerator() {
    return new TokenManagerCodeGenerator();
  }

  /**
   * The Parser class generator.
   */
  @Override
  public final ParserCodeGenerator getParserCodeGenerator() {
    return new ParserCodeGenerator();
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
