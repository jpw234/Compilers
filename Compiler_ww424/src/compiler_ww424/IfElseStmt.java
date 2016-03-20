package compiler_ww424;
import java.util.ArrayList;
import java.util.List;

import edu.cornell.cs.cs4120.xic.ir.*;

public class IfElseStmt extends Stmt {
	private Expr condition;
	private List<Stmt> ifbody;
	private List<Stmt> elsebody;
	
	public IfElseStmt(Expr cond, List<Stmt> ib, List<Stmt> eb,int lineNum,int colNum) {
		condition = cond; ifbody = ib; elsebody = eb;
		line   = lineNum;
		column = colNum;
	}
	
	public Expr getCond() {
		return condition;
	}
	
	public List<Stmt> getIfBody() {
		return ifbody;
	}
	
	public List<Stmt> getElseBody() {
		return elsebody;
	}
	
	public Type typecheck(SymTab s) {
		SymTab ifScope = new SymTab(s);
		
		if(condition.typecheck(ifScope).getType() != "bool") throw new Error(line + ":" + column + " error: " + "not boolean in if condition");
		
		for(int a = 0; a < ifbody.size(); a++) {
			if(a == ifbody.size() - 1) {
				Type t = ifbody.get(a).typecheck(ifScope);
				if(t.getType() != "unit" && 
						t.getType() != "void") {
					throw new Error(line + ":" + column + " error: " + "last stmt in if block does not typecheck");
				}
				else return new Type("unit");
			}
			else if(ifbody.get(a).typecheck(ifScope).getType() != "unit") throw new Error(line + ":" + column + " error: " + "stmt should be unit type");
		}
		
		SymTab elseScope = new SymTab(s);
		
		if(elsebody.size()==0) return new Type("unit");
		
		for(int a = 0; a < elsebody.size(); a++) {
			if(a == elsebody.size() - 1) {
				Type t = elsebody.get(a).typecheck(elseScope);
				if(t.getType() != "unit" && 
						t.getType() != "void") {
					throw new Error(line + ":" + column + " error: " + "last stmt in else block does not typecheck");
				}
				else return t;
			}
			else if(elsebody.get(a).typecheck(elseScope).getType() != "unit") throw new Error(line + ":" + column + " error: " + "stmt should be unit type");
		}
		
		throw new Error(line + ":" + column + " error: " + "shouldn't get here in ifelseblock typecheck");
	}
	public String toString(){
		String ifString = "";
		String elseString = "";
		if (ifbody != null){
			for (int i = 0 ; i <ifbody.size(); i++){
				ifString += (ifbody.get(i).toString()+" ");
			}
		}
		if (elsebody!= null){
			for (int i = 0 ; i <elsebody.size(); i++){
				ifString += (elsebody.get(i).toString()+" ");
			}
		}
		return String.format("(%s %s %s %s)", "if", condition.toString(),ifString, elseString);
	}
	public boolean returncheck() {
		boolean ifreturns = false;
		boolean elsereturns = false;
		if(ifbody.size()>=1) {
			if((ifbody.get(ifbody.size()-1) instanceof ReturnStmt))
			{
				ifreturns = true;
			}
			for(Stmt statement : ifbody) {
				if(statement instanceof Block)
				{
					if(((Block)statement).returncheck())
					{
						ifreturns = true;
					}
				}
				if(statement instanceof IfElseStmt)
				{
					if(((IfElseStmt)statement).returncheck())
					{
						ifreturns = true;
					}
				}
			}
		}
		if(elsebody.size()>=1) {
			if((elsebody.get(elsebody.size()-1) instanceof ReturnStmt))
			{
				elsereturns = true;
			}
			for(Stmt statement : elsebody) {
				if(statement instanceof Block)
				{
					if(((Block)statement).returncheck())
					{
						elsereturns = true;
					}
				}
				if(statement instanceof IfElseStmt)
				{
					if(((IfElseStmt)statement).returncheck())
					{
						elsereturns = true;
					}
				}
			}
		}
		return (ifreturns && elsereturns);
	}

	@Override
	public IRStmt buildIRStmt() {
		ArrayList<IRStmt> stmts = new ArrayList<IRStmt>();
		
		String trueLabel = LabelMaker.Generate_Unique_Label("_trueLabel");
		String endLabel = LabelMaker.Generate_Unique_Label("_endLabel");
		
		stmts.add(new IRCJump(condition.buildIRExpr(),
							  trueLabel));
		
		for(Stmt s : elsebody) {
			stmts.add(s.buildIRStmt());
		}
		
		stmts.add(new IRJump(new IRName(endLabel)));
		stmts.add(new IRLabel(trueLabel));
		
		for(Stmt s : ifbody) {
			stmts.add(s.buildIRStmt());
		}
		
		stmts.add(new IRLabel(endLabel));
		
		return new IRSeq(stmts);
	}
}
