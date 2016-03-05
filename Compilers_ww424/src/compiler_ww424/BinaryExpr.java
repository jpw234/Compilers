package compiler_ww424;

public class BinaryExpr extends Expr {
	private Expr left;
	private Expr right;
	private BinaryOp op;
	
	public BinaryExpr(Expr l, Expr r, BinaryOp o) {
		left = l;
		right = r;
		op = o;
	}
	
	public Expr getLeft() {
		return left;
	}
	
	public Expr getRight() {
		return right;
	}
	
	public BinaryOp getOp() {
		return op;
	}
	
	public Type typecheck(SymTab s) {
		Type ltype = left.typecheck(s);
		Type rtype = right.typecheck(s);
		if(!rtype.equals(ltype)) throw new Error("2 sides of binaryexpr do not match types");
		
		if(ltype.getDepth() > 0) {
			if(op == BinaryOp.PLUS || op == BinaryOp.EQEQ || op == BinaryOp.NEQ) {
				return ltype;
			}
			else throw new Error("that binary operation does not work on arrays");
		}
		
		else if(ltype.getType() == "bool") {
			if(op == BinaryOp.EQEQ || op == BinaryOp.NEQ || op == BinaryOp.AND ||
					op == BinaryOp.OR) return ltype;
			else throw new Error("that binary operation does not work on booleans");
		}
		
		else if(ltype.getType() == "int") {
			if(op == BinaryOp.EQEQ || op == BinaryOp.NEQ || op == BinaryOp.PLUS ||
				op == BinaryOp.MINUS || op == BinaryOp.TIMES || op == BinaryOp.DIV || 
				op == BinaryOp.LT || op == BinaryOp.LEQ || op == BinaryOp.GT ||
				op == BinaryOp.GEQ || op == BinaryOp.MOD || op == BinaryOp.HIGHMUL) {
				return ltype;
			}
			else throw new Error("that binary operation does not work on integers");
		}
		
		else throw new Error("this shouldn't happen but the BinaryExpr got messed up");
	}
}
