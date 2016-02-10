/* The following code was generated by JFlex 1.6.1 */

package compiler_ww424;


/**
 * This class is a scanner generated by 
 * <a href="http://www.jflex.de/">JFlex</a> 1.6.1
 * from the specification file <tt>lexer.flex</tt>
 */
class Lexer {

  /** This character denotes the end of file */
  public static final int YYEOF = -1;

  /** initial size of the lookahead buffer */
  private static final int ZZ_BUFFERSIZE = 16384;

  /** lexical states */
  public static final int YYINITIAL = 0;
  public static final int STRING = 2;
  public static final int CHAR = 4;

  /**
   * ZZ_LEXSTATE[l] is the state in the DFA for the lexical state l
   * ZZ_LEXSTATE[l+1] is the state in the DFA for the lexical state l
   *                  at the beginning of a line
   * l is of the form l = 2*k, k a non negative integer
   */
  private static final int ZZ_LEXSTATE[] = { 
     0,  0,  1,  1,  2, 2
  };

  /** 
   * Translates characters to character classes
   */
  private static final String ZZ_CMAP_PACKED = 
    "\11\0\1\3\1\2\1\0\1\3\1\1\22\0\1\3\1\34\1\55"+
    "\2\0\1\50\1\51\1\11\1\37\1\40\1\47\1\45\1\54\1\46"+
    "\1\0\1\4\1\6\11\5\1\43\1\44\1\32\1\31\1\33\2\0"+
    "\6\7\24\10\1\35\1\56\1\36\1\0\1\53\1\0\1\26\1\27"+
    "\2\10\1\14\1\16\1\25\1\20\1\15\2\10\1\21\1\10\1\24"+
    "\1\30\2\10\1\22\1\13\1\23\1\12\1\10\1\17\1\57\2\10"+
    "\1\41\1\52\1\42\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uff92\0";

  /** 
   * Translates characters to character classes
   */
  private static final char [] ZZ_CMAP = zzUnpackCMap(ZZ_CMAP_PACKED);

  /** 
   * Translates DFA states to action switch labels.
   */
  private static final int [] ZZ_ACTION = zzUnpackAction();

  private static final String ZZ_ACTION_PACKED_0 =
    "\3\0\1\1\2\2\1\3\2\4\1\5\1\6\11\5"+
    "\1\7\1\10\1\11\1\12\1\13\1\14\1\15\1\16"+
    "\1\17\1\20\1\21\1\22\1\23\1\24\1\25\1\26"+
    "\1\27\1\30\1\31\1\32\1\33\1\34\1\35\1\1"+
    "\1\34\1\36\1\37\1\2\2\5\1\40\7\5\1\41"+
    "\1\42\1\43\1\44\2\45\1\46\1\47\1\50\1\51"+
    "\1\37\1\0\1\52\1\5\1\53\6\5\1\0\1\54"+
    "\1\55\4\5\1\56\1\57\1\60\1\61\1\62\2\5"+
    "\1\63\1\64";

