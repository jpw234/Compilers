package compiler_ww424;

import java.util.List;

import edu.cornell.cs.cs4120.xic.ir.*;

public class Decl extends Stmt {
	private IDExpr name;
	private Type type;
	private List<Expr> accesses = null;
	
	public Decl(IDExpr n, Type t,int lineNum,int colNum) {
		name = n;
		type = t;
		line = lineNum;
		column = colNum;
	}
	
	public Decl(IDExpr n, Type t, List<Expr> a, int lineNum, int colNum) {
		name = n;
		type = t;
		accesses = a;
		line = lineNum;
		column = colNum;
	}
	
	public void setID (IDExpr n){
		name = n ;
	}
	public IDExpr getName() {
		return name;
	}
	
	public Type getType() {
		return type;
	}
	
	public void addAccess(Expr a) {
		accesses.add(a);
	}
	
	public List<Expr> getAccesses() {
		return accesses;
	}
	
	public Type typecheck(SymTab s) {
		try {
			if(s.lookup(name.getName()) != null) throw new Error("redeclared variable error");
		}
		catch(Error e) {
			if(e.getMessage() == "Semantic Error: var does not exist") {
				//do nothing
			}
			else throw new Error(line + ":" + column + " error: " + e.getMessage());
		}
			
		if(type.getType() != "underscore") s.add(name.getName(), type);
		
		Type dummyType = new Type("int");
		
		if(accesses != null) {
			for(int a = 0; a < accesses.size(); a++) {
				if(!dummyType.equals(accesses.get(a).typecheck(s))) {
					throw new Error(line + ":" + column + " error: " + "non-integer expression used as array access");
				}
			}
		}
		
		return new Type("unit");
	}
	
	@Override
	public void constantFold() {
		if(accesses != null) {
			for(int a = 0; a < accesses.size(); a++) {
				accesses.set(a, accesses.get(a).constantFold());
			}
		}
	}
	
	@Override
	public String toString(){
		String s = type.getType();
		if(type.getDepth() > 0) {//array type variable
			for(int i = type.getDepth()-1; i >= 0; i--){
				if (accesses == null) {
					s = "( [] " + s +")";
 				}
				else {
					if(i < accesses.size()) { s = "( [] " + s + " " + accesses.get(i).toString() + " )";}
					else {s = "( [] " + s + " )";}
				}

			}
		}
		s = "( " + name.toString() + " " + s + " )";
		return (type.getType()=="underscore")? "_" : s ;
	}
	@Override
	public IRStmt buildIRStmt() {
		if(type.getDepth() == 0) {return null;}//just normal declaration, e.g. x:int
		else{ //array declaration
			if(accesses.size() == 0) {return null;}
			List<IRStmt> seqList = new ArrayList<IRStmt>();
			IRTemp arrpointer = new IRTemp("a");
			int i = 0;//start with index 0
			String n = LabelMaker.Generate_Unique_Label("_n");
			IRTemp _n = new IRTemp(n);
			seqList.add(new IRMove(_n, accesses.get(i).buildIRExpr()));//_n = E[[expr]]
			seqList.add(new IRMove(_n, new IRBinOp(IRBinOp.OpType.MUL, new IRBinOp(IRBinOp.OpType.ADD, _n, new IRConst(1)), new IRConst(8))));//(_n+1)*8
			seqList.add(new IRMove(arrpointer, new IRCall(new IRName("_I_alloc_i"), _n)));
			seqList.add(new IRMove(new IRMem(arrpointer), _n));//arr[-1] = _n
			seqList.add(new IRMove(arrpointer, new IRBinOp(IRBinOp.OpType.ADD, arrpointer, new IRConst(8)))); // arr = arr + 8
			seqList.add(new IRMove(new IRTemp(name.getName()), arrpointer));
			return new IRSeq(seqList);
//			i++;
//			if(i < accesses.size()){
//				String j = LabelMaker.Generate_Unique_Label("_j");
//				IRTemp _j = new IRTemp(j);
//				seqList.add(new IRMove(_j, new IRConst(0)));
//				//while statement
//				String t = LabelMaker.Generate_Unique_Label("_TRUE");
//				String h = LabelMaker.Generate_Unique_Label("_WHILE_HEAD");
//				List<IRStmt> wList = new ArrayList<IRStmt>();
//				wList.add();
//				//
//			}
		}
	}
}
