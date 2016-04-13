package edu.cornell.cs.cs4120.xic.ir;

import java.util.ArrayList;

import edu.cornell.cs.cs4120.util.SExpPrinter;

/** RETURN statement */
public class IRReturn extends IRStmt {

    @Override
    public String label() {
        return "RETURN";
    }
    
    public IRSeq IRLower() {
    	ArrayList<IRStmt> k = new ArrayList<IRStmt>();
    	k.add(this);
    	return new IRSeq(k);
    }

    @Override
    public void printSExp(SExpPrinter p) {
        p.startList();
        p.printAtom("RETURN");
        p.endList();
    }
    
    public int bestCost() {
    	if(bestTile != null) return bestTile.getCost();
    	else {
    		bestTile = new AssemInstr("ret", "", 1);
    		return 1;
    	}
    }
}
