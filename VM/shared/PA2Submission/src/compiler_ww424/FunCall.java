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
		FunType ft = (FunType) s.lookup(name.getName());
		
		if(args.size() != ft.getInputs().getArgs().size()) throw new Error("incorrect # of args to fun");
		
		for(int a = 0; a < args.size(); a++) {
			if(!args.get(a).typecheck(s).equals(ft.getInputs().getArgs().get(a))) 
				throw new Error("incorrect type of arg to fun");
		}
		
		return ft.getOutputs();
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
