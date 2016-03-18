package edu.cornell.cs.cs4120.xic.ir;

import edu.cornell.cs.cs4120.util.SExpPrinter;
import java.util.ArrayList;

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
}
