package compiler_ww424;
import java.util.ArrayList;
import java.util.List;

import edu.cornell.cs.cs4120.xic.ir.IRSeq;
import edu.cornell.cs.cs4120.xic.ir.IRStmt;

public class Block extends Stmt {
	private List<Stmt> body;

	public Block(List<Stmt> b,int lineNum,int colNum) {
		body = b;
		line = lineNum;
		column = colNum;
	}

	public List<Stmt> getBody() {
		return body;
	}

	public Type typecheck(SymTab s) {
		SymTab newScope = new SymTab(s);
		
		if(body.size()==0) return new Type("unit");

		for(int a = 0; a < body.size(); a++) {
			if(a == body.size() - 1) {
				Type t = body.get(a).typecheck(newScope);
				if(t.getType() != "unit" && t.getType() != "void") {
					throw new Error(line + ":" + column + " error: " + "last stmt in block does not typecheck");
				}
				else return new Type("unit");
			}
			else if(body.get(a).typecheck(newScope).getType() != "unit") throw new Error(line + ":" + column + " error: " + "stmt should be unit type");
		}

		throw new Error(line + ":" + column + " error: " + "shouldn't get here in block typecheck");
	}
	public boolean returncheck() {
		if(body.size()>=1) {
			if((body.get(body.size()-1) instanceof ReturnStmt))
			{
				return true;
			}
			for(Stmt statement : body) {
				if(statement instanceof Block)
				{
					if(((Block)statement).returncheck())
					{
						return true;
					}
				}
				if(statement instanceof IfElseStmt)
				{
					if(((IfElseStmt)statement).returncheck())
					{
						return true;
					}
				}
			}
		}
		return false;
	}
	
	@Override
	public void constantFold() {
		for(int a = 0; a < body.size(); a++) {
			body.get(a).constantFold();
		}
	}

	@Override
	public String toString(){
		String s = "";
		if (body != null){
			for(int i = 0; i < body.size(); i++){
				s = s + body.get(i).toString() + "\n";
			}
		}
		s = "( " + s + " )";
		return s ;
	}
	
	@Override
	public IRStmt buildIRStmt() {
		// TODO Auto-generated method stub
		if (body == null) return null; 
		List<IRStmt> stmts = new ArrayList<IRStmt>();
		for (Stmt s : body){
			if (s.buildIRStmt() == null) continue;
			stmts.add(s.buildIRStmt());
		}
		return new IRSeq(stmts);
	}
	
	public void unreachableCodeRemove() {
		Function.unreachableCodeRemoveHelper(body);
	}
}
