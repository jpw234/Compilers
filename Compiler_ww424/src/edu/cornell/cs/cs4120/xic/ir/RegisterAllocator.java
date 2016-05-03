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
	
	private static int nextNumber() {
		int ret = globalNodeNumber;
		globalNodeNumber++;
		return ret;
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
		CFGNode prev = null;
		boolean addToPrev = false;
		
		List<IRStmt> stmts = flow.stmts();
		
		for(int a = 0; a < stmts.size(); a++) {
			//stmt in consideration
			IRStmt stmt = stmts.get(a);
			//node under construction
			CFGNode construct;
			ArrayList<String> uses;
			ArrayList<String> defs;
			boolean cacheAddToPrev = addToPrev;
			//stmts can be MOVE(dest,e), MOVE(TEMP(t), CALL(...)), EXP(CALL(...)), JUMP(e), CJUMP(e, l1, null), LABEL(l), or RETURN
			if(stmt instanceof IRMove) {
				uses = ((IRMove) stmt).getTemps();
				defs = new ArrayList<String>();
				
				//if the dest is a temp, it's defined by this CFG node
				if(((IRMove) stmt).target() instanceof IRTemp) {
					defs.add(((IRTemp) ((IRMove) stmt).target()).name());
				}
				//otherwise the dest is a mem(e) where e might use some temps, so add those potential temps to use
				else {
					ArrayList<String> targetUses = ((IRMove) stmt).target().getTemps();
					uses.removeAll(targetUses);
					uses.addAll(targetUses);
				}
				
				construct = new CFGNode(uses, defs, nextNumber());
				addToPrev = true;
			}
			else if(stmt instanceof IRExp) {
				uses = ((IRExp) stmt).getTemps();
				defs = new ArrayList<String>();
				construct = new CFGNode(uses, defs, nextNumber());
				addToPrev = true;
			}
			else if(stmt instanceof IRJump) {
				uses = new ArrayList<String>();
				defs = new ArrayList<String>();
				construct = new CFGNode(uses, defs, nextNumber());
				
				//jumps must target NAMEs
				String targetName = ((IRName) ((IRJump) stmt).target()).name();
				if(labelStore.containsKey(targetName)) {
					construct.addChild(labelStore.get(targetName));
				}
				else {
					CFGNode target = new CFGNode(new ArrayList<String>(), new ArrayList<String>(), nextNumber());
					labelStore.put(targetName, target);
					construct.addChild(target);
				}
				addToPrev = false;
			}
			else if(stmt instanceof IRCJump) {
				uses = ((IRCJump) stmt).getTemps();
				defs = new ArrayList<String>();
				construct = new CFGNode(uses, defs, nextNumber());
				
				//IRCJump has a LABEL in trueLabel and null in falseLabel in lowered IR tree
				String targetName = ((IRCJump) stmt).trueLabel();
				if(labelStore.containsKey(targetName)) {
					construct.addChild(labelStore.get(targetName));
				}
				else {
					CFGNode target = new CFGNode(new ArrayList<String>(), new ArrayList<String>(), nextNumber());
					labelStore.put(targetName, target);
					construct.addChild(target);
				}
				addToPrev = true;
			}
			else if(stmt instanceof IRLabel) {
				String targetName = ((IRLabel) stmt).name();
				if(labelStore.containsKey(targetName)) {
					construct = labelStore.get(targetName);
				}
				else {
					construct = new CFGNode(new ArrayList<String>(), new ArrayList<String>(), nextNumber());
					labelStore.put(targetName, construct);
				}
				addToPrev = true;
			}
			else if(stmt instanceof IRReturn) {
				construct = new CFGNode(new ArrayList<String>(), new ArrayList<String>(), nextNumber());
				//returns do not have children
				addToPrev = false;
			}
			else {
				throw new Error("illegal type of IRStmt in lowered IR tree");
			}
			
			//now construct holds the constructed CFGNode
			if(cacheAddToPrev) {
				prev.addChild(construct);
			}
			nodeSet.add(construct);
			prev = construct;
			if(a == 0) head = construct;
		}
		
		return head;
	}
}
