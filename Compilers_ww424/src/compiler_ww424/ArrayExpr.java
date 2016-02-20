package compiler_ww424;

import java.util.Stack;

public class ArrayExpr extends Expr{
	String type;
	Stack<Expr> s; 
	
	public ArrayExpr(String type){
		s = new Stack<Expr>();
		this.type = type;
	}
	
	public void push(Expr exp){
		s.add(exp);
	}
	
	@Override
	public String toString(){

		if (s == null || s.size() == 0 ){
			return type;
		}
		String ret = type;
		while (!s.empty()){
			Expr e = s.pop();
			ret = "( "+"[]" + ret + e.toString() + " )";
		}
		return ret;
	}
	
	
}
