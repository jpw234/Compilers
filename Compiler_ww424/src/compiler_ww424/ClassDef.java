package compiler_ww424;

import java.util.ArrayList;
import java.util.List;

public class ClassDef {
	private String name;
	private String extendClass;
	private ArrayList<Object> initial;
	private ArrayList<Object> decls;
	private ArrayList<Function> funcs;
	private ArrayList<String> functionNames;
	private ArrayList<String> fieldNames;
	
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
	
	public void constantFold() {
		for(int a = 0; a < funcs.size(); a++) {
			funcs.get(a).constantFold();
		}
	}
}
