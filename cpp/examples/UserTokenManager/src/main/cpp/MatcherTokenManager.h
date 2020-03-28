#include "TokenManager.h"

class MatcherTokenManager : public Basic::TokenManager {
public:
	MatcherTokenManager(Basic::CharStream* cs) {
	}
	virtual Basic::Token* getNextToken() {
		return nullptr;
	}
};