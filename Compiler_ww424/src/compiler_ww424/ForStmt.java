package compiler_ww424;

import java.util.ArrayList;
import java.util.List;
import edu.cornell.cs.cs4120.xic.ir.*;

public class ForStmt extends Stmt {
	private DeclAssign init;
	private Expr cond;
	private Assign update;
	private List<Stmt> body;
	
	public ForStmt(DeclAssign i, Expr c, Stmt e, List<Stmt> b, int lineNum, int colNum) {
		init = i;
		cond = c;
		update =(Assign)e;
		body = b;
		line = lineNum;
		column = colNum;
	}

	public DeclAssign getInit() {
		return init;
	}
	
	public Expr getCond() {
		return cond;
	}
	
	public Assign getUpdate() {
		return update;
	}
	
	public List<Stmt> getBody() {
		return body;
	}
	
	public Type typecheck(SymTab s) {
		SymTab newScope = new SymTab(s); //for loop variable is only in scope in the for loop
		
		init.typecheck(newScope);
		if(!cond.typecheck(newScope).equals(new Type("bool"))) throw new Error("condition for for loop doesn't typecheck to bool");
		update.typecheck(newScope);
		
		for(int a = 0; a < body.size(); a++) {
			if(a == body.size() - 1) {
				Type t = body.get(a).typecheck(s);
				if(t.getType() != "unit" && 
						t.getType() != "void") {
					throw new Error(line + ":" + column + " error: " + "last stmt in for block does not typecheck");
				}
				else return new Type("unit");
			}
			else if(body.get(a).typecheck(s).getType() != "unit") throw new Error(line + ":" + column + " error: " + "stmt should be unit type");
		}
		
		throw new Error("shouldn't get here in forblock typecheck");
	}
	
	public String toString(){
		String bodyString = "";
		for (int i = 0 ; i < body.size(); i ++){
			bodyString += body.get(i).toString()+" ";
		}
		
		return String.format("(%s %s, %s, %s (%s))", "for", init.toString(), cond.toString(), update.toString(), bodyString.trim());
	}
	
	@Override
	public IRStmt buildIRStmt() {
		ArrayList<IRStmt> stmts = new ArrayList<IRStmt>();
		
		//first do init, then make a while loop with body + update as its body
		stmts.add(init.buildIRStmt());
		
		ArrayList<Stmt> tempBody = new ArrayList<Stmt>(body);
		tempBody.add(update);
		
		WhileStmt temp = new WhileStmt(cond, tempBody, line, column);
		
		stmts.add(temp.buildIRStmt());
		
		return new IRSeq(stmts);
	}
}
