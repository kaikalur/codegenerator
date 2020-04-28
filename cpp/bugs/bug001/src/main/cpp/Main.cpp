#include <iostream>
#include <fstream>
#include <iomanip>
#include <string>

#include "JavaCC.h"
#include "Bug001ParserTokenManager.h"
#include "ParseException.h"
#include "StreamReader.h"
#if		defined(JJ8)
#include "DefaultCharStream.h"
#define CHARSTREAM DefaultCharStream
#elif 	defined(JJ7)
#include "CharStream.h"
#define CHARSTREAM CharStream
#endif
#include "Bug001Parser.h"

using namespace std;
using namespace bug001;

JJString ReadFileFully() {
	JJString code;
	code =
"{\n" 
"	iso org(3) 2 foo\n"
"}\n";

	return code;
}
static void usage(int argc, char**argv) {
	cerr << "Parser" << " [ spl in out err ]" << endl;
}
int main(int argc, char**argv) {
	istream*	input  = &cin;
	ostream*	output = &cout;
	ostream*	error  = &cerr;
	ifstream	spl;
	ifstream	ifs;
	ofstream	ofs;
	ofstream	efs;
	StreamReader*	sr = nullptr;
	CharStream *	cs = nullptr;

	try {
		if (argc == 5) {
			spl.open(argv[1]);
			ifs.open(argv[2]);
			ofs.open(argv[3]);
			efs.open(argv[4]);
			if (ifs.is_open() && ofs.is_open() && efs.is_open() && spl.is_open()) {
				input = &ifs;	output = &ofs;	error = &efs;
				sr = new StreamReader(spl);
				cs = new CHARSTREAM(sr);
			}
			else {
				cerr << "cannot open spl or in or out or err file" << endl;
				return 8;
			}
		} else
		if (argc == 1) {
			JJString s = ReadFileFully();
			*output << s << endl;
			cs = new CHARSTREAM(s.c_str(), s.size() - 1, 1, 1);
		}
		else {
			usage(argc, argv);
			return 0;
		}
		TokenManager *scanner = new Bug001ParserTokenManager(cs);
		Bug001Parser parser(scanner);
		parser.DefinitiveIdentifier();
     	*output << "Parser Version 0.1:  file parsed successfully." << endl;
	} catch (const ParseException& e) {
		clog << e.expectedTokenSequences << endl;
	}
	catch (...) {

	}
	if (spl.is_open()) spl.close();
	if (ifs.is_open()) ifs.close();
	if (ofs.is_open()) ofs.close();
	if (efs.is_open()) efs.close();
	if (cs) delete cs;
	if (sr) delete sr;

	return 0;
}
/*
public static void main(String args[]) {
    Parser parser;
    if (args.length == 0) {
      System.out.println("Parser Version 0.1:  Reading from standard input . . .");
      parser = new Parser(System.in);
    } else if (args.length == 1) {
      System.out.println("Parser Version 0.1:  Reading from file " + args[0] + " . . .");
      try {
        parser = new Parser(new java.io.FileInputStream(args[0]));
      } catch (java.io.FileNotFoundException e) {
        System.out.println("Parser Version 0.1:  File " + args[0] + " not found.");
        return;
      }
    } else {
      System.out.println("Parser Version 0.1:  Usage is one of:");
      System.out.println("         java Parser < inputfile");
      System.out.println("OR");
      System.out.println("         java Parser inputfile");
      return;
    }
    try {
      parser.specification();
      System.out.println("Parser Version 0.1:  file parsed successfully.");
    } catch (ParseException e) {
      System.out.println("Parser Version 0.1:  Encountered errors during parse.");
    }
  }
  */
