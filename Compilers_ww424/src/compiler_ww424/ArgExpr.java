package compiler_ww424;

import java.util.ArrayList;

public class ArgExpr extends Expr {
	public ArrayList<Expr> exps;
	
	public ArgExpr(){
		exps = new ArrayList<Expr>();
	}
	
	public ArgExpr(Object ... args){
		exps = new ArrayList<Expr>();
		for (Object a: args){
			if (a == null){
				continue;
			} else if (a instanceof String){
				exps.add(new AtomExpr((String) a));
			} else {
				exps.add((Expr) a);
			}
		}
	}
	public void add(Expr exp){
		exps.add(exp);
	}
	
	public void insert(Expr exp){
		exps.add(0, exp);
	}
	
	@Override
	public String toString(){
		String s = "";
		for (Expr e : exps){
			s += (e+" ");
		}
		return s ;
	}
}
