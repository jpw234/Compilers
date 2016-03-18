package compiler_ww424;

import java.util.ArrayList;
import java.util.List;

import edu.cornell.cs.cs4120.xic.ir.*;

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
	public void returnPass() {
		for(int a = 0; a < funcs.size(); a++) {
			funcs.get(a).returncheck();
		}
	}
	
	public void constantFold() {
		for(int a = 0; a < funcs.size(); a++) {
			funcs.get(a).constantFold();
		}
	}
	public IRFuncDecl buildIR(){
		ArrayList<IRStmt> funbody = new ArrayList<IRStmt>();
		for (Function f: funcs){
			funbody.add(f.buildIRStmt());
		}
		return new IRFuncDecl("program",new IRSeq(funbody));
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