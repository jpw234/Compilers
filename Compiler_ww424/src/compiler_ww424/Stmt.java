package compiler_ww424;

public abstract class Stmt {
	protected int line,column;
	public abstract Type typecheck(SymTab s);
	public int getLine() {
		return line;
	}
	public int getColumn() {
		return column;
	}
	public void constantFold() {
		return;
	}
}
