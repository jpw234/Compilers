package compiler_ww424;
import java.util.ArrayList;
import java.util.List;

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
		if(condition.typecheck(s).getType() == "bool") throw new Error("not boolean in if condition");
		
		for(int a = 0; a < body.size(); a++) {
			if(a == body.size() - 1) {
				if(body.get(a).typecheck(s).getType() != "unit" && 
						body.get(a).typecheck(s).getType() != "void") {
					throw new Error("last stmt in if block does not typecheck");
				}
				else return body.get(a).typecheck(s);
			}
			else if(body.get(a).typecheck(s).getType() != "unit") throw new Error("stmt should be unit type");
		}
		
		throw new Error("shouldn't get here in ifblock typecheck");
	}
	
	public String toString(){
		ArrayList<String> stms = new ArrayList<String> ();
		for (Stmt s : body){
			stms.add(s.toString());
		}
		String bodyString = String.format("%s", String.join(" ", stms));
		
		return String.format("(%s) (%s) (%s)", "while", condition.toString(),bodyString);
	}
}
