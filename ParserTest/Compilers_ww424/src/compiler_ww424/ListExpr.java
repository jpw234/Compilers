package compiler_ww424;

import java.util.ArrayList;

public class ListExpr extends Expr {
	public ArrayList<Expr> exps;
	
	public ListExpr(Object e1, Object e2){
		exps = new ArrayList<Expr>();
		exps.add((Expr) e1);
		exps.add((Expr) e2);
	}
	public void add(Object e){
		exps.add((Expr) e);
	}
	
	@Override
	public String toString(){
		return String.format("%s", exps);
	}
}
