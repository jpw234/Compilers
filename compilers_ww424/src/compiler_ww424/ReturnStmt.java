package compiler_ww424;

public class ReturnStmt {
	private Expr val;
	
	public ReturnStmt(Expr v) {
		val = v;
	}
	
	public Expr getExpr() {
		return val;
	}
	
	public Type typecheck(SymTab s) {
		Type expected = s.getRetval();
		if(expected == null) throw new Error("returned in scope not expecting return");
		else if(!val.typecheck(s).equals(expected)) throw new Error("returned wrong type");
		else return val.typecheck(s);
	}
}
