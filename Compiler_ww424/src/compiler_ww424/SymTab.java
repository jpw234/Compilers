package compiler_ww424;
import java.util.HashMap;

public class SymTab {
	private SymTab parent = null;
	private HashMap<String, Type> table;
	private Type retval = null;
	
	public SymTab(SymTab p) {
		parent = p;
	}
	
	public Type lookup(String id) {
		if(table.get(id) != null) return table.get(id);
		else if(parent != null) return parent.lookup(id);
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
	
	public void add(String id, Type t) {
		table.put(id, t);
	}
}