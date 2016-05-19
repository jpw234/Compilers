package compiler_ww424;

import java.util.ArrayList;
import java.util.List;

public class ClassDef {
	private String name;
	private String extendClass;
	private ArrayList<Object> initial = new ArrayList<Object>();
	private ArrayList<Object> decls = new ArrayList<Object>();
	private ArrayList<Function> funcs = new ArrayList<Function>();
	private ArrayList<String> functionNames = new ArrayList<String>();
	private ArrayList<String> fieldNames = new ArrayList<String>();
	
	private int line;
	private int column;
	private int bline;
	private int bcolumn;
	
	public ClassDef(String n, ArrayList<Object> b, int lineNum, int colNum, int bodLine, int bodCol) {
		name = n;
		extendClass = null;
		line = lineNum;
		column = colNum;
		bline = bodLine;
		bcolumn = bodCol;
		initial = b;
		
		for(Object k : initial) {
			if(k instanceof Function) {
				funcs.add((Function) k);
				functionNames.add(((Function) k).getName().getName());
			}
			else if(k instanceof Decl) {
				decls.add(k);
				fieldNames.add(((Decl) k).getName().getName());
			}
			else if(k instanceof DeclAssign) {
				decls.add(k);
				
				List<Decl> temp = ((DeclAssign) k).getLeft();
				for(Decl z : temp) {
					fieldNames.add(z.getName().getName());
				}
			}
			else if(k instanceof MultiDecl) {
				decls.add(k);
				
				List<Decl> temp = ((MultiDecl) k).getDeclList();
				for(Decl z : temp) {
					fieldNames.add(z.getName().getName());
				}
			}
			else throw new Error("unsupported type in ClassDef");
		}
	}
	
	public ClassDef(String n, String e, ArrayList<Object> b, int lineNum, int colNum, int bodLine, int bodCol) {
		name = n;
		extendClass = e;
		initial = b;
		line = lineNum;
		column = colNum;
		bline = bodLine;
		bcolumn = bodCol;
		
		for(Object k : initial) {
			if(k instanceof Function) {
				funcs.add((Function) k);
				functionNames.add(((Function) k).getName().getName());
			}
			else if(k instanceof Decl) {
				decls.add(k);
				fieldNames.add(((Decl) k).getName().getName());
			}
			else if(k instanceof DeclAssign) {
				decls.add(k);
				
				List<Decl> temp = ((DeclAssign) k).getLeft();
				for(Decl z : temp) {
					fieldNames.add(z.getName().getName());
				}
			}
			else if(k instanceof MultiDecl) {
				decls.add(k);
				
				List<Decl> temp = ((MultiDecl) k).getDeclList();
				for(Decl z : temp) {
					fieldNames.add(z.getName().getName());
				}
			}
			else throw new Error("unsupported type in ClassDef");
		}
	}
	
	public List<String> getFunctions() {
		return functionNames;
	}
	public List<Function> getFuncts() {
		return funcs;
	}
	
	public List<String> getFields() {
		return fieldNames;
	}
	public String getExtend(){
		return extendClass;
	}
	public String getName(){
		return name;
	}
	public void firstpass(SymTab s) {
		s.setClassContext(name);
		
		for(Object k : decls) {
			if(k instanceof Decl) {
				((Decl) k).typecheck(s);
			}
			else if(k instanceof DeclAssign) {
				((DeclAssign) k).typecheck(s);
			}
			else if(k instanceof MultiDecl) {
				((MultiDecl) k).typecheck(s);
			}
		}
		
		for(Function z : funcs) {
			z.firstPass(s);
		}
		
		s.setClassContext(null);
	}
	
	public Type typecheck(SymTab s) {
		s.setClassContext(name);
		
		for(int a = 0; a < funcs.size(); a++) {
			funcs.get(a).typecheck(s);
		}
		
		s.setClassContext(null);
		
		return null;
	}
	
	
	public String toString(){
	
		String s = "";
		String s_initial = "";
		String s_decls = "";
		String s_funcs = "";
		// Initials
		if (initial != null){
			for(int i = 0; i < initial.size(); i++){
				s_initial = s_initial + " " + initial.get(i).toString();
			}
		}
		s_initial = "( " + s_initial + " )";
		//Decls
		if (decls != null){
			for(int i = 0; i < decls.size(); i++){
				s_decls = s_decls + " " + decls.get(i).toString();
			}
		}
		s_decls = "( " + s_decls + " )";
		//Funcs
		if (funcs != null){
			for(int i = 0; i < funcs.size(); i++){
				s_funcs = s_funcs + " " + funcs.get(i).toString();
			}
		}
		s_funcs = "( " + s_funcs + " )";
		
		//add retType (from Tuple class) with "(" & ")" manually, because Tuple.toString() don't print parenthesis
		s = "(" + name.toString() + " (" + extendClass+") " + s_initial + s_decls + s_funcs +  " )";
		return s ;
	}
	
	
	public void constantFold() {
		for(int a = 0; a < funcs.size(); a++) {
			funcs.get(a).constantFold();
		}
	}
}
