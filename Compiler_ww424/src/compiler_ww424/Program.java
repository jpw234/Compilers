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
		ArrayList<String> uselist = new ArrayList<String> ();
		ArrayList<String> funlist = new ArrayList<String> ();
		if (!uselist.isEmpty()){
			for (Use u : imports){
				uselist.add(u.toString());
			}}
		if (!funlist.isEmpty()){
			for (Function f : funcs){
				funlist.add(f.toString());
			}
		}
		String useString = String.format("%s", String.join(" ", uselist));
		String funString = String.format("%s", String.join(" ", funlist));

		return String.format("((%s) (%s))", useString,funString);
	}
}