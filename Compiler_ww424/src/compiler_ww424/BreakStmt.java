package compiler_ww424;

public class BreakStmt extends Stmt{
	public BreakStmt(int lineNum, int colNum) {
		line = lineNum;
		column = colNum;
	}
	
	public Type typecheck(SymTab s) {
		return new Type("void");
	}
	
	@Override
	public String toString(){
		return "(break)";
	}
	//TODO: Implement buildIRStmt()
}
