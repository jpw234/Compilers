package compiler_ww424;

import java.util.ArrayList;

public class Use {
	private String name;
	
	private int line,column;
	public Use(String n,int lineNum,int colNum) {
		name = n;
		line = lineNum;
		column = colNum;
	}
	
	public String getString() {
		return name;
	}
	public int getLine() {
		return line;
	}
	public int getColumn()
	{
		return column;
	}
	
	
	public String toString(){
		
		return String.format("(%s %s)", "use", name);
	}
}
