package compiler_ww424;
import java.util.ArrayList;
import java.util.List;

import edu.cornell.cs.cs4120.xic.ir.ControlFlow;
import edu.cornell.cs.cs4120.xic.ir.*;

public class IfStmt extends Stmt {
	private Expr condition; 
	private List<Stmt> body;
	
	public IfStmt(Expr cond, List<Stmt> b,int lineNum,int colNum) {
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
		SymTab newScope = new SymTab(s);
		
		if(condition.typecheck(newScope).getType() != "bool") throw new Error(line + ":" + column + " error: " + "not boolean in if condition");
		
		if(body.size()==0) return new Type("unit");
		
		for(int a = 0; a < body.size(); a++) {
			if(a == body.size() - 1) {
				Type t = body.get(a).typecheck(newScope);
				if(t.getType() != "unit" && 
						t.getType() != "void") {
					throw new Error(line + ":" + column + " error: " + "last stmt in if block does not typecheck");
				}
				else return new Type("unit");
			}
			else if(body.get(a).typecheck(newScope).getType() != "unit") throw new Error(line + ":" + column + " error: " + "stmt should be unit type");
		}
		
		throw new Error(line + ":" + column + " error: " + "shouldn't get here in ifblock typecheck");
	}
	
	public String toString(){
		String bodylist = "";
		if (body != null){
			for (int i = 0 ; i < body.size() ; i++){
				bodylist += (body.get(i).toString()+" ");
			}
		}
		return String.format("(%s %s %s)", "if", condition.toString(),bodylist.trim());
	}
	@Override
	public IRStmt buildIRStmt() {
		String uniqueLabelEnd = LabelMaker.Generate_Unique_Label("endif");
		
		ArrayList<IRStmt> ifstmts = new ArrayList<IRStmt>();
		
		ifstmts.add(new IRCJump(new IRBinOp(IRBinOp.OpType.XOR,
											condition.buildIRExpr(),
											new IRConst(1)),
								uniqueLabelEnd));
		
		for(Stmt s : body) {
			ifstmts.add(s.buildIRStmt());
		}
		
		ifstmts.add(new IRLabel(uniqueLabelEnd));
		
		return new IRSeq(ifstmts).IRLower();
	}
}
