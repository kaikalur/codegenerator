#ifndef MY_ERROR_HANDLER
#define MY_ERROR_HANDLER

#include "ParserErrorHandler.h"

namespace java { namespace parser {

class MyErrorHandler: public ParserErrorHandler {
public:
	MyErrorHandler() {}
	virtual ~MyErrorHandler() {}
	virtual void handleUnexpectedToken(int expectedKind, const JJString& expectedImage, const JJString& expectedLabel, const Token* actual){}
	virtual void handleParseError(const Token* last, const Token* unexpected, const JJSimpleString& production){}
	virtual void handleOtherError(const JJString& message){}

};

} }
#endif