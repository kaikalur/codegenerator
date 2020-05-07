/*
 * MyErrorHandler.cpp
 *
 *  Created on: 12 avr. 2014
 *      Author: FrancisANDRE
 */

#include "MyErrorHandler.h"

namespace EG4 {

MyErrorHandler::MyErrorHandler() {
}

MyErrorHandler::~MyErrorHandler() {
}
void MyErrorHandler::handleUnexpectedToken(int expectedKind, const JJString& expectedImage, const JJString& expectedLabel, const Token* actual, Parser* parser) {
}
void MyErrorHandler::handleParseError(const Token* last, const Token* unexpected, const JJSimpleString& production, Parser* parser) {
}
void MyErrorHandler::handleOtherError(const JJString& message, Parser* parser) {
}

} /* namespace EG4 */
