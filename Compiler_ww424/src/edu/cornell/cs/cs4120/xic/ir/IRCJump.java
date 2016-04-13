package edu.cornell.cs.cs4120.xic.ir;

import java.util.ArrayList;

import edu.cornell.cs.cs4120.util.SExpPrinter;
import edu.cornell.cs.cs4120.xic.ir.visit.AggregateVisitor;
import edu.cornell.cs.cs4120.xic.ir.visit.CheckCanonicalIRVisitor;
import edu.cornell.cs.cs4120.xic.ir.visit.IRVisitor;

/**
 * An intermediate representation for a conditional transfer of control
 * CJUMP(expr, trueLabel, falseLabel)
 */
public class IRCJump extends IRStmt {
    private IRExpr expr;
    private String trueLabel, falseLabel;
    
    /**
     * Construct a CJUMP instruction with fall-through on false.
     * @param expr the condition for the jump
     * @param trueLabel the destination of the jump if {@code expr} evaluates
     *          to true
     */
    public IRCJump(IRExpr expr, String trueLabel) {
        this(expr, trueLabel, null);
    }
    
    /**
     *
     * @param expr the condition for the jump
     * @param trueLabel the destination of the jump if {@code expr} evaluates
     *          to true
     * @param falseLabel the destination of the jump if {@code expr} evaluates
     *          to false
     */
    public IRCJump(IRExpr expr, String trueLabel, String falseLabel) {
        this.expr = expr;
        this.trueLabel = trueLabel;
        this.falseLabel = falseLabel;
    }
    
    public IRExpr expr() {
        return expr;
    }
    
    public String trueLabel() {
        return trueLabel;
    }
    
    public String falseLabel() {
        return falseLabel;
    }
    
    public boolean hasFalseLabel() {
        return falseLabel != null;
    }
    
    @Override
    public String label() {
        return "CJUMP";
    }
    
    @Override
    public IRNode visitChildren(IRVisitor v) {
        IRExpr expr = (IRExpr) v.visit(this, this.expr);
        
        if (expr != this.expr) return new IRCJump(expr, trueLabel, falseLabel);
        
        return this;
    }
    
    @Override
    public <T> T aggregateChildren(AggregateVisitor<T> v) {
        T result = v.unit();
        result = v.bind(result, v.visit(expr));
        return result;
    }
    
    @Override
    public boolean isCanonical(CheckCanonicalIRVisitor v) {
        return !hasFalseLabel();
    }
    
    public IRSeq IRLower() {
        ArrayList<IRStmt> ret = new ArrayList<IRStmt>();
        
        IRESeq k = expr.IRLower();
        
        ret.add(k.stmt());
        ret.add(new IRCJump(k.expr(), trueLabel, falseLabel));
        
        return new IRSeq(ret);
    }
    
    
    @Override
    public void printSExp(SExpPrinter p) {
        p.startList();
        p.printAtom("CJUMP");
        expr.printSExp(p);
        p.printAtom(trueLabel);
        if (hasFalseLabel()) p.printAtom(falseLabel);
        p.endList();
    }
    
    public int bestCost() {
    	if(bestTile != null) return bestTile.getCost();
    	else {//min tiling
    		bestTileNum = 0;
    		bestCost = 2 + expr.bestCost();
    	}
    	if(expr instanceof IRBinOp) {//say here what the tile is
    		//calc cost
			IRBinOp e = (IRBinOp) expr;
			IRBinOp.OpType type = e.opType();
    		if(type == IRBinOp.OpType.EQ) {
    			if (bestCost > 2 ){
    				bestCost = 2;
    				bestTileNum = 1;
    			}
    		}
    		if(type == IRBinOp.OpType.NEQ){
    			if (bestCost > 2 ){
    				bestCost = 2;
    				bestTileNum = 2;
    			}
    		}
    		if (type == IRBinOp.OpType.LT){
    			if (bestCost > 2 ){
    				bestCost = 2;
    				bestTileNum = 3;
    			}
    		}
    		if (type == IRBinOp.OpType.GT){
    			if (bestCost > 2 ){
    				bestCost = 2;
    				bestTileNum = 4;
    			}
    		}
    		if (type == IRBinOp.OpType.LEQ){
    			if (bestCost > 2 ){
    				bestCost = 2;
    				bestTileNum = 5;
    			}
    		}
    		if (type == IRBinOp.OpType.GEQ){
    			if (bestCost > 2 ){
    				bestCost = 2;
    				bestTileNum = 6;
    			}
    		}
    	}
    	return bestCost;
    }
    
