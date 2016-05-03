package edu.cornell.cs.cs4120.xic.ir;
import java.util.HashSet;
/**
 * An intermediate representation for statements
 */
public abstract class IRStmt extends IRNode {
	private CFGNodeAE nodeAE = null;
	abstract IRSeq IRLower();
	
	public CFGNodeAE nodeAE(){
		return nodeAE;
	}
	
	public void nodeAE_init(HashSet<String> allexprs){
		nodeAE = new CFGNodeAE(allexprs);
	}
}
