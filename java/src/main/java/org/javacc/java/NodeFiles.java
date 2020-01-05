package org.javacc.java;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.javacc.Version;
import org.javacc.parser.Options;
import org.javacc.utils.OutputFile;
import org.javacc.utils.TemplateGenerator;
import org.javacc.jjtree.*;

final class NodeFiles {
  private NodeFiles() {}

  /**
   * ID of the latest version (of JJTree) in which one of the Node classes
   * was modified.
   */
  static final String nodeVersion = Version.majorDotMinor;

  private static Set<String> nodesToBuild = new HashSet<>();

  static void generateNodeType(String nodeType)
  {
    if (!nodeType.equals("Node") && !nodeType.equals("SimpleNode")) {
      nodesToBuild.add(nodeType);
    }
  }

  private static void generateTreeNodes() {
    File file = new File(JJTreeOptions.getJJTreeOutputDirectory(), JJTreeGlobals.parserName + "Tree.java");
    try {
      String[] options = new String[] {"MULTI", "NODE_USES_PARSER", "VISITOR", "TRACK_TOKENS", "NODE_PREFIX", "NODE_EXTENDS", "NODE_FACTORY", Options.USEROPTION__SUPPORT_CLASS_VISIBILITY_PUBLIC};

      OutputFile outputFile = new OutputFile(file, JJTreeGlobals.toolName, nodeVersion, options);
      PrintWriter pw = outputFile.getPrintWriter();

      if(JJTreeGlobals.packageName.length() > 0) {
        pw.println("package " + JJTreeGlobals.packageName + ";");
      }

      for (String node: nodesToBuild) {
		file = new File(JJTreeOptions.getASTNodeDirectory(), node + ".java");
		if (file.exists())
			continue;
        generateMULTINode(pw, node);
      }

      pw.close();
    } catch (IOException e) {
      throw new Error(e.toString());
    }
  }

  static void generatePrologue(PrintWriter pw)
  {
    if(JJTreeGlobals.packageName.length() > 0) {
      pw.write("package " + JJTreeGlobals.packageName + ";\n");
    }
  }


  static String nodeConstants()
  {
    return JJTreeGlobals.parserName + "TreeConstants";
  }

  static void generateTreeConstants()
  {
    String name = nodeConstants();
    File file = new File(JJTreeOptions.getJJTreeOutputDirectory(), name + ".java");

    try {
      OutputFile outputFile = new OutputFile(file);
      PrintWriter pw = outputFile.getPrintWriter();

      List<String> nodeIds = ASTNodeDescriptor.getNodeIds();
      List<String> nodeNames = ASTNodeDescriptor.getNodeNames();

      generatePrologue(pw);
      
      pw.println("public interface " + name);
      pw.println("{");

      for (int i = 0; i < nodeIds.size(); ++i) {
        String n = nodeIds.get(i);
        pw.println("  public final int " + n + " = " + i + ";");
      }

      pw.println();
      pw.println();

      pw.println("  public static String[] jjtNodeName = {");
      for (int i = 0; i < nodeNames.size(); ++i) {
        String n = nodeNames.get(i);
        pw.println("    \"" + n + "\",");
      }
      pw.println("  };");

      pw.println("}");
      pw.close();

    } catch (IOException e) {
      throw new Error(e.toString());
    }
  }


  static String visitorClass()
  {
    return JJTreeGlobals.parserName + "Visitor";
  }

  static void generateVisitor()
  {
    if (!JJTreeOptions.getVisitor()) {
      return;
    }

    String name = visitorClass();
    File file = new File(JJTreeOptions.getJJTreeOutputDirectory(), name + ".java");

    try {
      OutputFile outputFile = new OutputFile(file);
      PrintWriter pw = outputFile.getPrintWriter();

      List<String> nodeNames = ASTNodeDescriptor.getNodeNames();

      generatePrologue(pw);
      pw.println("public interface " + name);
      pw.println("{");

      String ve = mergeVisitorException();

      String argumentType = "Object";
      if (!JJTreeOptions.getVisitorDataType().equals("")) {
        argumentType = JJTreeOptions.getVisitorDataType();
      }

      pw.println("  public " + JJTreeOptions.getVisitorReturnType() + " visit(SimpleNode node, " + argumentType + " data)" +
          ve + ";");
      if (JJTreeOptions.getMulti()) {
        for (int i = 0; i < nodeNames.size(); ++i) {
          String n = nodeNames.get(i);
          if (n.equals("void")) {
            continue;
          }
          String nodeType = JJTreeOptions.getNodePrefix() + n;
          pw.println("  public " + JJTreeOptions.getVisitorReturnType() + " " + getVisitMethodName(nodeType) +
          "(" + nodeType +
              " node, " + argumentType + " data)" + ve + ";");
        }
      }
      pw.println("}");
      pw.close();

    } catch (IOException e) {
      throw new Error(e.toString());
    }
  }

