#include <iostream>
#include <fstream>
#include <string>
#include "JavaParser.h"

#include "JavaCC.h"
#include "JavaParserTokenManager.h"
#include "ParseException.h"
#include "StreamReader.h"


using namespace std;

JJString ReadFileFully() {
	JJString code;

	code =
		"import java.util.*;\n"

		"public class JavaGenerics\n"
		"{\n"
		"	private Map<String, List> map;\n"
		"\n"
		"	public void jriat()\n"
		"	{\n"
		"		int i = 1 >>> 2;\n"
		"		int j = 3 >> 4;\n"
		"		boolean b = 5 > 6;\n"
		"   }\n"
		"}\n";

	return code;
}
static void usage(int argc, const char** argv) {
}
using namespace Java;

int main(int argc, const char** argv) {
	istream*	input  = &cin;
	ostream*	output = &cout;
	ostream*	error  = &cerr;
	ifstream	ifs;
	ofstream	ofs;
	ofstream	efs;
	StreamReader*	sr = nullptr;
	CharStream *	cs = nullptr;

	try {
		if (argc == 4) {
			ifs.open(argv[1]);
			ofs.open(argv[2]);
			efs.open(argv[3]);
			if (ifs.is_open() && ofs.is_open() && efs.is_open()) {
				input = &ifs;	output = &ofs;	error = &efs;
				sr = new StreamReader(ifs);
				cs = new CharStream(sr);
			}
			else {
				cerr << "cannot open in or out or err file" << endl;
				return 8;
			}
		} else
		if (argc == 1) {
			JJString s = ReadFileFully();
			cout << s << endl;
			cs = new CharStream(s.c_str(), s.size() - 1, 1, 1);
		}
		else {
			usage(argc, argv);
			return 4;
		}

		TokenManager *scanner = new JavaParserTokenManager(cs);
		JavaParser parser(scanner);
		ofs << "parsing ";
		parser.CompilationUnit();
		ofs << "parsed" << endl;
	} catch (const ParseException& e) {
		clog << e.expectedTokenSequences << endl;
	}
	catch (...) {

	}
	if (ifs.is_open()) ifs.close();
	if (ofs.is_open()) ofs.close();
	if (efs.is_open()) efs.close();
	if (cs) delete cs;
	if (sr) delete sr;
	return 0;
}
#if 0
import java.io.*;

/**
 * Grammar to parse Java version 1.5
 * @author Sreenivasa Viswanadha - Simplified and enhanced for 1.5
 */
public class JavaParser
{
   /**
    * Class to hold modifiers.
    */
   static public final class ModifierSet
   {
     /* Definitions of the bits in the modifiers field.  */
     public static final int PUBLIC = 0x0001;
     public static final int PROTECTED = 0x0002;
     public static final int PRIVATE = 0x0004;
     public static final int ABSTRACT = 0x0008;
     public static final int STATIC = 0x0010;
     public static final int FINAL = 0x0020;
     public static final int SYNCHRONIZED = 0x0040;
     public static final int NATIVE = 0x0080;
     public static final int TRANSIENT = 0x0100;
     public static final int VOLATILE = 0x0200;
     public static final int STRICTFP = 0x1000;

     /** A set of accessors that indicate whether the specified modifier
         is in the set. */

     public boolean isPublic(int modifiers)
     {
       return (modifiers & PUBLIC) != 0;
     }

     public boolean isProtected(int modifiers)
     {
       return (modifiers & PROTECTED) != 0;
     }

     public boolean isPrivate(int modifiers)
     {
       return (modifiers & PRIVATE) != 0;
     }

     public boolean isStatic(int modifiers)
     {
       return (modifiers & STATIC) != 0;
     }

     public boolean isAbstract(int modifiers)
     {
       return (modifiers & ABSTRACT) != 0;
     }

     public boolean isFinal(int modifiers)
     {
       return (modifiers & FINAL) != 0;
     }

     public boolean isNative(int modifiers)
     {
       return (modifiers & NATIVE) != 0;
     }

     public boolean isStrictfp(int modifiers)
     {
       return (modifiers & STRICTFP) != 0;
     }

     public boolean isSynchronized(int modifiers)
     {
       return (modifiers & SYNCHRONIZED) != 0;
     }

     public boolean isTransient(int modifiers)
      {
       return (modifiers & TRANSIENT) != 0;
     }

     public boolean isVolatile(int modifiers)
     {
       return (modifiers & VOLATILE) != 0;
     }

     /**
      * Removes the given modifier.
      */
     static int removeModifier(int modifiers, int mod)
     {
        return modifiers & ~mod;
     }
   }

   public JavaParser(String fileName)
   {
      this(System.in);
      try { ReInit(new FileInputStream(new File(fileName))); }
      catch(Exception e) { e.printStackTrace(); }
   }

  public static void main(String args[]) {
    JavaParser parser;
    if (args.length == 0) {
      System.out.println("Java Parser Version 1.5:  Reading from standard input . . .");
      parser = new JavaParser(System.in);
    } else if (args.length == 1) {
      System.out.println("Java Parser Version 1.5:  Reading from file " + args[0] + " . . .");
      try {
        parser = new JavaParser(new java.io.FileInputStream(args[0]));
      } catch (java.io.FileNotFoundException e) {
        System.out.println("Java Parser Version 1.5:  File " + args[0] + " not found.");
        return;
      }
    } else {
      System.out.println("Java Parser Version 1.5:  Usage is one of:");
      System.out.println("         java JavaParser < inputfile");
      System.out.println("OR");
      System.out.println("         java JavaParser inputfile");
      return;
    }
    try {
      parser.CompilationUnit();
      System.out.println("Java Parser Version 1.5:  Java program parsed successfully.");
    } catch (ParseException e) {
      System.out.println(e.getMessage());
      System.out.println("Java Parser Version 1.5:  Encountered errors during parse.");
    }
  }

}
#endif
