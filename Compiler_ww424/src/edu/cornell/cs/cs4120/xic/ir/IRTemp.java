package edu.cornell.cs.cs4120.xic.ir;

import java.util.ArrayList;

import edu.cornell.cs.cs4120.util.SExpPrinter;

/**
 * An intermediate representation for a temporary register
 * TEMP(name)
 */
public class IRTemp extends IRExpr {
    private String name;

    /**
     *
     * @param name name of this temporary register
     */
    public IRTemp(String name) {
        this.name = name;
    }

    public String name() {
        return name;
    }

    @Override
    public String label() {
        return "TEMP(" + name + ")";
    }
    
    public IRESeq IRLower() {
    	return new IRESeq(new IRSeq(new ArrayList<IRStmt>()), this);
    }

    @Override
    public void printSExp(SExpPrinter p) {
        p.startList();
        p.printAtom("TEMP");
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
    		case 0: {
    			if(name.substring(0,4) == "_ARG") {
    				int num = Integer.parseInt(name.substring(4));
    				switch(num) {
    				case 0: bestTile = new AssemInstr("", "%rdi", 0); break;
    				case 1: bestTile = new AssemInstr("", "%rsi", 0); break;
    				case 2: bestTile = new AssemInstr("", "%rdx", 0); break;
    				case 3: bestTile = new AssemInstr("", "%rcx", 0); break;
    				case 4: bestTile = new AssemInstr("", "%r8", 0); break;
    				case 5: bestTile = new AssemInstr("", "%r9", 0); break;
    				default: bestTile = new AssemInstr("", ((num-5)*8) + "(%rbp)", 0);
    				}
    			}
    			else if(name.substring(0,4) == "_RET") {
    				int num = Integer.parseInt(name.substring(4));
    				switch(num) {
    				case 0: bestTile = new AssemInstr("", "%rax", 0); break;
    				case 1: bestTile = new AssemInstr("", "%rdx", 0); break;
    				//TODO: THIS IS WRONG
    				default: bestTile = new AssemInstr("", "%rax", 0);
    				}
    			}
    			else if(name == "_CALLRET") bestTile = new AssemInstr("", "%rax", 0);
    			else {
    				bestTile = new AssemInstr("\nsubq $8, %rsp", StackAssigner.getLocation(name), 0);
    			}
    		}
    		}
    	}
    	//_ARG0-5, registers, 6+, ((n-5)*8)(%rbp)
    	//_RET0 = %rax, _RET1 = %rdx, _RET2 = 0(%rdi), _RET3 = 8(%rdi), ...
    	//_CALLRET = _RET0 = %rax
    	return bestTile;
    }
}
