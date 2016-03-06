package compiler_ww424;

import java.util.ArrayList;

public class ArrExpr extends Expr {
	private IDExpr name;
	private ArrayList<Expr> accesses = new ArrayList<Expr>();
	private int depth=0;
	
	public ArrExpr(IDExpr n,int l,int c) {
		name = n;
		line = l;
		column = c;
	}
	
	public ArrExpr(IDExpr n, ArrayList<Expr> c) {
		name = n; accesses = c;
	}
	
	public void add(Expr e) {
		accesses.add(e);
	}
	public void addDepth(){
		depth +=1 ;
	}
	public int getDepth(){
		return depth;
	}
	public IDExpr getName() {
		return name;
	}
	
	public ArrayList<Expr> getAccesses() {
		return accesses;
	}
	
	public Type typecheck(SymTab s) {
		Type temp = s.lookup(name.getName());
		if(temp.getDepth()-accesses.size() < 0) throw new Error("illegal access: that is not an array");
		return new Type(temp.getType(), temp.getDepth()-accesses.size());
	}
}
