package compiler_ww424;
import java.util.List;

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
				if(body.get(a).typecheck(newScope).getType() != "unit" && 
						body.get(a).typecheck(newScope).getType() != "void") {
					throw new Error(line + ":" + column + " error: " + "last stmt in block does not typecheck");
				}
				else return body.get(a).typecheck(newScope);
			}
			else if(body.get(a).typecheck(newScope).getType() != "unit") throw new Error(line + ":" + column + " error: " + "stmt should be unit type");
		}

		throw new Error(line + ":" + column + " error: " + "shouldn't get here in block typecheck");
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
}
