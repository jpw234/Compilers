package compiler_ww424;

public abstract class Expr {
	private Type type;
	
	abstract Type typecheck(SymTab s);
	public Type getType() {
		return type;
	}
}