  private static int [] zzUnpackAction() {
    int [] result = new int[95];
    int offset = 0;
    offset = zzUnpackAction(ZZ_ACTION_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAction(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /** 
   * Translates a state to a row index in the transition table
   */
  private static final int [] ZZ_ROWMAP = zzUnpackRowMap();

  private static final String ZZ_ROWMAP_PACKED_0 =
    "\0\0\0\60\0\140\0\220\0\300\0\220\0\360\0\u0120"+
    "\0\220\0\u0150\0\220\0\u0180\0\u01b0\0\u01e0\0\u0210\0\u0240"+
    "\0\u0270\0\u02a0\0\u02d0\0\u0300\0\u0330\0\u0360\0\u0390\0\u03c0"+
    "\0\220\0\220\0\220\0\220\0\220\0\220\0\220\0\220"+
    "\0\220\0\220\0\220\0\220\0\220\0\220\0\220\0\220"+
    "\0\220\0\u03f0\0\220\0\u0420\0\u0450\0\220\0\u0480\0\u04b0"+
    "\0\u04e0\0\u0510\0\u0150\0\u0540\0\u0570\0\u05a0\0\u05d0\0\u0600"+
    "\0\u0630\0\u0660\0\220\0\220\0\220\0\220\0\u0690\0\u06c0"+
    "\0\220\0\220\0\220\0\220\0\220\0\u06f0\0\u0150\0\u0720"+
    "\0\u0150\0\u0750\0\u0780\0\u07b0\0\u07e0\0\u0810\0\u0840\0\u0870"+
    "\0\u06f0\0\u0150\0\u08a0\0\u08d0\0\u0900\0\u0930\0\u0150\0\u0150"+
    "\0\u0870\0\u0150\0\u0150\0\u0960\0\u0990\0\u0150\0\u0150";

  private static int [] zzUnpackRowMap() {
    int [] result = new int[95];
    int offset = 0;
    offset = zzUnpackRowMap(ZZ_ROWMAP_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackRowMap(String packed, int offset, int [] result) {
    int i = 0;  /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int high = packed.charAt(i++) << 16;
      result[j++] = high | packed.charAt(i++);
    }
    return j;
  }

  /** 
   * The transition table of the DFA
   */
  private static final int [] ZZ_TRANS = zzUnpackTrans();

  private static final String ZZ_TRANS_PACKED_0 =
    "\1\4\1\5\2\6\1\7\1\10\1\11\2\12\1\13"+
    "\1\14\1\12\1\15\1\16\1\17\1\20\1\12\1\21"+
    "\1\22\1\23\3\12\1\24\1\12\1\25\1\26\1\27"+
    "\1\30\1\31\1\32\1\33\1\34\1\35\1\36\1\37"+
    "\1\40\1\41\1\42\1\43\1\44\1\45\1\46\1\47"+
    "\1\50\1\51\1\4\1\12\1\52\2\4\52\52\1\53"+
    "\1\54\1\52\1\55\2\4\6\55\1\56\44\55\1\57"+
    "\1\55\62\0\1\6\61\0\1\60\60\0\2\10\56\0"+
    "\24\12\22\0\1\12\3\0\1\12\5\0\6\12\1\61"+
    "\15\12\22\0\1\12\3\0\1\12\5\0\14\12\1\62"+
    "\7\12\22\0\1\12\3\0\1\12\5\0\11\12\1\63"+
    "\5\12\1\64\4\12\22\0\1\12\3\0\1\12\5\0"+
    "\21\12\1\65\2\12\22\0\1\12\3\0\1\12\5\0"+
    "\13\12\1\66\10\12\22\0\1\12\3\0\1\12\5\0"+
    "\7\12\1\67\14\12\22\0\1\12\3\0\1\12\5\0"+
    "\7\12\1\70\14\12\22\0\1\12\3\0\1\12\5\0"+
    "\15\12\1\71\6\12\22\0\1\12\3\0\1\12\5\0"+
    "\23\12\1\72\22\0\1\12\3\0\1\12\31\0\1\73"+
    "\57\0\1\74\57\0\1\75\57\0\1\76\26\0\1\52"+
    "\2\0\52\52\2\0\1\52\5\0\1\77\1\100\13\0"+
    "\1\101\1\102\1\103\30\0\1\104\1\105\1\106\1\55"+
    "\2\0\6\55\1\0\44\55\1\0\1\55\5\0\1\77"+
    "\1\100\13\0\1\101\1\102\1\103\30\0\1\104\2\0"+
    "\1\60\2\0\55\60\5\0\7\12\1\107\14\12\22\0"+
    "\1\12\3\0\1\12\5\0\6\12\1\110\15\12\22\0"+
    "\1\12\3\0\1\12\5\0\16\12\1\111\5\12\22\0"+
    "\1\12\3\0\1\12\5\0\14\12\1\112\7\12\22\0"+
    "\1\12\3\0\1\12\5\0\10\12\1\113\13\12\22\0"+
    "\1\12\3\0\1\12\5\0\17\12\1\114\4\12\22\0"+
    "\1\12\3\0\1\12\5\0\16\12\1\115\5\12\22\0"+
    "\1\12\3\0\1\12\5\0\5\12\1\116\16\12\22\0"+
    "\1\12\3\0\1\12\5\0\23\12\1\117\22\0\1\12"+
    "\3\0\1\12\5\0\2\77\130\0\1\120\5\0\3\121"+
    "\55\0\7\12\1\122\14\12\22\0\1\12\3\0\1\12"+
    "\5\0\6\12\1\123\15\12\22\0\1\12\3\0\1\12"+
    "\5\0\14\12\1\124\7\12\22\0\1\12\3\0\1\12"+
    "\5\0\20\12\1\125\3\12\22\0\1\12\3\0\1\12"+
    "\5\0\5\12\1\126\16\12\22\0\1\12\3\0\1\12"+
    "\5\0\7\12\1\127\14\12\22\0\1\12\3\0\1\12"+
    "\5\0\14\12\1\130\7\12\22\0\1\12\3\0\1\12"+
    "\5\0\3\131\55\0\7\12\1\132\14\12\22\0\1\12"+
    "\3\0\1\12\5\0\7\12\1\133\14\12\22\0\1\12"+
    "\3\0\1\12\5\0\16\12\1\134\5\12\22\0\1\12"+
    "\3\0\1\12\5\0\15\12\1\135\6\12\22\0\1\12"+
    "\3\0\1\12\5\0\13\12\1\136\10\12\22\0\1\12"+
    "\3\0\1\12\5\0\17\12\1\137\4\12\22\0\1\12"+
    "\3\0\1\12";

  private static int [] zzUnpackTrans() {
    int [] result = new int[2496];
    int offset = 0;
    offset = zzUnpackTrans(ZZ_TRANS_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackTrans(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      value--;
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /* error codes */
  private static final int ZZ_UNKNOWN_ERROR = 0;
  private static final int ZZ_NO_MATCH = 1;
  private static final int ZZ_PUSHBACK_2BIG = 2;

  /* error messages for the codes above */
  private static final String ZZ_ERROR_MSG[] = {
    "Unknown internal scanner error",
    "Error: could not match input",
    "Error: pushback value was too large"
  };

  /**
   * ZZ_ATTRIBUTE[aState] contains the attributes of state <code>aState</code>
   */
  private static final int [] ZZ_ATTRIBUTE = zzUnpackAttribute();

  private static final String ZZ_ATTRIBUTE_PACKED_0 =
    "\3\0\1\11\1\1\1\11\2\1\1\11\1\1\1\11"+
    "\15\1\21\11\1\1\1\11\2\1\1\11\14\1\4\11"+
    "\2\1\5\11\1\0\11\1\1\0\17\1";

  private static int [] zzUnpackAttribute() {
    int [] result = new int[95];
    int offset = 0;
    offset = zzUnpackAttribute(ZZ_ATTRIBUTE_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAttribute(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }

  /** the input device */
  private java.io.Reader zzReader;

  /** the current state of the DFA */
  private int zzState;

  /** the current lexical state */
  private int zzLexicalState = YYINITIAL;

  /** this buffer contains the current text to be matched and is
      the source of the yytext() string */
  private char zzBuffer[] = new char[ZZ_BUFFERSIZE];

  /** the textposition at the last accepting state */
  private int zzMarkedPos;

  /** the current text position in the buffer */
  private int zzCurrentPos;

  /** startRead marks the beginning of the yytext() string in the buffer */
  private int zzStartRead;

  /** endRead marks the last character in the buffer, that has been read
      from input */
  private int zzEndRead;

  /** number of newlines encountered up to the start of the matched text */
  private int yyline;

  /** the number of characters up to the start of the matched text */
  private int yychar;

  /**
   * the number of characters from the last newline up to the start of the 
   * matched text
   */
  private int yycolumn;

  /** 
   * zzAtBOL == true <=> the scanner is currently at the beginning of a line
   */
  private boolean zzAtBOL = true;

  /** zzAtEOF == true <=> the scanner is at the EOF */
  private boolean zzAtEOF;

  /** denotes if the user-EOF-code has already been executed */
  private boolean zzEOFDone;
  
  /** 
   * The number of occupied positions in zzBuffer beyond zzEndRead.
   * When a lead/high surrogate has been read from the input stream
   * into the final zzBuffer position, this will have a value of 1;
   * otherwise, it will have a value of 0.
   */
  private int zzFinalHighSurrogate = 0;

  /* user code: */
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
  


  /**
   * Creates a new scanner
   *
   * @param   in  the java.io.Reader to read input from.
   */
  Lexer(java.io.Reader in) {
    this.zzReader = in;
  }


  /** 
   * Unpacks the compressed character translation table.
   *
   * @param packed   the packed character translation table
   * @return         the unpacked character translation table
   */
  private static char [] zzUnpackCMap(String packed) {
    char [] map = new char[0x110000];
    int i = 0;  /* index in packed string  */
    int j = 0;  /* index in unpacked array */
    while (i < 160) {
      int  count = packed.charAt(i++);
      char value = packed.charAt(i++);
      do map[j++] = value; while (--count > 0);
    }
    return map;
  }


  /**
   * Refills the input buffer.
   *
   * @return      <code>false</code>, iff there was new input.
   * 
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  private boolean zzRefill() throws java.io.IOException {

    /* first: make room (if you can) */
    if (zzStartRead > 0) {
      zzEndRead += zzFinalHighSurrogate;
      zzFinalHighSurrogate = 0;
      System.arraycopy(zzBuffer, zzStartRead,
                       zzBuffer, 0,
                       zzEndRead-zzStartRead);

      /* translate stored positions */
      zzEndRead-= zzStartRead;
      zzCurrentPos-= zzStartRead;
      zzMarkedPos-= zzStartRead;
      zzStartRead = 0;
    }

    /* is the buffer big enough? */
    if (zzCurrentPos >= zzBuffer.length - zzFinalHighSurrogate) {
      /* if not: blow it up */
      char newBuffer[] = new char[zzBuffer.length*2];
      System.arraycopy(zzBuffer, 0, newBuffer, 0, zzBuffer.length);
      zzBuffer = newBuffer;
      zzEndRead += zzFinalHighSurrogate;
      zzFinalHighSurrogate = 0;
    }

    /* fill the buffer with new input */
    int requested = zzBuffer.length - zzEndRead;
    int numRead = zzReader.read(zzBuffer, zzEndRead, requested);

    /* not supposed to occur according to specification of java.io.Reader */
    if (numRead == 0) {
      throw new java.io.IOException("Reader returned 0 characters. See JFlex examples for workaround.");
    }
    if (numRead > 0) {
      zzEndRead += numRead;
      /* If numRead == requested, we might have requested to few chars to
         encode a full Unicode character. We assume that a Reader would
         otherwise never return half characters. */
      if (numRead == requested) {
        if (Character.isHighSurrogate(zzBuffer[zzEndRead - 1])) {
          --zzEndRead;
          zzFinalHighSurrogate = 1;
        }
      }
      /* potentially more input available */
      return false;
    }

    /* numRead < 0 ==> end of stream */
    return true;
  }

    
  /**
   * Closes the input stream.
   */
  public final void yyclose() throws java.io.IOException {
    zzAtEOF = true;            /* indicate end of file */
    zzEndRead = zzStartRead;  /* invalidate buffer    */

    if (zzReader != null)
      zzReader.close();
  }


  /**
   * Resets the scanner to read from a new input stream.
   * Does not close the old reader.
   *
   * All internal variables are reset, the old input stream 
   * <b>cannot</b> be reused (internal buffer is discarded and lost).
   * Lexical state is set to <tt>ZZ_INITIAL</tt>.
   *
   * Internal scan buffer is resized down to its initial length, if it has grown.
   *
   * @param reader   the new input stream 
   */
  public final void yyreset(java.io.Reader reader) {
    zzReader = reader;
    zzAtBOL  = true;
    zzAtEOF  = false;
    zzEOFDone = false;
    zzEndRead = zzStartRead = 0;
    zzCurrentPos = zzMarkedPos = 0;
    zzFinalHighSurrogate = 0;
    yyline = yychar = yycolumn = 0;
    zzLexicalState = YYINITIAL;
    if (zzBuffer.length > ZZ_BUFFERSIZE)
      zzBuffer = new char[ZZ_BUFFERSIZE];
  }


  /**
   * Returns the current lexical state.
   */
  public final int yystate() {
    return zzLexicalState;
  }


  /**
   * Enters a new lexical state
   *
   * @param newState the new lexical state
   */
  public final void yybegin(int newState) {
    zzLexicalState = newState;
  }


  /**
   * Returns the text matched by the current regular expression.
   */
  public final String yytext() {
    return new String( zzBuffer, zzStartRead, zzMarkedPos-zzStartRead );
  }


  /**
   * Returns the character at position <tt>pos</tt> from the 
   * matched text. 
   * 
   * It is equivalent to yytext().charAt(pos), but faster
   *
   * @param pos the position of the character to fetch. 
   *            A value from 0 to yylength()-1.
   *
   * @return the character at position pos
   */
  public final char yycharat(int pos) {
    return zzBuffer[zzStartRead+pos];
  }


  /**
   * Returns the length of the matched text region.
   */
  public final int yylength() {
    return zzMarkedPos-zzStartRead;
  }


  /**
   * Reports an error that occured while scanning.
   *
   * In a wellformed scanner (no or only correct usage of 
   * yypushback(int) and a match-all fallback rule) this method 
   * will only be called with things that "Can't Possibly Happen".
   * If this method is called, something is seriously wrong
   * (e.g. a JFlex bug producing a faulty scanner etc.).
   *
   * Usual syntax/scanner level error handling should be done
   * in error fallback rules.
   *
   * @param   errorCode  the code of the errormessage to display
   */
  private void zzScanError(int errorCode) {
    String message;
    try {
      message = ZZ_ERROR_MSG[errorCode];
    }
    catch (ArrayIndexOutOfBoundsException e) {
      message = ZZ_ERROR_MSG[ZZ_UNKNOWN_ERROR];
    }

    throw new Error(message);
  } 


  /**
   * Pushes the specified amount of characters back into the input stream.
   *
   * They will be read again by then next call of the scanning method
   *
   * @param number  the number of characters to be read again.
   *                This number must not be greater than yylength()!
   */
  public void yypushback(int number)  {
    if ( number > yylength() )
      zzScanError(ZZ_PUSHBACK_2BIG);

    zzMarkedPos -= number;
  }


  /**
   * Resumes scanning until the next regular expression is matched,
   * the end of input is encountered or an I/O-Error occurs.
   *
   * @return      the next token
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  public Token yylex() throws java.io.IOException {
    int zzInput;
    int zzAction;

    // cached fields:
    int zzCurrentPosL;
    int zzMarkedPosL;
    int zzEndReadL = zzEndRead;
    char [] zzBufferL = zzBuffer;
    char [] zzCMapL = ZZ_CMAP;

    int [] zzTransL = ZZ_TRANS;
    int [] zzRowMapL = ZZ_ROWMAP;
    int [] zzAttrL = ZZ_ATTRIBUTE;

    while (true) {
      zzMarkedPosL = zzMarkedPos;

      boolean zzR = false;
      int zzCh;
      int zzCharCount;
      for (zzCurrentPosL = zzStartRead  ;
           zzCurrentPosL < zzMarkedPosL ;
           zzCurrentPosL += zzCharCount ) {
        zzCh = Character.codePointAt(zzBufferL, zzCurrentPosL, zzMarkedPosL);
        zzCharCount = Character.charCount(zzCh);
        switch (zzCh) {
        case '\u000B':
        case '\u000C':
        case '\u0085':
        case '\u2028':
        case '\u2029':
          yyline++;
          yycolumn = 0;
          zzR = false;
          break;
        case '\r':
          yyline++;
          yycolumn = 0;
          zzR = true;
          break;
        case '\n':
          if (zzR)
            zzR = false;
          else {
            yyline++;
            yycolumn = 0;
          }
          break;
        default:
          zzR = false;
          yycolumn += zzCharCount;
        }
      }

      if (zzR) {
        // peek one character ahead if it is \n (if we have counted one line too much)
        boolean zzPeek;
        if (zzMarkedPosL < zzEndReadL)
          zzPeek = zzBufferL[zzMarkedPosL] == '\n';
        else if (zzAtEOF)
          zzPeek = false;
        else {
          boolean eof = zzRefill();
          zzEndReadL = zzEndRead;
          zzMarkedPosL = zzMarkedPos;
          zzBufferL = zzBuffer;
          if (eof) 
            zzPeek = false;
          else 
            zzPeek = zzBufferL[zzMarkedPosL] == '\n';
        }
        if (zzPeek) yyline--;
      }
      zzAction = -1;

      zzCurrentPosL = zzCurrentPos = zzStartRead = zzMarkedPosL;
  
      zzState = ZZ_LEXSTATE[zzLexicalState];

      // set up zzAction for empty match case:
      int zzAttributes = zzAttrL[zzState];
      if ( (zzAttributes & 1) == 1 ) {
        zzAction = zzState;
      }


      zzForAction: {
        while (true) {
    
          if (zzCurrentPosL < zzEndReadL) {
            zzInput = Character.codePointAt(zzBufferL, zzCurrentPosL, zzEndReadL);
            zzCurrentPosL += Character.charCount(zzInput);
          }
          else if (zzAtEOF) {
            zzInput = YYEOF;
            break zzForAction;
          }
          else {
            // store back cached positions
            zzCurrentPos  = zzCurrentPosL;
            zzMarkedPos   = zzMarkedPosL;
            boolean eof = zzRefill();
            // get translated positions and possibly new buffer
            zzCurrentPosL  = zzCurrentPos;
            zzMarkedPosL   = zzMarkedPos;
            zzBufferL      = zzBuffer;
            zzEndReadL     = zzEndRead;
            if (eof) {
              zzInput = YYEOF;
              break zzForAction;
            }
            else {
              zzInput = Character.codePointAt(zzBufferL, zzCurrentPosL, zzEndReadL);
              zzCurrentPosL += Character.charCount(zzInput);
            }
          }
          int zzNext = zzTransL[ zzRowMapL[zzState] + zzCMapL[zzInput] ];
          if (zzNext == -1) break zzForAction;
          zzState = zzNext;

          zzAttributes = zzAttrL[zzState];
          if ( (zzAttributes & 1) == 1 ) {
            zzAction = zzState;
            zzMarkedPosL = zzCurrentPosL;
            if ( (zzAttributes & 8) == 8 ) break zzForAction;
          }

        }
      }

      // store back cached position
      zzMarkedPos = zzMarkedPosL;

      if (zzInput == YYEOF && zzStartRead == zzCurrentPos) {
        zzAtEOF = true;
        return null;
      }
      else {
        switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction]) {
          case 1: 
            { return token(TokenType.ERROR, "Illegal character <"+yytext()+">");
            }
          case 53: break;
          case 2: 
            { /* ignore */
            }
          case 54: break;
          case 3: 
            { return token(TokenType.SYMBOL, Subtype.DIV);
            }
          case 55: break;
          case 4: 
            { return token(TokenType.INTEGER, Integer.parseInt(yytext()));
            }
          case 56: break;
          case 5: 
            { return token(TokenType.ID, yytext());
            }
          case 57: break;
          case 6: 
            { string.setLength(0); line = yyline; col = yycolumn; yybegin(CHAR);
            }
          case 58: break;
          case 7: 
            { return token(TokenType.SYMBOL, Subtype.EQ);
            }
          case 59: break;
          case 8: 
            { return token(TokenType.SYMBOL, Subtype.LANGLE);
            }
          case 60: break;
          case 9: 
            { return token(TokenType.SYMBOL, Subtype.RANGLE);
            }
          case 61: break;
          case 10: 
            { return token(TokenType.SYMBOL, Subtype.NEGATION);
            }
          case 62: break;
          case 11: 
            { return token(TokenType.SYMBOL, Subtype.LBRACE);
            }
          case 63: break;
          case 12: 
            { return token(TokenType.SYMBOL, Subtype.RBRACE);
            }
          case 64: break;
          case 13: 
            { return token(TokenType.SYMBOL, Subtype.LPAREN);
            }
          case 65: break;
          case 14: 
            { return token(TokenType.SYMBOL, Subtype.RPAREN);
            }
          case 66: break;
          case 15: 
            { return token(TokenType.SYMBOL, Subtype.LBRACKET);
            }
          case 67: break;
          case 16: 
            { return token(TokenType.SYMBOL, Subtype.RBRACKET);
            }
          case 68: break;
          case 17: 
            { return token(TokenType.SYMBOL, Subtype.COLON);
            }
          case 69: break;
          case 18: 
            { return token(TokenType.SYMBOL, Subtype.SEMI);
            }
          case 70: break;
          case 19: 
            { return token(TokenType.SYMBOL, Subtype.PLUS);
            }
          case 71: break;
          case 20: 
            { return token(TokenType.SYMBOL, Subtype.MINUS);
            }
          case 72: break;
          case 21: 
            { return token(TokenType.SYMBOL, Subtype.TIMES);
            }
          case 73: break;
          case 22: 
            { return token(TokenType.SYMBOL, Subtype.MOD);
            }
          case 74: break;
          case 23: 
            { return token(TokenType.SYMBOL, Subtype.AND);
            }
          case 75: break;
          case 24: 
            { return token(TokenType.SYMBOL, Subtype.OR);
            }
          case 76: break;
          case 25: 
            { return token(TokenType.SYMBOL, Subtype.UNDERSCORE);
            }
          case 77: break;
          case 26: 
            { return token(TokenType.SYMBOL, Subtype.COMMA);
            }
          case 78: break;
          case 27: 
            { string.setLength(0); line = yyline; col = yycolumn; yybegin(STRING);
            }
          case 79: break;
          case 28: 
            { string.append(yytext());
            }
          case 80: break;
          case 29: 
            { yybegin(YYINITIAL); 
                    return token(TokenType.STRING, string.toString());
            }
          case 81: break;
          case 30: 
            { yybegin(YYINITIAL);
                    if(string.length() == 0) return token(TokenType.ERROR, "error:empty character literal");
                    return token(TokenType.CHARACTER, string.toString());
            }
          case 82: break;
          case 31: 
            { string.append('\\');
            }
          case 83: break;
          case 32: 
            { return token(TokenType.KEYWORD, Subtype.IF);
            }
          case 84: break;
          case 33: 
            { return token(TokenType.SYMBOL, Subtype.EQEQ);
            }
          case 85: break;
          case 34: 
            { return token(TokenType.SYMBOL, Subtype.LEQ);
            }
          case 86: break;
          case 35: 
            { return token(TokenType.SYMBOL, Subtype.GEQ);
            }
          case 87: break;
          case 36: 
            { return token(TokenType.SYMBOL, Subtype.NEQ);
            }
          case 88: break;
          case 37: 
            { int k = Integer.parseInt(yytext().substring(1));
                    if(k > 31 && k < 127) string.append((char) k);
                    else string.append(yytext());
            }
          case 89: break;
          case 38: 
            { string.append("\\r");
            }
          case 90: break;
          case 39: 
            { string.append("\\t");
            }
          case 91: break;
          case 40: 
            { string.append("\\n");
            }
          case 92: break;
          case 41: 
            { string.append("\"");
            }
          case 93: break;
          case 42: 
            { return token(TokenType.KEYWORD, Subtype.USE);
            }
          case 94: break;
          case 43: 
            { return token(TokenType.KEYWORD, Subtype.INT);
            }
          case 95: break;
          case 44: 
            { int k = Integer.parseInt(yytext().substring(2), 16);
                    if(k > 31 && k < 127) string.append((char) k);
                    else string.append(yytext());
            }
          case 96: break;
          case 45: 
            { return token(TokenType.KEYWORD, Subtype.ELSE);
            }
          case 97: break;
          case 46: 
            { return token(TokenType.KEYWORD, Subtype.TRUE);
            }
          case 98: break;
          case 47: 
            { return token(TokenType.KEYWORD, Subtype.BOOL);
            }
          case 99: break;
          case 48: 
            { int k = Integer.parseInt(yytext().substring(3), 16);
                    if(k > 31 && k < 127) string.append((char) k);
                    else string.append(yytext());
            }
          case 100: break;
          case 49: 
            { return token(TokenType.KEYWORD, Subtype.FALSE);
            }
          case 101: break;
          case 50: 
            { return token(TokenType.KEYWORD, Subtype.WHILE);
            }
          case 102: break;
          case 51: 
            { return token(TokenType.KEYWORD, Subtype.LENGTH);
            }
          case 103: break;
          case 52: 
            { return token(TokenType.KEYWORD, Subtype.RETURN);
            }
          case 104: break;
          default:
            zzScanError(ZZ_NO_MATCH);
        }
      }
    }
  }


}