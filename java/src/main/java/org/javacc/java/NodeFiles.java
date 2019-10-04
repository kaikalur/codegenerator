package org.javacc.java;

import org.javacc.Version;
import org.javacc.jjtree.ASTNodeDescriptor;
import org.javacc.jjtree.IO;
import org.javacc.jjtree.JJTreeGlobals;
import org.javacc.jjtree.JJTreeOptions;
import org.javacc.parser.Options;
import org.javacc.parser.OutputFile;
import org.javacc.utils.OutputFileGenerator;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

final class NodeFiles {
  private NodeFiles() {}

  /**
   * ID of the latest version (of JJTree) in which one of the Node classes
   * was modified.
   */
  static final String nodeVersion = Version.majorDotMinor;

  static Set<String>  nodesGenerated = new HashSet<>();

  static void generateNodeType(String nodeType)
  {
    File file = new File(JJTreeOptions.getJJTreeOutputDirectory(), nodeType + ".java");

    if (nodeType.equals("Node")) {} else if (nodeType.equals("SimpleNode")) {
      NodeFiles.generateNodeType("Node");
    } else {
      NodeFiles.generateNodeType("SimpleNode");
    }

    /*
     * Only build the node file if we're dealing with Node.java, or the NODE_BUILD_FILES option is
     * set.
     */
    if (!(nodeType.equals("Node") || JJTreeOptions.getBuildNodeFiles())) {
      return;
    }

    if (file.exists() && NodeFiles.nodesGenerated.contains(file.getName())) {
      return;
    }

    try {
      String[] options = new String[] {"MULTI", "NODE_USES_PARSER", "VISITOR", "TRACK_TOKENS", "NODE_PREFIX", "NODE_EXTENDS", "NODE_FACTORY", Options.USEROPTION__SUPPORT_CLASS_VISIBILITY_PUBLIC};

      OutputFile outputFile = new OutputFile(file, NodeFiles.nodeVersion, options);
      outputFile.setToolName("JJTree");
      PrintWriter pw = outputFile.getPrintWriter();

      NodeFiles.nodesGenerated.add(file.getName());

      if (!outputFile.needToWrite) {
        return;
      }

      if (nodeType.equals("Node")) {
        NodeFiles.generateNode(pw);
      } else if (nodeType.equals("SimpleNode")) {
        NodeFiles.generateSimpleNode(pw);
      } else {
        NodeFiles.generateMULTINode(pw, nodeType);
      }

      pw.close();
    } catch (IOException e) {
      throw new Error(e.toString());
    }
  }

  static void generatePrologue(PrintWriter pw)
  {
    // Output the node's package name. JJTreeGlobals.nodePackageName
    // will be the value of NODE_PACKAGE in OPTIONS; if that wasn't set it
    // will default to the parser's package name.
    // If the package names are different we will need to import classes
    // from the parser's package.
    if (Options.getNamespace() != null) {
      pw.println("package " + Options.getNamespace() +";");
    }
    
    if (!JJTreeGlobals.nodePackageName.equals("")) {
      pw.println("package " + JJTreeGlobals.nodePackageName + ";");
      pw.println();
      if (!JJTreeGlobals.nodePackageName.equals(JJTreeGlobals.packageName)) {
        pw.println("import " + JJTreeGlobals.packageName + ".*;");
        pw.println();
      }
    }
  }


  static String nodeConstants()
  {
    return JJTreeGlobals.parserName + "TreeConstants";
  }

