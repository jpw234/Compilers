package compiler_ww424;

public class NumAtom extends Expr {
	public int num;
	public NumAtom(int n){
		num = n;
	}
	
	@Override
	public String toString(){
		return Integer.toString(num);
	}
}
