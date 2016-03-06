package compiler_ww424;

import java.util.List;

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
	
	public void setID (IDExpr n){
		name = n ;
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
	
	@Override
	public String toString(){
		String s = type.getType();
		if(type.getDepth() > 0) {//array type variable
			for(int i = type.getDepth()-1; i >= 0; i--){
				if(i < accesses.size()) { s = "( [] " + s + accesses.get(i).toString() + " )";}
				else {s = "( [] " + s + " )";}
			}
		}
		s = "( " + name.toString() + " " + s + " )";
		return (type.getType()=="underscore")? "_" : s ;
	}
}
