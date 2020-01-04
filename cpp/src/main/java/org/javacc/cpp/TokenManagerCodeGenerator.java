package org.javacc.cpp;

import java.io.File;
import java.io.IOException;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.javacc.parser.CodeGenHelper;
import org.javacc.parser.CodeGeneratorSettings;
import org.javacc.parser.Options;
import org.javacc.parser.TokenizerData;

/**
 * Class that implements a table driven code generator for the token manager in
 * C++.
 */
public class TokenManagerCodeGenerator implements org.javacc.parser.TokenManagerCodeGenerator {
  private static final String TokenManagerTemplate = "/templates/cpp/TableDrivenTokenManager.template";
  private static final String TokenManagerTemplateH = "/templates/cpp/TableDrivenTokenManager.h.template";

  private final CodeGenHelper codeGenerator = new CppCodeGenHelper();

  @Override
  public void generateCode(CodeGeneratorSettings settings, TokenizerData tokenizerData) {
    String superClass = (String)settings.get(
                             Options.USEROPTION__TOKEN_MANAGER_SUPER_CLASS);
    settings.putAll(Options.getOptions());
    settings.put("maxOrdinal", tokenizerData.allMatches.size());
    settings.put("maxLexStates", tokenizerData.lexStateNames.length);
    settings.put("nfaSize", tokenizerData.nfa.size());
    settings.put("charsVectorSize", ((Character.MAX_VALUE >> 6) + 1));
    settings.put("stateSetSize", tokenizerData.nfa.size());
    settings.put("parserName", tokenizerData.parserName);
    settings.put("maxLongs", tokenizerData.allMatches.size()/64 + 1);
    settings.put("parserName", tokenizerData.parserName);
    settings.put("charStreamName", CodeGenHelper.getCharStreamName());
    settings.put("defaultLexState", tokenizerData.defaultLexState);
    settings.put("decls", tokenizerData.decls);
    settings.put("superClass", (superClass == null || superClass.equals(""))
                      ? "" : "extends " + superClass);
    settings.put("noDfa", Options.getNoDfa());
    settings.put("generatedStates", tokenizerData.nfa.size());

    try {
      codeGenerator.writeTemplate(TokenManagerCodeGenerator.TokenManagerTemplate, settings);

      codeGenerator.switchToIncludeFile(); // remaining variables
      codeGenerator.writeTemplate(TokenManagerCodeGenerator.TokenManagerTemplateH, settings, "charStreamName", "CharStream", "lexStateNameLength",
      	        tokenizerData.lexStateNames.length);
      codeGenerator.switchToStaticsFile();

      dumpDfaTables(codeGenerator, tokenizerData);
      dumpNfaTables(codeGenerator, tokenizerData);
      dumpMatchInfo(codeGenerator, tokenizerData);
    } catch(IOException ioe) {
      assert(false);
    }
  }

  @Override
  public void finish(CodeGeneratorSettings settings, TokenizerData tokenizerData) {
    if (!Options.getBuildTokenManager()) return;
    String fileName = Options.getOutputDirectory() + File.separator +
                      tokenizerData.parserName + "TokenManager.cc";
    codeGenerator.saveOutput(fileName);
  }

