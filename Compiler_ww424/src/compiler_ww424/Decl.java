package compiler_ww424;

public class Decl extends Stmt {
	private IDExpr name;
	private Type type;
	
	public Decl(IDExpr n, Type t) {
		name = n;
		type = t;
	}
	
	public IDExpr getName() {
		return name;
	}
	
	public Type getType() {
		return type;
	}
	
	public Type typecheck(SymTab s) {
		if(s.lookup(name.getName()) != null) throw new Error("redeclared variable error");
		
		if(type.getType() != "underscore") s.add(name.getName(), type);
		
		return new Type("unit");
	}
}
