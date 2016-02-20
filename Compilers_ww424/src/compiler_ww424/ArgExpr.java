package compiler_ww424;

import java.util.ArrayList;

public class ArgExpr extends Expr {
	public ArrayList<Expr> exps;
	
	public ArgExpr(){
		exps = new ArrayList<Expr>();
	}
	
	public ArgExpr(Expr e1){
		exps = new ArrayList<Expr>();
		exps.add(e1);
	}

	
	public ArgExpr(Expr type, Expr exp1){
		exps = new ArrayList<Expr>();
		exps.add(type);
		exps.add(exp1);
	}
	
	public void add(Expr exp){
		exps.add(exp);
	}
	
	public void insert(Expr exp){
		exps.add(0, exp);
	}
	
	@Override
	public String toString(){
		return String.format("%s", exps);
	}
}
