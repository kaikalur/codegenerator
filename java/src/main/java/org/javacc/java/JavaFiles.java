/* Copyright (c) 2006, Sun Microsystems, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the Sun Microsystems, Inc. nor the names of its
 *       contributors may be used to endorse or promote products derived from
 *       this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.javacc.java;

import org.javacc.Version;
import org.javacc.parser.JavaCCErrors;
import org.javacc.parser.JavaCCGlobals;
import org.javacc.parser.JavaCCParserConstants;
import org.javacc.parser.Options;
import org.javacc.parser.OutputFile;
import org.javacc.utils.OutputFileGenerator;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Generate CharStream, TokenManager and Exceptions.
 */
public class JavaFiles extends JavaCCGlobals implements JavaCCParserConstants
{
  /**
   * ID of the latest version (of JavaCC) in which one of the CharStream classes
   * or the CharStream interface is modified.
   */
  static final String charStreamVersion = Version.majorDotMinor;

  /**
   * ID of the latest version (of JavaCC) in which the TokenManager interface is modified.
   */
  static final String tokenManagerVersion = Version.majorDotMinor;

  /**
   * ID of the latest version (of JavaCC) in which the Token class is modified.
   */
  static final String tokenVersion = Version.majorDotMinor;

  /**
   * ID of the latest version (of JavaCC) in which the ParseException class is
   * modified.
   */
  static final String parseExceptionVersion = Version.majorDotMinor;

  /**
   * ID of the latest version (of JavaCC) in which the TokenMgrError class is
   * modified.
   */
  static final String tokenMgrErrorVersion = Version.majorDotMinor;

  
  public interface JavaResourceTemplateLocations {
		public String getTokenManagerTemplateResourceUrl();
		public String getTokenTemplateResourceUrl();
		public String getTokenMgrErrorTemplateResourceUrl();
		public String getJavaCharStreamTemplateResourceUrl();
		public String getCharStreamTemplateResourceUrl();
		public String getSimpleCharStreamTemplateResourceUrl();
		public String getParseExceptionTemplateResourceUrl();
  }
  
  
  public static class JavaModernResourceTemplateLocationImpl implements JavaResourceTemplateLocations {
		@Override
    public String getTokenMgrErrorTemplateResourceUrl() {
			// Same as Java
			return "/templates/TokenMgrError.template";
		}
		@Override
    public String getCharStreamTemplateResourceUrl() {
			// Same as Java
			return "/templates/CharStream.template";
		}
	  
	  @Override
    public String getTokenManagerTemplateResourceUrl() {
		// Same as Java
			return "/templates/TokenManager.template";
		}
		
		@Override
    public String getTokenTemplateResourceUrl() {
			// Same as Java
			return "/templates/Token.template";
		}
		
		@Override
    public String getSimpleCharStreamTemplateResourceUrl() {
			return "/templates/gwt/SimpleCharStream.template";
		}
		
		
		@Override
    public String getJavaCharStreamTemplateResourceUrl() {
			return "/templates/gwt/JavaCharStream.template";
		}

		
		@Override
    public String getParseExceptionTemplateResourceUrl() {
			return "/templates/gwt/ParseException.template";
		}
  }
  
  
  public static class JavaResourceTemplateLocationImpl implements JavaResourceTemplateLocations {
	  
	    @Override
      public String getTokenTemplateResourceUrl() {
			return "/templates/Token.template";
		}
		@Override
    public String getTokenManagerTemplateResourceUrl() {
			return "/templates/TokenManager.template";
		}
		@Override
    public String getTokenMgrErrorTemplateResourceUrl() {
			return "/templates/TokenMgrError.template";
		}
		@Override
    public String getJavaCharStreamTemplateResourceUrl() {
			return "/templates/JavaCharStream.template";
		}
		
		@Override
    public String getCharStreamTemplateResourceUrl() {
			return "/templates/CharStream.template";
		}
		@Override
    public String getSimpleCharStreamTemplateResourceUrl() {
			return "/templates/SimpleCharStream.template";
		}
		
		@Override
    public String getParseExceptionTemplateResourceUrl() {
			return "/templates/ParseException.template";
		}
		
}
  
  public static final JavaResourceTemplateLocations RESOURCES_JAVA_CLASSIC = new JavaResourceTemplateLocationImpl();
  public static final JavaResourceTemplateLocations RESOURCES_JAVA_MODERN = new JavaModernResourceTemplateLocationImpl();
  
  


