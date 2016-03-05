package compiler_ww424;

public abstract class Expr {
	
	protected int line;
	protected int column;
	
	private Type type;
	abstract Type typecheck(SymTab s);
	public Type getType() {
		return type;
	}
	public int getLine() {
		return line;
	}
	public int getColumn()
	{
		return column;
	}
}
