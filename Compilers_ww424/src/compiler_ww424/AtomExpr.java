package compiler_ww424;

public class AtomExpr extends Expr{
	String value;
	Type t;
	Boolean isString = false;
	public enum Type {
		INTEGER, CHARACTER, STRING, EXPR, TERMINAL
	}
	
	AtomExpr(String v){
		value = v;
		t = Type.TERMINAL;
	}
	
	AtomExpr(String v, Boolean isstr){
		value = v;
		t = Type.STRING;
		isString = isstr;
	}
	AtomExpr(int i){
		value = Integer.toString(i);
		t = Type.INTEGER;
	}
	AtomExpr(char c){
		value = Character.toString(c);
		t = Type.CHARACTER;
	}
	AtomExpr(Expr e){
		value = e.toString();
		t = Type.EXPR;
	}
	
	@Override
	public String toString(){
		
		if (isString){
			return "\"" + value + "\"";
		}else if (t == Type.CHARACTER){
			return  "\'" + value + "\'";
		}
		return value;
	}
}
