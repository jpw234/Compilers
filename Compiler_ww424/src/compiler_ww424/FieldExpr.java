package compiler_ww424;

import java.util.ArrayList;

public class FieldExpr extends ArrExpr {
	private String ownerClass;
	
	public FieldExpr(String cl, IDExpr s,int lineNum, int colNum) {
		super(s, lineNum, colNum);
		
		ownerClass = cl;
	}
	
	public FieldExpr(String cl, IDExpr s, ArrayList<Expr> list, int l, int c) {
		super(s, list, l, c);
		
		ownerClass = cl;
	}

	//TODO: decide if typecheck(SymTab s) and buildIRExpr() must be changed
	
	@Override
	public Type typecheck(SymTab s) {
		Type temp = s.lookup(ownerClass + "." + name.getName());
		if(temp.getDepth()-accesses.size() < 0) throw new Error(line + ":" + column + " error: " + "illegal access: that is not an array");
		for(int a = 0; a < accesses.size(); a++) {
			if(accesses.get(a).typecheck(s).getType() != "int") {
				throw new Error(line + ":" + column + " error: " + "illegal expr: non-integer value used as array access");
			}
		}
		type = new Type(temp.getType(), temp.getDepth()-accesses.size());
		return type;
	}
}
