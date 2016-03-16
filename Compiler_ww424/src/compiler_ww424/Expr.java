package compiler_ww424;
import edu.cornell.cs.cs4120.xic.ir.*;

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
	
	public Expr constantFold() {
		return this;
	}
	
	abstract IRExpr buildIRExpr();
}
