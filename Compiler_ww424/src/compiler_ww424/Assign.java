package compiler_ww424;

public class Assign extends Stmt {
	private ArrExpr left;
	private Expr right;
	
	public Assign(ArrExpr l, Expr r,int lineNum,int colNum) {
		left = l;
		right = r;
		line = lineNum;
		column = colNum;
	}
	
	public ArrExpr getLeft() {
		return left;
	}
	
	public Expr getRight() {
		return right;
	}
	
	public Type typecheck(SymTab s) {
		Type expected = s.lookup(left.getName().getName());
		Type resType = right.typecheck(s);
		if(expected.getDepth() > 0 && resType.getType() == "empty") return new Type("unit");
		if(!expected.equals(resType)) throw new Error("mismatched types in assignment");
		
		return new Type("unit");
	}
}
