package compiler_ww424;

import java.util.ArrayList;

public class ListExpr extends Expr {
	public ArrayList<Expr> exps;
	
	public ListExpr(){
		exps = new ArrayList<Expr>();
	}
	
	public ListExpr(Expr e1){
		exps = new ArrayList<Expr>();
		exps.add(e1);
	}
	
	public ListExpr(String op, Expr exp1){
		exps = new ArrayList<Expr>();
		exps.add(new AtomExpr(op));
		exps.add(exp1);
	}
	
	public ListExpr(String op, Expr exp1,Expr exp2){
		exps = new ArrayList<Expr>();
		exps.add(new AtomExpr(op));
		exps.add(exp1);
		exps.add(exp2);
	}
	
	public ListExpr(String op, Expr exp1,Expr exp2,Expr exp3){
		exps = new ArrayList<Expr>();
		exps.add(new AtomExpr(op));
		exps.add(exp1);
		exps.add(exp2);
		exps.add(exp3);
	}
	
	public ListExpr(Expr exp1, Expr exp2){
		exps = new ArrayList<Expr>();
		exps.add(exp1);
		exps.add(exp2);
	}
	
	public ListExpr(Expr exp1, Expr exp2, Expr exp3){
		exps = new ArrayList<Expr>();
		exps.add(exp1);
		exps.add(exp2);
		exps.add(exp3);
	}

	public ListExpr(Expr exp1, Expr exp2, Expr exp3,Expr exp4){
		exps = new ArrayList<Expr>();
		exps.add(exp1);
		exps.add(exp2);
		exps.add(exp3);
		exps.add(exp4);
	}


	
	public void add(Expr exp){
		exps.add(exp);
	}
	public void add(int i , Expr exp){
		exps.add(i, exp);
	}

	
	@Override
	public String toString(){
		return String.format("( %s )", exps);
	}
}
