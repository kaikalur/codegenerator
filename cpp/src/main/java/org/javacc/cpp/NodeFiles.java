
package org.javacc.cpp;

import org.javacc.Version;
import org.javacc.jjtree.ASTNodeDescriptor;
import org.javacc.jjtree.JJTreeGlobals;
import org.javacc.jjtree.JJTreeOptions;
import org.javacc.parser.CodeGeneratorSettings;
import org.javacc.parser.JavaCCContext;
import org.javacc.parser.Options;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

final class NodeFiles {

  private NodeFiles() {}

  private static List<String> headersForJJTreeH = new ArrayList<>();
  /**
   * ID of the latest version (of JJTree) in which one of the Node classes was
   * modified.
   */
  private static final String nodeVersion       = Version.version;

  private static Set<String>  nodesToBuild      = new HashSet<>();

  static void generateNodeType(String nodeType) {
    if (!nodeType.equals("Tree") && !nodeType.equals("Node")) {
      NodeFiles.nodesToBuild.add(nodeType);
    }
  }

  private static String nodeIncludeFile() {
    return new File(JJTreeOptions.getJJTreeOutputDirectory(), "Tree.h").getAbsolutePath();
  }

  private static String simpleNodeCodeFile() {
    return new File(JJTreeOptions.getJJTreeOutputDirectory(), "Node.cc").getAbsolutePath();
  }

  private static String jjtreeIncludeFile() {
    return new File(JJTreeOptions.getJJTreeOutputDirectory(), JJTreeGlobals.parserName + "Tree.h").getAbsolutePath();
  }

  private static String jjtreeASTNodeImplFile(String s) {
    return new File(JJTreeOptions.getASTNodeDirectory(), s + ".cc").getAbsolutePath();
  }

  private static String jjtreeImplFile(String s) {
    return new File(JJTreeOptions.getJJTreeOutputDirectory(), s + ".cc").getAbsolutePath();
  }

  private static String visitorIncludeFile() {
    String name = NodeFiles.visitorClass();
    return new File(JJTreeOptions.getJJTreeOutputDirectory(), name + ".h").getAbsolutePath();
  }

  static void generateOutputFiles(JavaCCContext context) throws IOException {
    NodeFiles.generateNodeHeader(context);
    NodeFiles.generateSimpleNode(context);
    NodeFiles.generateOneTree(context, false);
    NodeFiles.generateMultiTree(context);
    NodeFiles.generateTreeConstants(context);
    NodeFiles.generateVisitors(context);
  }

  private static void generateNodeHeader(JavaCCContext context) {
    CodeGeneratorSettings optionMap = CodeGeneratorSettings.of(Options.getOptions());
    optionMap.set("PARSER_NAME", JJTreeGlobals.parserName);
    optionMap.set("VISITOR_RETURN_TYPE", NodeFiles.getVisitorReturnType());
    optionMap.set("VISITOR_DATA_TYPE", NodeFiles.getVisitorArgumentType());
    optionMap.set("VISITOR_RETURN_TYPE_VOID", Boolean.valueOf(NodeFiles.getVisitorReturnType().equals("void")));

    try (CppCodeBuilder builder = CppCodeBuilder.ofHeader(context, optionMap)) {
      builder.setFile(new File(NodeFiles.nodeIncludeFile()));
      builder.setVersion(NodeFiles.nodeVersion).addTools(JJTreeGlobals.toolName);
      builder.addOption("MULTI", "NODE_USES_PARSER", "VISITOR", "TRACK_TOKENS", "NODE_PREFIX", "NODE_EXTENDS",
          "NODE_FACTORY", "SUPPORT_CLASS_VISIBILITY_PUBLIC");

      builder.printTemplate("/templates/cpp/Tree.h.template");
    } catch (IOException e) {
      throw new Error(e.toString());
    }
  }


  private static void generateSimpleNode(JavaCCContext context) {
    CodeGeneratorSettings optionMap = CodeGeneratorSettings.of(Options.getOptions());
    optionMap.set(Options.NONUSER_OPTION__PARSER_NAME, JJTreeGlobals.parserName);
    optionMap.set("VISITOR_RETURN_TYPE", NodeFiles.getVisitorReturnType());
    optionMap.set("VISITOR_DATA_TYPE", NodeFiles.getVisitorArgumentType());
    optionMap.set("VISITOR_RETURN_TYPE_VOID", Boolean.valueOf(NodeFiles.getVisitorReturnType().equals("void")));

    try (CppCodeBuilder builder = CppCodeBuilder.of(context, optionMap)) {
      builder.setFile(new File(NodeFiles.simpleNodeCodeFile()));
      builder.setVersion(NodeFiles.nodeVersion).addTools(JJTreeGlobals.toolName);
      builder.addOption("MULTI", "NODE_USES_PARSER", "VISITOR", "TRACK_TOKENS", "NODE_PREFIX", "NODE_EXTENDS",
          "NODE_FACTORY", Options.USEROPTION__SUPPORT_CLASS_VISIBILITY_PUBLIC);

      builder.printTemplate("/templates/cpp/Node.cc.template");
      builder.switchToIncludeFile();
      builder.printTemplate("/templates/cpp/Node.h.template");
    } catch (IOException e) {
      throw new Error(e.toString());
    }
  }

