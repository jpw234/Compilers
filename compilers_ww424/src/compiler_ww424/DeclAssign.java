package compiler_ww424;

import java.util.List;

public class DeclAssign extends Stmt {
	private List<Decl> left;
	private Expr right;
	
	public DeclAssign(List<Decl> l, Expr r) {
		left = l;
		right = r;
	}
	
	public List<Decl> getLeft() {
		return left;
	}
	
	public Expr getRight() {
		return right;
	}
	
	public Type typecheck(SymTab s) {
		Type rtype = right.typecheck(s);
		int numtypes = 1;
		if(rtype.getType() == "tuple") numtypes = ((Tuple) rtype).getArgs().size();
		
		if(numtypes != left.size()) throw new Error("incorrect number of arguments to multiple declaration");
		
		if(numtypes == 1) {
			if(left.get(0).getType().getType() == "underscore") return new Type("unit");
		}
		else {
			for(int a = 0; a < left.size(); a++) {
				if(left.get(0).getType().getType() == "underscore") continue;
				
				if(!left.get(a).getType().equals(((Tuple) rtype).getArgs().get(a))) throw new Error("mismatched types on var " + a + "of multiple decl");
				
				s.add(left.get(a).getName().getName(), ((Tuple) rtype).getArgs().get(a));
			}
		}
		
		return new Type("unit");
	}
}
