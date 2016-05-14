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

public class Token extends Symbol{
	protected int column, line;
  public Token(int symbol,int lin,int col) {
      super(symbol);
      column  = col;
      line    = lin;
	  left = lin;
	  right = col;
  }
  public Token(int symbol,String val,int lin,int col) {
      super(symbol);
      column  = col;
      line    = lin;
      value   = val;
	  left = lin;
	  right = col;
  }
  public Token(int symbol,long val,int lin,int col) {
      super(symbol);
      column  = col;
      line    = lin;
      value   = new Long(val);
	  left = lin;
	  right = col;
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
		if(type == sym.STRING || type == sym.CHARACTER || type == sym.error) {
			return new Token(type, value, line, col);
		}
		else {
			return new Token(type, value, yyline, yycolumn);
		}
	}
	private Token token(int type, long value) throws IOException{
		return new Token(type, value, yyline, yycolumn);
	}
	
%}
//%eofval{
//	return new Token(0, 0, line, col); 
//%eofval}
//%eofval{
//	return new Token(sym.EOF, 0, 0); 
//%eofval}

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
	"use" 							{ return token(sym.USE, "use"); }
	"if"							{ return token(sym.IF, "if"); }
	"while"							{ return token(sym.WHILE, "while"); }
	"else"							{ return token(sym.ELSE,"else"); }
	"return"						{ return token(sym.RETURN,"return"); }
	"length"						{ return token(sym.LENGTH,"length"); }
	"true"							{ return token(sym.TRUE,"true"); }
	"false"							{ return token(sym.FALSE,"false"); }
	"int"							{ return token(sym.INT,"int"); }
	"bool" 							{ return token(sym.BOOL,"bool"); }
	"class"							{ return token(sym.CLASS, "class"); }
	"new"							{ return token(sym.NEW, "new"); }
	"this"							{ return token(sym.THIS, "this"); }
	"extends"						{ return token(sym.EXTENDS, "extends"); }
	"null"							{ return token(sym.NULL, "null"); }
	"break"							{ return token(sym.BREAK, "break"); }
	
	/*symbols*/
	"="								{ return token(sym.EQ,"="); }
	"<="							{ return token(sym.LEQ,"<="); }
	">="							{ return token(sym.GEQ,">="); }
	"=="							{ return token(sym.EQEQ,"=="); }
	"!="							{ return token(sym.NEQ,"!="); }
	"["								{ return token(sym.LBRACKET,"["); }
	"]"								{ return token(sym.RBRACKET,"]"); }
	"("								{ return token(sym.LPAREN,"("); }
	")"								{ return token(sym.RPAREN,")"); }
	"{"								{ return token(sym.LBRACE,"{"); }
	"}"								{ return token(sym.RBRACE,"}"); }
	":"								{ return token(sym.COLON,":"); }
	";"								{ return token(sym.SEMI,";"); }
	"+"								{ return token(sym.PLUS,"+"); }
	"-"								{ return token(sym.MINUS,"-"); }
	"*"								{ return token(sym.TIMES,"*"); }
	"/"								{ return token(sym.DIV,"/"); }
	"%"								{ return token(sym.MOD,"%"); }
	"!"								{ return token(sym.NOT,"!"); }
	"<"								{ return token(sym.LANGLE,"<"); }
	">"								{ return token(sym.RANGLE,">"); }
	"&"								{ return token(sym.AND,"&"); }
	"|"								{ return token(sym.OR,"|"); }
	"_"								{ return token(sym.UNDERSCORE,"_"); }
	","								{ return token(sym.COMMA,","); }
	"*>>"							{ return token(sym.HIGHMUL,"*>>");}
	"."								{ return token(sym.DOT, "."); }
	
	{WhiteSpace}					{ /* ignore */ }
	{Comment} 						{ /* ignore */ }
	
	{Integer}						{ return token(sym.INTEGER, Long.parseLong(yytext())); }
	{Identifier}					{ return token(sym.ID, yytext()); }
	
	\"								{ string.setLength(0); line = yyline; col = yycolumn; yybegin(STRING); }
	\'								{ string.setLength(0); line = yyline; col = yycolumn; yybegin(CHAR); }
}

<STRING> {
	\"								{ yybegin(YYINITIAL); 
									  return token(sym.STRING, string.toString()); }
									  
	[^\n\r\"\\]+					{ string.append(yytext()); }
	\\t 							{ string.append("\t"); }
	\\n 							{ string.append("\n"); }
	\\r 							{ string.append("\r"); }
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
									  if(string.length() == 0) return token(sym.error, "error:empty character literal");
									  return token(sym.CHARACTER, string.toString()); }
	
	[^\n\r\'\\]+					{ string.append(yytext()); }
	\\t								{ string.append("\t"); }
	\\n 							{ string.append("\n"); }
	\\r 							{ string.append("\r"); }
	\\\" 							{ string.append("\""); }
	\\\' 							{ string.append("\'"); }
	\\								{ string.append('\\'); }
	\\0x {HexNumber}				{ int k = Integer.parseInt(yytext().substring(3), 16);
									  if(k > 31 && k < 127) string.append((char) k);
									  else string.append(yytext()); }
	\\ {Integer}					{ int k = Integer.parseInt(yytext().substring(1));
									  if(k > 31 && k < 127) string.append((char) k);
									  else string.append(yytext()); }
}

/* error fallback */
[^] 								{ return token(sym.error, "Illegal character <"+yytext()+">"); }

<STRING><<EOF>> {
 	return token(sym.error, "error: EOF encountered in String");
}

<CHAR><<EOF>> {
	return token(sym.error, "error: EOF encountered in Char");
}

<<EOF>> {
	return new Token(sym.EOF, 0, 0);
}