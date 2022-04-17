package org.skellig.plugin.language.testdata;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;

import static com.intellij.psi.TokenType.BAD_CHARACTER;
import static com.intellij.psi.TokenType.WHITE_SPACE;
import static org.skellig.plugin.language.testdata.psi.SkelligTestDataTypes.*;

%%

%{
  public _SkelligTestDataLexer() {
    this((java.io.Reader)null);
  }
%}

%public
%class _SkelligTestDataLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode

EOL=\R
WHITE_SPACE=\s+

NUMBER=[0-9]+(\.[0-9]*)?
SYMBOLS=[;:+|&\"?#!\^@\\*\w,><_-]+
SPACE=[ \t\n\x0B\f\r]+
COMMENT="//".*
BLOCK_COMMENT="/"\*([^*]|\*+[^*/])*(\*+"/")?
STRING=('([^'\\]|\\.)*'|\"([^\"\\]|\\.)*\")

%%
<YYINITIAL> {
  {WHITE_SPACE}                      { return WHITE_SPACE; }

  "regex:[^:=\\ \\n\\t\\f\\\\]"      { return KEY; }
  "="                                { return SEPARATOR; }
  "${"                               { return PARAMETEROPENBRACKET; }
  "{"                                { return OBJECTOPENBRACKET; }
  "}"                                { return OBJECTCLOSEBRACKET; }
  "["                                { return ARRAYOPENBRACKET; }
  "]"                                { return ARRAYCLOSEBRACKET; }
  "("                                { return FUNCTIONOPENBRACKET; }
  ")"                                { return FUNCTIONCLOSEBRACKET; }
  "."                                { return DOT; }
  "name"                             { return NAME; }
  "id"                               { return ID; }
  "if"                               { return IF; }
  "template"                         { return TEMPLATE; }
  "json"                             { return JSON; }
  "csv"                              { return CSV; }
  "variables"                        { return VARIABLES; }
  "message"                          { return MESSAGE; }
  "request"                          { return REQUEST; }
  "body"                             { return BODY; }
  "payload"                          { return PAYLOAD; }
  "validate"                         { return VALIDATE; }
  "assert"                           { return ASSERT; }
  "fromTest"                         { return FROMTEST; }

  {NUMBER}                           { return NUMBER; }
  {SYMBOLS}                          { return SYMBOLS; }
  {SPACE}                            { return SPACE; }
  {COMMENT}                          { return COMMENT; }
  {BLOCK_COMMENT}                    { return BLOCK_COMMENT; }
  {STRING}                           { return STRING; }

}

[^] { return BAD_CHARACTER; }
