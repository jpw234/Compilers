package compiler_ww424;

import java.util.List;

public class ArrExpr extends Expr {
	private IDExpr name;
	private List<Expr> accesses;
	
	public ArrExpr(IDExpr n) {
		name = n;
	}
	
	public ArrExpr(IDExpr n, List<Expr> c) {
		name = n; accesses = c;
	}
	
	public void add(Expr e) {
		accesses.add(e);
	}
	
	public IDExpr getName() {
		return name;
	}
	
	public List<Expr> getAccesses() {
		return accesses;
	}
	
	public Type typecheck(SymTab s) {
		Type temp = s.lookup(name.getName());
		if(temp.getDepth()-accesses.size() < 0) throw new Error("illegal access: that is not an array");
		return new Type(temp.getType(), temp.getDepth()-accesses.size());
	}
}
