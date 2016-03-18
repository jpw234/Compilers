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
		
		if(condition.typecheck(newScope).getType() != "bool") throw new Error(line + ":" + column + " error: " + "not boolean in if condition");
		
		if(body.size()==0) return new Type("unit");
		
		for(int a = 0; a < body.size(); a++) {
			if(a == body.size() - 1) {
				Type t = body.get(a).typecheck(newScope);
				if(t.getType() != "unit" && 
						t.getType() != "void") {
					throw new Error(line + ":" + column + " error: " + "last stmt in if block does not typecheck");
				}
				else return t;
			}
			else if(body.get(a).typecheck(newScope).getType() != "unit") throw new Error(line + ":" + column + " error: " + "stmt should be unit type");
		}
		
		throw new Error(line + ":" + column + " error: " + "shouldn't get here in ifblock typecheck");
	}
	
	@Override
	public void constantFold() {
		condition = condition.constantFold();
		for(int a = 0; a < body.size(); a++) {
			body.get(a).constantFold();
		}
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
