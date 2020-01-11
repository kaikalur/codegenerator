
package org.javacc.java;

import org.javacc.Version;
import org.javacc.jjtree.ASTNodeDescriptor;
import org.javacc.jjtree.JJTreeGlobals;
import org.javacc.jjtree.JJTreeOptions;
import org.javacc.parser.Options;

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

  private static Set<String> nodesToBuild = new HashSet<>();

  static void generateNodeType(String nodeType) {
    if (!nodeType.equals("Node") && !nodeType.equals("SimpleNode")) {
      nodesToBuild.add(nodeType);
    }
  }

  private static void generateTreeNodes() {
    JavaCodeGenBuilder builder = JavaCodeGenBuilder.of(Collections.emptyMap());
    builder.setFile(new File(JJTreeOptions.getJJTreeOutputDirectory(), JJTreeGlobals.parserName + "Tree.java"));
    builder.addTools(JJTreeGlobals.toolName).setVersion(nodeVersion);
    builder.addOption("MULTI", "NODE_USES_PARSER", "VISITOR", "TRACK_TOKENS", "NODE_PREFIX", "NODE_EXTENDS",
        "NODE_FACTORY", Options.USEROPTION__SUPPORT_CLASS_VISIBILITY_PUBLIC);
    builder.setPackageName(JJTreeGlobals.packageName);

    try {
      for (String node : nodesToBuild) {
        File file = new File(JJTreeOptions.getASTNodeDirectory(), node + ".java");
        if (file.exists())
          continue;

        NodeFiles.generateMULTINode(builder, node);
      }
      builder.build();
    } catch (IOException e) {
      throw new Error(e.toString());
    }
  }

  private static void generateMULTINode(JavaCodeGenBuilder builder, String nodeType) throws IOException {
    Map<String, Object> options = new HashMap<>(Options.getOptions());
    options.put(Options.NONUSER_OPTION__PARSER_NAME, JJTreeGlobals.parserName);
    options.put("NODE_TYPE", nodeType);
    options.put("VISITOR_RETURN_TYPE_VOID", Boolean.valueOf(JJTreeOptions.getVisitorReturnType().equals("void")));

    builder.printTemplate("/templates/MultiNode.template", options);
  }


  static String nodeConstants() {
    return JJTreeGlobals.parserName + "TreeConstants";
  }

  static void generateTreeConstants() {
    List<String> nodeIds = ASTNodeDescriptor.getNodeIds();
    List<String> nodeNames = ASTNodeDescriptor.getNodeNames();

    JavaCodeGenBuilder builder = JavaCodeGenBuilder.of(Collections.emptyMap());
    builder.setFile(new File(JJTreeOptions.getJJTreeOutputDirectory(), nodeConstants() + ".java"));
    builder.setPackageName(JJTreeGlobals.packageName);


    builder.println("public interface " + nodeConstants());
    builder.println("{");

    for (int i = 0; i < nodeIds.size(); ++i) {
      builder.println("  public final int ", nodeIds.get(i), " = ", i, ";");
    }

    builder.println();
    builder.println();

    builder.println("  public static String[] jjtNodeName = {");
    for (int i = 0; i < nodeNames.size(); ++i) {
      builder.println("    \"", nodeNames.get(i), "\",");
    }
    builder.println("  };");
    builder.println("}");

    builder.build();
  }


  static String visitorClass() {
    return JJTreeGlobals.parserName + "Visitor";
  }

  static void generateVisitor() {
    if (!JJTreeOptions.getVisitor()) {
      return;
    }

    List<String> nodeNames = ASTNodeDescriptor.getNodeNames();
    String ve = mergeVisitorException();

    String argumentType = "Object";
    if (!JJTreeOptions.getVisitorDataType().equals("")) {
      argumentType = JJTreeOptions.getVisitorDataType();
    }

    JavaCodeGenBuilder builder = JavaCodeGenBuilder.of(Collections.emptyMap());
    builder.setFile(new File(JJTreeOptions.getJJTreeOutputDirectory(), visitorClass() + ".java"));
    builder.setPackageName(JJTreeGlobals.packageName);

    builder.println("public interface " + visitorClass());
    builder.println("{");
    builder.println("  public ", JJTreeOptions.getVisitorReturnType(), " visit(SimpleNode node, ", argumentType,
        " data)", ve, ";");

    if (JJTreeOptions.getMulti()) {
      for (int i = 0; i < nodeNames.size(); ++i) {
        String n = nodeNames.get(i);
        if (!n.equals("void")) {
          String nodeType = JJTreeOptions.getNodePrefix() + n;
          builder.println("  public ", JJTreeOptions.getVisitorReturnType(), " ", getVisitMethodName(nodeType), "(",
              nodeType, " node, ", argumentType + " data)", ve, ";");
        }
      }
    }
    builder.println("}");

    builder.build();
  }

  static String defaultVisitorClass() {
    return JJTreeGlobals.parserName + "DefaultVisitor";
  }

  private static String getVisitMethodName(String className) {
    StringBuffer sb = new StringBuffer("visit");
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

    String ve = mergeVisitorException();
    String ret = JJTreeOptions.getVisitorReturnType();
    String argumentType = "Object";
    if (!JJTreeOptions.getVisitorDataType().equals("")) {
      argumentType = JJTreeOptions.getVisitorDataType();
    }
    List<String> nodeNames = ASTNodeDescriptor.getNodeNames();

    JavaCodeGenBuilder builder = JavaCodeGenBuilder.of(Collections.emptyMap());
    builder.setFile(new File(JJTreeOptions.getJJTreeOutputDirectory(), defaultVisitorClass() + ".java"));
    builder.setPackageName(JJTreeGlobals.packageName);

    builder.println("public class ", defaultVisitorClass(), " implements ", visitorClass(), "{");
    builder.println("  public ", ret, " defaultVisit(SimpleNode node, ", argumentType, " data)", ve, "{");
    builder.println("    node.childrenAccept(this, data);");
    builder.println("    return", (ret.trim().equals("void") ? "" : " data"), ";");
    builder.println("  }");
    builder.println("  public ", ret, " visit(SimpleNode node, ", argumentType, " data)", ve, "{");
    builder.println("    ", (ret.trim().equals("void") ? "" : "return "), "defaultVisit(node, data);");
    builder.println("  }");

    if (JJTreeOptions.getMulti()) {
      for (int i = 0; i < nodeNames.size(); ++i) {
        String n = nodeNames.get(i);
        if (n.equals("void")) {
          continue;
        }
        String nodeType = JJTreeOptions.getNodePrefix() + n;
        builder.println("  public ", ret, " ", getVisitMethodName(nodeType), "(", nodeType, " node, ", argumentType,
            " data)", ve, "{");
        builder.println("    ", (ret.trim().equals("void") ? "" : "return "), "defaultVisit(node, data);");
        builder.println("  }");
      }
    }
    builder.println("}");
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

    JavaCodeGenBuilder builder = JavaCodeGenBuilder.of(options);
    builder.setFile(new File(JJTreeOptions.getJJTreeOutputDirectory(), "Node.java"));
    builder.setPackageName(JJTreeGlobals.packageName);
    builder.printTemplate("/templates/Node.template");
    builder.build();

    builder = JavaCodeGenBuilder.of(options);
    builder.setFile(new File(JJTreeOptions.getJJTreeOutputDirectory(), "SimpleNode.java"));
    builder.setPackageName(JJTreeGlobals.packageName);
    builder.printTemplate("/templates/SimpleNode.template");
    builder.build();
  }

  static void generateOutputFiles() throws IOException {
    generateDefaultNode();
    if (!nodesToBuild.isEmpty())
      generateTreeNodes();
    generateTreeConstants();
    generateVisitor();
    generateDefaultVisitor();
  }
}
