package compiler_ww424;

import edu.cornell.cs.cs4120.xic.ir.*;

public class Length extends Expr {
	private Expr arg;

	
	public Length(Expr e, int lineNum, int columnNum) {
		arg = e;
        line = lineNum;
        column = columnNum;
	}
	
	public Expr getArg() {
		return arg;
	}
	
	public Type typecheck(SymTab s) {
		Type t = arg.typecheck(s);
		if(t.getDepth() < 1) throw new Error(line + ":" + column + " error: " + "argument to Length() is not an array");
		
		type = new Type("int");
		return type;
	}
	public String toString(){
		return String.format("(%s %s)", "length", arg.toString());
	}
	
	public Expr constantFold() {
		arg = arg.constantFold();
		return this;
	}
	
	public IRExpr buildIRExpr() {
		return new IRMem(new IRBinOp(IRBinOp.OpType.SUB,
									 arg.buildIRExpr(),
									 new IRConst(8)));
	}
}