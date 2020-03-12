#include <fstream>
#include <iomanip>
#include <iostream>
#include <string>
#include <stdlib.h>

#include "JavaParserConstants.h"
#include "CharStream.h"
#include "JavaParser.h"
#include "JavaParserTokenManager.h"

using namespace java::parser;
using namespace std;

JJString ReadFileFully(char *file_name) {
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
  if (argc < 2) {
    cout << "Usage: wjavaparser <java inputfile>" << endl;
    exit(1);
  }
  JJString s = ReadFileFully(argv[1]);
  CharStream *stream = new CharStream(s.c_str(), s.size() - 1, 1, 1);
  JavaParserTokenManager *scanner = new JavaParserTokenManager(stream);
  JavaParser parser(scanner);
  parser.setErrorHandler(new MyErrorHandler());
  parser.CompilationUnit();
  Node *root = (Node*)parser.jjtree.peekNode();
  if (root) {
    JJString buffer;
#if WIDE_CHAR
    //root->dumpToBuffer(L" ", L"\n", &buffer);
    //wcout << buffer << "\n";
    root->dump(L" ");
#else
    root->dumpToBuffer(" ", "\n", &buffer);
    printf("%s\n", buffer.c_str());
#endif
  }
}
