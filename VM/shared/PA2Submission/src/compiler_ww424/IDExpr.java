package compiler_ww424;

public class IDExpr extends Expr {
	private String name;
	
	public IDExpr(String s,int lineNum, int colNum) {
		name = s;
		line = lineNum;
		column = colNum;
	}
	
	public Type typecheck(SymTab s) {
		return s.lookup(name);
	}
	
	public String getName() {
		return name;
	}
	public String toString(){
		return name;
	}
}
