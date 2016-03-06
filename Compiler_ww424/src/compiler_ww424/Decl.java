package compiler_ww424;

import java.lang.reflect.Type;

public class Decl extends Stmt {
	private IDExpr name;
	private Type type;
	private List<Expr> accesses = null;
	
	public Decl(IDExpr n, Type t,int lineNum,int colNum) {
		name = n;
		type = t;
		line = lineNum;
		column = colNum;
	}
	
	public Decl(IDExpr n, Type t, List<Expr> a, int lineNum, int colNum) {
		name = n;
		type = t;
		accesses = a;
		line = lineNum;
		column = colNum;
	}
	
	public IDExpr getName() {
		return name;
	}
	
	public Type getType() {
		return type;
	}
	
	public void addAccess(Expr a) {
		accesses.add(a);
	}
	
	public List<Expr> getAccesses() {
		return accesses;
	}
	
	public Type typecheck(SymTab s) {
		if(s.lookup(name.getName()) != null) throw new Error("redeclared variable error");
		
		if(type.getType() != "underscore") s.add(name.getName(), type);
		
		Type dummyType = new Type("int");
		
		if(accesses != null) {
			for(int a = 0; a < accesses.size(); a++) {
				if(!dummyType.equals(accesses.get(a).typecheck(s))) {
					throw new Error("non-integer expression used as array access");
				}
			}
		}
		
		return new Type("unit");
	}
}
