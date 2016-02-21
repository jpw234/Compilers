package compiler_ww424;

import java.util.ArrayList;

public class ListExpr extends Expr {
	public ArrayList<Expr> exps;
	
	public ListExpr(){
		exps = new ArrayList<Expr>();
	}
	public ListExpr(Object ... args){
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
	
	public Boolean isEmpty(){
		return exps.isEmpty();
	}
	
	public int size(){
		return exps.size();
	}

	public void add(Expr ex){
		exps.add(ex);
	}
	
	public void insert(Expr ex){
		exps.add(0, ex);
	}

	
	@Override
	public String toString(){
		ArrayList<String> strings = new ArrayList<String>();
		for (Expr exp : exps) {
		    strings.add(exp.toString());
		}
		
		if (exps.size() ==1 && exps.get(0).toString() == "_"){
			return "_";
		}
		return String.format("(%s)", String.join(" ", strings));
	}
}
