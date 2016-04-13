package edu.cornell.cs.cs4120.xic.ir;

import java.util.HashMap;

public class StackAssigner {
	private static HashMap<String, String> map = new HashMap<String, String>();
	private static int counter = 0;

	public static String getLocation(String name) {
		if(map.containsKey(name)) {
			return map.get(name);
		}
		else {
			counter -= 8;
			
			String loc = counter + "(%rbp)";
			map.put(name, loc);
			
			return loc;
		}
	}
	
	public static void clear() {
		map = new HashMap<String, String>();
		counter = 0;
	}
	
	public static void setCounter(int s) {
		counter = s;
	}
}
