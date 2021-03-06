package compiler_ww424;

import java.util.ArrayList;
import java.util.List;
import edu.cornell.cs.cs4120.xic.ir.*;

public class ReturnStmt extends Stmt{
	private ArrayList<Expr> val;
	
	public ReturnStmt(ArrayList<Expr> v ,int lineNum,int colNum) {
		if(v==null) System.out.println("null input");
		val = v;
		line = lineNum;
		column = colNum;
	}
	
	public ArrayList<Expr> getExpr() {
		return val;
	}
	
	public void addVal(Expr e) {
		val.add(e);
	}
	
	public Type typecheck(SymTab s) {
		Type expected = s.getRetval();
		if(expected == null) throw new Error(line + ":" + column + " error: " + "returned in scope not expecting return");
		
		Type given = new Tuple(new ArrayList<Type>());
			
		for(int a = 0; a < val.size(); a++) {
			((Tuple)given).add(val.get(a).typecheck(s));
		}
		if(!given.equals(expected)) throw new Error(line + ":" + column + " error: " + "returned wrong type");
		else return new Type("void");
	}
	
	@Override
	public void constantFold() {
		for(int a = 0; a < val.size(); a++) {
			val.set(a, val.get(a).constantFold());
		}
	}
	
	@Override
	public String toString(){
		String s = "";
		if(val==null) return "";
		for(int i = 0; i < val.size(); i++){
			s = s + val.get(i).toString() + " ";
		}
		s = "(return " + s.trim() + " )";
		return s;
	}
	
	@Override
	public IRStmt buildIRStmt() {
		if(val.size() == 0){ return new IRReturn();} //void return
		else {//single return or multiple return
			List<IRStmt> retlist = new ArrayList<IRStmt>();
			for(int i = 0; i < val.size(); i++){
				String ret = "_RET"+i;
				retlist.add(new IRMove(new IRTemp(ret), val.get(i).buildIRExpr()));
			}
			retlist.add(new IRReturn());
			return new IRSeq(retlist);
		}
	}
}
