package compiler_ww424;

import java.util.List;
import java.util.ArrayList;

public class MultiDecl extends Stmt{
	private List<Decl> declList = null;
	
	public MultiDecl(List<IDExpr> idList, Decl varType, int lineNum, int colNum){
		declList = new ArrayList<Decl>();
		for(IDExpr id : idList){
			declList.add(new Decl(id, varType, id.getLine(), id.getColumn()));
		}
		line = lineNum;
		column = colNum;
	}
	
	public List<Decl> getDeclList(){
		return declList;
	}
	
	public Type typecheck(SymTab s){
		return null;
	}
}
