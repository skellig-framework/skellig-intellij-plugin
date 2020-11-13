package org.skellig.plugin.language.feature;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;

import static com.intellij.psi.TokenType.BAD_CHARACTER;
import static com.intellij.psi.TokenType.WHITE_SPACE;
import static org.skellig.plugin.language.feature.psi.SkelligFeatureTypes.*;

%%

%{
  public SkelligFeatureLexer() {
    this((java.io.Reader)null);
  }
%}

%public
%class SkelligFeatureLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode

EOL=\R
WHITE_SPACE=\s+

SYMBOLS=[;+&\"?#!\\*\w,_-]+
SPACE=[ \t\n\x0B\f\r]+
COMMENT="//".*
BLOCK_COMMENT="/"\*([^*]|\*+[^*/])*(\*+"/")?
STRING=('([^'\\]|\\.)*'|\"([^\"\\]|\\.)*\")
TAG_REGEX=@[a-zA-Z0-9_]+
PARAM_REGEX=<[a-zA-Z0-9_-]+>

%%
<YYINITIAL> {
  {WHITE_SPACE}        { return WHITE_SPACE; }

  "<"                  { return L_PARAM; }
  ">"                  { return R_PARAM; }
  "|"                  { return PARAM_SEPARATOR; }
  ":"                  { return COLON; }
  "Name"               { return NAME; }
  "Data"               { return DATA; }
  "Test"               { return TEST; }
  "Steps"              { return STEPS; }
  "keyword_space"      { return KEYWORD_SPACE; }
  "tag"                { return TAG; }

  {SYMBOLS}            { return SYMBOLS; }
  {SPACE}              { return SPACE; }
  {COMMENT}            { return COMMENT; }
  {BLOCK_COMMENT}      { return BLOCK_COMMENT; }
  {STRING}             { return STRING; }
  {TAG_REGEX}          { return TAG_REGEX; }
  {PARAM_REGEX}        { return PARAM_REGEX; }

}

[^] { return BAD_CHARACTER; }
