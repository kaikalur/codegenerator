
package org.javacc.java;

import org.javacc.Version;
import org.javacc.jjtree.ASTNodeDescriptor;
import org.javacc.jjtree.JJTreeGlobals;
import org.javacc.jjtree.JJTreeOptions;
import org.javacc.parser.CodeGeneratorSettings;
import org.javacc.parser.Options;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

final class NodeFiles {

  private NodeFiles() {}

  /**
   * ID of the latest version (of JJTree) in which one of the Node classes was
   * modified.
   */
  private static final String nodeVersion  = Version.version;

  private static Set<String>  nodesToBuild = new HashSet<>();

  static void generateNodeType(String nodeType) {
    if (!nodeType.equals("Node") && !nodeType.equals("SimpleNode")) {
      NodeFiles.nodesToBuild.add(nodeType);
    }
  }

  private static void generateTreeNodes() {
    try (JavaCodeBuilder builder = JavaCodeBuilder.of(CodeGeneratorSettings.create())) {
      builder.setFile(new File(JJTreeOptions.getJJTreeOutputDirectory(), JJTreeGlobals.parserName + "Tree.java"));
      builder.addTools(JJTreeGlobals.toolName).setVersion(NodeFiles.nodeVersion);
      builder.addOption("MULTI", "NODE_USES_PARSER", "VISITOR", "TRACK_TOKENS", "NODE_PREFIX", "NODE_EXTENDS",
          "NODE_FACTORY", Options.USEROPTION__SUPPORT_CLASS_VISIBILITY_PUBLIC);
      builder.setPackageName(JJTreeGlobals.packageName);

      for (String node : NodeFiles.nodesToBuild) {
        File file = new File(JJTreeOptions.getASTNodeDirectory(), node + ".java");
        if (file.exists()) {
          continue;
        }

        NodeFiles.generateMULTINode(builder, node);
      }
    } catch (IOException e) {
      throw new Error(e.toString());
    }
  }

  private static void generateMULTINode(JavaCodeBuilder builder, String nodeType) throws IOException {
    CodeGeneratorSettings options = CodeGeneratorSettings.of(Options.getOptions());
    options.set(Options.NONUSER_OPTION__PARSER_NAME, JJTreeGlobals.parserName);
    options.set("NODE_TYPE", nodeType);
    options.set("VISITOR_RETURN_TYPE_VOID", Boolean.valueOf(JJTreeOptions.getVisitorReturnType().equals("void")));
    builder.printTemplate("/templates/MultiNode.template", options);
  }

  private static void generateTreeConstants() {
    List<String> nodeIds = ASTNodeDescriptor.getNodeIds();
    List<String> nodeNames = ASTNodeDescriptor.getNodeNames();

    try (JavaCodeBuilder builder = JavaCodeBuilder.of(CodeGeneratorSettings.create())) {
      builder.setFile(new File(JJTreeOptions.getJJTreeOutputDirectory(), JavaTemplates.nodeConstants() + ".java"));
      builder.setPackageName(JJTreeGlobals.packageName);

      builder.println("public interface " + JavaTemplates.nodeConstants());
      builder.println("{");

      for (int i = 0; i < nodeIds.size(); ++i) {
        builder.println("  public final int ", nodeIds.get(i), " = ", i, ";");
      }

      builder.println();
      builder.println();

      builder.println("  public static String[] jjtNodeName = {");
      for (String nodeName : nodeNames) {
        builder.println("    \"", nodeName, "\",");
      }
      builder.println("  };");
      builder.println("}");
    } catch (IOException e) {
      throw new Error(e.toString());
    }
  }

  private static void generateVisitor() {
    if (!JJTreeOptions.getVisitor()) {
      return;
    }

    List<String> nodeNames = ASTNodeDescriptor.getNodeNames();
    String ve = NodeFiles.mergeVisitorException();

    String argumentType = "Object";
    if (!JJTreeOptions.getVisitorDataType().equals("")) {
      argumentType = JJTreeOptions.getVisitorDataType();
    }

    try (JavaCodeBuilder builder = JavaCodeBuilder.of(CodeGeneratorSettings.create())) {
      builder.setFile(new File(JJTreeOptions.getJJTreeOutputDirectory(), JavaTemplates.visitorClass() + ".java"));
      builder.setPackageName(JJTreeGlobals.packageName);

      builder.println("public interface " + JavaTemplates.visitorClass());
      builder.println("{");
      builder.println("  public ", JJTreeOptions.getVisitorReturnType(), " visit(SimpleNode node, ", argumentType,
          " data)", ve, ";");

      if (JJTreeOptions.getMulti()) {
        for (String n : nodeNames) {
          if (!n.equals("void")) {
            String nodeType = JJTreeOptions.getNodePrefix() + n;
            builder.println("  public ", JJTreeOptions.getVisitorReturnType(), " ",
                NodeFiles.getVisitMethodName(nodeType), "(", nodeType, " node, ", argumentType + " data)", ve, ";");
          }
        }
      }
      builder.println("}");
    } catch (IOException e) {
      throw new Error(e.toString());
    }
  }

