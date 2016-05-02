package edu.cornell.cs.cs4120.xic.ir;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

public class RegisterAllocator {
	
	private static ArrayList<CFGNode> nodeSet;
	private static HashMap<String, CFGNode> labelStore;
	private static int globalNodeNumber;
	
	private static void reset() {
		nodeSet = new ArrayList<CFGNode>();
		labelStore = new HashMap<String, CFGNode>();
		globalNodeNumber = 1;
	}
	
	//Inputs: the IRSeq the allocator is analyzing
	//Assumes StackAssigner was cleared (or is in the desired other state) before call
	//Invariant: IRSeq flow is non-empty
	public static void generateAssignment(IRSeq flow) {
		reset();
		
		CFGNode head = generateCFG(flow);
	}
	
	//takes an IRSeq and returns the CFGNode representing the head of the CFG
	//also sets global variable nodeSet to the set of all CFG nodes
	private static CFGNode generateCFG(IRSeq flow) {
		CFGNode head = null;
		CFGNode prev;
		
		List<IRStmt> stmts = flow.stmts();
		
		for(int a = 0; a < stmts.size(); a++) {
			//stmt in consideration
			IRStmt stmt = stmts.get(a);
			//node under construction
			CFGNode construct;
			ArrayList<String> uses;
			ArrayList<String> defs;
			//stmts can be MOVE(dest,e), MOVE(TEMP(t), CALL(...)), EXP(CALL(...)), JUMP(e), CJUMP(e, l1, null), LABEL(l), or RETURN
			if(stmt instanceof IRMove) {
				uses = ((IRMove) stmt).getTemps();
				defs = new ArrayList<String>();
				if(((IRMove) stmt).target() instanceof IRTemp) {
					defs.add(((IRTemp) ((IRMove) stmt).target()).name());
				}
				construct = new CFGNode(uses, defs, globalNodeNumber);
				globalNodeNumber++;
			}
		}
		
	}
}
