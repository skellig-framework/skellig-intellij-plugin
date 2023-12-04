package org.skellig.plugin.language.teststep;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;

import static com.intellij.psi.TokenType.BAD_CHARACTER;
import static com.intellij.psi.TokenType.WHITE_SPACE;
import static org.skellig.plugin.language.teststep.psi.SkelligTestStepTypes.*;

%%

%{
  public SkelligTestStepLexer() {
    this((java.io.Reader)null);
  }
%}

%public
%class SkelligTestStepLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode

FLOAT=[0-9]+'.'[0-9]+
INT=[0-9]+
ID=[a-zA-Z0-9\-_]+
KEY_SYMBOLS=[._\-&'%$£!?`¬#~@\\:]+
KEYWORDS=(variables | validate | payload | body | request | message)
VALUE_SYMBOLS=[><|+/*]+
STRING=\"[^\"\\]*(\\.[^\"\\]*)*\"
NEWLINE=\r?\n
COMMENT="//".*

%%
<YYINITIAL> {
  "name"                { return NAME; }
  "="                   { return VALUE_ASSIGN; }
  "<="                  { return LESSER_EQUAL; }
  ">="                  { return GREATER_EQUAL; }
  "=="                  { return EQUAL; }
  "!="                  { return NOT_EQUAL; }
  ","                   { return COMMA; }
  "${"                  { return REFERENCE_BRACKET; }
  "{"                   { return OBJECT_L_BRACKET; }
  "}"                   { return OBJECT_R_BRACKET; }
  "("                   { return FUNCTION_L_BRACKET; }
  ")"                   { return FUNCTION_R_BRACKET; }
  "["                   { return ARRAY_L_BRACKET; }
  "]"                   { return ARRAY_R_BRACKET; }
  " "                   { return WHITE_SPACE; }

  {FLOAT}               { return FLOAT; }
  {INT}                 { return INT; }
  {ID}                  { return ID; }
  {KEY_SYMBOLS}         { return KEY_SYMBOLS; }
  {KEYWORDS}            { return KEYWORDS; }
  {VALUE_SYMBOLS}       { return VALUE_SYMBOLS; }
  {STRING}              { return STRING; }
  {NEWLINE}             { return NEWLINE; }
  {COMMENT}             { return COMMENT; }

}

[^] { return BAD_CHARACTER; }
