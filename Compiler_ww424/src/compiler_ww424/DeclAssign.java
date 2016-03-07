package compiler_ww424;

import java.util.List;

public class DeclAssign extends Stmt {
	private List<Decl> left;
	private Expr right;
	
	public DeclAssign(List<Decl> l, Expr r,int lineNum,int colNum) {
		left = l;
		right = r;
		line = lineNum;
		column = colNum;
	}
	
	public List<Decl> getLeft() {
		return left;
	}
	
	public Expr getRight() {
		return right;
	}
	
	public Type typecheck(SymTab s) {
		for(int a = 0; a < left.size(); a++) {
			try {
				if(s.lookup(left.get(a).getName().getName()) != null) throw new Error("redeclared variable error");
			}
			catch(Error e) {
				if(e.getMessage() == "Semantic Error: var does not exist") {
					//do nothing
				}
				else throw new Error(line + ":" + column + " error: " + e.getMessage());
			}
		}
		
		Type rtype = right.typecheck(s);
		int numtypes = 1;
		if(rtype.getType() == "tuple") numtypes = ((Tuple) rtype).getArgs().size();
		
		if(numtypes != left.size()) throw new Error(line + ":" + column + " error: " + "incorrect number of arguments to multiple declaration");
		
		if(numtypes == 1) {
			if(left.get(0).getType().getType() == "underscore") {
				if(rtype instanceof FunType) return new Type("unit");
				else throw new Error(line + ":" + column + " error: " + "underscore can only be used with function calls");
			}
			if(!left.get(0).getType().equals(rtype)) throw new Error(line + ":" + column + " " + "error: mismatched types on declassign");
			s.add(left.get(0).getName().getName(), left.get(0).getType());
		}
		else {
			for(int a = 0; a < left.size(); a++) {
				if(left.get(a).getType().getType() == "underscore") continue;
				
				if(!left.get(a).getType().equals(((Tuple) rtype).getArgs().get(a))) throw new Error(line + ":" + column + " " + "error: mismatched types on var " + a + "of multiple decl");
				
				s.add(left.get(a).getName().getName(), ((Tuple) rtype).getArgs().get(a));
			}
		}
		
		return new Type("unit");
	}
	
	@Override
	public String toString(){
		String s = "";
		for(int i = 0; i < left.size(); i++){
			s = s + " " + left.get(i).toString();
		}
		if(left.size() > 1) {s = "( " + s + " )";}
		s = "(= " + s + " " + right.toString() + " )";
		return s ;
	}
}
