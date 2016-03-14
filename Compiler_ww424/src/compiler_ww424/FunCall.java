package compiler_ww424;
import java.util.List;

public class FunCall extends Expr {
	private IDExpr name;
	private List<Expr> args;
	
	public FunCall(IDExpr n, List<Expr> a,int linNum,int colNum) {
		name = n; args = a;
		line = linNum;
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
			
			return ft.getOutputs();
		}
		catch(Error e) {
			if(e.getMessage() == "Semantic Error: var does not exist") throw new Error(line + ":" + column + " error: " + e.getMessage());
			else throw e;
		}
		
	}
	
	@Override
	public String toString(){
		String s = "";
		if (args == null) {
			return "( " + name.toString() +  " )";
		}
		for(int i = 0; i < args.size(); i++){
			s = s + " " + args.get(i).toString();
		}
		s = "( " + name.toString() + " " + s + " )";
		return s ;
	}
}
