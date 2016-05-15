package compiler_ww424;

import java.util.ArrayList;
import edu.cornell.cs.cs4120.xic.ir.*;

public class MethodCall extends FunCall {
	private String ownerClass;
	
	public MethodCall(String cl, IDExpr name, ArrayList<Expr> args, int lineNum, int colNum) {
		super(name, args, lineNum, colNum);
		
		ownerClass = cl;
	}
	
	//TODO: decide if typecheck(SymTab s) and buildIRExpr() must be changed
}
