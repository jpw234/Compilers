package compiler_ww424;

public class BinaryExpr extends Expr {
	private Expr left;
	private Expr right;
	private BinaryOp op;
	private int leftLine;
	private int leftCol;
	private int rightLine;
	private int rightCol;
	
	public BinaryExpr(Expr l, Expr r, BinaryOp o,int ellineNum,int elcolNum,int erlineNum,int ercolNum) {
		left = l;
		right = r;
		op = o;
		leftLine = ellineNum;
		leftCol = elcolNum;
		rightLine = erlineNum;
		rightCol = ercolNum;
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
	
	@Override
	public String toString(){
		String s = "";
		String operand = "";
		switch(op){
			case PLUS: operand = "+";
				break;
			case MINUS: operand = "-";
				break;
			case TIMES: operand = "*";
				break;
			case HIGHMUL: operand = "*>>";
				break;
			case DIV: operand = "/";
				break;
			case MOD: operand = "%";
				break;
			case LT: operand = "<";
				break;
			case LEQ: operand = "<=";
				break;
			case GEQ: operand = ">=";
				break;
			case GT: operand = ">";
				break;
			case EQEQ: operand = "==";
				break;
			case NEQ: operand = "!=";
				break;
			case AND: operand = "&";
				break;
			case OR: operand = "|";
				break;
		}
		s = "( " + operand + " " + left.toString() + " " + right.toString() + " )";
		return s ;
	}
}
