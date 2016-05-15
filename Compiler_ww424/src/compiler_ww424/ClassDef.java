package compiler_ww424;

import java.util.ArrayList;

public class ClassDef {
	private String name;
	private String extendClass;
	private ArrayList<Object> body;
	
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
	}
	
	public ClassDef(String n, String e, ArrayList<Object> b, int lineNum, int colNum, int bodLine, int bodCol) {
		name = n;
		extendClass = e;
		body = b;
		line = lineNum;
		column = colNum;
		bline = bodLine;
		bcolumn = bodCol;
	}
}