  public static void gen_JavaCharStream(JavaResourceTemplateLocations locations) {
    try {
      final File file = new File(Options.getOutputDirectory(), "JavaCharStream.java");
      final OutputFile outputFile = new OutputFile(file, charStreamVersion, new String[] {Options.USEROPTION__STATIC, Options.USEROPTION__SUPPORT_CLASS_VISIBILITY_PUBLIC});

      if (!outputFile.needToWrite)
      {
        return;
      }

      final PrintWriter ostr = outputFile.getPrintWriter();

      if (cu_to_insertion_point_1.size() != 0 &&
          cu_to_insertion_point_1.get(0).kind == PACKAGE
      ) {
        for (int i = 1; i < cu_to_insertion_point_1.size(); i++) {
          if (cu_to_insertion_point_1.get(i).kind == SEMICOLON) {
            cline = cu_to_insertion_point_1.get(0).beginLine;
            ccol = cu_to_insertion_point_1.get(0).beginColumn;
            for (int j = 0; j <= i; j++) {
              printToken(cu_to_insertion_point_1.get(j), ostr, true);
            }
            ostr.println("");
            ostr.println("");
            break;
          }
        }
      }
      String prefix = Options.getStatic() ? "static " : "";
      Map<String, Object> options = new HashMap<>(Options.getOptions());
      options.put("PREFIX", prefix);
      
      OutputFileGenerator generator = new OutputFileGenerator(
    		  locations.getJavaCharStreamTemplateResourceUrl(), options);
      
      generator.generate(ostr);

      ostr.close();
    } catch (IOException e) {
      System.err.println("Failed to create JavaCharStream " + e);
      JavaCCErrors.semantic_error("Could not open file JavaCharStream.java for writing.");
      throw new Error();
    }
  }



  public static void gen_SimpleCharStream(JavaResourceTemplateLocations locations) {
    try {
      final File file = new File(Options.getOutputDirectory(), "SimpleCharStream.java");
      final OutputFile outputFile = new OutputFile(file, charStreamVersion, new String[] {Options.USEROPTION__STATIC, Options.USEROPTION__SUPPORT_CLASS_VISIBILITY_PUBLIC});

      if (!outputFile.needToWrite)
      {
        return;
      }

      final PrintWriter ostr = outputFile.getPrintWriter();

      if (cu_to_insertion_point_1.size() != 0 &&
          cu_to_insertion_point_1.get(0).kind == PACKAGE
      ) {
        for (int i = 1; i < cu_to_insertion_point_1.size(); i++) {
          if (cu_to_insertion_point_1.get(i).kind == SEMICOLON) {
            cline = cu_to_insertion_point_1.get(0).beginLine;
            ccol = cu_to_insertion_point_1.get(0).beginColumn;
            for (int j = 0; j <= i; j++) {
              printToken(cu_to_insertion_point_1.get(j), ostr, true);
            }
            ostr.println("");
            ostr.println("");
            break;
          }
        }
      }
      String prefix = Options.getStatic() ? "static " : "";
      Map<String, Object> options = new HashMap<>(Options.getOptions());
      options.put("PREFIX", prefix);
      
      OutputFileGenerator generator = new OutputFileGenerator(
    		  locations.getSimpleCharStreamTemplateResourceUrl(), options);
      
      generator.generate(ostr);

      ostr.close();
    } catch (IOException e) {
      System.err.println("Failed to create SimpleCharStream " + e);
      JavaCCErrors.semantic_error("Could not open file SimpleCharStream.java for writing.");
      throw new Error();
    }
  }



  public static void gen_CharStream(JavaResourceTemplateLocations locations) {
    try {
      final File file = new File(Options.getOutputDirectory(), "CharStream.java");
      final OutputFile outputFile = new OutputFile(file, charStreamVersion, new String[] {Options.USEROPTION__STATIC, Options.USEROPTION__SUPPORT_CLASS_VISIBILITY_PUBLIC});

      if (!outputFile.needToWrite)
      {
        return;
      }

      final PrintWriter ostr = outputFile.getPrintWriter();

      if (cu_to_insertion_point_1.size() != 0 &&
          cu_to_insertion_point_1.get(0).kind == PACKAGE
      ) {
        for (int i = 1; i < cu_to_insertion_point_1.size(); i++) {
          if (cu_to_insertion_point_1.get(i).kind == SEMICOLON) {
            cline = cu_to_insertion_point_1.get(0).beginLine;
            ccol = cu_to_insertion_point_1.get(0).beginColumn;
            for (int j = 0; j <= i; j++) {
              printToken(cu_to_insertion_point_1.get(j), ostr, true);
            }
            ostr.println("");
            ostr.println("");
            break;
          }
        }
      }
      
      OutputFileGenerator generator = new OutputFileGenerator(
    		  locations.getCharStreamTemplateResourceUrl(), Options.getOptions());
      
      generator.generate(ostr);

      ostr.close();
    } catch (IOException e) {
      System.err.println("Failed to create CharStream " + e);
      JavaCCErrors.semantic_error("Could not open file CharStream.java for writing.");
      throw new Error();
    }
  }


  
  public static void gen_JavaModernFiles() {
	  genMiscFile("Provider.java","/templates/gwt/Provider.template" );
	  genMiscFile("StringProvider.java","/templates/gwt/StringProvider.template" );
	  
	  // This provides a bridge to standard Java readers.
	  genMiscFile("StreamProvider.java","/templates/gwt/StreamProvider.template" );
  }

