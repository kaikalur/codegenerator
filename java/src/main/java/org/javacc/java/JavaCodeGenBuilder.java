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

package org.javacc.java;

import org.javacc.utils.CodeGenBuilder;

import java.util.Map;


/**
 * The {@link JavaCodeGenBuilder} class.
 */
public class JavaCodeGenBuilder extends CodeGenBuilder<JavaCodeGenBuilder> {

  private final StringBuffer buffer = new StringBuffer();


  private String packageName;

  /**
   * Constructs an instance of {@link CodeGenBuilder}.
   *
   * @param settings
   */
  private JavaCodeGenBuilder(Map<String, Object> settings) {
    super(settings);
  }

  /**
   * Get the {@link StringBuffer}
   */
  protected final StringBuffer getBuffer() {
    return buffer;
  }

  /**
   * Set the Java package name
   *
   * @param packageName
   */
  public JavaCodeGenBuilder setPackageName(String packageName) {
    this.packageName = packageName;
    return this;
  }

  @Override
  public final void build() {
    StringBuffer buffer = new StringBuffer();

    if (packageName.length() > 0) {
      buffer.append("package ").append(packageName).append(";\n\n");
    }

    buffer.append(getBuffer());

    store(getFile(), buffer);
  }

  /**
   * Constructs an instance of {@link JavaCodeGenBuilder}.
   *
   * @param settings
   */
  public static JavaCodeGenBuilder of(Map<String, Object> settings) {
    return new JavaCodeGenBuilder(settings);
  }
}
