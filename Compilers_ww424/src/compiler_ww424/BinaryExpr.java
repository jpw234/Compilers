package compiler_ww424;

public class BinaryExpr extends Expr {
	
	public Expr expl;
	public Expr expr;
	public String op;
	
	public BinaryExpr(String type, Expr el, Expr er){
		expl =  el;
		expr =  er;
		op = type;
	}
	@Override
	public String toString(){
		return String.format("(%s %s %s)", op, expl, expr);
	}
}
