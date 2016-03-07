package compiler_ww424;

import java.util.ArrayList;

public class UnaryExpr extends Expr {
	private UnaryOp op;
	private Expr expr;
	
	public UnaryExpr(UnaryOp o, Expr e,int lineNum,int colNum) {
		op = o; expr = e;
		line = lineNum;
		column = colNum;
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
		else throw new Error(line + ":" + column + " error: " + "unaryexpr failed to typecheck");
	}
	
	public String toString(){
		String operator = "!";
		if (op == UnaryOp.ARITHNEG){
			operator ="-";
		}
		return String.format("(%s %s)", operator, expr.toString());
	}
}
