
package org.javacc.csharp;

import org.javacc.Version;
import org.javacc.jjtree.ASTNodeDescriptor;
import org.javacc.jjtree.JJTreeGlobals;
import org.javacc.jjtree.JJTreeOptions;
import org.javacc.parser.Options;
import org.javacc.utils.CodeGenBuilder.GenericCodeBuilder;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

final class NodeFiles {

  private NodeFiles() {}

  /**
   * ID of the latest version (of JJTree) in which one of the Node classes was
   * modified.
   */
  static final String        nodeVersion  = Version.version;

  private static Set<String> nodesToBuild = new HashSet<String>();

  static void generateNodeType(String nodeType) {
    if (!nodeType.equals("Node") && !nodeType.equals("SimpleNode")) {
      nodesToBuild.add(nodeType);
    }
  }

  private static void generateTreeNodes() {
    Map<String, Object> options = new HashMap<>(Options.getOptions());
    options.put(Options.NONUSER_OPTION__PARSER_NAME, JJTreeGlobals.parserName);
    options.put("VISITOR_RETURN_TYPE_VOID", Boolean.valueOf(JJTreeOptions.getVisitorReturnType().equals("void")));

    GenericCodeBuilder builder = GenericCodeBuilder.of(options);
    builder.setFile(new File(JJTreeOptions.getJJTreeOutputDirectory(), JJTreeGlobals.parserName + "Tree.cs"));
    builder.setVersion(nodeVersion).addTools(JJTreeGlobals.toolName);
    builder.addOption("MULTI", "NODE_USES_PARSER", "VISITOR", "TRACK_TOKENS", "NODE_PREFIX", "NODE_EXTENDS",
        "NODE_FACTORY", Options.USEROPTION__SUPPORT_CLASS_VISIBILITY_PUBLIC);

    try {
      if (JJTreeOptions.stringValue(Options.USEROPTION__NAMESPACE).length() > 0) {
        builder.println("namespace " + JJTreeOptions.stringValue("NAMESPACE_OPEN"));
      }

      for (String node : nodesToBuild) {
        builder.printTemplate("/templates/csharp/MultiNode.template", Collections.singletonMap("NODE_TYPE", node));
      }

      if (JJTreeOptions.stringValue(Options.USEROPTION__NAMESPACE).length() > 0) {
        builder.println(JJTreeOptions.stringValue("NAMESPACE_CLOSE"));
      }
      builder.build();
    } catch (IOException e) {
      throw new Error(e.toString());
    }
  }


  static String nodeConstants() {
    return JJTreeGlobals.parserName + "TreeConstants";
  }

  static void generateTreeConstants() {


    GenericCodeBuilder builder = GenericCodeBuilder.of(Collections.emptyMap());
    builder.setFile(new File(JJTreeOptions.getJJTreeOutputDirectory(), nodeConstants() + ".cs"));

    List<String> nodeIds = ASTNodeDescriptor.getNodeIds();
    List<String> nodeNames = ASTNodeDescriptor.getNodeNames();

    if (JJTreeOptions.stringValue(Options.USEROPTION__NAMESPACE).length() > 0) {
      builder.println("namespace " + JJTreeOptions.stringValue("NAMESPACE_OPEN"));
    }
    builder.println("public class " + nodeConstants());
    builder.println("{");

    for (int i = 0; i < nodeIds.size(); ++i) {
      String n = nodeIds.get(i);
      builder.println("  public const int " + n + " = " + i + ";");
    }

    builder.println();
    builder.println();

    builder.println("  public static string[] jjtNodeName = {");
    for (int i = 0; i < nodeNames.size(); ++i) {
      String n = nodeNames.get(i);
      builder.println("    \"" + n + "\",");
    }
    builder.println("  };");

    builder.println("}");
    if (JJTreeOptions.stringValue(Options.USEROPTION__NAMESPACE).length() > 0) {
      builder.println(JJTreeOptions.stringValue("NAMESPACE_CLOSE"));
    }
    builder.build();
  }


  static String visitorClass() {
    return JJTreeGlobals.parserName + "Visitor";
  }

