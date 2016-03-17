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
		type = t;
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
	
	public IRExpr buildIRExpr() {
		return new IRConst(42); // THIS IS TO GET THIS TO COMPILE WHILE WAITING FOR JARED REMOVE ASAP
	}
}