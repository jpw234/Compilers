<<<<<<< HEAD
package compiler_ww424;

%%
%class Lexer
%unicode
%line
%column
%type Token

%{
public enum TokenType {
	ID, INTEGER, CHARACTER, STRING, KEYWORD, SYMBOL, ERROR
}
public enum Subtype {
	LBRACE, RBRACE, LPAREN, RPAREN, LBRACKET, RBRACKET, COMMA,
	COLON, SEMI, EQ, EQEQ, LEQ, GEQ, NEQ, PLUS, MINUS, NEGATION, TIMES, DIV, MOD,
	LANGLE, RANGLE, AND, OR, USE, IF, WHILE, ELSE, RETURN, LENGTH, TRUE, FALSE, INT, BOOL, UNDERSCORE
}
public class Token {
	
	private TokenType ttype;
	private String value;
	private int intVal;
	private int column, line;
	private Subtype stype;
	
	
	public Token(TokenType type, Subtype s, int l, int c){
		ttype = type;
		stype = s;
		column = c;
		line = l;
	}
	public Token(TokenType type, String val, int l, int c) {
		ttype = type;
		value = val;
		column = c;
		line = l;
	}
	public Token(TokenType type, int i, int l, int c) {
		ttype = type;
		intVal = i;
		column = c;
		line = l;
	}
	
	public TokenType getType() {
		return ttype;
	}
	public int getCol() {
		return column;
	}
	public int getLine() {
		return line;
	}
	public Subtype getSubtype() {
		return stype;
	}
	public String getValue() {
		return value;
	}
	public int getIntValue() {
		return intVal;
	}
}


	StringBuffer string = new StringBuffer();
	int line = 0;
	int col = 0;
	private Token token(TokenType type, Subtype s) {
		return new Token(type, s, yyline, yycolumn);
	}
	private Token token(TokenType type, String value) {
		if(type == TokenType.STRING || type == TokenType.CHARACTER || type == TokenType.ERROR) {
			return new Token(type, value, line, col);
		}
		else return new Token(type, value, yyline, yycolumn);
	}
	private Token token(TokenType type, int value) {
		return new Token(type, value, yyline, yycolumn);
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
	"use" 							{ return token(TokenType.KEYWORD, Subtype.USE); }
	"if"							{ return token(TokenType.KEYWORD, Subtype.IF); }
	"while"							{ return token(TokenType.KEYWORD, Subtype.WHILE); }
	"else"							{ return token(TokenType.KEYWORD, Subtype.ELSE); }
	"return"						{ return token(TokenType.KEYWORD, Subtype.RETURN); }
	"length"						{ return token(TokenType.KEYWORD, Subtype.LENGTH); }
	"true"							{ return token(TokenType.KEYWORD, Subtype.TRUE); }
	"false"							{ return token(TokenType.KEYWORD, Subtype.FALSE); }
	"int"							{ return token(TokenType.KEYWORD, Subtype.INT); }
	"bool" 							{ return token(TokenType.KEYWORD, Subtype.BOOL); }
	
	/*symbols*/
	"="								{ return token(TokenType.SYMBOL, Subtype.EQ); }
	"<="							{ return token(TokenType.SYMBOL, Subtype.LEQ); }
	">="							{ return token(TokenType.SYMBOL, Subtype.GEQ); }
	"=="							{ return token(TokenType.SYMBOL, Subtype.EQEQ); }
	"!="							{ return token(TokenType.SYMBOL, Subtype.NEQ); }
	"["								{ return token(TokenType.SYMBOL, Subtype.LBRACE); }
	"]"								{ return token(TokenType.SYMBOL, Subtype.RBRACE); }
	"("								{ return token(TokenType.SYMBOL, Subtype.LPAREN); }
	")"								{ return token(TokenType.SYMBOL, Subtype.RPAREN); }
	"{"								{ return token(TokenType.SYMBOL, Subtype.LBRACKET); }
	"}"								{ return token(TokenType.SYMBOL, Subtype.RBRACKET); }
	":"								{ return token(TokenType.SYMBOL, Subtype.COLON); }
	";"								{ return token(TokenType.SYMBOL, Subtype.SEMI); }
	"+"								{ return token(TokenType.SYMBOL, Subtype.PLUS); }
	"-"								{ return token(TokenType.SYMBOL, Subtype.MINUS); }
	"*"								{ return token(TokenType.SYMBOL, Subtype.TIMES); }
	"/"								{ return token(TokenType.SYMBOL, Subtype.DIV); }
	"%"								{ return token(TokenType.SYMBOL, Subtype.MOD); }
	"!"								{ return token(TokenType.SYMBOL, Subtype.NEGATION); }
	"<"								{ return token(TokenType.SYMBOL, Subtype.LANGLE); }
	">"								{ return token(TokenType.SYMBOL, Subtype.RANGLE); }
	"&"								{ return token(TokenType.SYMBOL, Subtype.AND); }
	"|"								{ return token(TokenType.SYMBOL, Subtype.OR); }
	"_"								{ return token(TokenType.SYMBOL, Subtype.UNDERSCORE); }
	","								{ return token(TokenType.SYMBOL, Subtype.COMMA); }
	
	{WhiteSpace}					{ /* ignore */ }
	{Comment} 						{ /* ignore */ }
	
	{Integer}						{ return token(TokenType.INTEGER, Integer.parseInt(yytext())); }
	{Identifier}					{ return token(TokenType.ID, yytext()); }
	
	\"								{ string.setLength(0); line = yyline; col = yycolumn; yybegin(STRING); }
	\'								{ string.setLength(0); line = yyline; col = yycolumn; yybegin(CHAR); }
}

<STRING> {
	\"								{ yybegin(YYINITIAL); 
									  return token(TokenType.STRING, string.toString()); }
									  
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
									  if(string.length() == 0) return token(TokenType.ERROR, "error:empty character literal");
									  return token(TokenType.CHARACTER, string.toString()); }
	
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
[^] 								{ return token(TokenType.ERROR, "Illegal character <"+yytext()+">"); }

=======
package compiler_ww424;
import java_cup.runtime.*;
import java.io.IOException; 
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
	private Token token(int type, String value) throws IOException{
		if(type == sym.STRING || type == sym.CHARACTER ||type == sym.ID) {
			return new Token(type, value, line, col);
		}
		else {
      throw new IOException("INVALID STRING TOKEN GENERATION. THIS SHOULD NEVER FIRE.");
      }
	}
	private Token token(int type, int value) throws IOException{
    if(type == sym.INTEGER) {
			return new Token(type, value, yyline, yycolumn);
		}
    else {
        throw new IOException("INVALID INTEGER TOKEN GENERATION. THIS SHOULD NEVER FIRE.");
    }
	}
	
%}
//%eofval{
//	return new Token(0, 0, line, col); 
//%eofval}
%eofval{
	return new Token(sym.EOF, 0, 0); 
%eofval}

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
	"["								{ return token(sym.LBRACKET); }
	"]"								{ return token(sym.RBRACKET); }
	"("								{ return token(sym.LPAREN); }
	")"								{ return token(sym.RPAREN); }
	"{"								{ return token(sym.LBRACE); }
	"}"								{ return token(sym.RBRACE); }
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
									  if(string.length() == 0) {
                      throw new IOException(Integer.toString(line+1) + 
                      ":" + Integer.toString(col+1) + " " + "error:empty character literal");
									  }
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
[^] 								{ throw new IOException(Integer.toString(yyline+1) + ":" + Integer.toString(yycolumn+1) + " " + "Illegal character <"+yytext()+">");}

>>>>>>> 2dc7cedc50198d761366675eb36819cb7ed19d4a