  static void generateVisitor() {
    if (!JJTreeOptions.getVisitor()) {
      return;
    }

    GenericCodeBuilder builder = GenericCodeBuilder.of(Collections.emptyMap());
    builder.setFile(new File(JJTreeOptions.getJJTreeOutputDirectory(), visitorClass() + ".cs"));

    List<String> nodeNames = ASTNodeDescriptor.getNodeNames();

    if (JJTreeOptions.stringValue(Options.USEROPTION__NAMESPACE).length() > 0) {
      builder.println("namespace " + JJTreeOptions.stringValue("NAMESPACE_OPEN"));
    }
    builder.println("public interface " + visitorClass());
    builder.println("{");

    String ve = mergeVisitorException();

    String argumentType = "object";
    if (!JJTreeOptions.getVisitorDataType().equals("")) {
      argumentType = JJTreeOptions.getVisitorDataType();
    }

    builder.println(
        "  " + JJTreeOptions.getVisitorReturnType() + " Visit(SimpleNode node, " + argumentType + " data)" + ve + ";");
    if (JJTreeOptions.getMulti()) {
      for (int i = 0; i < nodeNames.size(); ++i) {
        String n = nodeNames.get(i);
        if (n.equals("void")) {
          continue;
        }
        String nodeType = JJTreeOptions.getNodePrefix() + n;
        builder.println("  " + JJTreeOptions.getVisitorReturnType() + " " + getVisitMethodName(nodeType) + "("
            + nodeType + " node, " + argumentType + " data)" + ve + ";");
      }
    }
    builder.println("}");
    if (JJTreeOptions.stringValue(Options.USEROPTION__NAMESPACE).length() > 0) {
      builder.println(JJTreeOptions.stringValue("NAMESPACE_CLOSE"));
    }
    builder.build();
  }

  static String defaultVisitorClass() {
    return JJTreeGlobals.parserName + "DefaultVisitor";
  }

  private static String getVisitMethodName(String className) {
    StringBuffer sb = new StringBuffer("Visit");
    if (JJTreeOptions.booleanValue("VISITOR_METHOD_NAME_INCLUDES_TYPE_NAME")) {
      sb.append(Character.toUpperCase(className.charAt(0)));
      for (int i = 1; i < className.length(); i++) {
        sb.append(className.charAt(i));
      }
    }

    return sb.toString();
  }

  static void generateDefaultVisitor() {
    if (!JJTreeOptions.getVisitor()) {
      return;
    }

    GenericCodeBuilder builder = GenericCodeBuilder.of(Collections.emptyMap());
    builder.setFile(new File(JJTreeOptions.getJJTreeOutputDirectory(), defaultVisitorClass() + ".cs"));

    List<String> nodeNames = ASTNodeDescriptor.getNodeNames();

    if (JJTreeOptions.stringValue(Options.USEROPTION__NAMESPACE).length() > 0) {
      builder.println("namespace " + JJTreeOptions.stringValue("NAMESPACE_OPEN"));
    }
    builder.println("public class " + defaultVisitorClass() + " : " + visitorClass() + "{");

    String ve = mergeVisitorException();

    String argumentType = "object";
    if (!JJTreeOptions.getVisitorDataType().equals("")) {
      argumentType = JJTreeOptions.getVisitorDataType();
    }

    String ret = JJTreeOptions.getVisitorReturnType();
    builder.println("  public virtual " + ret + " defaultVisit(SimpleNode node, " + argumentType + " data)" + ve + "{");
    builder.println("    node.childrenAccept(this, data);");
    builder.println("    return" + (ret.trim().equals("void") ? "" : " data") + ";");
    builder.println("  }");

    builder.println("  public virtual " + ret + " Visit(SimpleNode node, " + argumentType + " data)" + ve + "{");
    builder.println("    " + (ret.trim().equals("void") ? "" : "return ") + "defaultVisit(node, data);");
    builder.println("  }");

    if (JJTreeOptions.getMulti()) {
      for (int i = 0; i < nodeNames.size(); ++i) {
        String n = nodeNames.get(i);
        if (n.equals("void")) {
          continue;
        }
        String nodeType = JJTreeOptions.getNodePrefix() + n;
        builder.println("  public virtual " + ret + " " + getVisitMethodName(nodeType) + "(" + nodeType + " node, "
            + argumentType + " data)" + ve + "{");
        builder.println("    " + (ret.trim().equals("void") ? "" : "return ") + "defaultVisit(node, data);");
        builder.println("  }");
      }
    }
    builder.println("}");
    if (JJTreeOptions.stringValue(Options.USEROPTION__NAMESPACE).length() > 0) {
      builder.println(JJTreeOptions.stringValue("NAMESPACE_CLOSE"));
    }
    builder.build();
  }

  private static String mergeVisitorException() {
    String ve = JJTreeOptions.getVisitorException();
    if (!"".equals(ve)) {
      ve = " throws " + ve;
    }
    return ve;
  }

  private static void generateDefaultNode() throws IOException {
    Map<String, Object> options = new HashMap<>(Options.getOptions());
    options.put(Options.NONUSER_OPTION__PARSER_NAME, JJTreeGlobals.parserName);
    options.put("VISITOR_RETURN_TYPE_VOID", Boolean.valueOf(JJTreeOptions.getVisitorReturnType().equals("void")));

    GenericCodeBuilder builder = GenericCodeBuilder.of(options);
    builder.setFile(new File(JJTreeOptions.getJJTreeOutputDirectory(), "Node.cs"));
    builder.printTemplate("/templates/csharp/Node.template");
    builder.build();
  }

  static void generateOutputFiles() throws IOException {
    generateDefaultNode();
    generateTreeNodes();
    generateTreeConstants();
    generateVisitor();
    generateDefaultVisitor();
  }

}
