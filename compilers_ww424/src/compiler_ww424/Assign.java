package compiler_ww424;

public class Assign extends Stmt {
	private IDExpr left;
	private Expr right;
	
	public Assign(IDExpr l, Expr r) {
		left = l;
		right = r;
	}
	
	public IDExpr getLeft() {
		return left;
	}
	
	public Expr getRight() {
		return right;
	}
	
	public Type typecheck(SymTab s) {
		Type expected = s.lookup(left.getName());
		Type resType = right.typecheck(s);
		if(!expected.equals(resType)) throw new Error("mismatched types in assignment");
		
		return new Type("unit");
	}

}
