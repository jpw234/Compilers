package compiler_ww424;

public class IDExpr extends Expr {
	private String name;
	
	public IDExpr(String s) {
		name = s;
	}
	
	public Type typecheck(SymTab s) {
		return s.lookup(name);
	}
	
	public String getName() {
		return name;
	}
}
