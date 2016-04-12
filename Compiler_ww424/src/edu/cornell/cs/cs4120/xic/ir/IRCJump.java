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

	@Override
    /*
    //CJUMP(e,l) ⇒ cmp e1, e2
    //             [jne|je|jgt|…] l
   */
	public AssemInstr makeAssembly() {
		
		String data = "";
		data = "testq " + expr.makeAssembly().getSource() + "\n";
		data += "jnz " + trueLabel ;  
		return new AssemInstr(data ,"",expr.makeAssembly().getCost() + 2);
		// TODO Auto-generated method stub
		/* Binary Operation */ 
//		String data = "";
//		if (expr instanceof IRBinOp){
//			IRBinOp e = (IRBinOp)expr;
//			IRBinOp.OpType type = e.opType();
////			IRExpr e1 = e.left();
////			IRExpr e2 = e.right();
////			String data = "cmpq " + e1.makeAssembly().getData() +" "
////								  + e2.makeAssembly().getData() +"\n";
//			if (type == IRBinOp.OpType.GEQ){
//				data += "jge " + trueLabel + " \n";
//				return new AssemInstr(data,"");
//			}
//			else if (type == IRBinOp.OpType.EQ){
//				data += "je " + trueLabel + " \n";
//				return new AssemInstr(data,"");
//			}
//			else if (type == IRBinOp.OpType.GT){
//				data += "jg " + trueLabel + " \n";
//				return new AssemInstr(data,"");
//			}
//			else if (type == IRBinOp.OpType.LEQ){
//				data += "jlq " + trueLabel + " \n";
//				return new AssemInstr(data,"");
//			}
//			else if (type == IRBinOp.OpType.LT){
//				data += "jl " + trueLabel + " \n";
//				return new AssemInstr(data,"");
//			}
//			else if (type == IRBinOp.OpType.NEQ){
//				data += "jnq " + trueLabel + " \n";
//				return new AssemInstr(data,"");
//			}
//			else if (type == IRBinOp.OpType.OR){
//				data += "or " + e1.makeAssembly().getData() +" "
//						  	 + e2.makeAssembly().getData() +"\n";
//				data += "jnz " + trueLabel + " \n";
//				return new AssemInstr(data,"");
//			}
//			else if (type == IRBinOp.OpType.AND){
//				data += "and " + e1.makeAssembly().getData() +" "
//						  	 + e2.makeAssembly().getData() +"\n";
//				data += "jnz " + trueLabel + " \n";
//				return new AssemInstr(data,"");
//			}
//
//		}
//		//This is for boolean expression
//		else {
//			data = "testq " + expr.makeAssembly().getSource();
//			data += "jnz " + trueLabel + " \n";
//			return new AssemInstr(data,"");
//		}
//		return null;
	}
}
