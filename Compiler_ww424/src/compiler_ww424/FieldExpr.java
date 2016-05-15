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
}