  static String defaultVisitorClass()
  {
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

  static void generateDefaultVisitor()
  {
    if (!JJTreeOptions.getVisitor()) {
      return;
    }

    String className = defaultVisitorClass();
    File file = new File(JJTreeOptions.getJJTreeOutputDirectory(), className + ".java");

    try {
      PrintWriter pw = new OutputFile(file).getPrintWriter();

      List<String> nodeNames = ASTNodeDescriptor.getNodeNames();

      generatePrologue(pw);
      pw.println("public class " + className + " implements " + visitorClass() + "{");

      String ve = mergeVisitorException();

      String argumentType = "Object";
      if (!JJTreeOptions.getVisitorDataType().equals("")) {
        argumentType = JJTreeOptions.getVisitorDataType();
      }

      String ret = JJTreeOptions.getVisitorReturnType();
      pw.println("  public " + ret + " defaultVisit(SimpleNode node, " + argumentType + " data)" +
          ve + "{");
      pw.println("    node.childrenAccept(this, data);");
      pw.println("    return" + (ret.trim().equals("void") ? "" : " data") + ";");
      pw.println("  }");

      pw.println("  public " + ret + " visit(SimpleNode node, " + argumentType + " data)" +
          ve + "{");
      pw.println("    " + (ret.trim().equals("void") ? "" : "return ") + "defaultVisit(node, data);");
      pw.println("  }");

      if (JJTreeOptions.getMulti()) {
        for (int i = 0; i < nodeNames.size(); ++i) {
          String n = nodeNames.get(i);
          if (n.equals("void")) {
            continue;
          }
          String nodeType = JJTreeOptions.getNodePrefix() + n;
          pw.println("  public " + ret + " " + getVisitMethodName(nodeType) +
          "(" + nodeType +
              " node, " + argumentType + " data)" + ve + "{");
          pw.println("    " + (ret.trim().equals("void") ? "" : "return ") + "defaultVisit(node, data);");
          pw.println("  }");
        }
      }
      pw.println("}");
      pw.close();

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

  private static void generateDefaultNode() throws IOException
  {
    File file = new File(JJTreeOptions.getJJTreeOutputDirectory(), "Node.java");
    PrintWriter pw = new OutputFile(file).getPrintWriter();

    generatePrologue(pw);

    Map<String, Object> options = new HashMap<>(Options.getOptions());
    options.put(Options.NONUSER_OPTION__PARSER_NAME, JJTreeGlobals.parserName);
    options.put("VISITOR_RETURN_TYPE_VOID", Boolean.valueOf(JJTreeOptions.getVisitorReturnType().equals("void")));

    TemplateGenerator.generateTemplate(pw, "/templates/Node.template", options);

    pw.close();

    file = new File(JJTreeOptions.getJJTreeOutputDirectory(), "SimpleNode.java");
    pw = new OutputFile(file).getPrintWriter();
    
    generatePrologue(pw);

    TemplateGenerator.generateTemplate(pw, "/templates/SimpleNode.template", options);

    pw.close();
  }

  private static void generateMULTINode(PrintWriter pw, String nodeType) throws IOException
  {
    generatePrologue(pw);

    Map<String, Object> options = new HashMap<>(Options.getOptions());
    options.put(Options.NONUSER_OPTION__PARSER_NAME, JJTreeGlobals.parserName);
    options.put("NODE_TYPE", nodeType);
    options.put("VISITOR_RETURN_TYPE_VOID", Boolean.valueOf(JJTreeOptions.getVisitorReturnType().equals("void")));

    TemplateGenerator.generateTemplate(pw, "/templates/MultiNode.template", options);
  }

  static void generateOutputFiles() throws IOException {
    generateDefaultNode();
    if(!nodesToBuild.isEmpty())
      generateTreeNodes();
    generateTreeConstants();
    generateVisitor();
    generateDefaultVisitor();
  }
}
