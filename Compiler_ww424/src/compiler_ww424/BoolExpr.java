package compiler_ww424;

public class BoolExpr extends Expr {
	private boolean val;
	
	public BoolExpr(boolean v,int linNum, int colNum) {
		val = v;
		line = linNum;
		column = colNum;
	}
	
	public boolean getVal() {
		return val;
	}
	
	public Type typecheck(SymTab s) {
		return new Type("bool");
	}

	@Override
	public String toString(){
		String s = "";
		if(val) {s = "true";}
		else {s = "false";}
		return s ;
	}
}
