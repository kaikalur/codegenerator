#include "TokenManager.h"
#include "MyToken.h"
#include "MyTokenConstants.h"
using namespace FOO::BAR;

class MatcherTokenManager : public Basic::TokenManager {
public:
	MatcherTokenManager(Basic::CharStream* cs) : cs(cs) {
	}
	virtual Basic::Token* getNextToken() {
		Basic::Token* token = nullptr;
		if (cs->endOfInput())
			token =   new MyToken(_EOF, "EOF");
		else {
			JJChar jjchar = cs->readChar();
			switch (jjchar) {
			case '{': token = new MyToken(LBRACE, cs->getImage());  break;
			case '}': token = new MyToken(RBRACE, cs->getImage()); break;
			case '\n': token = new MyToken(NL, cs->getImage());  break;
			case '\r': token = new MyToken(LF, cs->getImage());  break;

			}
		}
		if (token) {
			token->beginLine() = cs->getBeginLine();
			token->beginColumn() = cs->getBeginColumn();
			token->endLine() = cs->getEndLine();
			token->endColumn() = cs->getEndColumn();
		}
		return token;
	}
	virtual void lexicalError() {
	}
private:
	Basic::CharStream* cs;
};