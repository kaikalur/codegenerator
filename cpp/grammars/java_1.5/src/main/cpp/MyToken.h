#ifndef MY_TOKEN
#define MY_TOKEN
#include "JavaCC.h"
#include "DefaultToken.h"
#include "JavaParserConstants.h"

namespace Java {
		class MyToken : public JavaCC::DefaultToken {
		public:
			MyToken(int kind, JavaCC::JJString image) : JavaCC::DefaultToken(kind, image) {
				this->mykind = kind;
				this->myimage = image;
			}
			int realKind = GT;

			static Token* newToken(int ofKind, JavaCC::JJString tokenImage) {
				return new MyToken(ofKind, tokenImage);
			}

		private:
			int					mykind;
			JavaCC::JJString	myimage;
		};
}
#endif
