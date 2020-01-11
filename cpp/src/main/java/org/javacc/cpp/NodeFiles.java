
package org.javacc.cpp;

import org.javacc.Version;
import org.javacc.jjtree.ASTNodeDescriptor;
import org.javacc.jjtree.JJTreeGlobals;
import org.javacc.jjtree.JJTreeOptions;
import org.javacc.parser.Options;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

final class NodeFiles {

  private NodeFiles() {}

  private static List<String> headersForJJTreeH = new ArrayList<>();
  /**
   * ID of the latest version (of JJTree) in which one of the Node classes was
   * modified.
   */
  static final String         nodeVersion       = Version.version;

  private static Set<String>  nodesToBuild      = new HashSet<>();

  static void generateNodeType(String nodeType) {
    if (!nodeType.equals("Node") && !nodeType.equals("SimpleNode")) {
      nodesToBuild.add(nodeType);
    }
  }

  public static String nodeIncludeFile() {
    return new File(JJTreeOptions.getJJTreeOutputDirectory(), "Node.h").getAbsolutePath();
  }

  public static String simpleNodeIncludeFile() {
    return new File(JJTreeOptions.getJJTreeOutputDirectory(), "SimpleNode.h").getAbsolutePath();
  }

  public static String simpleNodeCodeFile() {
    return new File(JJTreeOptions.getJJTreeOutputDirectory(), "SimpleNode.cc").getAbsolutePath();
  }

  public static String jjtreeIncludeFile() {
    return new File(JJTreeOptions.getJJTreeOutputDirectory(), JJTreeGlobals.parserName + "Tree.h").getAbsolutePath();
  }

  public static String jjtreeIncludeFile(String s) {
    return new File(JJTreeOptions.getJJTreeOutputDirectory(), s + ".h").getAbsolutePath();
  }

  public static String jjtreeASTNodeInterfaceFile(String s) {
    return new File(JJTreeOptions.getASTNodeDirectory(), s + ".h").getAbsolutePath();
  }

  public static String jjtreeASTNodeImplFile(String s) {
    return new File(JJTreeOptions.getASTNodeDirectory(), s + ".cc").getAbsolutePath();
  }

  public static String jjtreeImplFile() {
    return new File(JJTreeOptions.getJJTreeOutputDirectory(), JJTreeGlobals.parserName + "Tree.cc").getAbsolutePath();
  }

  public static String jjtreeImplFile(String s) {
    return new File(JJTreeOptions.getJJTreeOutputDirectory(), s + ".cc").getAbsolutePath();
  }

  private static String visitorIncludeFile() {
    String name = visitorClass();
    return new File(JJTreeOptions.getJJTreeOutputDirectory(), name + ".h").getAbsolutePath();
  }

  static void generateOutputFiles() throws IOException {
    generateNodeHeader();
    generateSimpleNode();
    generateOneTree(false);
    generateMultiTree();
    generateTreeConstants();
    generateVisitors();
  }

  private static void generateNodeHeader() {
    Map<String, Object> optionMap = new HashMap<>(Options.getOptions());
    optionMap.put("PARSER_NAME", JJTreeGlobals.parserName);
    optionMap.put("VISITOR_RETURN_TYPE", getVisitorReturnType());
    optionMap.put("VISITOR_DATA_TYPE", getVisitorArgumentType());
    optionMap.put("VISITOR_RETURN_TYPE_VOID", Boolean.valueOf(getVisitorReturnType().equals("void")));

    CppCodeGenBuilder builder = CppCodeGenBuilder.ofHeader(optionMap);
    builder.setFile(new File(nodeIncludeFile()));
    builder.setVersion(nodeVersion).addTools(JJTreeGlobals.toolName);
    builder.addOption("MULTI", "NODE_USES_PARSER", "VISITOR", "TRACK_TOKENS", "NODE_PREFIX", "NODE_EXTENDS",
        "NODE_FACTORY", "SUPPORT_CLASS_VISIBILITY_PUBLIC");

    try {
      builder.printTemplate("/templates/cpp/Node.h.template");
      builder.build();
    } catch (IOException e) {
      throw new Error(e.toString());
    }
  }


