package compiler_ww424;
public class LabelMaker {
	
	private static long number=0;
	public static String Generate_Unique_Label(String name) {
		
		String retString = new String(name+number);
		number += 1;
		return retString;
	}
}