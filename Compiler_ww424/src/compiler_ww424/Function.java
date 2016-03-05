package compiler_ww424;

import java.util.List;
import java.util.ArrayList;

public class Function {
	private IDExpr name;
	private List<Decl> args;
	private Tuple retType;
	private List<Stmt> body;
	
	//these Tuples must be created in the .cup file
	
	public Function(IDExpr n, List<Decl> a, Tuple r, List<Stmt> b) {
		name = n; args = a; retType = r; body = b;
	}
	
	public IDExpr getName() {
		return name;
	}
	
	public List<Decl> getArgs() {
		return args;
	}
	
	public Tuple getRetType() {
		return retType;
	}
	
	public List<Stmt> getBody() {
		return body;
	}
	
	public Type typecheck(SymTab s) {
		if(s.lookup(name.getName()) != null) throw new Error("function redeclared error");
		
		SymTab newScope = new SymTab(s);
				
		Tuple inputs = new Tuple(new ArrayList<Type>());
		
		for(int a = 0; a < args.size(); a++) {
			args.get(a).typecheck(newScope);
			inputs.add(args.get(a).getType());
		}
		
		for(int a = 0; a < body.size(); a++) {
			if(a == body.size() - 1) {
				if(body.get(a).typecheck(newScope).getType() != "unit" && 
						body.get(a).typecheck(newScope).getType() != "void") {
					throw new Error("last stmt in function block does not typecheck");
				}
				else return body.get(a).typecheck(newScope);
			}
			else if(body.get(a).typecheck(newScope).getType() != "unit") throw new Error("stmt should be unit type");
		}
		
		FunType ret = new FunType(inputs, retType);
		
		s.add(name.getName(), ret);
		
		return null;		
	}
}
