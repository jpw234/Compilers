package compiler_ww424;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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

	public HashMap<String,Wrapper> buildDispachTable(){
		HashMap<String,Wrapper> dispatchTable = new HashMap<String, Wrapper>();
		if (classes == null || classes.size() ==0) return dispatchTable;
		while (dispatchTable.size() != classes.size()){
			for (int i =0; i<classes.size();i++){
				DispatchTableHelper(classes.get(i),dispatchTable);
			}
		}
		return dispatchTable;
	}
	public void DispatchTableHelper(ClassDef cl,HashMap<String,Wrapper> map){
		if (cl.getExtend() == null){
			List<String> temp1 = new ArrayList<String>();
			//Mangling Name
			for(Function f : cl.getFuncts()){
				List<Type> input = new ArrayList<Type>();
				for(int d =0;d< f.getArgs().size();d++){
					input.add(f.getArgs().get(d).getType());
				}
				FunType ft = new FunType(new Tuple(input), f.getRetType());
				temp1.add(FunCall.mangle_name(f.getName().getName(), ft));
			}
			//Normal Name 
			List<String> temp2 = new ArrayList<String>();
			temp2.addAll(cl.getFunctions());
			StringWrapper methods = new StringWrapper(temp2,temp1);
			map.put(cl.getName(), new Wrapper(cl.getFields(),methods));
		}else{
			String parentClass = cl.getExtend();
			//String parentClass = cl.getExtend();
			if (!map.containsKey(parentClass)) return;
			//Parents'
			List<String> parentsClassField = map.get(parentClass).getFields();
			StringWrapper parentsClassMethod= map.get(parentClass).getMethods();
			//Child's Fields
			List<String> Fields = new ArrayList<String>();
			Fields.addAll(parentsClassField);
			Fields.addAll(cl.getFields());
			//Child's Methods
			
			//Mangling Name
			List<String> temp = new ArrayList<String>();
			temp.addAll(parentsClassMethod.getMangleMethod());
			for(Function f : cl.getFuncts()){
				List<Type> input = new ArrayList<Type>();
				for(int d =0;d< f.getArgs().size();d++){
					input.add(f.getArgs().get(d).getType());
				}
				FunType ft = new FunType(new Tuple(input), f.getRetType());
				temp.add(FunCall.mangle_name(f.getName().getName(), ft));
			}
			//Normal Name
			List<String> tempN = new ArrayList<String>();
			tempN.addAll(parentsClassMethod.getMethod());
			tempN.addAll(cl.getFunctions());
			StringWrapper Methods = new StringWrapper(tempN,temp);
			map.put(cl.getName(), new Wrapper(Fields,Methods));
		}
	}

	
	
	public String toString(){

		String s_imports = "";
		String s_initial = "";
		String s_funcs = "";
		String s_classes = "";
		String s_decls = "";
		

		if (imports != null){
			for (int i = 0 ; i < imports.size(); i++){
				s_imports += imports.get(i).toString()+" ";
			}
		}
		if (initial != null){
			for (int i = 0 ; i < initial.size(); i++){
				s_initial += initial.get(i).toString()+" ";
			}
		}
		if (funcs != null){
			for (int i = 0 ; i < funcs.size(); i++){
				s_funcs += funcs.get(i).toString()+" ";
			}
		}
		if (classes != null){
			for (int i = 0 ; i < classes.size(); i++){
				s_funcs += classes.get(i).toString()+" ";
			}
		}		
		if (decls != null){
			for (int i = 0 ; i < decls.size(); i++){
				s_decls += decls.get(i).toString()+" ";
			}
		}
		return String.format("((%s) (%s) (%s) (%s) (%s))", s_imports,s_initial,s_funcs,s_classes,s_decls);
	}
}