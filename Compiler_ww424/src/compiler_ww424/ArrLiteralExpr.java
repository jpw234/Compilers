package compiler_ww424;

import java.util.ArrayList;
import edu.cornell.cs.cs4120.xic.ir.*;

public class ArrLiteralExpr extends Expr {
	private ArrayList<Expr> values;
	private ArrayList<Expr> accesses = new ArrayList<Expr>();
	private Boolean isString = false;
	private String str;
	
	public ArrLiteralExpr(ArrayList<Expr> v, int l, int c) {
		values = v;
		line = l;
		column = c;
	}
	
	public ArrLiteralExpr(ArrayList<Expr> v, ArrayList<Expr> a, int l, int c) {
		values = v; accesses = a; line = l; column = c;
	}

	//for string
	public ArrLiteralExpr(String s, int l, int c) {
		line = l;
		column = c;
		isString = true;
		str = s;
		values = new ArrayList<Expr>();
		for(int i = 0; i < s.length(); i++){
			addValue(new NumExpr((int)s.charAt(i), l, c));
		}
	}
	
	public ArrayList<Expr> getValues() {
		return values;
	}
	
	public ArrayList<Expr> getAccesses() {
		return accesses;
	}
	
	public void addValue(Expr v) {
		values.add(v);
	}
	
	public void addAccess(Expr v) {
		accesses.add(v);
	}
	
	public Type typecheck(SymTab s) {
		if(values.size() == 0) {
			if(accesses.size() != 0) throw new Error(line + ":" + column + " error: " + "tried to access empty array");
			else type = new Type("empty", 1); return type;
		}
		
		Type t = values.get(0).typecheck(s);
		for(int a = 1; a < values.size(); a++) {
			if(!t.equals(values.get(a).typecheck(s))) throw new Error(line + ":" + column + " error: " + "mismatched args to array literal");
		}
		
		if(accesses.size() > (t.getDepth() + 1)) throw new Error(line + ":" + column + " error: " + "tried to access something that isn't an array");
		
		Type dummyInt = new Type("int");
		for(int a = 0; a < accesses.size(); a++) {
			if(!dummyInt.equals(accesses.get(a).typecheck(s))) throw new Error(line + ":" + column + " error: " + "non-integer expr in array access");
		}
		
		t.addDepth();
		
		type = new Type(t.getType(), t.getDepth()-accesses.size());
		return type;
	}
	@Override
	public String toString(){
		String s = "";
		if(isString){s = "\"" + str + "\"";}
		else{//tuple
			for(int i = 0; i < values.size(); i++){s = s + " " + values.get(i).toString();}
			s = "( " + s + " )";
		}
		for(int i = 0; i < accesses.size(); i++){
			s = "( [] " + s + accesses.get(i).toString() + " )";
		}
		
		return s ;
	}
	
	@Override
	public Expr constantFold() {
		for(int a = 0; a < values.size(); a++) {
			values.set(a, values.get(a).constantFold());
		}
		//only have to fold the first access b/c recursive call does remaining
		if(!accesses.isEmpty()) accesses.set(0, accesses.get(0).constantFold());
		
		//if the first access isn't a constant, we just return this
		if(!(accesses.get(0) instanceof NumExpr)) return this;
		
		else {
			//set the return to the element specified by the access
			Expr ret = values.get(((NumExpr) accesses.get(0)).getIntVal()); 
			
			//now we need to check if we're done, or if we have more nesting/accesses to do
			if(accesses.size() == 1) return ret;
			
			else {
				//if there's more nestings, the val is either an ArrExpr or an ArrLiteralExpr, or an IDExpr
				if(ret instanceof ArrExpr) {
					for(int a = 1; a < accesses.size(); a++) {
						((ArrExpr) ret).add(accesses.get(a));
					}
				}
				else if(ret instanceof ArrLiteralExpr) {
					for(int a = 1; a < accesses.size(); a++) {
						((ArrLiteralExpr) ret).addAccess(accesses.get(a));
					}
				}
				//here ret must be IDExpr
				else {
					ret = new ArrExpr(((IDExpr) ret), (ArrayList<Expr>) accesses.subList(1, accesses.size()), line, column);
				}
				
				return ret.constantFold();
			}
		}
	}
	
	public IRExpr buildIRExpr() {
		ArrayList<IRStmt> stmtlist = new ArrayList<IRStmt>();
		
		IRTemp arrpointer = new IRTemp("a");
		
		//allocate (n+1)*8 bytes of memory and move the pointer to temp register "a"
		stmtlist.add(new IRMove(arrpointer, new IRCall(new IRName("_I_alloc_i"), new IRConst((values.size() + 1) * 8))));
		//put the length in this spot
		stmtlist.add(new IRMove(new IRMem(arrpointer), new IRConst(values.size())));
		//shift the pointer to the array up 8, so length is in spot -1
		stmtlist.add(new IRMove(arrpointer, new IRBinOp(IRBinOp.OpType.ADD,
														arrpointer,
														new IRConst(8))));
		//now fill the array
		for(int a = 0; a < values.size(); a++) {
			//add value a to position at arrpointer + (a*8)
			stmtlist.add(new IRMove(new IRMem(new IRBinOp(IRBinOp.OpType.ADD,
														  arrpointer,
														  new IRConst(a*8))),
									values.get(a).buildIRExpr()));
		}
		
		//Create base eseq which initializes the ArrLiteralExpr and returns it
		//This is the result of buildIRExpr in the case of no accesses
		IRESeq eseq = new IRESeq(new IRSeq(stmtlist), arrpointer);
		
		if(accesses.size() == 0) return eseq;
		
		IRESeq k = new IRESeq(new IRSeq(new ArrayList<IRStmt>()),
				  			  arrpointer);

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
				ArrExpr.get_offset(a, arrpointer, accesses));
		}
		
		return k;
	}
}