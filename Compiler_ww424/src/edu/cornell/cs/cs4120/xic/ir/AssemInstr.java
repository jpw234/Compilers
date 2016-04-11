package edu.cornell.cs.cs4120.xic.ir;

public class AssemInstr {
	private String data = "";
	private String source = "";
	
	public AssemInstr(String dt, String src) {
		data = dt;
		source = src;
	}
	
	public String getData() {
		return data;
	}
	
	public String getSource() {
		return source;
	}
	
	public void setData(String dt) {
		data = dt;
	}
	
	public void setSource(String src) {
		source = src;
	}
}
