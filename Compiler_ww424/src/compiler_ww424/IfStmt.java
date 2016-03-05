package compiler_ww424;
import java.util.List;

public class IfStmt extends Stmt {
	private BinaryExpr condition; 
	private List<Stmt> body;
	
	public IfStmt(BinaryExpr cond, List<Stmt> b) {
		condition = cond; body = b;
	}
	
	public BinaryExpr getCond() {
		return condition;
	}
	
	public List<Stmt> getBody() {
		return body;
	}
	
	public Type typecheck(SymTab s) {
		SymTab newScope = new SymTab(s);
		
		if(condition.typecheck(newScope).getType() == "bool") throw new Error("not boolean in if condition");
		
		for(int a = 0; a < body.size(); a++) {
			if(a == body.size() - 1) {
				if(body.get(a).typecheck(newScope).getType() != "unit" && 
						body.get(a).typecheck(newScope).getType() != "void") {
					throw new Error("last stmt in if block does not typecheck");
				}
				else return body.get(a).typecheck(newScope);
			}
			else if(body.get(a).typecheck(newScope).getType() != "unit") throw new Error("stmt should be unit type");
		}
		
		throw new Error("shouldn't get here in ifblock typecheck");
	}
}