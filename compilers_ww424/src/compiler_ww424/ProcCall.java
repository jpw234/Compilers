package compiler_ww424;

import java.util.List;

public class ProcCall extends Stmt {
	private IDExpr name;
	private List<Expr> args;
	
	public ProcCall(IDExpr n, List<Expr> a) {
		name = n; args = a;
	}
	
	public IDExpr getName() {
		return name;
	}
	
	public List<Expr> getArgs() {
		return args;
	}
	
	public Type typecheck(SymTab s) {
		FunType ft = (FunType) s.lookup(name.getName());
		
		if(args.size() != ft.getInputs().getArgs().size()) throw new Error("incorrect # of args to fun");
		
		for(int a = 0; a < args.size(); a++) {
			if(!args.get(a).typecheck(s).equals(ft.getInputs().getArgs().get(a))) 
				throw new Error("incorrect type of arg to fun");
		}
		
		return new Type("unit");
	}
}
