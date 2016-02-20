package compiler_ww424;

public class AtomExpr extends Expr{
	String value;
	
	AtomExpr(String v){
		value = v;
	}
	AtomExpr(int i){
		value = Integer.toString(i);
	}
	
	@Override
	public String toString(){
		return "( "+value+ " )";
	}
}
