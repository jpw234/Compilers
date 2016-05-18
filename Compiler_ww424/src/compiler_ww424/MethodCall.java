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
	
	public Type typecheck(SymTab s) {
		try {
			ft = (FunType) s.lookupFunction(ownerClass + "." + name.getName());

			if(args.size() != ft.getInputs().getArgs().size()) throw new Error(line + ":" + column + " error: " + "incorrect # of args to fun");

			for(int a = 0; a < args.size(); a++) {
				if(!args.get(a).typecheck(s).equals(ft.getInputs().getArgs().get(a))) 
					throw new Error(line + ":" + column + " error: " + "incorrect type of arg to fun");
			}

			type = ft.getOutputs();
			return type;
		}
		catch(Error e) {
			if(e.getMessage() == "Semantic Error: var does not exist") throw new Error(line + ":" + column + " error: " + e.getMessage());
			else throw e;
		}

	}

}
