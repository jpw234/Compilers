package compiler_ww424;

import java.util.ArrayList;
import java.util.List;

public class Program {
	List<Use> imports;
	List<Function> funcs;

	public Program(List<Use> i, List<Function> f) {
		imports = i;
		funcs = f;
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

		for(int a = 0; a < funcs.size(); a++) {
			funcs.get(a).firstPass(s);
		}
	}

	public void secondPass(SymTab s) {
		for(int a = 0; a < funcs.size(); a++) {
			funcs.get(a).typecheck(s);
		}
	}

	public String toString(){
		String useString = "";
		String funString = "";
		for (int i = 0 ; i < imports.size(); i++){
			useString += imports.get(i).toString()+" ";
		}
		
		for (int i = 0 ; i < funcs.size(); i++){
			funString += funcs.get(i).toString()+" ";
		}
		return String.format("((%s) (%s))", useString,funString);
	}
}