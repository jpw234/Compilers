package compiler_ww424;

import java.util.ArrayList;
import java.util.Stack;

public class Type {
	private int arrDepth = 0;
	private String type;

	public Type(String t) {
		type = t;
	}

	public Type(String t, int d) {
		type = t;
		arrDepth = d;
	}

	public void addDepth() {
		arrDepth++;
	}

	public String getType() {
		return type;
	}

	public int getDepth() {
		return arrDepth;
	}

	public boolean equals(Type t) {
		if(t.getType()=="tuple") {
			return ((Tuple) t).equals(this);
		}
		if((t.getType()=="empty" && this.getDepth()>=1)
			|| (this.getType()=="empty" && t.getDepth()>=1)) {
				return true;
		}
		if(t.getType()=="null" && type=="null") return true;
		if(t.getType()=="null" && !this.equals(new Type("int")) && !this.equals(new Type("bool"))) return true;
		if(type == "null" && !t.equals(new Type("int")) && !t.equals(new Type("bool"))) return true;
		return ((type==t.getType()) && (arrDepth==t.getDepth()));
	}
	public String toString(){
		String s = type ; 
		for (int i =0; i < arrDepth ; i++){
			s = "( [] "+ s +" )";
		}
		return s;
	}
}

