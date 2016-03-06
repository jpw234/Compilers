package compiler_ww424;

public class NumExpr extends Expr {
    private int intvalue;
    private char charval;
    private Boolean isChar = false;
    
    public NumExpr(int i,int lineNum,int colNum) {
        intvalue = i;
        line = lineNum;
        column = colNum;
    }
    public NumExpr(char c, int lineNum, int colNum){
        charval = c;
        line = lineNum;
        column = colNum;
        isChar = true;
    }
    
    public Type typecheck(SymTab s) {
        return new Type("int");
    }
    public int getIntVal() {
        return intvalue;
    }
    public char getCharVal(){
        return charval;
    }
}
