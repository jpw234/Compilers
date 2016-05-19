package compiler_ww424;

import edu.cornell.cs.cs4120.xic.ir.*;

public class IDExpr extends Expr {
	private String name;
	
	public IDExpr(String s,int lineNum, int colNum) {
		name = s;
		line = lineNum;
		column = colNum;
	}
	
	public Type typecheck(SymTab s) {
		if(name == "this") {
			if(s.getClassContext() == null) throw new Error("used 'this' outside class");
			
			type = new Type(s.getClassContext());
			return type;
		}
		
		type = s.lookup(name);
		return type;
	}
	
	public String getName() {
		return name;
	}
	public String toString(){
		return name;
	}
	
	public IRExpr buildIRExpr() {
		return new IRTemp(name);
	}
}