  private static void genMiscFile(String fileName, String templatePath) throws Error {
	try {
	  final File file = new File(Options.getOutputDirectory(), fileName);
	  final OutputFile outputFile = new OutputFile(file, parseExceptionVersion, new String[] {/* cba -- 2013/07/22 -- previously wired to a typo version of this option -- KEEP_LINE_COL */ Options.USEROPTION__KEEP_LINE_COLUMN});

	  if (!outputFile.needToWrite)
	  {
	    return;
	  }

	  final PrintWriter ostr = outputFile.getPrintWriter();

	  if (cu_to_insertion_point_1.size() != 0 &&
	      cu_to_insertion_point_1.get(0).kind == PACKAGE
	  ) {
	    for (int i = 1; i < cu_to_insertion_point_1.size(); i++) {
	      if (cu_to_insertion_point_1.get(i).kind == SEMICOLON) {
	        cline = cu_to_insertion_point_1.get(0).beginLine;
	        ccol = cu_to_insertion_point_1.get(0).beginColumn;
	        for (int j = 0; j <= i; j++) {
	          printToken(cu_to_insertion_point_1.get(j), ostr, true);
	        }
	        ostr.println("");
	        ostr.println("");
	        break;
	      }
	    }
	  }
	  
	  OutputFileGenerator generator = new OutputFileGenerator( templatePath, Options.getOptions());
	  
	  generator.generate(ostr);

	  ostr.close();
	} catch (IOException e) {
	  System.err.println("Failed to create " + fileName + " "+ e);
	  JavaCCErrors.semantic_error("Could not open file "+fileName+" for writing.");
	  throw new Error();
	}
}
  

  public static void gen_ParseException(JavaResourceTemplateLocations locations) {
    try {
      final File file = new File(Options.getOutputDirectory(), "ParseException.java");
      final OutputFile outputFile = new OutputFile(file, parseExceptionVersion, new String[] {/* cba -- 2013/07/22 -- previously wired to a typo version of this option -- KEEP_LINE_COL */ Options.USEROPTION__KEEP_LINE_COLUMN});

      if (!outputFile.needToWrite)
      {
        return;
      }

      final PrintWriter ostr = outputFile.getPrintWriter();

      if (cu_to_insertion_point_1.size() != 0 &&
          cu_to_insertion_point_1.get(0).kind == PACKAGE
      ) {
        for (int i = 1; i < cu_to_insertion_point_1.size(); i++) {
          if (cu_to_insertion_point_1.get(i).kind == SEMICOLON) {
            cline = cu_to_insertion_point_1.get(0).beginLine;
            ccol = cu_to_insertion_point_1.get(0).beginColumn;
            for (int j = 0; j <= i; j++) {
              printToken(cu_to_insertion_point_1.get(j), ostr, true);
            }
            ostr.println("");
            ostr.println("");
            break;
          }
        }
      }
      
      OutputFileGenerator generator = new OutputFileGenerator(
    		  locations.getParseExceptionTemplateResourceUrl(), Options.getOptions());
      
      generator.generate(ostr);

      ostr.close();
    } catch (IOException e) {
      System.err.println("Failed to create ParseException " + e);
      JavaCCErrors.semantic_error("Could not open file ParseException.java for writing.");
      throw new Error();
    }
  }



