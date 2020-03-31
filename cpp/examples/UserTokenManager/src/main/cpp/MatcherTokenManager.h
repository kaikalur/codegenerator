#include "TokenManager.h"
#include "MatcherConstants.h"
#include "MyToken.h"
using namespace Basic;
using namespace FOO::BAR;

class MatcherTokenManager : public Basic::TokenManager {
public:
	MatcherTokenManager(Basic::CharStream* cs) : cs(cs) {
	}
	virtual Basic::Token* getNextToken() {
		Basic::Token* token = nullptr;
		if (cs->endOfInput())
			token =   new MyToken(Basic::_EOF, "EOF");
		else {
			JJChar jjchar = cs->readChar();
			switch (jjchar) {
			case '{': token = new MyToken(Basic::LBRACE, cs->getImage());  break;
			case '}': token = new MyToken(Basic::RBRACE, cs->getImage()); break;
			case '\n': token = new MyToken(Basic::NL, cs->getImage());  break;
			case '\r': token = new MyToken(Basic::LF, cs->getImage());  break;

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
private:
	Basic::CharStream* cs;
};