package edu.cornell.cs.cs4120.xic.ir;

import java.util.ArrayList;

import edu.cornell.cs.cs4120.util.SExpPrinter;

/**
 * An intermediate representation for a 64-bit integer constant.
 * CONST(n)
 */
public class IRConst extends IRExpr {
    private long value;

    /**
     *
     * @param value value of this constant
     */
    public IRConst(long value) {
        this.value = value;
    }

    public long value() {
        return value;
    }

    @Override
    public String label() {
        return "CONST(" + value + ")";
    }

    @Override
    public boolean isConstant() {
        return true;
    }

    public IRESeq IRLower() {
        return new IRESeq(new IRSeq(new ArrayList<IRStmt>()), this);
    }

    @Override
    public void printSExp(SExpPrinter p) {
        p.startList();
        p.printAtom("CONST");
        p.printAtom(String.valueOf(value));
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
    		case 0:
    			bestTile = new AssemInstr("", "$" + String.valueOf(value), 0);
    		}
    	}
    	return bestTile;
    }
}