  private static void generateOneTree(JavaCCContext context,boolean generateOneTreeImpl) {
    CodeGeneratorSettings optionMap = CodeGeneratorSettings.of(Options.getOptions());
    optionMap.set("PARSER_NAME", JJTreeGlobals.parserName);
    optionMap.set("VISITOR_RETURN_TYPE", NodeFiles.getVisitorReturnType());
    optionMap.set("VISITOR_DATA_TYPE", NodeFiles.getVisitorArgumentType());
    optionMap.set("VISITOR_RETURN_TYPE_VOID", Boolean.valueOf(NodeFiles.getVisitorReturnType().equals("void")));

    try (CppCodeBuilder builder =
        generateOneTreeImpl ? CppCodeBuilder.of(context, optionMap) : CppCodeBuilder.ofHeader(context, optionMap)) {
      builder.setFile(new File(NodeFiles.jjtreeIncludeFile()));
      builder.setVersion(NodeFiles.nodeVersion).addTools(JJTreeGlobals.toolName);
      builder.addOption("MULTI", "NODE_USES_PARSER", "VISITOR", "TRACK_TOKENS", "NODE_PREFIX", "NODE_EXTENDS",
          "NODE_FACTORY", "SUPPORT_CLASS_VISIBILITY_PUBLIC");

      builder.switchToIncludeFile();
      builder.println("#include \"Node.h\"");
      for (String s : NodeFiles.nodesToBuild) {
        builder.println("#include \"" + s + ".h\"");
        if (generateOneTreeImpl) {
          builder.switchToMainFile();
          builder.printTemplate("/templates/cpp/MultiNode.cc.template",
              CodeGeneratorSettings.create().set("NODE_TYPE", s));
          builder.switchToIncludeFile();
        }
      }
    } catch (IOException e) {
      throw new Error(e.toString());
    }
  }

  private static void generateMultiTree(JavaCCContext context) {
    for (String node : NodeFiles.nodesToBuild) {
      if (new File(NodeFiles.jjtreeASTNodeImplFile(node)).exists()) {
        continue;
      }

      CodeGeneratorSettings optionMap = CodeGeneratorSettings.of(Options.getOptions());
      optionMap.set(Options.NONUSER_OPTION__PARSER_NAME, JJTreeGlobals.parserName);
      optionMap.set("VISITOR_RETURN_TYPE", NodeFiles.getVisitorReturnType());
      optionMap.set("VISITOR_DATA_TYPE", NodeFiles.getVisitorArgumentType());
      optionMap.set("VISITOR_RETURN_TYPE_VOID", Boolean.valueOf(NodeFiles.getVisitorReturnType().equals("void")));
      optionMap.set("NODE_TYPE", node);

      try (CppCodeBuilder builder = CppCodeBuilder.of(context, optionMap)) {
        builder.setFile(new File(NodeFiles.jjtreeImplFile(node)));
        builder.setVersion(NodeFiles.nodeVersion).addTools(JJTreeGlobals.toolName);
        builder.addOption("MULTI", "NODE_USES_PARSER", "VISITOR", "TRACK_TOKENS", "NODE_PREFIX", "NODE_EXTENDS",
            "NODE_FACTORY", Options.USEROPTION__SUPPORT_CLASS_VISIBILITY_PUBLIC);

        builder.printTemplate("/templates/cpp/MultiNode.cc.template");
        builder.switchToIncludeFile();
        builder.printTemplate("/templates/cpp/MultiNode.h.template");
      } catch (IOException e) {
        throw new Error(e.toString());
      }
    }
  }

  private static String nodeConstants() {
    return JJTreeGlobals.parserName + "TreeConstants";
  }

  private static void generateTreeConstants(JavaCCContext context) {
    List<String> nodeIds = ASTNodeDescriptor.getNodeIds();
    List<String> nodeNames = ASTNodeDescriptor.getNodeNames();

    File file = new File(JJTreeOptions.getJJTreeOutputDirectory(), NodeFiles.nodeConstants() + ".h");
    NodeFiles.headersForJJTreeH.add(file.getName());

    try (CppCodeBuilder builder = CppCodeBuilder.ofHeader(context, CodeGeneratorSettings.create())) {
      builder.setFile(file);

      builder.println("\n#include \"JavaCC.h\"");

      boolean hasNamespace = Options.stringValue("NAMESPACE").length() > 0;
      if (hasNamespace) {
        builder.println("namespace " + Options.stringValue("NAMESPACE_OPEN"));
      }

      builder.println("enum {");
      for (int i = 0; i < nodeIds.size(); ++i) {
        String n = nodeIds.get(i);
        builder.println("  " + n + " = " + i + ",");
      }

      builder.println("};");
      builder.println();

      for (int i = 0; i < nodeNames.size(); ++i) {
        builder.println("  static JJChar jjtNodeName_arr_", i, "[] = ");
        builder.printCharArray(nodeNames.get(i));
        builder.println(";");
      }
      builder.println("  static JJString jjtNodeName[] = {");
      for (int i = 0; i < nodeNames.size(); i++) {
        builder.println("jjtNodeName_arr_", i, ", ");
      }
      builder.println("  };");

      if (hasNamespace) {
        builder.println(Options.stringValue("NAMESPACE_CLOSE"));
      }
    } catch (IOException e) {
      throw new Error(e);
    }
  }

