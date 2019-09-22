all: javaparser wjavaparser

javaparser: main.cc gen/*.cc
	$(CXX) $(CXXFLAGS) -std=c++11 -Wall -fpermissive -o javaparser -I. main.cc gen/*.cc

wjavaparser: main.cc gen/*.cc
	$(CXX) $(CXXFLAGS) -std=c++11 -Wall -fpermissive -DWIDE_CHAR=1 -DJAVACC_CHAR_TYPE=wchar_t -o wjavaparser -I. main.cc gen/*.cc

gen/Java1.1.jj: Java1.1.jjt
	java -cp ../../../../core/target/javacc.jar:../../target/cpp-codegenerator.jar jjtree $(JAVACC_OPTS) $<

gen/*.cc: gen/Java1.1.jj
	java -cp ../../../../core/target/javacc.jar:../../target/cpp-codegenerator.jar javacc $(JAVACC_OPTS) $<

clean:
	/bin/rm -rf gen javaparser* wjavaparser*
