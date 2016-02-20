package compiler_ww424;

public class OpAtom extends Expr {
	public String op;
	public OpAtom(String oper){
		op = oper;
	}
	
	@Override
	public String toString(){
		return op;
	}
}