  public static void gen_TokenMgrError(JavaResourceTemplateLocations locations) {
	  

	  boolean isLegacyExceptionHandling = Options.isLegacyExceptionHandling();
	String filename = isLegacyExceptionHandling ? "TokenMgrError.java" : "TokenMgrException.java";
    try {
      
	final File file = new File(Options.getOutputDirectory(), filename);
      final OutputFile outputFile = new OutputFile(file, tokenMgrErrorVersion, new String[0]);

      if (!outputFile.needToWrite)
      {
        return;
      }

      final PrintWriter ostr = outputFile.getPrintWriter();

      if (cu_to_insertion_point_1.size() != 0 &&
          cu_to_insertion_point_1.get(0).kind == PACKAGE
      ) {
        for (int i = 1; i < cu_to_insertion_point_1.size(); i++) {
          if (cu_to_insertion_point_1.get(i).kind == SEMICOLON) {
            cline = cu_to_insertion_point_1.get(0).beginLine;
            ccol = cu_to_insertion_point_1.get(0).beginColumn;
            for (int j = 0; j <= i; j++) {
              printToken(cu_to_insertion_point_1.get(j), ostr, true);
            }
            ostr.println("");
            ostr.println("");
            break;
          }
        }
      }
      
      
      
      OutputFileGenerator generator = new OutputFileGenerator( locations.getTokenMgrErrorTemplateResourceUrl(), Options.getOptions());
      
      generator.generate(ostr);

      ostr.close();
      
      
    } catch (IOException e) {
      System.err.println("Failed to create "+filename+" " + e);
      JavaCCErrors.semantic_error("Could not open file "+filename+" for writing.");
      throw new Error();
    }
  }



  public static void gen_Token(JavaResourceTemplateLocations locations) {
    try {
      final File file = new File(Options.getOutputDirectory(), "Token.java");
      final OutputFile outputFile = new OutputFile(file, tokenVersion, new String[] {Options.USEROPTION__TOKEN_EXTENDS, /* cba -- 2013/07/22 -- previously wired to a typo version of this option -- KEEP_LINE_COL */ Options.USEROPTION__KEEP_LINE_COLUMN, Options.USEROPTION__SUPPORT_CLASS_VISIBILITY_PUBLIC});

      if (!outputFile.needToWrite)
      {
        return;
      }

      final PrintWriter ostr = outputFile.getPrintWriter();

      if (cu_to_insertion_point_1.size() != 0 &&
          cu_to_insertion_point_1.get(0).kind == PACKAGE
      ) {
        for (int i = 1; i < cu_to_insertion_point_1.size(); i++) {
          if (cu_to_insertion_point_1.get(i).kind == SEMICOLON) {
            cline = cu_to_insertion_point_1.get(0).beginLine;
            ccol = cu_to_insertion_point_1.get(0).beginColumn;
            for (int j = 0; j <= i; j++) {
              printToken(cu_to_insertion_point_1.get(j), ostr, true);
            }
            ostr.println("");
            ostr.println("");
            break;
          }
        }
      }
      
      OutputFileGenerator generator = new OutputFileGenerator(
    		  locations.getTokenTemplateResourceUrl(), Options.getOptions());
      
      generator.generate(ostr);
 
      ostr.close();
    } catch (IOException e) {
      System.err.println("Failed to create Token " + e);
      JavaCCErrors.semantic_error("Could not open file Token.java for writing.");
      throw new Error();
    }
  }



  public static void gen_TokenManager(JavaResourceTemplateLocations locations) {
    try {
      final File file = new File(Options.getOutputDirectory(), "TokenManager.java");
      final OutputFile outputFile = new OutputFile(file, tokenManagerVersion, new String[] {Options.USEROPTION__SUPPORT_CLASS_VISIBILITY_PUBLIC});

      if (!outputFile.needToWrite)
      {
        return;
      }

      final PrintWriter ostr = outputFile.getPrintWriter();

      if (cu_to_insertion_point_1.size() != 0 &&
          cu_to_insertion_point_1.get(0).kind == PACKAGE
      ) {
        for (int i = 1; i < cu_to_insertion_point_1.size(); i++) {
          if (cu_to_insertion_point_1.get(i).kind == SEMICOLON) {
            cline = cu_to_insertion_point_1.get(0).beginLine;
            ccol = cu_to_insertion_point_1.get(0).beginColumn;
            for (int j = 0; j <= i; j++) {
              printToken(cu_to_insertion_point_1.get(j), ostr, true);
            }
            ostr.println("");
            ostr.println("");
            break;
          }
        }
      }

      OutputFileGenerator generator = new OutputFileGenerator(
    		  locations.getTokenManagerTemplateResourceUrl(), Options.getOptions());
      
      generator.generate(ostr);
      
      ostr.close();
    } catch (IOException e) {
      System.err.println("Failed to create TokenManager " + e);
      JavaCCErrors.semantic_error("Could not open file TokenManager.java for writing.");
      throw new Error();
    }
  }

	
  public static void reInit()
  {
  }

}
