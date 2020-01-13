
package org.javacc.java;

import org.javacc.jjtree.JJTreeGlobals;
import org.javacc.parser.Options;

/**
 * The {@link JavaTemplates} class.
 */
public abstract class JavaTemplates {

  private static final JavaTemplates RESOURCES_JAVA_CLASSIC = new JavaClassicTemplates();
  private static final JavaTemplates RESOURCES_JAVA_MODERN  = new JavaModernTemplates();


  public abstract String getJavaCharStreamTemplateResourceUrl();

  public abstract String getSimpleCharStreamTemplateResourceUrl();

  public abstract String getParseExceptionTemplateResourceUrl();


  public static String getTokenMgrErrorClass() {
    return Options.isLegacyExceptionHandling() ? "TokenMgrError" : "TokenMgrException";
  }

  public static String nodeConstants() {
    return JJTreeGlobals.parserName + "TreeConstants";
  }

  public static String visitorClass() {
    return JJTreeGlobals.parserName + "Visitor";
  }

  public static String defaultVisitorClass() {
    return JJTreeGlobals.parserName + "DefaultVisitor";
  }

  /**
   * The {@link JavaClassicTemplates} class.
   */
  private static class JavaClassicTemplates extends JavaTemplates {

    @Override
    public String getJavaCharStreamTemplateResourceUrl() {
      return "/templates/JavaCharStream.template";
    }

    @Override
    public String getSimpleCharStreamTemplateResourceUrl() {
      return "/templates/SimpleCharStream.template";
    }

    @Override
    public String getParseExceptionTemplateResourceUrl() {
      return "/templates/ParseException.template";
    }
  };

  /**
   * The {@link JavaModernTemplates} class.
   */
  private static class JavaModernTemplates extends JavaTemplates {

    @Override
    public String getJavaCharStreamTemplateResourceUrl() {
      return "/templates/gwt/JavaCharStream.template";
    }

    @Override
    public String getSimpleCharStreamTemplateResourceUrl() {
      return "/templates/gwt/SimpleCharStream.template";
    }

    @Override
    public String getParseExceptionTemplateResourceUrl() {
      return "/templates/gwt/ParseException.template";
    }
  };

  public static boolean isJavaModern() {
    return Options.getJavaTemplateType().equals(Options.JAVA_TEMPLATE_TYPE_MODERN);
  }

  public static JavaTemplates getTemplates() {
    return isJavaModern() ? RESOURCES_JAVA_MODERN : RESOURCES_JAVA_CLASSIC;
  }
}
