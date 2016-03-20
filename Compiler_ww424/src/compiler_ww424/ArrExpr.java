package compiler_ww424;

import java.util.ArrayList;
import java.util.List;
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
	
	// invariant: depth <= acc.size()
	public static IRExpr get_offset(int depth, IRExpr baseExpr, List<Expr> acc) {
		IRExpr pass = baseExpr;
		
		for(int a = 0; a < depth; a++) {
			if(a == depth - 1) {
				pass = new IRBinOp(IRBinOp.OpType.ADD,
								   pass,
								   new IRBinOp(IRBinOp.OpType.MUL,
										       acc.get(a).buildIRExpr(),
										       new IRConst(8)));
			}
			else {
				pass = new IRMem(new IRBinOp(IRBinOp.OpType.ADD, 
										 	 pass,
										 	 new IRBinOp(IRBinOp.OpType.MUL,
												 	 	 acc.get(a).buildIRExpr(),
												 	 	 new IRConst(8))));
			}
		}
		return pass;
	}
	
	public IRExpr getAddress() {
		IRTemp idVal = (IRTemp)( name.buildIRExpr());
		
		IRESeq k = new IRESeq(new IRSeq(new ArrayList<IRStmt>()),
				name.buildIRExpr());
		
		for(int a = 0; a < accesses.size(); a++) {
			String live_label = LabelMaker.Generate_Unique_Label("_ARRAY_EXPR_BOUNDS_CHECK_PASS");
			k = new IRESeq(
							new IRSeq(
								new IRCJump(
										new IRBinOp(IRBinOp.OpType.AND,
													new IRBinOp(IRBinOp.OpType.GEQ,
																accesses.get(a).buildIRExpr(),
																new IRConst(0)),
													new IRBinOp(IRBinOp.OpType.LT,
																accesses.get(a).buildIRExpr(),
																new IRMem(new IRBinOp(IRBinOp.OpType.SUB,
																					  k,
																					  new IRConst(8))))),
										live_label),
								new IRExp(
									new IRCall(new IRName("_I_outOfBounds_p"))
									),
								new IRLabel(live_label)
							),
							get_offset(a, name.buildIRExpr(), accesses));
		}
		
		return k;
	}
	
	public IRExpr buildIRExpr() {
		return this.getAddress();
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
