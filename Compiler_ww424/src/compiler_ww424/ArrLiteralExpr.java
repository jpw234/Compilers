package compiler_ww424;

import java.util.ArrayList;

import sun.net.www.protocol.http.AuthCacheValue.Type;

public class ArrLiteralExpr extends Expr {
	private ArrayList<Expr> values;
	private ArrayList<Expr> accesses = new ArrayList<Expr>();
	
	public ArrLiteralExpr(ArrayList<Expr> v, int l, int c) {
		values = v;
		line = l;
		column = c;
	}
	
	public ArrLiteralExpr(ArrayList<Expr> v, ArrayList<Expr> a, int l, int c) {
		values = v; accesses = a; line = l; column = c;
	}
	
	public ArrayList<Expr> getValues() {
		return values;
	}
	
	public ArrayList<Expr> getAccesses() {
		return accesses;
	}
	
	public void addValue(Expr v) {
		values.add(v);
	}
	
	public void addAccess(Expr v) {
		accesses.add(v);
	}
	
	public Type typecheck(SymTab s) {
		if(values.size() == 0) {
			if(accesses.size() != 0) throw new Error("tried to access empty array");
			else return new Type("empty", 1);
		}
		
		Type t = values.get(0).typecheck(s);
		for(int a = 1; a < values.size(); a++) {
			if(!t.equals(values.get(a).typecheck(s))) throw new Error("mismatched args to array literal");
		}
		
		if(accesses.size() > (t.getDepth() + 1)) throw new Error("tried to access something that isn't an array");
		
		Type dummyInt = new Type("int");
		for(int a = 0; a < accesses.size(), a++) {
			if(!dummyInt.equals(accesses.get(a).typecheck(s))) throw new Error("non-integer expr in array access");
		}
		
		t.addDepth();
		return t;
	}
}