  static void generateTreeConstants()
 {
    String name = NodeFiles.nodeConstants();
    File file = new File(JJTreeOptions.getJJTreeOutputDirectory(), name + ".java");

    try {
      OutputFile outputFile = new OutputFile(file);
      PrintWriter pw = outputFile.getPrintWriter();

      List<String> nodeIds = ASTNodeDescriptor.getNodeIds();
      List<String> nodeNames = ASTNodeDescriptor.getNodeNames();

      NodeFiles.generatePrologue(pw);
      pw.println("public interface " + name);
      pw.println("{");

      for (int i = 0; i < nodeIds.size(); ++i) {
        String n = nodeIds.get(i);
        pw.println("  public final int " + n + " = " + i + ";");
      }

      pw.println();
      pw.println();

      pw.println("  public String[] jjtNodeName = {");
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

    String name = NodeFiles.visitorClass();
    File file = new File(JJTreeOptions.getJJTreeOutputDirectory(), name + ".java");

    try {
      OutputFile outputFile = new OutputFile(file);
      PrintWriter pw = outputFile.getPrintWriter();

      List<String> nodeNames = ASTNodeDescriptor.getNodeNames();

      NodeFiles.generatePrologue(pw);
      pw.println("public interface " + name);
      pw.println("{");

      String ve = NodeFiles.mergeVisitorException();

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
          pw.println("  public " + JJTreeOptions.getVisitorReturnType() + " " + NodeFiles.getVisitMethodName(nodeType) +
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
    if (Options.booleanValue("VISITOR_METHOD_NAME_INCLUDES_TYPE_NAME")) {
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

    String className = NodeFiles.defaultVisitorClass();
    File file = new File(JJTreeOptions.getJJTreeOutputDirectory(), className + ".java");

    try {
      PrintWriter pw = new OutputFile(file).getPrintWriter();

      List<String> nodeNames = ASTNodeDescriptor.getNodeNames();

      NodeFiles.generatePrologue(pw);
      pw.println("public class " + className + " implements " + NodeFiles.visitorClass() + "{");

      String ve = NodeFiles.mergeVisitorException();

      String argumentType = "Object";
      if (!JJTreeOptions.getVisitorDataType().equals("")) {
        argumentType = JJTreeOptions.getVisitorDataType();
      }

      String ret = JJTreeOptions.getVisitorReturnType();
      pw.println("  public " + ret + " defaultVisit(SimpleNode node, " + argumentType + " data)" + ve + "{");
      pw.println("    node.childrenAccept(this, data);");
      pw.println("    return" + (ret.trim().equals("void") ? "" : " data") + ";");
      pw.println("  }");

      pw.println("  public " + ret + " visit(SimpleNode node, " + argumentType + " data)" + ve + "{");
      pw.println("    " + (ret.trim().equals("void") ? "" : "return ") + "defaultVisit(node, data);");
      pw.println("  }");

      if (JJTreeOptions.getMulti()) {
        for (int i = 0; i < nodeNames.size(); ++i) {
          String n = nodeNames.get(i);
          if (n.equals("void")) {
            continue;
          }
          String nodeType = JJTreeOptions.getNodePrefix() + n;
          pw.println("  public " + ret + " " + NodeFiles.getVisitMethodName(nodeType) +
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


  private static void generateNode(PrintWriter pw) throws IOException {

    NodeFiles.generatePrologue(pw);

    Map<String, Object> options = new HashMap<>(Options.getOptions());
    options.put(Options.NONUSER_OPTION__PARSER_NAME, JJTreeGlobals.parserName);

    OutputFileGenerator generator = new OutputFileGenerator("/templates/Node.template", options);

    generator.generate(pw);

    pw.close();
  }


  private static void generateSimpleNode(PrintWriter pw) throws IOException {
    NodeFiles.generatePrologue(pw);

    Map<String, Object> options = new HashMap<>(Options.getOptions());
    options.put(Options.NONUSER_OPTION__PARSER_NAME, JJTreeGlobals.parserName);
    options.put("VISITOR_RETURN_TYPE_VOID", Boolean.valueOf(JJTreeOptions.getVisitorReturnType().equals("void")));

    OutputFileGenerator generator = new OutputFileGenerator(
        "/templates/SimpleNode.template", options);

    generator.generate(pw);

    pw.close();
  }


  private static void generateMULTINode(PrintWriter pw, String nodeType) throws IOException
  {
    NodeFiles.generatePrologue(pw);

    Map<String, Object> options = new HashMap<>(Options.getOptions());
    options.put(Options.NONUSER_OPTION__PARSER_NAME, JJTreeGlobals.parserName);
    options.put("NODE_TYPE", nodeType);
    options.put("VISITOR_RETURN_TYPE_VOID", Boolean.valueOf(JJTreeOptions.getVisitorReturnType().equals("void")));

    OutputFileGenerator generator = new OutputFileGenerator(
        "/templates/MultiNode.template", options);

    generator.generate(pw);
  }

  static void generateOutputFiles() throws IOException {
    generateTreeConstants();
    generateVisitor();
    generateDefaultVisitor();
    JJTreeState.generateTreeState();
  }

}
