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
		try {
			Type expected = s.lookup(left.getName().getName());
			expected = new Type(expected.getType(), expected.getDepth() - left.getDepth());
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
}