  private void dumpDfaTables(
      CodeGenHelper codeGenerator, TokenizerData tokenizerData) {
    Map<Integer, int[]> startAndSize = new HashMap<>();
    int i = 0;

    codeGenerator.genCodeLine();
    codeGenerator.genCodeLine("static const long stringLiterals[] = {");
    for (int key : tokenizerData.literalSequence.keySet()) {
      int[] arr = new int[2];
      List<String> l = tokenizerData.literalSequence.get(key);
      List<Integer> kinds = tokenizerData.literalKinds.get(key);
      arr[0] = i;
      arr[1] = l.size();
      int j = 0;
      if (i > 0) codeGenerator.genCodeLine(", ");
      for (String s : l) {
        if (j > 0) codeGenerator.genCodeLine(", ");
        int kind = kinds.get(j);
        boolean ignoreCase = tokenizerData.ignoreCaseKinds.contains(kind);
        codeGenerator.genCode(s.length());
        codeGenerator.genCode(", ");
        codeGenerator.genCode(ignoreCase ? 1 : 0);
        for (int k = 0; k < s.length(); k++) {
          codeGenerator.genCode(", ");
          codeGenerator.genCode((int)s.charAt(k));
          i++;
        }
        if(ignoreCase) {
          for (int k = 0; k < s.length(); k++) {
            codeGenerator.genCode(", ");
            codeGenerator.genCode((int)s.toUpperCase().charAt(k));
            i++;
          }
        }
        codeGenerator.genCode(", " + kind);
        codeGenerator.genCode(
            ", " + tokenizerData.kindToNfaStartState.get(kind));
        i += 4;
        j++;
      }
      startAndSize.put(key, arr);
    }
    codeGenerator.genCodeLine("};");
    codeGenerator.genCodeLine();

    final String staticString = Options.getStatic() ? "static " : "";
    // Token actions.
    codeGenerator.genCodeLine(
        staticString + "int " + tokenizerData.parserName + "TokenManager::getStartAndSize(int index, int isCount)\n{");
    codeGenerator.genCodeLine("  switch(index) {");
    for (int key : tokenizerData.literalSequence.keySet()) {
      int[] arr = startAndSize.get(key);
      codeGenerator.genCodeLine("    case " + key + ": { return (isCount == 0) ? " +
                                 arr[0] + " : " + arr[1] + ";}");
    }
    codeGenerator.genCodeLine("  }");
    codeGenerator.genCodeLine("  return -1;");
    codeGenerator.genCodeLine("}\n");
  }

  private void dumpNfaTables(
      CodeGenHelper codeGenerator, TokenizerData tokenizerData) {
    // WE do the following for java so that the generated code is reasonable
    // size and can be compiled. May not be needed for other languages.
    Map<Integer, TokenizerData.NfaState> nfa = tokenizerData.nfa;
    
    int length = 0;
    int lengths[] = new int[nfa.size()];
    for (int i = 0; i < nfa.size(); i++) {
      TokenizerData.NfaState tmp = nfa.get(i);
      if (tmp != null) {
        BitSet bits = new BitSet();
        for (char c : tmp.characters) {
          bits.set(c);
        }

        lengths[i] = 0;
        long[] longs = bits.toLongArray();
        for (int k = 0; k < longs.length; k++) {
          int rep = 1;
          while (k + rep < longs.length && longs[k + rep] == longs[k]) rep++;
          k += rep - 1;
          lengths[i] = lengths[i] + 2;
        }
        length = Math.max(length, lengths[i]);
      }
    }

    codeGenerator.genCodeLine("static const long jjCharData[][" + (length + 1) + "] = {");
    for (int i = 0; i < nfa.size(); i++) {
      TokenizerData.NfaState tmp = nfa.get(i);
      if (i > 0) codeGenerator.genCodeLine(",");
      if (tmp == null) {
        codeGenerator.genCode("{}");
        continue;
      }
      codeGenerator.genCode("{");
      BitSet bits = new BitSet();
      for (char c : tmp.characters) {
        bits.set(c);
      }
      long[] longs = bits.toLongArray();
      codeGenerator.genCode(lengths[i]);
      for (int k = 0; k < longs.length; k++) {
        int rep = 1;
        while (k + rep < longs.length && longs[k + rep] == longs[k]) rep++;
        codeGenerator.genCode(", ", rep + ", ");
        codeGenerator.genCode("" + Long.toString(longs[k]));
        k += rep - 1;
      }
      codeGenerator.genCode("}");
    }
    codeGenerator.genCodeLine("};");
    codeGenerator.genCodeLine();

    length = 0;
    for (int i = 0; i < nfa.size(); i++) {
      TokenizerData.NfaState tmp = nfa.get(i);
      if (tmp == null) {
        continue;
      }
      length = Math.max(length, tmp.compositeStates.size());
    }
    codeGenerator.genCodeLine(
        "static const int jjcompositeState[][" + length + "] = {");
    for (int i = 0; i < nfa.size(); i++) {
      TokenizerData.NfaState tmp = nfa.get(i);
      if (i > 0) codeGenerator.genCodeLine(", ");
      if (tmp == null) {
        codeGenerator.genCode("{}");
        continue;
      }
      codeGenerator.genCode("{");
      int k = 0;
      for (int st : tmp.compositeStates) {
        if (k++ > 0) codeGenerator.genCode(", ");
        codeGenerator.genCode(st);
      }
      codeGenerator.genCode("}");
    }
    codeGenerator.genCodeLine("};");
    codeGenerator.genCodeLine();

    codeGenerator.genCodeLine("static const int jjmatchKinds[] = {");
    for (int i = 0; i < nfa.size(); i++) {
      TokenizerData.NfaState tmp = nfa.get(i);
      if (i > 0) codeGenerator.genCodeLine(", ");
      // TODO(sreeni) : Fix this mess.
      if (tmp == null) {
        codeGenerator.genCode(Integer.MAX_VALUE);
        continue;
      }
      codeGenerator.genCode(tmp.kind);
    }
    codeGenerator.genCodeLine("};");
    codeGenerator.genCodeLine();

    length = 0;
    for (int i = 0; i < nfa.size(); i++) {
      TokenizerData.NfaState tmp = nfa.get(i);
      if (tmp == null) {
        continue;
      }
      length = Math.max(length, tmp.nextStates.size());
    }
    codeGenerator.genCodeLine("static const int jjnextStateSet[][" + (length + 1) + "] = {");
    for (int i = 0; i < nfa.size(); i++) {
      TokenizerData.NfaState tmp = nfa.get(i);
      if (i > 0) codeGenerator.genCodeLine(", ");
      if (tmp == null) {
        codeGenerator.genCode("{0}");
        continue;
      }
      codeGenerator.genCode("{");
      codeGenerator.genCode(tmp.nextStates.size());
      for (int s : tmp.nextStates) {
        codeGenerator.genCode(", ");
        codeGenerator.genCode(s);
      }
      codeGenerator.genCode("}");
    }
    codeGenerator.genCodeLine("};");
    codeGenerator.genCodeLine();

    codeGenerator.genCodeLine("static const int jjInitStates[]  = {");
    int k = 0;
    for (int i : tokenizerData.initialStates.keySet()) {
      if (k++ > 0) codeGenerator.genCode(", ");
      codeGenerator.genCode(tokenizerData.initialStates.get(i));
    }
    codeGenerator.genCodeLine("};");
    codeGenerator.genCodeLine();

    codeGenerator.genCodeLine("static const int canMatchAnyChar[] = {");
    k = 0;
    for (int i = 0; i < tokenizerData.wildcardKind.size(); i++) {
      if (k++ > 0) codeGenerator.genCode(", ");
      codeGenerator.genCode(tokenizerData.wildcardKind.get(i));
    }
    codeGenerator.genCodeLine("};");
    codeGenerator.genCodeLine();
  }