  private static String getVisitMethodName(String className) {
    StringBuffer sb = new StringBuffer("visit");
    if (Options.booleanValue("VISITOR_METHOD_NAME_INCLUDES_TYPE_NAME")) {
      sb.append(Character.toUpperCase(className.charAt(0)));
      for (int i = 1; i < className.length(); i++) {
        sb.append(className.charAt(i));
      }
    }

    return sb.toString();
  }

  private static void generateDefaultVisitor() {
    if (!JJTreeOptions.getVisitor()) {
      return;
    }

    String ve = NodeFiles.mergeVisitorException();
    String ret = JJTreeOptions.getVisitorReturnType();
    String argumentType = "Object";
    if (!JJTreeOptions.getVisitorDataType().equals("")) {
      argumentType = JJTreeOptions.getVisitorDataType();
    }
    List<String> nodeNames = ASTNodeDescriptor.getNodeNames();

    try (JavaCodeBuilder builder = JavaCodeBuilder.of(CodeGeneratorSettings.create())) {
      builder
      .setFile(new File(JJTreeOptions.getJJTreeOutputDirectory(), JavaTemplates.defaultVisitorClass() + ".java"));
      builder.setPackageName(JJTreeGlobals.packageName);

      builder.println("public class ", JavaTemplates.defaultVisitorClass(), " implements ",
          JavaTemplates.visitorClass(), "{");
      builder.println("  public ", ret, " defaultVisit(SimpleNode node, ", argumentType, " data)", ve, "{");
      builder.println("    node.childrenAccept(this, data);");
      builder.println("    return", (ret.trim().equals("void") ? "" : " data"), ";");
      builder.println("  }");
      builder.println("  public ", ret, " visit(SimpleNode node, ", argumentType, " data)", ve, "{");
      builder.println("    ", (ret.trim().equals("void") ? "" : "return "), "defaultVisit(node, data);");
      builder.println("  }");

      if (JJTreeOptions.getMulti()) {
        for (String n : nodeNames) {
          if (n.equals("void")) {
            continue;
          }
          String nodeType = JJTreeOptions.getNodePrefix() + n;
          builder.println("  public ", ret, " ", NodeFiles.getVisitMethodName(nodeType), "(", nodeType, " node, ",
              argumentType, " data)", ve, "{");
          builder.println("    ", (ret.trim().equals("void") ? "" : "return "), "defaultVisit(node, data);");
          builder.println("  }");
        }
      }
      builder.println("}");
    } catch (IOException e) {
      throw new Error(e.toString());
    }
  }

  private static String mergeVisitorException() {
    String ve = JJTreeOptions.getVisitorException();
    if (!"".equals(ve)) {
      ve = " throws " + ve;
    }
    return ve;
  }

  private static void generateDefaultNode() throws IOException {
    CodeGeneratorSettings options = CodeGeneratorSettings.of(Options.getOptions());
    options.set(Options.NONUSER_OPTION__PARSER_NAME, JJTreeGlobals.parserName);
    options.set("VISITOR_RETURN_TYPE_VOID", Boolean.valueOf(JJTreeOptions.getVisitorReturnType().equals("void")));

    try (JavaCodeBuilder builder = JavaCodeBuilder.of(options)) {
      builder.setFile(new File(JJTreeOptions.getJJTreeOutputDirectory(), "Node.java"));
      builder.setPackageName(JJTreeGlobals.packageName);
      builder.printTemplate("/templates/Node.template");
    }

    try (JavaCodeBuilder builder = JavaCodeBuilder.of(options)) {
      builder.setFile(new File(JJTreeOptions.getJJTreeOutputDirectory(), "SimpleNode.java"));
      builder.setPackageName(JJTreeGlobals.packageName);
      builder.printTemplate("/templates/SimpleNode.template");
    }
  }

  static void generateOutputFiles() throws IOException {
    NodeFiles.generateDefaultNode();
    if (!NodeFiles.nodesToBuild.isEmpty()) {
      NodeFiles.generateTreeNodes();
    }
    NodeFiles.generateTreeConstants();
    NodeFiles.generateVisitor();
    NodeFiles.generateDefaultVisitor();
  }
}
