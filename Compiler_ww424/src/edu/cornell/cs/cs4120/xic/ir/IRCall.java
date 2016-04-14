package edu.cornell.cs.cs4120.xic.ir;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import compiler_ww424.LabelMaker;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import edu.cornell.cs.cs4120.xic.ir.visit.AggregateVisitor;
import edu.cornell.cs.cs4120.xic.ir.visit.CheckCanonicalIRVisitor;
import edu.cornell.cs.cs4120.xic.ir.visit.IRVisitor;

/**
 * An intermediate representation for a function call
 * CALL(e_target, e_1, ..., e_n)
 */
public class IRCall extends IRExpr {
    private IRExpr target;
    private List<IRExpr> args;

    /**
     *
     * @param target address of the code for this function call
     * @param args arguments of this function call
     */
    public IRCall(IRExpr target, IRExpr... args) {
        this(target, Arrays.asList(args));
    }

    /**
     *
     * @param target address of the code for this function call
     * @param args arguments of this function call
     */
    public IRCall(IRExpr target, List<IRExpr> args) {
        this.target = target;
        this.args = args;
    }

    public IRExpr target() {
        return target;
    }

    public List<IRExpr> args() {
        return args;
    }

    @Override
    public String label() {
        return "CALL";
    }

    @Override
    public IRNode visitChildren(IRVisitor v) {
        boolean modified = false;

        IRExpr target = (IRExpr) v.visit(this, this.target);
        if (target != this.target) modified = true;

        List<IRExpr> results = new ArrayList<>(args.size());
        for (IRExpr arg : args) {
            IRExpr newExpr = (IRExpr) v.visit(this, arg);
            if (newExpr != arg) modified = true;
            results.add(newExpr);
        }

        if (modified) return new IRCall(target, results);

        return this;
    }

    @Override
    public <T> T aggregateChildren(AggregateVisitor<T> v) {
        T result = v.unit();
        result = v.bind(result, v.visit(target));
        for (IRExpr arg : args)
            result = v.bind(result, v.visit(arg));
        return result;
    }

    @Override
    public boolean isCanonical(CheckCanonicalIRVisitor v) {
        return !v.inExpr();
    }
    
    public IRESeq IRLower() {
    	ArrayList<IRStmt> stmts = new ArrayList<IRStmt>();
    	ArrayList<IRExpr> temps = new ArrayList<IRExpr>();
    	
    	IRESeq t = target.IRLower();
    	stmts.add(t.stmt()); 
    	
    	for(int a = args.size() - 1; a >= 0; a--) {
    		String lab = "_PASSARG" + a;
    		
    		IRESeq k = args.get(a).IRLower();
    		stmts.add(k.stmt());
    		stmts.add(new IRMove(new IRTemp(lab), k.expr()));
    		temps.add(new IRTemp(lab));
    	}
    	
    	//IRTemp ret = new IRTemp("_CALLRET");
    	
    	stmts.add(new IRMove(new IRTemp("_CALLRET"), new IRCall(t.expr(), temps)));
    	
    	return new IRESeq(new IRSeq(stmts), new IRTemp("_CALLRET"));
    }

    @Override
    public void printSExp(SExpPrinter p) {
        p.startList();
        p.printAtom("CALL");
        target.printSExp(p);
        for (IRExpr arg : args)
            arg.printSExp(p);
        p.endList();
    }
    
    public int bestCost() {
    	if(bestTile != null) return bestTile.getCost();
    	else {
    		bestTileNum = 0;
    		bestCost = 1;
    	}
    	return bestCost;
    }
    
    public AssemInstr getBestTile() {
    	if(bestTile != null) return bestTile;
    	else {
    		this.bestCost();
    		AssemInstr targetAssem = target.getBestTile();
    		switch(bestTileNum) {
    		case 0: {//mintile
    			bestTile = new AssemInstr(targetAssem.getData() + "\ncall " + targetAssem.getSource(), "%rax", 
    									  targetAssem.getCost() + 1);
    		}
    		}
    	}
    	return bestTile;
    }
}
