package compiler_ww424;

import java.util.ArrayList;
import java.util.List;

public class ProcCall extends Stmt {
	private IDExpr name;
	private List<Expr> args;
	
	public ProcCall(IDExpr n, List<Expr> a,int lineNum,int colNum) {
		name = n; args = a;
		line = lineNum;
		column = colNum;
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
	public String toString(){
		if (args == null) { return String.format("(%s %s)", name,""); }
		String arglist = "";
		for (int i =0 ; i <args.size(); i++){
			arglist += (args.get(i).toString());
		}		
		return String.format("(%s %s)", name,arglist);
	}
	
}
