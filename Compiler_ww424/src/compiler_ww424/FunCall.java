package compiler_ww424;
import java.util.List;
import java.util.ArrayList;
import edu.cornell.cs.cs4120.xic.ir.*;

public class FunCall extends Expr {
	protected IDExpr name;
	protected ArrayList<Expr> args;
	protected FunType ft;

	public static String mangle_name(String n, FunType t){
		String res = "_I" + n.replaceAll("_", "__") + "_";
		Tuple out = t.getOutputs();
		if(out == null || out.getArgs() == null||out.getArgs().size() == 0 ) res = res + "p";
		else if(out.getArgs().size() > 1) res = res + "t" + out.getArgs().size();
		if (out != null ) {
			for(int a = 0; a < out.getArgs().size(); a++) {
				if (out.getArgs().get(a) == null ) continue;
				for(int b = 0; b < out.getArgs().get(a).getDepth(); b++) {
					res = res + "a";
				}
				if(out.getArgs().get(a).getType().equals("int")) res = res + "i";
				else res = res + "b";
			}
		}

		Tuple in = t.getInputs();
		if (in != null){
			for(int a = 0; a < in.getArgs().size(); a++) {
				if (in.getArgs().get(a) == null ) continue;
				if(in.getArgs().get(a) instanceof Tuple){
					for(int b = 0; b < ((Tuple)(in.getArgs().get(a))).getArgs().get(0).getDepth(); b++) {
						res = res + "a";
					}
					if(((Tuple)(in.getArgs().get(a))).getArgs().get(0).getType().equals("int")) res = res + "i";
					else if(((Tuple)(in.getArgs().get(a))).getArgs().get(0).getType().equals("bool")) res = res + "b";
				}
				else{
					for(int b = 0; b < in.getArgs().get(a).getDepth(); b++) {
						res = res + "a";
					}
					if(in.getArgs().get(a).getType().equals("int")) res = res + "i";
					else if(in.getArgs().get(a).getType().equals("bool")) res = res + "b";
				}
			}
		}
		//System.out.println(res);
		return res;
	} 

	public FunCall(IDExpr n, ArrayList<Expr> a,int linNum,int colNum) {
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
			ft = (FunType) s.lookupFunction(name.getName());

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

	public IRExpr buildIRExpr() {

		Tuple inputs = new Tuple(new ArrayList<Type>());
		for(int a = 0; a < args.size(); a++) {
			inputs.add(args.get(a).getType());
		}

		//FunType ft = new FunType(inputs, (Tuple) type);

		ArrayList<IRExpr> irargs = new ArrayList<IRExpr>();
		for(int a = 0; a < args.size(); a++) {
			irargs.add(args.get(a).buildIRExpr());
		}

		//return new IRCall(new IRName(name.getName()), irargs);
		return new IRCall(new IRName(mangle_name(name.getName(), ft)), irargs);
	}

	public Expr constantFold() {
		for(int a = 0; a < args.size(); a++) {
			args.set(a, args.get(a).constantFold());
		}

		return this;
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
	
	@Override
	public Type getType() {
		return this.type;
	}
}
