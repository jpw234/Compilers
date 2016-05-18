package compiler_ww424;

import java.util.ArrayList;
import java.util.List;

import edu.cornell.cs.cs4120.xic.ir.*;

public class Program {
	List<Use> imports;
	List<Object> initial;
	List<Function> funcs;
	List<ClassDef> classes;
	List<Object> decls;

	public Program(List<Use> i, List<Object> f) {
		imports = i;
		initial = f;
		
		funcs = new ArrayList<Function>();
		classes = new ArrayList<ClassDef>();
		//all decls (Decl, DeclAssign, MultiDecl) must be condensed to one list b/c must be treated in order
		decls = new ArrayList<Object>();
		
		for(Object k : initial) {
			if(k instanceof Function)  {
				funcs.add((Function) k);
			}
			else if(k instanceof ClassDef) {
				classes.add((ClassDef) k);
			}
			else if(k instanceof Decl) {
				decls.add(k);
			}
			else if(k instanceof DeclAssign) {
				decls.add(k);
			}
			else if(k instanceof MultiDecl) {
				decls.add(k);
			}
			else throw new Error("unacceptable type in program");
		}
	}

	public List<Use> getImports() {
		return imports;
	}

	public List<Function> getFunctions() {
		return funcs;
	}

	public void addImport(Use u) {
		imports.add(u);
	}

	public void addFunction(Function f) {
		funcs.add(f);
	}

	public void firstPass(SymTab s) {
		//TODO: Insert use logic
		
		//first do all the globals, in order
		for(Object a : decls) {
			if(a instanceof Decl) {
				((Decl) a).typecheck(s);
			}
			else if(a instanceof DeclAssign) {
				((DeclAssign) a).typecheck(s);
			}
			else if(a instanceof MultiDecl) {
				((MultiDecl) a).typecheck(s);
			}
		}

		for(int a = 0; a < funcs.size(); a++) {
			funcs.get(a).firstPass(s);
		}
		
		for(int a = 0; a < classes.size(); a++) {
			classes.get(a).firstpass(s);
		}
	}

	public void secondPass(SymTab s) {
		for(int a = 0; a < funcs.size(); a++) {
			funcs.get(a).typecheck(s);
		}
		for(int a = 0; a < classes.size(); a++) {
			classes.get(a).typecheck(s);
		}
	}
	public void returnPass() {
		for(int a = 0; a < funcs.size(); a++) {
			funcs.get(a).returncheck();
		}
	}
	
	public void constantFold() {
		for(int a = 0; a < funcs.size(); a++) {
			funcs.get(a).constantFold();
		}
		for(int a = 0; a < classes.size(); a++) {
			classes.get(a).constantFold();
		}
	}
	
	public void unreachableCodeRemove() {
		for(int a = 0; a < funcs.size(); a++) {
			funcs.get(a).unreachableCodeRemove();
		}
	}

	public String toString(){

		String useString = "";
		String funString = "";
		if (imports == null && funcs == null){
			return String.format("((%s) (%s))", useString,funString);
		}
		if (imports != null){
			for (int i = 0 ; i < imports.size(); i++){
				useString += imports.get(i).toString()+" ";
			}
		}
		if (funcs != null){
			for (int i = 0 ; i < funcs.size(); i++){
				funString += funcs.get(i).toString()+" ";
			}
		}
		return String.format("((%s) (%s))", useString,funString);
	}
}