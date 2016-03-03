package compiler_ww424;

public class NumExpr extends Expr {
	private int value;
	
	public NumExpr(int i) {
		value = i;
	}
	
	public Type typecheck(SymTab s) {
		return new Type("int");
	}
}