  private static void generateSimpleNode() {
    Map<String, Object> optionMap = new HashMap<String, Object>(Options.getOptions());
    optionMap.put(Options.NONUSER_OPTION__PARSER_NAME, JJTreeGlobals.parserName);
    optionMap.put("VISITOR_RETURN_TYPE", getVisitorReturnType());
    optionMap.put("VISITOR_DATA_TYPE", getVisitorArgumentType());
    optionMap.put("VISITOR_RETURN_TYPE_VOID", Boolean.valueOf(getVisitorReturnType().equals("void")));

    CppCodeGenBuilder builder = CppCodeGenBuilder.of(optionMap);
    builder.setFile(new File(simpleNodeCodeFile()));
    builder.setVersion(nodeVersion).addTools(JJTreeGlobals.toolName);
    builder.addOption("MULTI", "NODE_USES_PARSER", "VISITOR", "TRACK_TOKENS", "NODE_PREFIX", "NODE_EXTENDS",
        "NODE_FACTORY", Options.USEROPTION__SUPPORT_CLASS_VISIBILITY_PUBLIC);

    try {
      builder.printTemplate("/templates/cpp/SimpleNode.cc.template");
      builder.switchToIncludeFile();
      builder.printTemplate("/templates/cpp/SimpleNode.h.template");
      builder.build();
    } catch (IOException e) {
      throw new Error(e.toString());
    }
  }

  private static void generateOneTree(boolean generateOneTreeImpl) {
    Map<String, Object> optionMap = new HashMap<>(Options.getOptions());
    optionMap.put("PARSER_NAME", JJTreeGlobals.parserName);
    optionMap.put("VISITOR_RETURN_TYPE", getVisitorReturnType());
    optionMap.put("VISITOR_DATA_TYPE", getVisitorArgumentType());
    optionMap.put("VISITOR_RETURN_TYPE_VOID", Boolean.valueOf(getVisitorReturnType().equals("void")));

    CppCodeGenBuilder builder =
        generateOneTreeImpl ? CppCodeGenBuilder.of(optionMap) : CppCodeGenBuilder.ofHeader(optionMap);
    builder.setFile(new File(jjtreeIncludeFile()));
    builder.setVersion(nodeVersion).addTools(JJTreeGlobals.toolName);
    builder.addOption("MULTI", "NODE_USES_PARSER", "VISITOR", "TRACK_TOKENS", "NODE_PREFIX", "NODE_EXTENDS",
        "NODE_FACTORY", "SUPPORT_CLASS_VISIBILITY_PUBLIC");

    try {
      builder.switchToIncludeFile();
      builder.println("#include \"SimpleNode.h\"");
      for (String s : nodesToBuild) {
        builder.println("#include \"" + s + ".h\"");
        if(generateOneTreeImpl) {
          builder.switchToMainFile();
          builder.printTemplate("/templates/cpp/MultiNodeImpl.template", Collections.singletonMap("NODE_TYPE", s));
          builder.switchToIncludeFile();
        }
      }

      builder.build();
    } catch (IOException e) {
      throw new Error(e.toString());
    }
  }

  private static void generateMultiTree() {
    for (String node : nodesToBuild) {
      try {
        Map<String, Object> optionMap = new HashMap<String, Object>(Options.getOptions());
        optionMap.put(Options.NONUSER_OPTION__PARSER_NAME, JJTreeGlobals.parserName);
        optionMap.put("VISITOR_RETURN_TYPE", getVisitorReturnType());
        optionMap.put("VISITOR_DATA_TYPE", getVisitorArgumentType());
        optionMap.put("VISITOR_RETURN_TYPE_VOID", Boolean.valueOf(getVisitorReturnType().equals("void")));
        optionMap.put("NODE_TYPE", node);


        CppCodeGenBuilder builder = CppCodeGenBuilder.of(optionMap);
        builder.setFile(new File(jjtreeImplFile(node)));
        builder.setVersion(nodeVersion).addTools(JJTreeGlobals.toolName);
        builder.addOption("MULTI", "NODE_USES_PARSER", "VISITOR", "TRACK_TOKENS", "NODE_PREFIX", "NODE_EXTENDS",
            "NODE_FACTORY", Options.USEROPTION__SUPPORT_CLASS_VISIBILITY_PUBLIC);
        
        builder.printTemplate("/templates/cpp/MultiNodeImpl.template");
        builder.switchToIncludeFile();
        builder.printTemplate("/templates/cpp/MultiNodeInterface.template");
        builder.build();
      } catch (IOException e) {
        throw new Error(e.toString());
      }
    }
  }

  static String nodeConstants() {
    return JJTreeGlobals.parserName + "TreeConstants";
  }

