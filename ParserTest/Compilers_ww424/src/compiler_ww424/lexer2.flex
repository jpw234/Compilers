package compiler_ww424;
import java_cup.runtime.*;
%%
%class Lexer
%unicode
%line
%column
%cup

%{
/*public enum TokenType {
	LBRACE, RBRACE, LPAREN, RPAREN, LBRACKET, RBRACKET, COMMA,
	COLON, SEMI, EQ, EQEQ, LEQ, GEQ, NEQ, PLUS, MINUS, NEGATION, TIMES, DIV, MOD,
	LANGLE, RANGLE, AND, OR, 
  USE, IF, WHILE, ELSE, RETURN, LENGTH, TRUE, FALSE, INT, BOOL, UNDERSCORE,
  ID, INTEGER, CHARACTER, STRING
}*/
public class Token extends Symbol{
	protected int column, line;
  public Token(int symbol,int lin,int col) {
      super(symbol);
      column  = col;
      line    = lin;
  }
  public Token(int symbol,String val,int lin,int col) {
      super(symbol);
      column  = col;
      line    = lin;
      value   = val;
  }
  public Token(int symbol,int val,int lin,int col) {
      super(symbol);
      column  = col;
      line    = lin;
      value   = new Integer(val);
  }
	public int getCol() {
		return column;
	}
	public int getLine() {
		return line;
	}
}
	StringBuffer string = new StringBuffer();
	int line = 0;
	int col = 0;
	private Token token(int type) {
		return new Token(type, yyline, yycolumn);
	}
	private Token token(int type, String value) {
		if(type == sym.STRING || type == sym.CHARACTER) {
			return new Token(type, value, line, col);
		}
		else {
      throw new IOException("INVALID STRING TOKEN GENERATION. THIS SHOULD NEVER FIRE.");
      }
	}
	private Token token(int type, int value) {
    if(type == sym.INTEGER) {
			return new Token(type, value, yyline, yycolumn);
		}
    else {
        throw new IOException("INVALID INTEGER TOKEN GENERATION. THIS SHOULD NEVER FIRE.");
    }
	}
	
%}


LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]
WhiteSpace = {LineTerminator} | [ \t\f]

Comment = "//" {InputCharacter}*
Integer = [1-9] [0-9]* | 0
HexNumber = [0-9A-F]+

Letter = [a-zA-Z]
Identifier = {Letter} [a-zA-Z0-9_']*

%state STRING
%state CHAR

%%

<YYINITIAL> {
	/*keywords*/
	"use" 							{ return token(sym.USE); }
	"if"							{ return token(sym.IF); }
	"while"							{ return token(sym.WHILE); }
	"else"							{ return token(sym.ELSE); }
	"return"						{ return token(sym.RETURN); }
	"length"						{ return token(sym.LENGTH); }
	"true"							{ return token(sym.TRUE); }
	"false"							{ return token(sym.FALSE); }
	"int"							{ return token(sym.INT); }
	"bool" 							{ return token(sym.BOOL); }
	
	/*symbols*/
	"="								{ return token(sym.EQ); }
	"<="							{ return token(sym.LEQ); }
	">="							{ return token(sym.GEQ); }
	"=="							{ return token(sym.EQEQ); }
	"!="							{ return token(sym.NEQ); }
	"["								{ return token(sym.LBRACE); }
	"]"								{ return token(sym.RBRACE); }
	"("								{ return token(sym.LPAREN); }
	")"								{ return token(sym.RPAREN); }
	"{"								{ return token(sym.LBRACKET); }
	"}"								{ return token(sym.RBRACKET); }
	":"								{ return token(sym.COLON); }
	";"								{ return token(sym.SEMI); }
	"+"								{ return token(sym.PLUS); }
	"-"								{ return token(sym.MINUS); }
	"*"								{ return token(sym.TIMES); }
	"/"								{ return token(sym.DIV); }
	"%"								{ return token(sym.MOD); }
	"!"								{ return token(sym.NEGATION); }
	"<"								{ return token(sym.LANGLE); }
	">"								{ return token(sym.RANGLE); }
	"&"								{ return token(sym.AND); }
	"|"								{ return token(sym.OR); }
	"_"								{ return token(sym.UNDERSCORE); }
	","								{ return token(sym.COMMA); }
	
	{WhiteSpace}					{ /* ignore */ }
	{Comment} 						{ /* ignore */ }
	
	{Integer}						{ return token(sym.INTEGER, Integer.parseInt(yytext())); }
	{Identifier}					{ return token(sym.ID, yytext()); }
	
	\"								{ string.setLength(0); line = yyline; col = yycolumn; yybegin(STRING); }
	\'								{ string.setLength(0); line = yyline; col = yycolumn; yybegin(CHAR); }
}

<STRING> {
	\"								{ yybegin(YYINITIAL); 
									  return token(sym.STRING, string.toString()); }
									  
	[^\n\r\"\\]+					{ string.append(yytext()); }
	\\t 							{ string.append("\\t"); }
	\\n 							{ string.append("\\n"); }
	\\r 							{ string.append("\\r"); }
	\\\" 							{ string.append("\""); }
	\\\\							{ string.append('\\'); }
	\\0x {HexNumber}				{ int k = Integer.parseInt(yytext().substring(3), 16);
									  if(k > 31 && k < 127) string.append((char) k);
									  else string.append(yytext()); }
	\\x {HexNumber}					{ int k = Integer.parseInt(yytext().substring(2), 16);
									  if(k > 31 && k < 127) string.append((char) k);
									  else string.append(yytext()); }
	\\ {Integer}					{ int k = Integer.parseInt(yytext().substring(1));
									  if(k > 31 && k < 127) string.append((char) k);
									  else string.append(yytext()); }
}

<CHAR> {
	\'								{ yybegin(YYINITIAL);
									  if(string.length() == 0) throw IOException(line.toString() + ":" + col.toString() + " error:empty character literal");
									  return token(sym.CHARACTER, string.toString()); }
	
	[^\n\r\'\\]+					{ string.append(yytext()); }
	\\t								{ string.append("\\t"); }
	\\n 							{ string.append("\\n"); }
	\\r 							{ string.append("\\r"); }
	\\\" 							{ string.append("\""); }
	\\								{ string.append('\\'); }
	\\0x {HexNumber}				{ int k = Integer.parseInt(yytext().substring(3), 16);
									  if(k > 31 && k < 127) string.append((char) k);
									  else string.append(yytext()); }
	\\ {Integer}					{ int k = Integer.parseInt(yytext().substring(1));
									  if(k > 31 && k < 127) string.append((char) k);
									  else string.append(yytext()); }
}

/* error fallback */
[^] 								{ throw IOException(yyline.toString() + ":" + yycolumn.toString() + " " + "INVALID INTEGER TOKEN GENERATION. "Illegal character <"+yytext()+">");}