  private void dumpMatchInfo(
      CodeGenHelper codeGenerator, TokenizerData tokenizerData) {
    Map<Integer, TokenizerData.MatchInfo> allMatches =
        tokenizerData.allMatches;

    // A bit ugly.

    BitSet toSkip = new BitSet(allMatches.size());
    BitSet toSpecial = new BitSet(allMatches.size());
    BitSet toMore = new BitSet(allMatches.size());
    BitSet toToken = new BitSet(allMatches.size());
    int[] newStates = new int[allMatches.size()];
    toSkip.set(allMatches.size() + 1, true);
    toToken.set(allMatches.size() + 1, true);
    toMore.set(allMatches.size() + 1, true);
    toSpecial.set(allMatches.size() + 1, true);
    // Kind map.
    codeGenerator.genCodeLine("static const JJString jjstrLiteralImages[] = {");

    int k = 0;
    for (int i : allMatches.keySet()) {
      TokenizerData.MatchInfo matchInfo = allMatches.get(i);
      switch(matchInfo.matchType) {
        case SKIP: toSkip.set(i); break;
        case SPECIAL_TOKEN: toSpecial.set(i); break;
        case MORE: toMore.set(i); break;
        case TOKEN: toToken.set(i); break;
      }
      newStates[i] = matchInfo.newLexState;
      String image = matchInfo.image;
      if (k++ > 0) codeGenerator.genCodeLine(", ");
      if (image != null) {
        codeGenerator.genCode("\"");
        for (int j = 0; j < image.length(); j++) {
          if (image.charAt(j) <= 0xff) {
            codeGenerator.genCode(
                "\\" + Integer.toOctalString(image.charAt(j)));
          } else {
            String hexVal = Integer.toHexString(image.charAt(j));
            if (hexVal.length() == 3)
              hexVal = "0" + hexVal;
            codeGenerator.genCode("\\u" + hexVal);
          }
        }
        codeGenerator.genCode("\"");
      } else {
        codeGenerator.genCodeLine("\"\"");
      }
    }
    codeGenerator.genCodeLine("};");
    codeGenerator.genCodeLine();

    // Now generate the bit masks.
    generateBitVector("jjtoSkip", toSkip, codeGenerator);
    generateBitVector("jjtoSpecial", toSpecial, codeGenerator);
    generateBitVector("jjtoMore", toMore, codeGenerator);
    generateBitVector("jjtoToken", toToken, codeGenerator);

    codeGenerator.genCodeLine("static const int jjnewLexState[] = {");
    for (int i = 0; i < newStates.length; i++) {
      if (i > 0) codeGenerator.genCode(", ");
      //codeGenerator.genCode("0x" + Integer.toHexString(newStates[i]));
      codeGenerator.genCode("" + Integer.toString(newStates[i]));
    }
    codeGenerator.genCodeLine("};");
    codeGenerator.genCodeLine();

    // Action functions.

    final String staticString = Options.getStatic() ? "static " : "";
    // Token actions.
    codeGenerator.genCodeLine(
        staticString + "void " + tokenizerData.parserName + "TokenManager::TokenLexicalActions(Token * matchedToken) {");
    dumpLexicalActions(allMatches, TokenizerData.MatchType.TOKEN,
                       "matchedToken->kind", codeGenerator);
    codeGenerator.genCodeLine("}");

    codeGenerator.genCodeLine(staticString + "void " + tokenizerData.parserName + "TokenManager::SkipLexicalActions(Token * matchedToken) {");
    dumpLexicalActions(allMatches, TokenizerData.MatchType.SKIP, "jjmatchedKind", codeGenerator);
    dumpLexicalActions(allMatches, TokenizerData.MatchType.SPECIAL_TOKEN, "jjmatchedKind", codeGenerator);
    codeGenerator.genCodeLine("}");

    // More actions.
    codeGenerator.genCodeLine(staticString + "void " + tokenizerData.parserName + "TokenManager::MoreLexicalActions() {");
    codeGenerator.genCodeLine("jjimageLen += (lengthOfMatch = jjmatchedPos + 1);");
    dumpLexicalActions(allMatches, TokenizerData.MatchType.MORE,
                       "jjmatchedKind", codeGenerator);
    codeGenerator.genCodeLine("}");

    codeGenerator.genCodeLine("static const JJChar lexStateNames[] = {");
    for (int i = 0; i < tokenizerData.lexStateNames.length; i++) {
      if (i > 0) {
        codeGenerator.genCode(", ");
      }
      codeGenerator.genCode("\"" + tokenizerData.lexStateNames[i] + "\"");
    }
    codeGenerator.genCodeLine("};");
  }

