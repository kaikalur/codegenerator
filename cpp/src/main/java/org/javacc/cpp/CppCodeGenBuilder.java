/*
 * Copyright (c) 2001-2018 Territorium Online Srl / TOL GmbH. All Rights
 * Reserved.
 *
 * This file contains Original Code and/or Modifications of Original Code as
 * defined in and that are subject to the Territorium Online License Version
 * 1.0. You may not use this file except in compliance with the License. Please
 * obtain a copy of the License at http://www.tol.info/license/ and read it
 * before using this file.
 *
 * The Original Code and all software distributed under the License are
 * distributed on an 'AS IS' basis, WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESS
 * OR IMPLIED, AND TERRITORIUM ONLINE HEREBY DISCLAIMS ALL SUCH WARRANTIES,
 * INCLUDING WITHOUT LIMITATION, ANY WARRANTIES OF MERCHANTABILITY, FITNESS FOR
 * A PARTICULAR PURPOSE, QUIET ENJOYMENT OR NON-INFRINGEMENT. Please see the
 * License for the specific language governing rights and limitations under the
 * License.
 */

package org.javacc.cpp;

import static org.javacc.parser.JavaCCGlobals.cu_name;
import static org.javacc.parser.JavaCCGlobals.jjtreeGenerated;

import org.javacc.parser.CodeGeneratorSettings;
import org.javacc.parser.Options;
import org.javacc.utils.CodeGenBuilder;

import java.io.File;


/**
 * The {@link CppCodeGenBuilder} class.
 */
public class CppCodeGenBuilder extends CodeGenBuilder {

  private enum Buffer {
    Main,
    Include,
    Statistic;
  }


  private final StringBuffer mainBuffer;
  private final StringBuffer includeBuffer = new StringBuffer();
  private final StringBuffer staticsBuffer = new StringBuffer();


  private Buffer kind;

  /**
   * Constructs an instance of {@link CodeGenBuilder}.
   *
   * @param fileName
   * @param settings
   */
  public CppCodeGenBuilder(String fileName, CodeGeneratorSettings settings) {
    super(new StringBuffer(), fileName, settings);
    this.mainBuffer = super.getBuffer();
    this.kind = Buffer.Main;
  }

  /**
   * Get the {@link StringBuffer}
   */
  @Override
  protected final StringBuffer getBuffer() {
    switch (kind) {
      case Include:
        return includeBuffer;
      case Statistic:
        return staticsBuffer;
      default:
    }
    return mainBuffer;
  }

  /**
   * Generate a class with a given name, an array of superclass and another
   * array of super interfaes
   */
  public void genClassStart(String mod, String name, String[] superClasses, String[] superInterfaces) {
    genCode("class " + name);
    if (superClasses.length > 0 || superInterfaces.length > 0) {
      genCode(" : ");
    }

    genCommaSeperatedString(superClasses);
    genCommaSeperatedString(superInterfaces);
    genCodeLine(" {");
    genCodeLine("public:");
  }

  @Override
  public final void build() {
    String incfilePath = getFileName().replace(".cc", ".h");
    String incfileName = new File(incfilePath).getName();
    includeBuffer.insert(0, "#pragma once\n");


    // dump the statics into the main file with the code.

    mainBuffer.insert(0, staticsBuffer);

    // Finally enclose the whole thing in the namespace, if specified.
    if (Options.stringValue(Options.USEROPTION__NAMESPACE).length() > 0) {
      mainBuffer.insert(0, "namespace " + Options.stringValue("NAMESPACE_OPEN") + "\n");
      mainBuffer.append(Options.stringValue("NAMESPACE_CLOSE") + "\n");
      includeBuffer.append(Options.stringValue("NAMESPACE_CLOSE") + "\n");
    }

    if (jjtreeGenerated) {
      mainBuffer.insert(0, "#include \"" + cu_name + "Tree.h\"\n");
    }
    if (Options.getTokenManagerUsesParser())
      mainBuffer.insert(0, "#include \"" + cu_name + ".h\"\n");
    mainBuffer.insert(0, "#include \"TokenMgrError.h\"\n");
    mainBuffer.insert(0, "#include \"" + incfileName + "\"\n");
    mainBuffer.insert(0, "/* " + new File(getFileName()).getName() + " */\n");

    fixupLongLiterals(includeBuffer);
    fixupLongLiterals(mainBuffer);
    write(incfilePath, includeBuffer);
    write(getFileName(), mainBuffer);
  }

  public void generateMethodDefHeader(String modsAndRetType, String className, String nameAndParams) {
    generateMethodDefHeader(modsAndRetType, className, nameAndParams, null);
  }

  public void generateMethodDefHeader(String qualifiedModsAndRetType, String className, String nameAndParams,
      String exceptions) {
    // for C++, we generate the signature in the header file and body in main
    // file
    includeBuffer.append(qualifiedModsAndRetType + " " + nameAndParams);
    // if (exceptions != null)
    // includeBuffer.append(" throw(" + exceptions + ")");
    includeBuffer.append(";\n");

    String modsAndRetType = null;
    int i = qualifiedModsAndRetType.lastIndexOf(':');
    if (i >= 0)
      modsAndRetType = qualifiedModsAndRetType.substring(i + 1);

    if (modsAndRetType != null) {
      i = modsAndRetType.lastIndexOf("virtual");
      if (i >= 0)
        modsAndRetType = modsAndRetType.substring(i + "virtual".length());
    }
    if (qualifiedModsAndRetType != null) {
      i = qualifiedModsAndRetType.lastIndexOf("virtual");
      if (i >= 0)
        qualifiedModsAndRetType = qualifiedModsAndRetType.substring(i + "virtual".length());
    }
    String qualifierClass = (className == null) ? "" : className + "::";
    mainBuffer.append("\n" + qualifiedModsAndRetType + " " + qualifierClass + nameAndParams);
    // if (exceptions != null)
    // mainBuffer.append(" throw( " + exceptions + ")");
    switchToMainFile();
  }

  // HACK
  private void fixupLongLiterals(StringBuffer sb) {
    for (int i = 0; i < sb.length() - 1; i++) {
      // int beg = i;
      char c1 = sb.charAt(i);
      char c2 = sb.charAt(i + 1);
      if (Character.isDigit(c1) || (c1 == '0' && c2 == 'x')) {
        i += c1 == '0' ? 2 : 1;
        while (CodeGenBuilder.isHexDigit(sb.charAt(i)))
          i++;
        if (sb.charAt(i) == 'L') {
          sb.insert(i, "UL");
        }
        i++;
      }
    }
  }

  protected final void genCommaSeperatedString(String[] strings) {
    for (int i = 0; i < strings.length; i++) {
      if (i > 0) {
        genCode(", ");
      }

      genCode(strings[i]);
    }
  }

  @Override
  public final String escapeToUnicode(String text) {
    return text;
  }

  public void switchToMainFile() {
    this.kind = Buffer.Main;
  }

  public void switchToIncludeFile() {
    this.kind = Buffer.Include;
  }

  public void switchToStaticsFile() {
    this.kind = Buffer.Statistic;
  }
}
