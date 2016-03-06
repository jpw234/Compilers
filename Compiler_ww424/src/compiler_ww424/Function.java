package compiler_ww424;

import java.util.List;
import java.util.ArrayList;

public class Function {
	private IDExpr name;
	private List<Decl> args;
	private Tuple retType;
	private List<Stmt> body;

	private int line;
	private int column;
	//these Tuples must be created in the .cup file

	public Function(IDExpr n, List<Decl> a, Tuple r, List<Stmt> b,int lineNum,int colNum) {
		name = n; args = a; retType = r; body = b;
		line = lineNum;
		column = colNum;
	}
	public Function(IDExpr n, List<Decl> a, Tuple r,int lineNum,int colNum) {
		name = n; args = a; retType = r; 
		line = lineNum;
		column = colNum;
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

	public void firstPass(SymTab s) {
		if(s.lookup(name.getName()) != null) throw new Error("function redeclared error");

		Tuple inputs = new Tuple(new ArrayList<Type>());
		if (!args.isEmpty()){
			for(int a = 0; a < args.size(); a++) {
				inputs.add(args.get(a).getType());
			}}

		FunType ret = new FunType(inputs, retType);

		s.add(name.getName(), ret);
	}

	public Type typecheck(SymTab s) {
		SymTab newScope = new SymTab(s);

		for(int a = 0; a < args.size(); a++) {
			args.get(a).typecheck(newScope);
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

		return null;		
	}
	public int getLine() {
		return line;
	}
	public int getColumn()
	{
		return column;
	}

	@Override
	public String toString(){
		String s = "";
		String s_arg = "";

		for(int i = 0; i < args.size(); i++){
			s_arg = s_arg + " " + args.get(i).toString();
		}

		s_arg = "( " + s_arg + " )";
		String s_slist = "";
		for(int i = 0; i < body.size(); i++){
			s_slist = s_slist + " " + body.get(i).toString();
		}
		//add retType (from Tuple class) with "(" & ")" manually, because Tuple.toString() don't print parenthesis
		s = "( " + name.toString() + " " + s_arg + " ( " + retType.toString() + " ) " + " ( " + s_slist + " ) " + " )";
		return s ;
	}
}