  private void dumpLexicalActions(
      Map<Integer, TokenizerData.MatchInfo> allMatches,
      TokenizerData.MatchType matchType, String kindString,
      CodeGenHelper codeGenerator) {
    codeGenerator.genCodeLine("  switch(" + kindString + ") {");
    for (int i : allMatches.keySet()) {
      TokenizerData.MatchInfo matchInfo = allMatches.get(i);
      if (matchInfo.action == null ||
          matchInfo.matchType != matchType) {
        continue;
      }
      codeGenerator.genCodeLine("    case " + i + ": {\n");
      codeGenerator.genCodeLine("      " + matchInfo.action);
      codeGenerator.genCodeLine("      break;");
      codeGenerator.genCodeLine("    }");
    }
    codeGenerator.genCodeLine("    default: break;");
    codeGenerator.genCodeLine("  }");
  }

  private static void generateBitVector(String name, BitSet bits, CodeGenHelper codeGenerator) {
	codeGenerator.genCodeLine("static const long " + name + "[] = {");
    codeGenerator.genCode("   ");
    long[] longs = bits.toLongArray();
    for (int i = 0; i < longs.length; i++) {
      if (i > 0) codeGenerator.genCode(", ");
      codeGenerator.genCode("" + Long.toString(longs[i]));
    }
    codeGenerator.genCodeLine("};");
  }
}
