package compiler_ww424;

import java.util.ArrayList;
import edu.cornell.cs.cs4120.xic.ir.*;

public class ArrExpr extends Expr {
	private IDExpr name;
	private ArrayList<Expr> accesses ;
	private int depth=0;
	
	public ArrExpr(IDExpr n,int l,int c) {
		name = n;
		accesses = new ArrayList<Expr>();
		line = l;
		column = c;
	}
	
	public ArrExpr(IDExpr n, ArrayList<Expr> list, int l, int c) {
		name = n; 
		accesses = list;
		line = l;
		column = c;
	}
	
	public void add(Expr e) {
		accesses.add(e);
	}
	public void addDepth(){
		depth +=1 ;
	}
	public int getDepth(){
		return depth;
	}
	public IDExpr getName() {
		return name;
	}
	
	public ArrayList<Expr> getAccesses() {
		return accesses;
	}
	
	public Type typecheck(SymTab s) {
		Type temp = s.lookup(name.getName());
		if(temp.getDepth()-accesses.size() < 0) throw new Error(line + ":" + column + " error: " + "illegal access: that is not an array");
		for(int a = 0; a < accesses.size(); a++) {
			if(accesses.get(a).typecheck(s).getType() != "int") {
				throw new Error(line + ":" + column + " error: " + "illegal expr: non-integer value used as array access");
			}
		}
		type = new Type(temp.getType(), temp.getDepth()-accesses.size());
		return type;
	}
	
	@Override
	public Expr constantFold() {
		for(int a = 0; a < accesses.size(); a++) {
			accesses.set(a, accesses.get(a).constantFold());
		}
		
		return this;
	}
	
	public IRExpr buildIRExpr() {
		//TODO: add out-of-bounds checking
		IRTemp idVal = (IRTemp) name.buildIRExpr();
		
		IRExpr pass = idVal;
		
		for(int a = 0; a < accesses.size(); a++) {
			if(a == accesses.size() - 1) {
				pass = new IRBinOp(IRBinOp.OpType.ADD,
								   pass,
								   new IRBinOp(IRBinOp.OpType.MUL,
										       accesses.get(a).buildIRExpr(),
										       new IRConst(8)));
			}
			else {
				pass = new IRMem(new IRBinOp(IRBinOp.OpType.ADD, 
										 	 pass,
										 	 new IRBinOp(IRBinOp.OpType.MUL,
												 	 	 accesses.get(a).buildIRExpr(),
												 	 	 new IRConst(8))));
			}
		}
		return pass;
	}
	
	@Override
	public String toString(){
		String s = name.toString();
		if(depth > 0) {//array type variable
			for(int i = 0; i < depth; i++){
				if(i < accesses.size()) { s = "( [] " + s + " " + accesses.get(i).toString() + " )";}
				else {s = "( [] " + s + " )";}
			}
		}
		
		return s ;
	}
}
