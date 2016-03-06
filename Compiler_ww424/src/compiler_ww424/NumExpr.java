package compiler_ww424;

public class NumExpr extends Expr {
	private int value;
	
	public NumExpr(int i,int lineNum,int colNum) {
		value = i;
		line = lineNum;
		column = colNum;
	}
	
	public Type typecheck(SymTab s) {
		return new Type("int");
	}
	public int getVal() {
		return value;
	}
}
