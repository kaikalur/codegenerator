/*
 * MyErrorHandler.h
 *
 *  Created on: 12 avr. 2014
 *      Author: FrancisANDRE
 */

#ifndef MYERRORHANDLER_H_
#define MYERRORHANDLER_H_
#include "ErrorHandler.h"

namespace EG4 {

class MyErrorHandler : public ErrorHandler {
public:
	MyErrorHandler();
	virtual ~MyErrorHandler();
	virtual void handleUnexpectedToken(int expectedKind, const JJString& expectedImage, const JJString& expectedLabel, const Token* actual);
	virtual void handleParseError(const Token* last, const Token* unexpected, const JJSimpleString& production);
	virtual void handleOtherError(const JJString& message);
};

} /* namespace EG4 */

#endif /* MYERRORHANDLER_H_ */
