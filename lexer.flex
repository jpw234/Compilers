
%%
%class Lexer
%unicode
%line
%column
%type Token

%{
public enum TokenType {
	ID, INTEGER, CHARACTER, STRING, KEYWORD, SYMBOL
}
public enum Subtype {
	LBRACE, RBRACE, LPAREN, RPAREN, LBRACKET, RBRACKET,
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
	private Token token(TokenType type, Subtype s) {
		return new Token(type, s, yyline, yycolumn);
	}
	private Token token(TokenType type, String value) {
		return new Token(type, value, yyline, yycolumn);
	}
	private Token token(TokenType type, int value) {
		return new Token(type, value, yyline, yycolumn);
	}
	
%}


LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]
WhiteSpace = {LineTerminator} | [ \t\f]

Comment = "//" {InputCharacter}* "\n"
Integer = [1-9] [0-9]* | 0
HexNumber = [0-9A-F]+

Letter = [a-zA-Z]
Identifier = {Letter} [a-zA-Z0-9_]*

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
	
	{WhiteSpace}					{ /* ignore */ }
	{Comment} 						{ /* ignore */ }
	
	{Integer}						{ return token(TokenType.INTEGER, Integer.parseInt(yytext())); }
	{Identifier}					{ return token(TokenType.ID, yytext()); }
	
	\"								{ string.setLength(0); yybegin(STRING); }
	\'								{ string.setLength(0); yybegin(CHAR); }
}

<STRING> {
	\"								{ yybegin(YYINITIAL); 
									  return token(TokenType.STRING, string.toString()); }
									  
	[^\n\r\"\\]+					{ string.append(yytext()); }
	\\t 							{ string.append('\t'); }
	\\n 							{ string.append('\n'); }
	\\r 							{ string.append('\r'); }
	\\\" 							{ string.append('\"'); }
	\\								{ string.append('\\'); }
	\\x {HexNumber}					{ int k = Integer.parseInt(yytext().substring(2), 16);
									  if(k > 31 && k < 127) string.append((char) k);
									  else string.append(yytext()); }
	\\ {Integer}					{ int k = Integer.parseInt(yytext().substring(2));
									  if(k > 31 && k < 127) string.append((char) k);
									  else string.append(yytext()); }
}

<CHAR> {
	\'								{ yybegin(YYINITIAL);
									  return token(TokenType.CHARACTER, string.toString()); }
	
	[^\n\r\"\\]+					{ string.append(yytext()); }
	\\t 							{ string.append('\t'); }
	\\n 							{ string.append('\n'); }
	\\r 							{ string.append('\r'); }
	\\\" 							{ string.append('\"'); }
	\\								{ string.append('\\'); }
	\\x {HexNumber}					{ int k = Integer.parseInt(yytext().substring(2), 16);
									  if(k > 31 && k < 127) string.append((char) k);
									  else string.append(yytext()); }
	\\ {Integer}					{ int k = Integer.parseInt(yytext().substring(2));
									  if(k > 31 && k < 127) string.append((char) k);
									  else string.append(yytext()); }
}

/* error fallback */
[^] 								{ throw new Error("Illegal character <"+
									  yytext()+">"); }

