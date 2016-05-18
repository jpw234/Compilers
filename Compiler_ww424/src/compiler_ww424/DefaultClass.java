package compiler_ww424;

import edu.cornell.cs.cs4120.xic.ir.*;

public class DefaultClass extends Expr {
	private String className;
	
	public DefaultClass(String cn, int l, int c) {
		className = cn;
		line = l;
		column = c;
	}
	
	public String getClassType() {
		return className;
	}
	
	public Type typecheck(SymTab s) {
		type = new Type(className);
		return type;
	}
	
	//TODO: implement buildIRExpr()
	public IRExpr buildIRExpr() {
		return null;
	}
}
