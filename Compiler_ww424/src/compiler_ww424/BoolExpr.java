package compiler_ww424;

public class BoolExpr extends Expr {
	private boolean val;
	
	public BoolExpr(boolean v) {
		val = v;
	}
	
	public boolean getVal() {
		return val;
	}
	
	public Type typecheck(SymTab s) {
		return new Type("bool");
	}
}
