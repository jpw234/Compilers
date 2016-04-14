package edu.cornell.cs.cs4120.xic.ir;

import edu.cornell.cs.cs4120.util.SExpPrinter;
import java.util.ArrayList;
import edu.cornell.cs.cs4120.xic.ir.visit.AggregateVisitor;
import edu.cornell.cs.cs4120.xic.ir.visit.IRVisitor;

/**
 * An intermediate representation for a move statement
 * MOVE(target, expr)
 */
public class IRMove extends IRStmt {
    private IRExpr target;	
    private IRExpr expr;

    /**
     *
     * @param target the destination of this move
     * @param expr the expression whose value is to be moved
     */
    public IRMove(IRExpr target, IRExpr expr) {
        this.target = target;
        this.expr = expr;
    }

    public IRExpr target() {
        return target;
    }

    public IRExpr expr() {
        return expr;
    }

    @Override
    public String label() {
        return "MOVE";
    }

    @Override
    public IRNode visitChildren(IRVisitor v) {
        IRExpr target = (IRExpr) v.visit(this, this.target);
        IRExpr expr = (IRExpr) v.visit(this, this.expr);

        if (target != this.target || expr != this.expr)
            return new IRMove(target, expr);

        return this;
    }

    @Override
    public <T> T aggregateChildren(AggregateVisitor<T> v) {
        T result = v.unit();
        result = v.bind(result, v.visit(target));
        result = v.bind(result, v.visit(expr));
        return result;
    }
    
    public IRSeq IRLower() {
    	IRESeq targetLowered = target.IRLower();
    	
    	ArrayList<IRStmt> stmts = new ArrayList<IRStmt>();
    	stmts.add(targetLowered.stmt());
    	
    	
    	
    	if(targetLowered.expr() instanceof IRTemp) {
    		IRESeq exprLowered = expr.IRLower();
    		stmts.add(exprLowered.stmt());
    		stmts.add(new IRMove(targetLowered.expr(), exprLowered.expr()));
    		
    		return new IRSeq(stmts);
    	}
    	
    	else if(targetLowered.expr() instanceof IRMem) {
    		stmts.add(new IRMove(new IRTemp("_MOVENAIVE"), ((IRMem) targetLowered.expr()).expr() ));
    		
    		IRESeq exprLowered = expr.IRLower();
    		stmts.add(exprLowered.stmt());
    		stmts.add(new IRMove(new IRMem(new IRTemp("_MOVENAIVE")), exprLowered.expr()));
    		
    		return new IRSeq(stmts);
    	}
    	
    	else {
    		throw new Error("left side of MOVE is not MEM or TEMP");
    	}		
    }
    
    @Override
    public void printSExp(SExpPrinter p) {
        p.startList();
        p.printAtom("MOVE");
        target.printSExp(p);
        expr.printSExp(p);
        p.endList();
    }
    
    public int bestCost() {
    	if(bestTile != null) return bestTile.getCost();
    	else {
    		bestTileNum = 0;
    		bestCost = target.bestCost() + expr.bestCost() + 1;
    	}
    	return bestCost;
    }
    
    public AssemInstr getBestTile() {
    	if(bestTile != null) return bestTile;
    	else {
    		this.bestCost();
    		switch(bestTileNum) {
    		case 0: {//mintile
    			AssemInstr targetAssem = target.getBestTile();
        		AssemInstr exprAssem = expr.getBestTile();
        		String data = exprAssem.getData() + "\n" + targetAssem.getData() + "\n" +
        					  "movq " + exprAssem.getSource() + ", %r10 \n" + 
        					  "movq %r10, " + targetAssem.getSource();
        		bestTile = new AssemInstr(data, "", targetAssem.getCost() + exprAssem.getCost() + 1);
    		}
    		}
    	}
    	return bestTile;
    }
}
