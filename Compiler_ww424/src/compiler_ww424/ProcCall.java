package compiler_ww424;

import java.util.ArrayList;
import java.util.List;

import edu.cornell.cs.cs4120.xic.ir.*;

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
		try {
			FunType ft = (FunType) s.lookupFunction(name.getName());
		
			if(args.size() != ft.getInputs().getArgs().size()) throw new Error(line + ":" + column + " error: " + "incorrect # of args to fun");
		
			for(int a = 0; a < args.size(); a++) {
				if(!args.get(a).typecheck(s).equals(ft.getInputs().getArgs().get(a))) 
					throw new Error(line + ":" + column + " error: " + "incorrect type of arg to fun");
			}
		}
		catch(Error e) {
			if(e.getMessage() == "Semantic Error: var does not exist") throw new Error(line + ":" + column + " error: " + e.getMessage());
			else throw e;
		}
		return new Type("unit");
	}
	
	@Override
	public void constantFold() {
		for(int a = 0; a < args.size(); a++) {
			args.set(a, args.get(a).constantFold());
		}
	}
	
	public String toString(){
		if (args == null) { return String.format("(%s %s)", name,""); }
		String arglist = "";
		for (int i =0 ; i <args.size(); i++){
			arglist += (args.get(i).toString());
		}		
		return String.format("(%s %s)", name,arglist);
	}
	
	@Override
	public IRStmt buildIRStmt() {
		// TODO Auto-generated method stub
		List<IRStmt> proc = new ArrayList<IRStmt>();
		for(int i = 0; i < args.size(); i++){
			String argNum = "_ARG" + i;
			proc.add(new IRMove(new IRTemp(argNum), args.get(i).buildIRExpr()));
		}
		proc.add(new IRExp(new IRName(name.toString())));
		return new IRSeq(proc);
	}
	
}
