#include "TokenManager.h"
#include "MatcherConstants.h"
using namespace Basic;

class MatcherTokenManager : public Basic::TokenManager {
public:
	MatcherTokenManager(Basic::CharStream* cs) : cs(cs) {
	}
	virtual Basic::Token* getNextToken() {
		Basic::Token* token = nullptr;
		JJChar jjchar = cs->readChar();
		switch (jjchar) {
		case '{': token = new Basic::Token(Basic::LBRACE, cs->getImage());  break;
		case '}': token = new Basic::Token(Basic::RBRACE, cs->getImage());  break;
		case '\n': token = new Basic::Token(Basic::NL, cs->getImage());  break;
		case '\r': token = new Basic::Token(Basic::LF, cs->getImage());  break;
		case 0: token = new Basic::Token(Basic::_EOF, cs->getImage());  break;

		}
		return token;
	}
private:
	Basic::CharStream* cs;
};