package edu.cornell.cs.cs4120.xic.ir;

public class AssemInstr {
	private String data = "";
	private String source = "";
	private int cost = 0;
	
	public AssemInstr(String dt, String src, int c) {
		data = dt;
		source = src;
		cost = c;
	}
	
	public String getData() {
		return data;
	}
	
	public String getSource() {
		return source;
	}
	
	public int getCost() {
		return cost;
	}
	
	public void setData(String dt) {
		data = dt;
	}
	
	public void setSource(String src) {
		source = src;
	}
	
	public void setCost(int c) {
		cost = c;
	}
}
