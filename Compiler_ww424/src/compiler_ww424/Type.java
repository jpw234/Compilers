package compiler_ww424;

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
	
	public String getType() {
		return type;
	}
	
	public int getDepth() {
		return arrDepth;
	}
	
	public boolean equals(Type t) {
		return ((type==t.getType()) && (arrDepth==t.getDepth()));
	}
}

