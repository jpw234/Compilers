package compiler_ww424;

import java.util.ArrayList;
import edu.cornell.cs.cs4120.xic.ir.*;

public class NumExpr extends Expr {
    private int intvalue;
    private char charval;
    private Boolean isChar = false;
    
   
    
    public NumExpr(int i,int lineNum,int colNum) {
        intvalue = i;
        line = lineNum;
        column = colNum;
        type = new Type("int");
    }
    public NumExpr(char c, int lineNum, int colNum){
        charval = c;
        intvalue = (int) c;
        line = lineNum;
        column = colNum;
        isChar = true;
        type = new Type("int");
    }
    
    public Type typecheck(SymTab s) {
        type = new Type("int");
        return type;
    }
    public int getIntVal() {
        return intvalue;
    }
    public char getCharVal(){
        return charval;
    }
    
    public IRExpr buildIRExpr() {
    	return new IRConst((long) intvalue);
    }
    
	public String toString(){
		if (!isChar){
			return String.valueOf(intvalue);
		}
		return "\'"+charval+"\'";
	}
}
