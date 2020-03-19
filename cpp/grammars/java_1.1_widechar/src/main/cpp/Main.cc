#include <fstream>
#include <iomanip>
#include <iostream>
#include <string>
#include <stdlib.h>

#include "JavaParserConstants.h"
#include "CharStream.h"
#include "JavaParser.h"
#include "JavaParserTokenManager.h"
#include "ErrorHandler.h"

using namespace java::parser;
using namespace std;

static JJString readHelloWorld() {
	JJString s;
	s =
L"public class HelloWorld\n {\n"
L"	public static void main(String args[]) {\n"
L"		System.out.println(\"Hello World!\"); \n"
L"	}\n"
L"}\n";
	return s;
}
static JJString ReadFileFully(char *file_name) {
  JJString s;
  wifstream fp_in;
  fp_in.open(file_name, ios::in);
  // Very inefficient.
  while (!fp_in.eof()) {
   s += fp_in.get();
  }
  return s;
}

int main(int argc, char **argv) {
	JJString s;
  if (argc < 2)
	s = readHelloWorld();
  else
	s = ReadFileFully(argv[1]);
  CharStream *stream = new CharStream(s.c_str(), s.size() - 1, 1, 1);
  JavaParserTokenManager *scanner = new JavaParserTokenManager(stream);
  JavaParser parser(scanner);
  parser.CompilationUnit();
  Node *root = (Node*)parser.jjtree.peekNode();
#if 0
  if (root) {
    JJString buffer;
#if (JAVACC_CHAR_TYPE_SIZEOF != 1)
   	root->dumpToBuffer(L" ", L"\n", &buffer);
    wcout << buffer << "\n";
    root->dump(L" ");
#else
    root->dumpToBuffer(" ", "\n", &buffer);
    printf("%s\n", buffer.c_str());
#endif
	int  a;
	const int ca = "";
	const JJString* b = &a;
	const JJString* const c = &a;
	const JJString const * d = &a;

	c = &s;
	d = &s;

  }
#endif
}
