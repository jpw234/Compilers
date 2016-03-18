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
		if(condition.typecheck(s).getType() != "bool") throw new Error(line + ":" + column + " error: " + "not boolean in if condition");
		
		if(body.size()==0) return new Type("unit");
		
		for(int a = 0; a < body.size(); a++) {
			if(a == body.size() - 1) {
				Type t = body.get(a).typecheck(s);
				if(t.getType() != "unit" && 
						t.getType() != "void") {
					throw new Error(line + ":" + column + " error: " + "last stmt in while block does not typecheck");
				}
				else return t;
			}
			else if(body.get(a).typecheck(s).getType() != "unit") throw new Error(line + ":" + column + " error: " + "stmt should be unit type");
		}
		
		throw new Error(line + ":" + column + " error: " + "shouldn't get here in whileblock typecheck");
	}
	
	@Override
	public void constantFold() {
		condition = condition.constantFold();
		for(int a = 0; a < body.size(); a++) {
			body.get(a).constantFold();
		}
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
}
