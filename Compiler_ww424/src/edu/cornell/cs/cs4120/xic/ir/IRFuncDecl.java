package edu.cornell.cs.cs4120.xic.ir;

import java.util.ArrayList;
import java.util.List;

import edu.cornell.cs.cs4120.util.SExpPrinter;
import edu.cornell.cs.cs4120.xic.ir.visit.AggregateVisitor;
import edu.cornell.cs.cs4120.xic.ir.visit.IRVisitor;
import edu.cornell.cs.cs4120.xic.ir.visit.InsnMapsBuilder;

/** An IR function declaration */
public class IRFuncDecl extends IRNode {
    private String name;
    private IRStmt body;
    
    public IRFuncDecl(String name, IRStmt stmt) {
        this.name = name;
        body = stmt;
    }
    
    public String name() {
        return name;
    }
    
    public IRStmt body() {
        return body;
    }
    
    @Override
    public String label() {
        return "FUNC " + name;
    }
    
    @Override
    public IRNode visitChildren(IRVisitor v) {
        IRStmt stmt = (IRStmt) v.visit(this, body);
        
        if (stmt != body) return new IRFuncDecl(name, stmt);
        
        return this;
    }
    
    @Override
    public <T> T aggregateChildren(AggregateVisitor<T> v) {
        T result = v.unit();
        result = v.bind(result, v.visit(body));
        return result;
    }
    
    @Override
    public InsnMapsBuilder buildInsnMapsEnter(InsnMapsBuilder v) {
        v.addNameToCurrentIndex(name);
        v.addInsn(this);
        return v;
    }
    
    @Override
    public IRNode buildInsnMaps(InsnMapsBuilder v) {
        return this;
    }
    
    @Override
    public void printSExp(SExpPrinter p) {
        p.startList();
        p.printAtom("FUNC");
        p.printAtom(name);
        body.printSExp(p);
        p.endList();
    }
    
    public void IRLower(){
        IRStmt stmtLow = body.IRLower();
        List<IRStmt> listLow = unwrapIRStmt(stmtLow);
        body = new IRSeq(listLow);
        
    }
    
    /*
     //CJUMP(e,l) ⇒ cmp e1, e2
     //             [jne|je|jgt|…] l
     public Assembly toAssembly(){
     name:
     body.toAssembly()
     }*/
    
    public List<IRStmt> unwrapIRStmt(IRStmt s){
        List<IRStmt> unwrapS = new ArrayList<IRStmt>();
        for (IRStmt st:((IRSeq) s).stmts()){
            if(st instanceof IRSeq){
                List<IRStmt> tmp = unwrapIRStmt(st);
                for(IRStmt b:tmp){
                    unwrapS.add(b);
                }
            }
            else{unwrapS.add(st);}
        }
        return unwrapS;
    }
    
    public int bestCost() {
    	if(bestTile != null) return bestTile.getCost();
    	else {
    		bestTileNum = 0;
    		bestCost = body.bestCost() + 2;
    	}
    	return bestCost;
    }
    
    public AssemInstr getBestTile() {
    	if(bestTile != null) return bestTile;
    	else {
    		StackAssigner.clear();
    		this.bestCost();
    		switch(bestTileNum) {
    		case 0: {//mintile
    			AssemInstr child = body.getBestTile();
        		String data = "\n\n" + name + ": \n"+
        					  "pushq %rbp \n"+
        					  "movq %rsp, %rbp \n";
        		data += child.getData();
        		bestTile = new AssemInstr(data, "", child.getCost() + 2);
    		}
    		}
    	}
    	return bestTile;
    }
}
