package compiler_ww424;
import java.util.ArrayList;
import java.util.List;

public class Tuple extends Type {
	private List<Type> args;
	
	public Tuple(List<Type> l) {
		super("tuple");
		args = l;
	}
	
	public void add(Type t) {
		args.add(t);
	}
	
	public List<Type> getArgs() {
		if (args == null) return new ArrayList<Type>();
		return args;
	}
	
	@Override
	public boolean equals(Type t) {
		if(args.size() == 1 && t.getType() == args.get(0).getType()) return true;
		else if(t.getType() != "tuple") return false;
		
		if(args.size() != ((Tuple) t).getArgs().size()) return false;
		
		for(int a = 0; a < args.size(); a++) {
			if(!args.get(a).equals(((Tuple) t).getArgs().get(a))) return false;
		}
		
		return true;
	}
	@Override 
	public String toString(){
		if (args == null){
			return "";
		}
		String s = "";
		for (int i =0 ; i <args.size();i++){
			s += (args.get(i).toString() +" ");
		}
		return s.trim();
		
	}
}
