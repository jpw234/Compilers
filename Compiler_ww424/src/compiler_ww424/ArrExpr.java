package compiler_ww424;

import java.util.ArrayList;
import java.util.List;
import edu.cornell.cs.cs4120.xic.ir.*;

public class ArrExpr extends Expr {
	protected IDExpr name;
	protected ArrayList<Expr> accesses ;
	protected int depth=0;
	
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
	
	public IRExpr getElement() {
		List<IRStmt> seqList = new ArrayList<IRStmt>();
		String _aP = LabelMaker.Generate_Unique_Label("_arrPtr");//temp address
		IRTemp arrPtr = new IRTemp(_aP);
		seqList.add(new IRMove(new IRTemp(_aP), new IRTemp(name.getName())));//get starting location
		for(int i = 0; i < accesses.size(); i++){
			String live_label = LabelMaker.Generate_Unique_Label("_ARRAY_EXPR_BOUNDS_CHECK_PASS");
			seqList.add(new IRCJump(new IRBinOp(IRBinOp.OpType.AND,
						new IRBinOp(IRBinOp.OpType.GEQ, accesses.get(i).buildIRExpr(), new IRConst(0)),
						new IRBinOp(IRBinOp.OpType.LT, accesses.get(i).buildIRExpr(), new IRMem(new IRBinOp(IRBinOp.OpType.SUB, new IRTemp(_aP), new IRConst(8))))),
						live_label));
			seqList.add(new IRExp(new IRCall(new IRName("_I_outOfBounds_p"))));
			seqList.add(new IRLabel(live_label));
			seqList.add(new IRMove(new IRTemp(_aP), new IRBinOp(IRBinOp.OpType.ADD, new IRTemp(_aP), 
													new IRBinOp(IRBinOp.OpType.MUL, accesses.get(i).buildIRExpr(), new IRConst(8)))));
			seqList.add(new IRMove(new IRTemp(_aP), new IRMem(new IRTemp(_aP))));
		}
		return new IRESeq(new IRSeq(seqList), new IRTemp(_aP));
	}
	
	public IRExpr buildIRExpr() {
		if(depth > 0)
			return this.getElement();
		else
			return new IRTemp(name.getName());
	}
	
	//For modify memory value, e.g. x:int[] = {1,2,3,4,5}  x[2] = 7
	public IRExpr getAddress() {
		List<IRStmt> seqList = new ArrayList<IRStmt>();
		String _aP = LabelMaker.Generate_Unique_Label("_arrPtr");//temp address
		IRTemp arrPtr = new IRTemp(_aP);
		seqList.add(new IRMove(new IRTemp(_aP), new IRTemp(name.getName())));//get starting location
		for(int i = 0; i < accesses.size(); i++){
			String live_label = LabelMaker.Generate_Unique_Label("_ARRAY_EXPR_BOUNDS_CHECK_PASS");
			seqList.add(new IRCJump(new IRBinOp(IRBinOp.OpType.AND,
						new IRBinOp(IRBinOp.OpType.GEQ, accesses.get(i).buildIRExpr(), new IRConst(0)),
						new IRBinOp(IRBinOp.OpType.LT, accesses.get(i).buildIRExpr(), new IRMem(new IRBinOp(IRBinOp.OpType.SUB, new IRTemp(_aP), new IRConst(8))))),
						live_label));
			seqList.add(new IRExp(new IRCall(new IRName("_I_outOfBounds_p"))));
			seqList.add(new IRLabel(live_label));
			seqList.add(new IRMove(new IRTemp(_aP), new IRBinOp(IRBinOp.OpType.ADD, new IRTemp(_aP), 
													new IRBinOp(IRBinOp.OpType.MUL, accesses.get(i).buildIRExpr(), new IRConst(8)))));
			if(i != accesses.size()-1) {seqList.add(new IRMove(new IRTemp(_aP), new IRMem(new IRTemp(_aP))));}
		}
		return new IRESeq(new IRSeq(seqList), new IRTemp(_aP));
	}
	
	public IRExpr buildIRExpr_Addr() {
		if(depth > 0)
			return this.getAddress();
		else
			return new IRTemp(name.getName());
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
