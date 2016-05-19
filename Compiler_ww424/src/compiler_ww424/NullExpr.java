package compiler_ww424;

import edu.cornell.cs.cs4120.xic.ir.*;

public class NullExpr extends Expr {
	public NullExpr(int lineNum, int colNum) {
		line = lineNum;
		column = colNum;
	}
	
	public Type typecheck(SymTab s) {
		return new Type("null");
	}
	public String toString(){
		return "";
	}
	public IRExpr buildIRExpr() {
		return new IRMem(new IRConst(0));
		//TODO: check this
	}
}
