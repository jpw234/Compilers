package compiler_ww424;

import java.util.List;
import java.util.ArrayList;
import edu.cornell.cs.cs4120.xic.ir.*;

public class Function {
	private IDExpr name;
	private List<Decl> args;
	private Tuple retType;
	private List<Stmt> body;

	private int line;
	private int column;
	private int bline;
	private int bcolumn;
	//these Tuples must be created in the .cup file

	public Function(IDExpr n, List<Decl> a, Tuple r, List<Stmt> b,int lineNum,int colNum,int bodLine,int bodCol) {
		name = n; args = a; retType = r; body = b;
		line = lineNum;
		column = colNum;
		bline = bodLine;
		bcolumn = bodCol;
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
		try{
			if(s.lookup(name.getName()) != null) throw new Error(line + ":" + column + " error: " + "function redeclared error");
		}
		catch(Error e) {
			if(e.getMessage() == "Semantic Error: var does not exist") {
				//do nothing
			}
			else throw new Error(line + ":" + column + " error: " + e.getMessage());
		}

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
		
		newScope.setRetval(retType);

		for(int a = 0; a < args.size(); a++) {
			args.get(a).typecheck(newScope);
		}

		for(int a = 0; a < body.size(); a++) {
			if(a == body.size() - 1) {
				Type t = body.get(a).typecheck(newScope);
				if(t.getType() != "unit" && 
						t.getType() != "void") {
					throw new Error(line + ":" + column + " error: " + "last stmt in function block does not typecheck");
				}
				else return new Type("unit");
			}
			else if(body.get(a).typecheck(newScope).getType() != "unit") throw new Error(line + ":" + column + " error: " + "stmt should be unit type");
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
	public void returncheck() {
		//MAKE THIS LESS HACKY
		if(retType.toString() == "")
		{
			return;
		}
		else
		{
			
			if((body.size()>=1) && (body.get(body.size()-1) instanceof ReturnStmt))
			{
				return;
			}
			for(Stmt statement : body) {
				if(statement instanceof Block)
				{
					if(((Block)statement).returncheck())
					{
						return;
					}
				}
				if(statement instanceof IfElseStmt)
				{
					if(((IfElseStmt)statement).returncheck())
					{
						return;
					}
				}
			}
			throw new Error(bline + ":" + bcolumn + " error: Missing return statement!");
		}
	}
	
	public void constantFold() {
		for(int a = 0; a < args.size(); a++) {
			args.get(a).constantFold();
		}
		for(int a = 0; a < body.size(); a++) {
			body.get(a).constantFold();
		}
	}
	
	@Override
	public String toString(){
		String s = "";
		String s_arg = "";
		String s_slist = "";
		if (args != null){
			for(int i = 0; i < args.size(); i++){
				s_arg = s_arg + " " + args.get(i).toString();
			}
		}
		s_arg = "( " + s_arg + " )";
		if (body != null){
			for(int i = 0; i < body.size(); i++){
				s_slist = s_slist + " " + body.get(i).toString();
			}
		}
		//add retType (from Tuple class) with "(" & ")" manually, because Tuple.toString() don't print parenthesis
		s = "(" + name.toString() + " " + s_arg + "( " + retType.toString() + " ) " + "( " + s_slist + " )" + " )";
		return s ;
	}
	
	IRFuncDecl buildIR(){
		List<IRStmt> seqList = new ArrayList<IRStmt>();
		//handle argument
		for(int i = 0; i < args.size(); i++){
			String argPrefix = "_ARG" + i;
			seqList.add(new IRMove(new IRTemp(args.get(i).getName().toString()), new IRTemp(argPrefix)));
		}
		//function body
		for(int i = 0; i < body.size(); i++){
			if(body.get(i).buildIRStmt() == null) {continue;}
			seqList.add(body.get(i).buildIRStmt()); //return statement is included in body
			if(i == body.size()-1 && !(body.get(i) instanceof ReturnStmt)){
				seqList.add(new IRReturn());
			}
		}
		Tuple inputs = new Tuple(new ArrayList<Type>());
		//handle mangle name
		if (!args.isEmpty()){
			for(int a = 0; a < args.size(); a++) {
				inputs.add(args.get(a).getType());
		}}
		FunType ret = new FunType(inputs, retType);
		//System.out.println("FuncDecl"+FunCall.mangle_name(name.getName(), ret));
		return new IRFuncDecl(FunCall.mangle_name(name.getName(), ret), new IRSeq(seqList));
	}
}
