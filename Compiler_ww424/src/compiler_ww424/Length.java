package compiler_ww424;

public class Length extends Expr {
	private Expr arg;

	
	public Length(Expr e, int lineNum, int columnNum) {
		arg = e;
        line = lineNum;
        column = columnNum;
	}
	
	public Expr getArg() {
		return arg;
	}
	
	public Type typecheck(SymTab s) {
		Type t = arg.typecheck(s);
		if(t.getDepth() < 1) throw new Error("argument to Length() is not an array");
		
		return new Type("int");
	}
	public String toString(){
		return String.format("(%s %s)", "length", arg.toString());
	}
}