    public AssemInstr getBestTile() {
    	if(bestTile != null) return bestTile;
    	else {
    		this.bestCost();
    		switch(bestTileNum) {
    		case 0: {//mintile
    			AssemInstr child = expr.getBestTile();
        		String data = child.getData() + "\ntestq " + child.getSource() + "\n";
        		data += "jnz " + trueLabel;
        		bestTile = new AssemInstr(data, "", child.getCost() + 2);
    		}
    		case 1: {//IRCJUMP(IRBinOp(EQ, IRNode(x), IRNode(y)), truelabel)
    			IRExpr e1 = ((IRBinOp)expr).left();
    			IRExpr e2 = ((IRBinOp)expr).right();
				String data =   "movq " + e1.getBestTile().getSource() + ", %10 \n"
 					   		+   "movq " + e2.getBestTile().getSource() + ", %11 \n"
 					   		+ "cmpq	%11, %10"
 					   		+ "je   " + trueLabel;
				bestTile = new AssemInstr(data, "", 2);	
    		}
    		case 2: {//IRCJUMP(IRBinOp(NEQ, IRNode(x), IRNode(y)), truelabel)
    			IRExpr e1 = ((IRBinOp)expr).left();
    			IRExpr e2 = ((IRBinOp)expr).right();
				String data =   "movq " + e1.getBestTile().getSource() + ", %10 \n"
 					   		+   "movq " + e2.getBestTile().getSource() + ", %11 \n"
 					   		+ "cmpq	%11, %10"
 					   		+ "jne  " + trueLabel;
				bestTile = new AssemInstr(data, "", 2);	
    		}
    		case 3: {//IRCJUMP(IRBinOp(LT, IRNode(x), IRNode(y)), truelabel)
    			IRExpr e1 = ((IRBinOp)expr).left();
    			IRExpr e2 = ((IRBinOp)expr).right();
				String data =   "movq " + e1.getBestTile().getSource() + ", %10 \n"
 					   		+   "movq " + e2.getBestTile().getSource() + ", %11 \n"
 					   		+ "cmpq	%11, %10"
 					   		+ "jl   " + trueLabel;
				bestTile = new AssemInstr(data, "", 2);	
    		}
    		case 4: {//IRCJUMP(IRBinOp(GT, IRNode(x), IRNode(y)), truelabel)
    			IRExpr e1 = ((IRBinOp)expr).left();
    			IRExpr e2 = ((IRBinOp)expr).right();
				String data =   "movq " + e1.getBestTile().getSource() + ", %10 \n"
 					   		+   "movq " + e2.getBestTile().getSource() + ", %11 \n"
 					   		+ "cmpq	%11, %10"
 					   		+ "jg   " + trueLabel;
				bestTile = new AssemInstr(data, "", 2);	
    		}
    		case 5: {// IRCJUMP(IRBinOp(LEQ, IRNode(x), IRNode(y)), truelabel)
    			IRExpr e1 = ((IRBinOp)expr).left();
    			IRExpr e2 = ((IRBinOp)expr).right();
				String data =   "movq " + e1.getBestTile().getSource() + ", %10 \n"
 					   		+   "movq " + e2.getBestTile().getSource() + ", %11 \n"
 					   		+ "cmpq	%11, %10"
 					   		+ "jle   " + trueLabel;
				bestTile = new AssemInstr(data, "", 2);	
    		}
    		case 6: {//IRCJUMP(IRBinOp(GEQ, IRNode(x), IRNode(y)), truelabel)
    			IRExpr e1 = ((IRBinOp)expr).left();
    			IRExpr e2 = ((IRBinOp)expr).right();
				String data =   "movq " + e1.getBestTile().getSource() + ", %10 \n"
 					   		+   "movq " + e2.getBestTile().getSource() + ", %11 \n"
 					   		+ "cmpq	%11, %10"
 					   		+ "jge   " + trueLabel;
				bestTile = new AssemInstr(data, "", 2);	
    		}
    		}
    	}
    	return bestTile;
    }
}
