package compiler_ww424;

import java.util.List;

public class Function {
	private IDExpr name;
	private Tuple args;
	private Tuple retType;
	private List<Stmt> body;
	
	//these Tuples must be created in the .cup file
	
	public Function(IDExpr n, Tuple a, Tuple r, List<Stmt> b) {
		name = n; args = a; retType = r; body = b;
	}
	
	public IDExpr getName() {
		return name;
	}
	
	public Tuple getArgs() {
		return args;
	}
	
	public Tuple getRetType() {
		return retType;
	}
	
	public List<Stmt> getBody() {
		return body;
	}
}
