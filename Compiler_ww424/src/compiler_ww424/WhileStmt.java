package compiler_ww424;
import java.util.ArrayList;
import java.util.List;

import edu.cornell.cs.cs4120.xic.ir.*;

public class WhileStmt extends Stmt {
	private Expr condition; 
	private List<Stmt> body;
	
	public WhileStmt(Expr cond, List<Stmt> b,int lineNum,int colNum) {
		condition = cond; body = b;
		line = lineNum;
		column = colNum;
	}
	
	public Expr getCond() {
		return condition;
	}
	
	public List<Stmt> getBody() {
		return body;
	}
	
	public Type typecheck(SymTab s) {
		if(!condition.typecheck(s).equals(new Type("bool"))) throw new Error(line + ":" + column + " error: " + "not boolean in if condition");
		
		if(body.size()==0) return new Type("unit");
		
		for(int a = 0; a < body.size(); a++) {
			if(a == body.size() - 1) {
				Type t = body.get(a).typecheck(s);
				if(t.getType() != "unit" && 
						t.getType() != "void") {
					throw new Error(line + ":" + column + " error: " + "last stmt in while block does not typecheck");
				}
				else return new Type("unit");
			}
			else if(body.get(a).typecheck(s).getType() != "unit") throw new Error(line + ":" + column + " error: " + "stmt should be unit type");
		}
		
		throw new Error(line + ":" + column + " error: " + "shouldn't get here in whileblock typecheck");
	}
	
	public String toString(){
		if (condition==null && body == null ){
			return String.format("(%s %s (%s))", "while", "","");
		}
		if (condition!=null && body == null ){
			return String.format("(%s %s (%s))", "while", condition.toString(),"");
		}
		String bodyString = "";
		for (int i = 0 ; i < body.size(); i ++){
			bodyString += body.get(i).toString()+" ";
		}
		
		return String.format("(%s %s (%s))", "while", condition.toString(),bodyString.trim());
	}

	@Override
	public IRStmt buildIRStmt() {
		ArrayList<IRStmt> stmts = new ArrayList<IRStmt>();
		
		String startWhile = LabelMaker.Generate_Unique_Label("_startWhile");
		String endWhile = LabelMaker.Generate_Unique_Label("_endWhile");
		
		stmts.add(new IRLabel(startWhile));
		stmts.add(new IRCJump(new IRBinOp(IRBinOp.OpType.XOR,
										  condition.buildIRExpr(),
										  new IRConst(1)),
							  endWhile));
		
		for(Stmt s : body) {
			stmts.add(s.buildIRStmt());
		}
		
		stmts.add(new IRJump(new IRName(startWhile)));
		stmts.add(new IRLabel(endWhile));
		
		return new IRSeq(stmts);
	}
}
