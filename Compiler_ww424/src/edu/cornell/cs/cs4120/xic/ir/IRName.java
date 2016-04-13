package edu.cornell.cs.cs4120.xic.ir;

import java.util.ArrayList;

import edu.cornell.cs.cs4120.util.SExpPrinter;

/**
 * An intermediate representation for named memory address
 * NAME(n)
 */
public class IRName extends IRExpr {
    private String name;
    
    /**
     *
     * @param name name of this memory address
     */
    public IRName(String name) {
        this.name = name;
    }
    
    public String name() {
        return name;
    }
    
    @Override
    public String label() {
        return "NAME(" + name + ")";
    }
    
    public IRESeq IRLower() {
        return new IRESeq(new IRSeq(new ArrayList<IRStmt>()), this);
    }
    
    @Override
    public void printSExp(SExpPrinter p) {
        p.startList();
        p.printAtom("NAME");
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
    			bestTile = new AssemInstr(name, "", 0);
    		}
    		}
    	}
    	return bestTile;
    }
}
