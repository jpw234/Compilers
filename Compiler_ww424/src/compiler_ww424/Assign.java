package compiler_ww424;

import edu.cornell.cs.cs4120.xic.ir.*;

public class Assign extends Stmt {
	private Expr left;
	private Expr right;
	
	public Assign(Expr l, Expr r,int lineNum,int colNum) {
		left = l;
		right = r;
		line = lineNum;
		column = colNum;
	}
	
	public Expr getLeft() {
		return left;
	}
	
	public Expr getRight() {
		return right;
	}
	
	public Type typecheck(SymTab s) {
		if(left instanceof ArrExpr) {
			ArrExpr temp = (ArrExpr) left;
			try {
				Type expected = left.typecheck(s);
				Type resType = right.typecheck(s);
				if(expected.getDepth() > 0 && resType.getType() == "empty") return new Type("unit");
				if(!expected.equals(resType)) throw new Error(line + ":" + column + " error: " + "mismatched types in assignment");
			
				return new Type("unit");
			}
			catch(Error e) {
				if(e.getMessage() == "Semantic Error: var does not exist") {
					throw new Error(line + ":" + column + " error: " + e.getMessage());
				}
				else throw e;
			}
		}
		else if(left instanceof FieldExpr) {
			FieldExpr temp = (FieldExpr) left;
			try {
				Type expected = left.typecheck(s);
				Type resType = right.typecheck(s);
				if(expected.getDepth() > 0 && resType.getType() == "empty") return new Type("unit");
				if(!expected.equals(resType)) throw new Error(line + ":" + column + " error: " + "mismatched types in assignment");
			
				return new Type("unit");
			}
			catch(Error e) {
				if(e.getMessage() == "Semantic Error: var does not exist") {
					throw new Error(line + ":" + column + " error: " + e.getMessage());
				}
				else throw e;
			}
		}
		else throw new Error("left side of Assign must be lvalue");
	}
	
	@Override
	public void constantFold() {
		left = (ArrExpr) left.constantFold();
		right = right.constantFold();
	}
	
	@Override
	public String toString(){
		String s = "";
		s = "( = " + left.toString() + " " + right.toString() + " )";
		return s ;
	}
	
	@Override
	public IRStmt buildIRStmt() {
		// TODO Auto-generated method stub
		if(left instanceof ArrExpr) {
			ArrExpr temp = (ArrExpr) left;
			if (temp.getDepth() == 0 ){
				return new IRMove( temp.getName().buildIRExpr(), right.buildIRExpr());
			}
			else{
				//getDepth != 0 --> An array 
				return new IRMove(new IRMem(temp.buildIRExpr_Addr()), right.buildIRExpr());
			}
		}
		else if(left instanceof FieldExpr) {
			FieldExpr temp = (FieldExpr) left;
			//TODO: implement this
			return null;
		}
		else throw new Error("not variable or field on left of assignment");
	}
}
