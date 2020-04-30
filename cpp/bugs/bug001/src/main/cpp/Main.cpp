#include <iostream>
#include <fstream>
#include <iomanip>
#include <string>

#include "JavaCC.h"
#include "TyperTokenManager.h"
#include "ParseException.h"
#include "StreamReader.h"
#if		defined(JJ8)
#include "DefaultCharStream.h"
#define CHARSTREAM DefaultCharStream
#elif 	defined(JJ7)
#include "CharStream.h"
#define CHARSTREAM CharStream
#endif
#include "Typer.h"

using namespace std;
using namespace ASN1::Typer;

JJString ReadFileFully() {
	JJString code;
	code = "ModuleTestEmpty-00 { iso org(3) } DEFINITIONS ::= BEGIN END\n";	
	return code;
}
static void usage(int argc, char**argv) {
	cerr << "Parser" << " [ in out err ]" << endl;
}
int main(int argc, char**argv) {
	istream*	input  = &cin;
	ostream*	output = &cout;
	ostream*	error  = &cerr;
	ifstream	ifs;
	ofstream	ofs;
	ofstream	efs;
	StreamReader*	sr = nullptr;
	CharStream *	cs = nullptr;

	try {
		if (argc == 5) {
			ifs.open(argv[2]);
			ofs.open(argv[3]);
			efs.open(argv[4]);
			if (ifs.is_open() && ofs.is_open() && efs.is_open()) {
				input = &ifs;	output = &ofs;	error = &efs;
				sr = new StreamReader(ifs);
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
		TokenManager *scanner = new TyperTokenManager(cs);
		Typer parser(scanner);
		parser.ModuleDefinitionList();
     	*output << "Parser Version 0.1:  file parsed successfully." << endl;
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
