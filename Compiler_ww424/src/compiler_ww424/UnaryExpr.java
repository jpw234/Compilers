package compiler_ww424;

public class UnaryExpr extends Expr {
	private UnaryOp op;
	private Expr expr;
	
	public UnaryExpr(UnaryOp o, Expr e) {
		op = o; expr = e;
	}
	
	public UnaryOp getOp() {
		return op;
	}
	
	public Expr getExpr() {
		return expr;
	}
	
	public Type typecheck(SymTab s) {
		Type t = expr.typecheck(s);
		if(t.getType() == "bool" && t.getDepth() == 0 && op == UnaryOp.BOOLNEG) return new Type("bool");
		else if(t.getType() == "int" && t.getDepth() == 0 && op == UnaryOp.ARITHNEG) return new Type("int");
		else throw new Error("unaryexpr failed to typecheck");
	}
}
