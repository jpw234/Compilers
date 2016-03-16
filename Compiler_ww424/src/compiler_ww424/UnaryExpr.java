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
	
	public Expr constantFold() {
		expr = expr.constantFold();
		
		if(op == UnaryOp.ARITHNEG) {
			if(expr instanceof NumExpr) {
				return new NumExpr((-1 * ((NumExpr) expr).getIntVal()), expr.getLine(), expr.getColumn());
			}
			
			else if(expr instanceof ArrLiteralExpr) {
				ArrLiteralExpr val = (ArrLiteralExpr) expr;
				ArrayList<Expr> acc = val.getAccesses();
				Expr curr = val;
				for(int a = 0; a < acc.size(); a++) {
					//if this access isn't a simple NumExpr we can't fold
					if(!(acc.get(a) instanceof NumExpr)) { return this; }
					
					//if it is a simple NumExpr we follow that access to go "one deeper" into the ArrLiteral
					curr = ((ArrLiteralExpr) curr).getValues().get(((NumExpr) acc.get(a)).getIntVal());
					
					//if it's the last access
					if(a == acc.size()-1) {
						//if it's a simple numeric expression
						if(curr instanceof NumExpr) {
							//create a new NumExpr in the typical manner
							return new NumExpr((-1 * ((NumExpr) curr).getIntVal()), curr.getLine(), curr.getColumn());
						}
						else return this;
					}
					
					//if it's not the last access we simply "fall through"
				}
			}
			
			else return this;
		}
		//in this branch op is BOOLNEG
		else {
			if(expr instanceof BoolExpr) {
				return new BoolExpr(!((BoolExpr) expr).getVal(), expr.getLine(), expr.getColumn());
			}
			
			else if(expr instanceof ArrLiteralExpr) {
				ArrLiteralExpr val = (ArrLiteralExpr) expr;
				ArrayList<Expr> acc = val.getAccesses();
				Expr curr = val;
				for(int a = 0; a < acc.size(); a++) {
					//if this access isn't a simple NumExpr we can't fold
					if(!(acc.get(a) instanceof NumExpr)) { return this; }
					
					//if it is a simple NumExpr we follow that access to go "one deeper" into the ArrLiteral
					curr = ((ArrLiteralExpr) curr).getValues().get(((NumExpr) acc.get(a)).getIntVal());
					
					//if it's the last access
					if(a == acc.size()-1) {
						//if it's a simple numeric expression
						if(curr instanceof BoolExpr) {
							//create a new NumExpr in the typical manner
							return new BoolExpr((!((BoolExpr) curr).getVal()), curr.getLine(), curr.getColumn());
						}
						else return this;
					}
					
					//if it's not the last access we simply "fall through"
				}
			}
			//if it's not a BoolExpr or an ArrLiteralExpr after constant folding, no folding.
			else return this;
		}
		
		return this;
	}
}
