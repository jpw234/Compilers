package compiler_ww424;
import java.util.List;

public class IfElseStmt extends Stmt {
	private Expr condition;
	private List<Stmt> ifbody;
	private List<Stmt> elsebody;
	
	public IfElseStmt(BinaryExpr cond, List<Stmt> ib, List<Stmt> eb,int lineNum,int colNum) {
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
		
		if(condition.typecheck(ifScope).getType() == "bool") throw new Error("not boolean in if condition");
		
		for(int a = 0; a < ifbody.size(); a++) {
			if(a == ifbody.size() - 1) {
				if(ifbody.get(a).typecheck(ifScope).getType() != "unit" && 
						ifbody.get(a).typecheck(ifScope).getType() != "void") {
					throw new Error("last stmt in if block does not typecheck");
				}
				else return ifbody.get(a).typecheck(ifScope);
			}
			else if(ifbody.get(a).typecheck(ifScope).getType() != "unit") throw new Error("stmt should be unit type");
		}
		
		SymTab elseScope = new SymTab(s);
		
		for(int a = 0; a < elsebody.size(); a++) {
			if(a == elsebody.size() - 1) {
				if(elsebody.get(a).typecheck(elseScope).getType() != "unit" && 
				elsebody.get(a).typecheck(elseScope).getType() != "void") {
					throw new Error("last stmt in else block does not typecheck");
				}
				else return elsebody.get(a).typecheck(elseScope);
			}
			else if(elsebody.get(a).typecheck(elseScope).getType() != "unit") throw new Error("stmt should be unit type");
		}
		
		throw new Error("shouldn't get here in ifelseblock typecheck");
	}
}
