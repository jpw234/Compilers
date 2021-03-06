package edu.cornell.cs.cs4120.xic.ir;

import edu.cornell.cs.cs4120.util.SExpPrinter;
import java.util.ArrayList;
import edu.cornell.cs.cs4120.xic.ir.visit.InsnMapsBuilder;

/**
 * An intermediate representation for naming a memory address
 */
public class IRLabel extends IRStmt {
    private String name;

    /**
     *
     * @param name name of this memory address
     */
    public IRLabel(String name) {
        this.name = name;
    }

    public String name() {
        return name;
    }

    @Override
    public String label() {
        return "LABEL(" + name + ")";
    }

    @Override
    public InsnMapsBuilder buildInsnMapsEnter(InsnMapsBuilder v) {
        v.addNameToCurrentIndex(name);
        return v;
    }
    
    public IRSeq IRLower() {
    	ArrayList<IRStmt> k = new ArrayList<IRStmt>();
    	k.add(this);
    	return new IRSeq(k);
    }

    @Override
    public void printSExp(SExpPrinter p) {
        p.startList();
        p.printAtom("LABEL");
        p.printAtom(name);
        p.endList();
    }
    
    public int bestCost() {
    	if(bestTile != null) return bestTile.getCost();
    	else {
    		bestTileNum = 0;
    		bestCost = 0;
    	}
    	return bestCost;
    }
    
    public AssemInstr getBestTile() {
    	if(bestTile != null) return bestTile;
    	else {
    		this.bestCost();
    		switch(bestTileNum) {
    		case 0: {//mintile
    			bestTile = new AssemInstr("\n"+name+":", "", 0);
    		}
    		}
    	}
    	return bestTile;
    }
}
