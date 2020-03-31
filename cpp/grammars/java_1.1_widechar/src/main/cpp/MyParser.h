#include "ErrorHandler.h"
#include "Token.h"
#include "JavaParser.h"

namespace java { namespace parser {

class MyParser {
};

class MyErrorHandler: public ErrorHandler {
	virtual void handleUnexpectedToken(int expectedKind, const JJString& expectedImage, const JJString& expectedLabel, const Token* actual, JavaParser* parser) { }
	virtual void handleParseError(const Token* last, const Token* unexpected, const JJString& production, JavaParser* parser) { }
};

} }