  private static String visitorClass() {
    return JJTreeGlobals.parserName + "Visitor";
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

  private static String getVisitorArgumentType() {
    String ret = Options.stringValue("VISITOR_DATA_TYPE");
    return (ret == null) || ret.equals("") || ret.equals("Object") ? "void *" : ret;
  }

  private static String getVisitorReturnType() {
    String ret = Options.stringValue("VISITOR_RETURN_TYPE");
    return (ret == null) || ret.equals("") || ret.equals("Object") ? "void" : ret;
  }

  private static void generateVisitors(JavaCCContext context) {
    if (!JJTreeOptions.getVisitor()) {
      return;
    }

    try (CppCodeBuilder builder = CppCodeBuilder.ofHeader(context, CodeGeneratorSettings.create())) {
      builder.setFile(new File(NodeFiles.visitorIncludeFile()));

      builder.println("\n#include \"JavaCC.h\"");
      builder.println("#include \"" + JJTreeGlobals.parserName + "Tree.h" + "\"");

      boolean hasNamespace = Options.stringValue("NAMESPACE").length() > 0;
      if (hasNamespace) {
        builder.println("namespace " + Options.stringValue("NAMESPACE_OPEN"));
      }

      NodeFiles.generateVisitorInterface(builder);
      NodeFiles.generateDefaultVisitor(builder);

      if (hasNamespace) {
        builder.println(Options.stringValue("NAMESPACE_CLOSE"));
      }
    } catch (IOException e) {
      throw new Error(e);
    }
  }

  private static void generateVisitorInterface(CppCodeBuilder builder) {
    String name = NodeFiles.visitorClass();
    List<String> nodeNames = ASTNodeDescriptor.getNodeNames();

    builder.println("class " + name);
    builder.println("{");

    String argumentType = NodeFiles.getVisitorArgumentType();
    String returnType = NodeFiles.getVisitorReturnType();
    if (!JJTreeOptions.getVisitorDataType().equals("")) {
      argumentType = JJTreeOptions.getVisitorDataType();
    }
    builder.println("public:");

    builder.println("  virtual " + returnType + " visit(const Node *node, " + argumentType + " data) = 0;");
    if (JJTreeOptions.getMulti()) {
      for (String n : nodeNames) {
        if (n.equals("void")) {
          continue;
        }
        String nodeType = JJTreeOptions.getNodePrefix() + n;
        builder.println("  virtual " + returnType + " " + NodeFiles.getVisitMethodName(nodeType) + "(const " + nodeType
            + " *node, " + argumentType + " data) = 0;");
      }
    }

    builder.println("  virtual ~" + name + "() { }");
    builder.println("};");
  }

  private static String defaultVisitorClass() {
    return JJTreeGlobals.parserName + "DefaultVisitor";
  }

  private static void generateDefaultVisitor(CppCodeBuilder builder) {
    String className = NodeFiles.defaultVisitorClass();
    List<String> nodeNames = ASTNodeDescriptor.getNodeNames();

    builder.println("class " + className + " : public " + NodeFiles.visitorClass() + " {");

    String argumentType = NodeFiles.getVisitorArgumentType();
    String ret = NodeFiles.getVisitorReturnType();

    builder.println("public:");
    builder.println("  virtual " + ret + " defaultVisit(const Node *node, " + argumentType + " data) = 0;");

    builder.println("  virtual " + ret + " visit(const Node *node, " + argumentType + " data) {");
    builder.println("    " + (ret.trim().equals("void") ? "" : "return ") + "defaultVisit(node, data);");
    builder.println("  }");

    if (JJTreeOptions.getMulti()) {
      for (String n : nodeNames) {
        if (n.equals("void")) {
          continue;
        }
        String nodeType = JJTreeOptions.getNodePrefix() + n;
        builder.println("  virtual " + ret + " " + NodeFiles.getVisitMethodName(nodeType) + "(const " + nodeType
            + " *node, " + argumentType + " data) {");
        builder.println("    " + (ret.trim().equals("void") ? "" : "return ") + "defaultVisit(node, data);");
        builder.println("  }");
      }
    }
    builder.println("  ~" + className + "() { }");
    builder.println("};");
  }
}
