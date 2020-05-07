#ifndef MY_ERROR_HANDLER
#define MY_ERROR_HANDLER

#include "ErrorHandler.h"

namespace java { namespace parser {

class MyErrorHandler: public ErrorHandler {
public:
	MyErrorHandler();
	virtual ~MyErrorHandler();
	virtual void handleUnexpectedToken(int expectedKind, const JJString& expectedImage, const JJString& expectedLabel, const Token* actual, JavaParser* parser){}
	virtual void handleParseError(const Token* last, const Token* unexpected, const JJSimpleString& production, JavaParser* parser){}
	virtual void handleOtherError(const JJString& message, JavaParser* parser){}

};

} }
#endif