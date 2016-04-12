package compiler_ww424;

import edu.cornell.cs.cs4120.xic.ir.*;

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
		type = new Type("bool");
		return type;
	}
	
	public IRExpr buildIRExpr() {
		long numval;
		if(val) numval = 1; else numval = 0;
		
		return new IRConst(numval);
	}

	@Override
	public String toString(){
		String s = "";
		if(val) {s = "true";}
		else {s = "false";}
		return s ;
	}
}
