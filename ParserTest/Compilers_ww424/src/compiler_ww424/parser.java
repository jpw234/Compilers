
//----------------------------------------------------
// The following code was generated by CUP v0.11b 20150326
//----------------------------------------------------

package compiler_ww424;

import java_cup.runtime.*;

/** CUP v0.11b 20150326 generated parser.
  */
public class parser
 extends java_cup.runtime.lr_parser {

  @Override
  public final Class<?> getSymbolContainer() {
    return sym.class;
  }

  /** Default constructor. */
  @Deprecated
  public parser() {super();}

  /** Constructor which sets the default scanner. */
  @Deprecated
  public parser(java_cup.runtime.Scanner s) {super(s);}

  /** Constructor which sets the default scanner and a SymbolFactory. */
  public parser(java_cup.runtime.Scanner s, java_cup.runtime.SymbolFactory sf) {super(s,sf);}

  /** Production table. */
  protected static final short _production_table[][] = 
    unpackFromStrings(new String[] {
    "\000\013\000\002\002\004\000\002\002\004\000\002\002" +
    "\003\000\002\003\005\000\002\003\005\000\002\003\005" +
    "\000\002\003\005\000\002\003\003\000\002\003\003\000" +
    "\002\003\003\000\002\004\005" });

  /** Access to production table. */
  @Override
  public short[][] production_table() {return _production_table;}

  /** Parse-action table. */
  protected static final short[][] _action_table = 
    unpackFromStrings(new String[] {
    "\000\022\000\006\014\010\015\004\001\002\000\020\002" +
    "\ufffa\005\ufffa\006\ufffa\007\ufffa\010\ufffa\014\ufffa\015\ufffa" +
    "\001\002\000\010\002\024\014\010\015\004\001\002\000" +
    "\020\002\ufff8\005\ufff8\006\ufff8\007\ufff8\010\ufff8\014\ufff8" +
    "\015\ufff8\001\002\000\020\002\uffff\005\016\006\015\007" +
    "\013\010\014\014\uffff\015\uffff\001\002\000\022\002\ufff9" +
    "\005\ufff9\006\ufff9\007\ufff9\010\ufff9\013\011\014\ufff9\015" +
    "\ufff9\001\002\000\006\014\010\015\004\001\002\000\020" +
    "\002\ufff7\005\016\006\015\007\013\010\014\014\ufff7\015" +
    "\ufff7\001\002\000\006\014\010\015\004\001\002\000\006" +
    "\014\010\015\004\001\002\000\006\014\010\015\004\001" +
    "\002\000\006\014\010\015\004\001\002\000\020\002\ufffc" +
    "\005\ufffc\006\ufffc\007\013\010\014\014\ufffc\015\ufffc\001" +
    "\002\000\020\002\ufffb\005\ufffb\006\ufffb\007\013\010\014" +
    "\014\ufffb\015\ufffb\001\002\000\020\002\ufffd\005\ufffd\006" +
    "\ufffd\007\ufffd\010\ufffd\014\ufffd\015\ufffd\001\002\000\020" +
    "\002\ufffe\005\ufffe\006\ufffe\007\ufffe\010\ufffe\014\ufffe\015" +
    "\ufffe\001\002\000\020\002\001\005\016\006\015\007\013" +
    "\010\014\014\001\015\001\001\002\000\004\002\000\001" +
    "\002" });

  /** Access to parse-action table. */
  @Override
  public short[][] action_table() {return _action_table;}

  /** {@code reduce_goto} table. */
  protected static final short[][] _reduce_table = 
    unpackFromStrings(new String[] {
    "\000\022\000\010\002\004\003\006\004\005\001\001\000" +
    "\002\001\001\000\006\003\022\004\005\001\001\000\002" +
    "\001\001\000\002\001\001\000\002\001\001\000\006\003" +
    "\011\004\005\001\001\000\002\001\001\000\006\003\021" +
    "\004\005\001\001\000\006\003\020\004\005\001\001\000" +
    "\006\003\017\004\005\001\001\000\006\003\016\004\005" +
    "\001\001\000\002\001\001\000\002\001\001\000\002\001" +
    "\001\000\002\001\001\000\002\001\001\000\002\001\001" +
    "" });

  /** Access to {@code reduce_goto} table. */
  @Override
  public short[][] reduce_table() {return _reduce_table;}

  /** Instance of action encapsulation class. */
  protected CUP$parser$actions action_obj;

  /** Action encapsulation object initializer. */
  @Override
  protected void init_actions()
    {
      action_obj = new CUP$parser$actions(this);
    }

  /** Invoke a user supplied parse action. */
  @Override
  public java_cup.runtime.Symbol do_action(
    int                        act_num,
    java_cup.runtime.lr_parser parser,
    java.util.Stack<java_cup.runtime.Symbol> stack,
    int                        top)
    throws java.lang.Exception
  {
    /* call code in generated class */
    return action_obj.CUP$parser$do_action(act_num, parser, stack, top);
  }

  /** Indicates start state. */
  @Override
  public int start_state() {return 0;}
  /** Indicates start production. */
  @Override
  public int start_production() {return 1;}

  /** {@code EOF} Symbol index. */
  @Override
  public int EOF_sym() {return 0;}

  /** {@code error} Symbol index. */
  @Override
  public int error_sym() {return 1;}



    
    /* Change the method report_error so it will display the line and
       column of where the error occurred in the input as well as the
       reason for the error which is passed into the method in the
       String 'message'. */
    public void report_error(String message, Object info) {
   
        /* Create a StringBuilder called 'm' with the string 'Error' in it. */
        StringBuilder m = new StringBuilder("Error");
   
        /* Check if the information passed to the method is the same
           type as the type java_cup.runtime.Symbol. */
        if (info instanceof java_cup.runtime.Symbol) {
            /* Declare a java_cup.runtime.Symbol object 's' with the
               information in the object info that is being typecasted
               as a java_cup.runtime.Symbol object. */
            java_cup.runtime.Symbol s = ((java_cup.runtime.Symbol) info);
   
            /* Check if the line number in the input is greater or
               equal to zero. */
            if (s.left >= 0) {                
                /* Add to the end of the StringBuilder error message
                   the line number of the error in the input. */
                m.append(" in line "+(s.left+1));   
                /* Check if the column number in the input is greater
                   or equal to zero. */
                if (s.right >= 0)                    
                    /* Add to the end of the StringBuilder error message
                       the column number of the error in the input. */
                    m.append(", column "+(s.right+1));
            }
        }
   
        /* Add to the end of the StringBuilder error message created in
           this method the message that was passed into this method. */
        m.append(" : "+message);
   
        /* Print the contents of the StringBuilder 'm', which contains
           an error message, out on a line. */
        System.err.println(m);
    }
   
    /* Change the method report_fatal_error so when it reports a fatal
       error it will display the line and column number of where the
       fatal error occurred in the input as well as the reason for the
       fatal error which is passed into the method in the object
       'message' and then exit.*/
    public void report_fatal_error(String message, Object info) {
        report_error(message, info);
        System.exit(1);
    }


/** Cup generated class to encapsulate user supplied action code.*/
class CUP$parser$actions {
    private final parser parser;

    /** Constructor */
    CUP$parser$actions(parser parser) {
        this.parser = parser;
    }

    /** Method with the actual generated action code for actions 0 to 10. */
    public final java_cup.runtime.Symbol CUP$parser$do_action_part00000000(
            int                        CUP$parser$act_num,
            java_cup.runtime.lr_parser CUP$parser$parser,
            java.util.Stack<java_cup.runtime.Symbol> CUP$parser$stack,
            int                        CUP$parser$top)
            throws java.lang.Exception {
            /* Symbol object for return from actions */
            java_cup.runtime.Symbol CUP$parser$result;

        /* select the action based on the action number */
        switch (CUP$parser$act_num) {
        /*. . . . . . . . . . . . . . . . . . . .*/
        case 0: // expr_list ::= expr_list expr 
            {
                Object RESULT = null;
                int elleft = CUP$parser$stack.elementAt(CUP$parser$top-1).left;
                int elright = CUP$parser$stack.elementAt(CUP$parser$top-1).right;
                Object el = CUP$parser$stack.elementAt(CUP$parser$top-1).<Object> value();
                int eleft = CUP$parser$stack.peek().left;
                int eright = CUP$parser$stack.peek().right;
                Object e = CUP$parser$stack.peek().<Object> value();
                
                if(el instanceof ListExpr){
                    ((ListExpr) el).add(e);
                    RESULT = el;
                } else {
                    RESULT = new ListExpr(el, e);
                }
                
              
                CUP$parser$result = parser.getSymbolFactory().newSymbol("expr_list",0, CUP$parser$stack.elementAt(CUP$parser$top-1), CUP$parser$stack.peek(), RESULT);
            }
            return CUP$parser$result;

        /*. . . . . . . . . . . . . . . . . . . .*/
        case 1: // $START ::= expr_list EOF 
            {
                Object RESULT = null;
                int start_valleft = CUP$parser$stack.elementAt(CUP$parser$top-1).left;
                int start_valright = CUP$parser$stack.elementAt(CUP$parser$top-1).right;
                Object start_val = CUP$parser$stack.elementAt(CUP$parser$top-1).<Object> value();
                RESULT = start_val;
                CUP$parser$result = parser.getSymbolFactory().newSymbol("$START",0, CUP$parser$stack.elementAt(CUP$parser$top-1), CUP$parser$stack.peek(), RESULT);
            }
            /* ACCEPT */
            CUP$parser$parser.done_parsing();
            return CUP$parser$result;

        /*. . . . . . . . . . . . . . . . . . . .*/
        case 2: // expr_list ::= expr 
            {
                Object RESULT = null;
                int eleft = CUP$parser$stack.peek().left;
                int eright = CUP$parser$stack.peek().right;
                Object e = CUP$parser$stack.peek().<Object> value();
                 RESULT = e; 
                CUP$parser$result = parser.getSymbolFactory().newSymbol("expr_list",0, CUP$parser$stack.peek(), CUP$parser$stack.peek(), RESULT);
            }
            return CUP$parser$result;

        /*. . . . . . . . . . . . . . . . . . . .*/
        case 3: // expr ::= expr MULTIPLY expr 
            {
                Object RESULT = null;
                int e1left = CUP$parser$stack.elementAt(CUP$parser$top-2).left;
                int e1right = CUP$parser$stack.elementAt(CUP$parser$top-2).right;
                Object e1 = CUP$parser$stack.elementAt(CUP$parser$top-2).<Object> value();
                int e2left = CUP$parser$stack.peek().left;
                int e2right = CUP$parser$stack.peek().right;
                Object e2 = CUP$parser$stack.peek().<Object> value();
                 RESULT = new BinaryExpr("*", e1, e2); 
                CUP$parser$result = parser.getSymbolFactory().newSymbol("expr",1, CUP$parser$stack.elementAt(CUP$parser$top-2), CUP$parser$stack.peek(), RESULT);
            }
            return CUP$parser$result;

        /*. . . . . . . . . . . . . . . . . . . .*/
        case 4: // expr ::= expr DIVIDE expr 
            {
                Object RESULT = null;
                int e1left = CUP$parser$stack.elementAt(CUP$parser$top-2).left;
                int e1right = CUP$parser$stack.elementAt(CUP$parser$top-2).right;
                Object e1 = CUP$parser$stack.elementAt(CUP$parser$top-2).<Object> value();
                int e2left = CUP$parser$stack.peek().left;
                int e2right = CUP$parser$stack.peek().right;
                Object e2 = CUP$parser$stack.peek().<Object> value();
                 RESULT = new BinaryExpr("/", e1, e2); 
                CUP$parser$result = parser.getSymbolFactory().newSymbol("expr",1, CUP$parser$stack.elementAt(CUP$parser$top-2), CUP$parser$stack.peek(), RESULT);
            }
            return CUP$parser$result;

        /*. . . . . . . . . . . . . . . . . . . .*/
        case 5: // expr ::= expr PLUS expr 
            {
                Object RESULT = null;
                int e1left = CUP$parser$stack.elementAt(CUP$parser$top-2).left;
                int e1right = CUP$parser$stack.elementAt(CUP$parser$top-2).right;
                Object e1 = CUP$parser$stack.elementAt(CUP$parser$top-2).<Object> value();
                int e2left = CUP$parser$stack.peek().left;
                int e2right = CUP$parser$stack.peek().right;
                Object e2 = CUP$parser$stack.peek().<Object> value();
                 RESULT = new BinaryExpr("+", e1, e2); 
                CUP$parser$result = parser.getSymbolFactory().newSymbol("expr",1, CUP$parser$stack.elementAt(CUP$parser$top-2), CUP$parser$stack.peek(), RESULT);
            }
            return CUP$parser$result;

        /*. . . . . . . . . . . . . . . . . . . .*/
        case 6: // expr ::= expr MINUS expr 
            {
                Object RESULT = null;
                int e1left = CUP$parser$stack.elementAt(CUP$parser$top-2).left;
                int e1right = CUP$parser$stack.elementAt(CUP$parser$top-2).right;
                Object e1 = CUP$parser$stack.elementAt(CUP$parser$top-2).<Object> value();
                int e2left = CUP$parser$stack.peek().left;
                int e2right = CUP$parser$stack.peek().right;
                Object e2 = CUP$parser$stack.peek().<Object> value();
                 RESULT = new BinaryExpr("-", e1, e2); 
                CUP$parser$result = parser.getSymbolFactory().newSymbol("expr",1, CUP$parser$stack.elementAt(CUP$parser$top-2), CUP$parser$stack.peek(), RESULT);
            }
            return CUP$parser$result;

        /*. . . . . . . . . . . . . . . . . . . .*/
        case 7: // expr ::= NUMBER 
            {
                Object RESULT = null;
                int nleft = CUP$parser$stack.peek().left;
                int nright = CUP$parser$stack.peek().right;
                Integer n = CUP$parser$stack.peek().<Integer> value();
                 RESULT = new NumAtom(n.intValue()); 
                CUP$parser$result = parser.getSymbolFactory().newSymbol("expr",1, CUP$parser$stack.peek(), CUP$parser$stack.peek(), RESULT);
            }
            return CUP$parser$result;

        /*. . . . . . . . . . . . . . . . . . . .*/
        case 8: // expr ::= ID 
            {
                Object RESULT = null;
                int xleft = CUP$parser$stack.peek().left;
                int xright = CUP$parser$stack.peek().right;
                String x = CUP$parser$stack.peek().<String> value();
                 RESULT = new IDAtom(x); 
                CUP$parser$result = parser.getSymbolFactory().newSymbol("expr",1, CUP$parser$stack.peek(), CUP$parser$stack.peek(), RESULT);
            }
            return CUP$parser$result;

        /*. . . . . . . . . . . . . . . . . . . .*/
        case 9: // expr ::= assignment 
            {
                Object RESULT = null;
                int aleft = CUP$parser$stack.peek().left;
                int aright = CUP$parser$stack.peek().right;
                Object a = CUP$parser$stack.peek().<Object> value();
                 RESULT = a; 
                CUP$parser$result = parser.getSymbolFactory().newSymbol("expr",1, CUP$parser$stack.peek(), CUP$parser$stack.peek(), RESULT);
            }
            return CUP$parser$result;

        /*. . . . . . . . . . . . . . . . . . . .*/
        case 10: // assignment ::= ID EQ expr 
            {
                Object RESULT = null;
                int xleft = CUP$parser$stack.elementAt(CUP$parser$top-2).left;
                int xright = CUP$parser$stack.elementAt(CUP$parser$top-2).right;
                String x = CUP$parser$stack.elementAt(CUP$parser$top-2).<String> value();
                int eleft = CUP$parser$stack.peek().left;
                int eright = CUP$parser$stack.peek().right;
                Object e = CUP$parser$stack.peek().<Object> value();
                 RESULT = new BinaryExpr("=", new IDAtom(x), e); 
                CUP$parser$result = parser.getSymbolFactory().newSymbol("assignment",2, CUP$parser$stack.elementAt(CUP$parser$top-2), CUP$parser$stack.peek(), RESULT);
            }
            return CUP$parser$result;

        /* . . . . . .*/
        default:
            throw new Exception(
                  "Invalid action number " + CUP$parser$act_num + " found in internal parse table");

        }
    } /* end of method */

    /** Method splitting the generated action code into several parts. */
    public final java_cup.runtime.Symbol CUP$parser$do_action(
            int                        CUP$parser$act_num,
            java_cup.runtime.lr_parser CUP$parser$parser,
            java.util.Stack<java_cup.runtime.Symbol> CUP$parser$stack,
            int                        CUP$parser$top)
            throws java.lang.Exception {
            return CUP$parser$do_action_part00000000(
                           CUP$parser$act_num,
                           CUP$parser$parser,
                           CUP$parser$stack,
                           CUP$parser$top);
    }
}

}