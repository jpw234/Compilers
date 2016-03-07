package compiler_ww424;
import java.util.ArrayList;
import java.util.List;

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
	
	public String toString(){
		String bodylist = "";
		if (body != null){
			for (int i = 0 ; i < body.size() ; i++){
				bodylist += (body.get(i).toString()+" ");
			}
		}
		return String.format("(%s %s %s)", "if", condition.toString(),bodylist.trim());
	}
}