  static void generateTreeConstants() {
    List<String> nodeIds = ASTNodeDescriptor.getNodeIds();
    List<String> nodeNames = ASTNodeDescriptor.getNodeNames();

    File file = new File(JJTreeOptions.getJJTreeOutputDirectory(), nodeConstants() + ".h");
    headersForJJTreeH.add(file.getName());

    CppCodeGenBuilder builder = CppCodeGenBuilder.ofHeader(Collections.emptyMap());
    builder.setFile(file);

    builder.println("\n#include \"JavaCC.h\"");

    builder.println("enum {");
    for (int i = 0; i < nodeIds.size(); ++i) {
      String n = nodeIds.get(i);
      builder.println("  " + n + " = " + i + ",");
    }

    builder.println("};");
    builder.println();

    for (int i = 0; i < nodeNames.size(); ++i) {
      builder.println("  static JAVACC_CHAR_TYPE jjtNodeName_arr_", i, "[] = ");
      builder.printCharArray(nodeNames.get(i));
      builder.println(";");
    }
    builder.println("  static JAVACC_STRING_TYPE jjtNodeName[] = {");
    for (int i = 0; i < nodeNames.size(); i++) {
      builder.println("jjtNodeName_arr_", i, ", ");
    }
    builder.println("  };");
    builder.build();
  }

  static String visitorClass() {
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
    return ret == null || ret.equals("") || ret.equals("Object") ? "void *" : ret;
  }

  private static String getVisitorReturnType() {
    String ret = Options.stringValue("VISITOR_RETURN_TYPE");
    return ret == null || ret.equals("") || ret.equals("Object") ? "void" : ret;
  }

  static void generateVisitors() {
    if (!JJTreeOptions.getVisitor()) {
      return;
    }

    CppCodeGenBuilder builder = CppCodeGenBuilder.ofHeader(Collections.emptyMap());
    builder.setFile(new File(visitorIncludeFile()));

    builder.println("\n#include \"JavaCC.h\"");
    builder.println("#include \"" + JJTreeGlobals.parserName + "Tree.h" + "\"");

    boolean hasNamespace = Options.stringValue("NAMESPACE").length() > 0;
    if (hasNamespace) {
      builder.println("namespace " + Options.stringValue("NAMESPACE_OPEN"));
    }

    generateVisitorInterface(builder);
    generateDefaultVisitor(builder);

    if (hasNamespace) {
      builder.println(Options.stringValue("NAMESPACE_CLOSE"));
    }
    builder.build();
  }

  private static void generateVisitorInterface(CppCodeGenBuilder builder) {
    String name = visitorClass();
    List<String> nodeNames = ASTNodeDescriptor.getNodeNames();

    builder.println("class " + name);
    builder.println("{");

    String argumentType = getVisitorArgumentType();
    String returnType = getVisitorReturnType();
    if (!JJTreeOptions.getVisitorDataType().equals("")) {
      argumentType = JJTreeOptions.getVisitorDataType();
    }
    builder.println("public:");

    builder.println("  virtual " + returnType + " visit(const SimpleNode *node, " + argumentType + " data) = 0;");
    if (JJTreeOptions.getMulti()) {
      for (int i = 0; i < nodeNames.size(); ++i) {
        String n = nodeNames.get(i);
        if (n.equals("void")) {
          continue;
        }
        String nodeType = JJTreeOptions.getNodePrefix() + n;
        builder.println("  virtual " + returnType + " " + getVisitMethodName(nodeType) + "(const " + nodeType
            + " *node, " + argumentType + " data) = 0;");
      }
    }

    builder.println("  virtual ~" + name + "() { }");
    builder.println("};");
  }

  static String defaultVisitorClass() {
    return JJTreeGlobals.parserName + "DefaultVisitor";
  }

  private static void generateDefaultVisitor(CppCodeGenBuilder builder) {
    String className = defaultVisitorClass();
    List<String> nodeNames = ASTNodeDescriptor.getNodeNames();

    builder.println("class " + className + " : public " + visitorClass() + " {");

    String argumentType = getVisitorArgumentType();
    String ret = getVisitorReturnType();

    builder.println("public:");
    builder.println("  virtual " + ret + " defaultVisit(const SimpleNode *node, " + argumentType + " data) = 0;");
    // ostr.println(" node->childrenAccept(this, data);");
    // ostr.println(" return" + (ret.trim().equals("void") ? "" : " data") +
    // ";");
    // ostr.println(" }");

    builder.println("  virtual " + ret + " visit(const SimpleNode *node, " + argumentType + " data) {");
    builder.println("    " + (ret.trim().equals("void") ? "" : "return ") + "defaultVisit(node, data);");
    builder.println("  }");

    if (JJTreeOptions.getMulti()) {
      for (int i = 0; i < nodeNames.size(); ++i) {
        String n = nodeNames.get(i);
        if (n.equals("void")) {
          continue;
        }
        String nodeType = JJTreeOptions.getNodePrefix() + n;
        builder.println("  virtual " + ret + " " + getVisitMethodName(nodeType) + "(const " + nodeType + " *node, "
            + argumentType + " data) {");
        builder.println("    " + (ret.trim().equals("void") ? "" : "return ") + "defaultVisit(node, data);");
        builder.println("  }");
      }
    }
    builder.println("  ~" + className + "() { }");
    builder.println("};");
  }
}
