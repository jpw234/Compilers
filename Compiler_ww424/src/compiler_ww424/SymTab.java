package compiler_ww424;
import java.util.HashMap;

public class SymTab {
	private SymTab parent = null;
	private HashMap<String, Type> table = new HashMap<String, Type>();
	private Type retval = null;
	private String classContext = null;
	
	public SymTab(SymTab p) {
		parent = p;
	}
	
	public Type lookup(String id) {
		/*System.out.println("Looking for :" + id);
		for (HashMap.Entry<String, Type> entry : table.entrySet()) {
			String key = entry.getKey().toString();
			Type value = entry.getValue();
			System.out.println(key + ":" + value.toString());
		}*/
		if(table.get(id) != null) return table.get(id);
		else if(classContext != null && table.get(classContext + "." + id) != null) return table.get(classContext + "." + id);
		else if(parent != null) return parent.lookup(id);
		else throw new Error("Semantic Error: var does not exist");
	}
	public Type lookupFunction(String id) {
		/*System.out.println("Looking for :" + id);
		for (HashMap.Entry<String, Type> entry : table.entrySet()) {
			String key = entry.getKey().toString();
			Type value = entry.getValue();
			System.out.println(key + ":" + value.toString());
		}*/
		if(table.get(id) != null) return table.get(id);
		else if(table.get("*"+id) != null) return table.get("*"+id);
		else if(classContext != null && table.get(classContext + "." + id) != null) return table.get(classContext + "." + id);
		else if(classContext != null && table.get("*"+ classContext + "." + id) != null) return table.get("*" + classContext + "." + id);
		else if(parent != null) return parent.lookupFunction(id);
		else throw new Error("Semantic Error: var does not exist");
	}
	
	public void setRetval(Type t) {
		retval = t;
	}
	
	public Type getRetval() {
		if(retval != null) return retval;
		else if(parent != null) return parent.getRetval();
		else return null;
	}
	
	public void setClassContext(String s) {
		classContext = s;
	}
	
	public String getClassContext() {
		return classContext;
	}
	
	//invariant: class fields/functions are entered as class.field (i.e. method len() of class array() is array.len)
	public void add(String id, Type t) {
		table.put(id, t);
	}
}
