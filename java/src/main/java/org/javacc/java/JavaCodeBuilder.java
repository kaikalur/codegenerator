
package org.javacc.java;

import org.javacc.parser.CodeGeneratorSettings;
import org.javacc.utils.CodeBuilder;

import java.util.Map;


/**
 * The {@link JavaCodeBuilder} class.
 */
public class JavaCodeBuilder extends CodeBuilder<JavaCodeBuilder> {

  private final StringBuffer buffer = new StringBuffer();


  private String packageName;

  /**
   * Constructs an instance of {@link CodeBuilder}.
   *
   * @param options
   */
  private JavaCodeBuilder(CodeGeneratorSettings options) {
    super(options);
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
  public JavaCodeBuilder setPackageName(String packageName) {
    this.packageName = packageName;
    return this;
  }

  @Override
  protected final void build() {
    StringBuffer buffer = new StringBuffer();

    if (packageName.length() > 0) {
      buffer.append("package ").append(packageName).append(";\n\n");
    }

    buffer.append(getBuffer());

    store(getFile(), buffer);
  }

  /**
   * Constructs an instance of {@link JavaCodeBuilder}.
   *
   * @param options
   */
  public static JavaCodeBuilder of(CodeGeneratorSettings options) {
    return new JavaCodeBuilder(options);
  }
}
