/* Generated By:JJTree: Do not edit this line. ASTBitwiseAndNode.cc Version 8.0 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=true,NODE_PREFIX=AST,NODE_EXTENDS=MyNode,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
#include <typeinfo>
#include "ASTBitwiseAndNode.h"
#include "Boolean.h"
#include "Integer.h"
using namespace std;

  
  ASTBitwiseAndNode::ASTBitwiseAndNode(int id) : Node(id) {
  }
  ASTBitwiseAndNode::~ASTBitwiseAndNode() {
  }
  void ASTBitwiseAndNode::interpret()
  {
     jjtGetChild(0)->interpret();
     jjtGetChild(1)->interpret();

	 const Node* top = stack.top();
	 if (typeid(*top) == typeid(Boolean)) {
		 unique_ptr<Boolean> left((Boolean*)stack.top()); stack.pop();
		 unique_ptr<Boolean> righ((Boolean*)stack.top()); stack.pop();
		 stack.push(new Boolean(*left & *righ));
	 }
	 if (typeid(*top) == typeid(Integer)) {
		 unique_ptr<Integer> left((Integer*)stack.top()); stack.pop();
		 unique_ptr<Integer> righ((Integer*)stack.top()); stack.pop();
		 stack.push(new Integer(*left & *righ));
	 }
  }


/* JavaCC - OriginalChecksum=c62edb0e2ae78a9a84d458895a3f504a (do not edit this line) */
