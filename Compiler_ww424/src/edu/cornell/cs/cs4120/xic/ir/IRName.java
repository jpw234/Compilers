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
    
    /*
     LABEL(l ) ⇒ l*/
	@Override
	public AssemInstr makeAssembly() {
		// TODO Auto-generated method stub
		return new AssemInstr(name,"",0);
	}
    
    @Override
    public void printSExp(SExpPrinter p) {
        p.startList();
        p.printAtom("NAME");
        p.printAtom(name);
        p.endList();
    }

}
