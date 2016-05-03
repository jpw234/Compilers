package edu.cornell.cs.cs4120.xic.ir;

import java.util.ArrayList;
import java.util.Stack;
/* Represents a single node in the CFG
 * CFG nodes have use-sets and def-sets upon construction describing the temps they use and define
 * They also have in and out sets which are updated during the liveness analysis
 * They also have a unique number in the CFG (which helps during successor analysis to identify loops)
 * They have a set of child CFGNodes (with minimum size 0 and maximum size 2)
 * Finally they have a set of successor CFGNodes (calculated after graph is completed to assist with liveness analysis)
 */
public class CFGNode {
	private ArrayList<String> useSet;
	private ArrayList<String> defSet;
	private ArrayList<String> inSet = new ArrayList<String>();
	private ArrayList<String> outSet = new ArrayList<String>();
	private int uniqueNum;
	private ArrayList<CFGNode> children = new ArrayList<CFGNode>();
	
	public CFGNode(ArrayList<String> u, ArrayList<String> d, int k) {
		useSet = u;
		defSet = d;
		uniqueNum = k;
	}
	
	public ArrayList<String> getUseSet() {
		return useSet;
	}
	
	public ArrayList<String> getDefSet() {
		return defSet;
	}
	
	public ArrayList<String> getInSet() {
		return inSet;
	}
	
	public ArrayList<String> getOutSet() {
		return outSet;
	}
	
	public int getUniqueNum() {
		return uniqueNum;
	}
	
	public ArrayList<CFGNode> getChildren() {
		return children;
	}
	
	public void addChild(CFGNode c) {
		children.add(c);
	}
	
	public void setInSet(ArrayList<String> i) {
		inSet = i;
	}
	
	public void setOutSet(ArrayList<String> o) {
		outSet = o;
	}
	
	//helper function which checks current successor set for the given node #
	//returns true if the given # is in the successor set or is this node's own number
	private boolean hasChild(int k) {
		for(int a = 0; a < children.size(); a++) {
			if(children.get(a).getUniqueNum() == k) return true;
		}
		return false;
	}
}
