#ifndef MY_ERROR_HANDLER
#define MY_ERROR_HANDLER

#include "ErrorHandler.h"

namespace java { namespace parser {

class MyErrorHandler: public ErrorHandler {
public:
	MyErrorHandler();
	virtual ~MyErrorHandler();
	virtual void handleUnexpectedToken(int expectedKind, const JJString& expectedImage, const JJString& expectedLabel, const Token* actual, Parser* parser){}
	virtual void handleParseError(const Token* last, const Token* unexpected, const JJSimpleString& production, Parser* parser){}
	virtual void handleOtherError(const JJString& message, Parser* parser){}

};

} }
#endif