package compiler_ww424;

public class Length extends Expr {
	private Expr arg;
	
	public Length(Expr e) {
		arg = e;
	}
	
	public Expr getArg() {
		return arg;
	}
	
	public Type typecheck(SymTab s) {
		Type t = arg.typecheck(s);
		if(t.getDepth() < 1) throw new Error("argument to Length() is not an array");
		
		return new Type("int");
	}
}