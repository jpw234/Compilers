package edu.cornell.cs.cs4120.xic.ir;

import java.util.ArrayList;

import compiler_ww424.LabelMaker;
import edu.cornell.cs.cs4120.util.InternalCompilerError;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import edu.cornell.cs.cs4120.xic.ir.visit.AggregateVisitor;
import edu.cornell.cs.cs4120.xic.ir.visit.CheckConstFoldedIRVisitor;
import edu.cornell.cs.cs4120.xic.ir.visit.IRVisitor;

/**
 * An intermediate representation for a binary operation
 * OP(left, right)
 */
public class IRBinOp extends IRExpr {

    /**
     * Binary operators
     */
    public enum OpType {
        ADD, SUB, MUL, HMUL, DIV, MOD, AND, OR, XOR, LSHIFT, RSHIFT, ARSHIFT,
        EQ, NEQ, LT, GT, LEQ, GEQ;

        @Override
        public String toString() {
            switch (this) {
            case ADD:
                return "ADD";
            case SUB:
                return "SUB";
            case MUL:
                return "MUL";
            case HMUL:
                return "HMUL";
            case DIV:
                return "DIV";
            case MOD:
                return "MOD";
            case AND:
                return "AND";
            case OR:
                return "OR";
            case XOR:
                return "XOR";
            case LSHIFT:
                return "LSHIFT";
            case RSHIFT:
                return "RSHIFT";
            case ARSHIFT:
                return "ARSHIFT";
            case EQ:
                return "EQ";
            case NEQ:
                return "NEQ";
            case LT:
                return "LT";
            case GT:
                return "GT";
            case LEQ:
                return "LEQ";
            case GEQ:
                return "GEQ";
            }
            throw new InternalCompilerError("Unknown op type");
        }
    };

    private OpType type;
    private IRExpr left, right;

    public IRBinOp(OpType type, IRExpr left, IRExpr right) {
        this.type = type;
        this.left = left;
        this.right = right;
    }

    public OpType opType() {
        return type;
    }

    public IRExpr left() {
        return left;
    }

    public IRExpr right() {
        return right;
    }

    @Override
    public String label() {
        return type.toString();
    }

    @Override
    public IRNode visitChildren(IRVisitor v) {
        IRExpr left = (IRExpr) v.visit(this, this.left);
        IRExpr right = (IRExpr) v.visit(this, this.right);

        if (left != this.left || right != this.right)
            return new IRBinOp(type, left, right);

        return this;
    }

    @Override
    public <T> T aggregateChildren(AggregateVisitor<T> v) {
        T result = v.unit();
        result = v.bind(result, v.visit(left));
        result = v.bind(result, v.visit(right));
        return result;
    }

    @Override
    public boolean isConstFolded(CheckConstFoldedIRVisitor v) {
        return !isConstant();
    }

    @Override
    public boolean isConstant() {
        return left.isConstant() && right.isConstant();
    }

    public IRESeq IRLower() {
    	ArrayList<IRStmt> stmts = new ArrayList<IRStmt>();
    	IRESeq llower = left.IRLower();
    	IRESeq rlower = right.IRLower();
    	stmts.add(llower.stmt());
    	if(rlower.stmt() instanceof IRSeq && ((IRSeq)rlower.stmt()).stmts().isEmpty()){
    		return new IRESeq(new IRSeq(stmts), new IRBinOp(type,  llower.expr(), rlower.expr()));
    	}
    	String genLabel = LabelMaker.Generate_Unique_Label("_BINOPNAIVE");
    	stmts.add(new IRMove( new IRTemp(genLabel), llower.expr()));
    	stmts.add(rlower.stmt());
    	return new IRESeq(new IRSeq(stmts), new IRBinOp(type,  new IRTemp(genLabel), rlower.expr()));
    }

    @Override
    public void printSExp(SExpPrinter p) {
        p.startList();
        p.printAtom(type.toString());
        left.printSExp(p);
        right.printSExp(p);
        p.endList();
    }
    
    public int bestCost() {
    	if(bestTile != null) return bestTile.getCost();
    	else {
    		bestTileNum = 0;
    		int overhead = left.bestCost() + right.bestCost();
    		switch(type) {
    		case ADD: bestCost = overhead + 3; break;
    		case SUB: bestCost = overhead + 3; break;
    		case MUL: bestCost = overhead + 7; break;
    		case HMUL: bestCost = overhead + 7; break;
    		case DIV: bestCost = overhead + 8; break;
    		case MOD: bestCost = overhead + 8; break;
    		case AND: bestCost = overhead + 3; break;
    		case OR: bestCost = overhead + 3; break;
    		case XOR: bestCost = overhead + 3; break;
    		case LSHIFT: bestCost = overhead; break;
    		case RSHIFT: bestCost = overhead; break;
    		case ARSHIFT: bestCost = overhead; break;
    		case EQ: bestCost = overhead + 8; break;
    		case NEQ: bestCost = overhead + 9; break;
    		case LT: bestCost = overhead + 8; break;
    		case GEQ: bestCost = overhead + 9; break;
    		case LEQ: bestCost = overhead + 13; break;
    		case GT: bestCost = overhead + 14; break;
    		}
    	}
    	return bestCost;
    }
    
    public AssemInstr getBestTile() {
    	if(bestTile != null) return bestTile;
    	else{
    		this.bestCost();
    		switch(bestTileNum) {
    		case 0: {//mintile
    			AssemInstr leftData = left.getBestTile();
    			AssemInstr rightData = right.getBestTile();
    			String store = StackAssigner.getLocation(LabelMaker.Generate_Unique_Label("_BINOP_STORE"));
    			String push = "\nsubq $8, %rsp";
    			switch(type) {
    			case ADD: {
    				String data = "\nmovq " + leftData.getSource() + ", %r11" +
    							  "\naddq " + rightData.getSource() + ", %r11" +
    							  "\nmovq %r11, " + store;
    				bestTile = new AssemInstr(leftData.getData() + rightData.getData() + push + data, store,
    										  leftData.getCost() + rightData.getCost() + 3); break;
    			}
    			case SUB: {
    				String data = "\nmovq " + leftData.getSource() + ", %r11" +
							      "\nsubq " + rightData.getSource() + ", %r11" +
							      "\nmovq %r11, " + store;
    				bestTile = new AssemInstr(leftData.getData() + rightData.getData() + push + data, store,
										      leftData.getCost() + rightData.getCost() + 3); break;
    			}
    			case MUL: {
    				String data = "\nmovq %rax, %r11"
    							+ "\nmovq %rdx, %r12"
    							+ "\nmovq " + leftData.getSource() + ", %rax"
    							+ "\nmovq " + rightData.getSource() + ", %r15"
    							+ "\nimulq %r15"
    							+ "\nmovq %rax, " + store
    							+ "\nmovq %r11, %rax"
    							+ "\nmovq %r12, %rdx";
    				bestTile = new AssemInstr(leftData.getData() + rightData.getData() + push + data, store,
										      leftData.getCost() + rightData.getCost() + 7); break;
    			}
    			case HMUL: {
    				String data = "\nmovq %rax, %r11"
								+ "\nmovq %rdx, %r12"
								+ "\nmovq " + leftData.getSource() + ", %rax"
								+ "\nmovq " + rightData.getSource() + ", %r15"
								+ "\nimulq %r15"
								+ "\nmovq %rdx, " + store
								+ "\nmovq %r11, %rax"
								+ "\nmovq %r12, %rdx";
    				bestTile = new AssemInstr(leftData.getData() + rightData.getData() + push + data, store,
									      	  leftData.getCost() + rightData.getCost() + 7); break;
    			}
    			case DIV: {
    				String data = "\nmovq %rax, %r11"
    							+ "\nmovq %rdx, %r12"
    							+ "\nxorq %rdx, %rdx"
    							+ "\nmovq " + leftData.getSource() + ", %rax"
    							+ "\nmovq " + rightData.getSource() + ", %r15"
    							+ "\nidivq %r15"
    							+ "\nmovq %rax, " + store
    							+ "\nmovq %r11, %rax"
    							+ "\nmovq %r12, %rdx";
    				bestTile = new AssemInstr(leftData.getData() + rightData.getData() + push + data, store,
    										  leftData.getCost() + rightData.getCost() + 8); break;
    			}
    			case MOD: {
    				String data = "\nmovq %rax, %r11"
								+ "\nmovq %rdx, %r12"
								+ "\nxorq %rdx, %rdx"
								+ "\nmovq " + leftData.getSource() + ", %rax"
								+ "\nmovq " + rightData.getSource() + ", %r15"
								+ "\nidivq %r15"
								+ "\nmovq %rdx, " + store
								+ "\nmovq %r11, %rax"
								+ "\nmovq %r12, %rdx";
    				bestTile = new AssemInstr(leftData.getData() + rightData.getData() + push + data, store,
										      leftData.getCost() + rightData.getCost() + 8); break;
    			}
    			case AND: {
    				String data = "\nmovq " + leftData.getSource() + ", %r11"
    							+ "\nandq " + rightData.getSource() + ", %r11"
    							+ "\nmovq %r11, " + store;
    				bestTile = new AssemInstr(leftData.getData() + rightData.getData() + push + data, store,
    										  leftData.getCost() + rightData.getCost() + 3); break;
    			}
    			case OR: {
    				String data = "\nmovq " + leftData.getSource() + ", %r11"
								+ "\norq " + rightData.getSource() + ", %r11"
								+ "\nmovq %r11, " + store;
    				bestTile = new AssemInstr(leftData.getData() + rightData.getData() + push + data, store,
    										  leftData.getCost() + rightData.getCost() + 3); break;
    			}
    			case XOR: {
    				String data = "\nmovq " + leftData.getSource() + ", %r11"
								+ "\nxorq " + rightData.getSource() + ", %r11"
								+ "\nmovq %r11, " + store;
    				bestTile = new AssemInstr(leftData.getData() + rightData.getData() + push + data, store,
										      leftData.getCost() + rightData.getCost() + 3); break;
    			}
    			case LSHIFT: {
    				//TODO: add optional support for shifts
    				bestTile = null;
    			}
    			case RSHIFT: {
    				bestTile = null;
    			}
    			case ARSHIFT: {
    				bestTile = null;
    			}
    			case EQ: {
    				String data = "\nmovq " + leftData.getSource() + ", %r11"
    							+ "\ncmpq " + rightData.getSource() + ", %r11"
    	    					+ "\npushf"
    							+ "\nmovq $64, %r12"
    							+ "\npopq %r13"
    							+ "\nandq %r13, %r12"
    							+ "\nsarq $6, %r12"
    							+ "\nmovq %r12, " + store;
    				bestTile = new AssemInstr(leftData.getData() + rightData.getData() + push +  data, store,
						      				  leftData.getCost() + rightData.getCost() + 8); break;
    			}
    			case NEQ: {
    				String data = "\nmovq " + leftData.getSource() + ", %r11"
								+ "\ncmpq " + rightData.getSource() + ", %r11"
								+ "\npushf"
								+ "\nmovq $64, %r12"
								+ "\npopq %r13"
								+ "\nandq %r13, %r12"
								+ "\nsarq $6, %r12" //same as EQ but XOR with 1 at the end
								+ "\nxorq $1, %r12"
								+ "\nmovq %r12, " + store;
    				bestTile = new AssemInstr(leftData.getData() + rightData.getData() + push + data, store,
					      				  	  leftData.getCost() + rightData.getCost() + 9); break;
    			}
    			case LT: {
    				String data = "\nmovq " + leftData.getSource() + ", %r11"
								+ "\ncmpq " + rightData.getSource() + ", %r11"
								+ "\npushf"
								+ "\nmovq $128, %r12"
								+ "\npopq %r13"
								+ "\nandq %r13, %r12"
								+ "\nsarq $7, %r12"
								+ "\nmovq %r12, " + store;
    				bestTile = new AssemInstr(leftData.getData() + rightData.getData() + push + data, store,
					      				      leftData.getCost() + rightData.getCost() + 8); break;
    			}
    			case GEQ: {
    				String data = "\nmovq " + leftData.getSource() + ", %r11"
								+ "\ncmpq " + rightData.getSource() + ", %r11"
								+ "\npushf"
								+ "\nmovq $128, %r12"
								+ "\npopq %r13"
								+ "\nandq %r13, %r12"
								+ "\nsarq $7, %r12" //same as LT but flipped (XOR with 1)
								+ "\nxorq $1, %r12"
								+ "\nmovq %r12, " + store;
    				bestTile = new AssemInstr(leftData.getData() + rightData.getData() + push + data, store,
					      				      leftData.getCost() + rightData.getCost() + 9); break;
    			}
    			case LEQ: {
    				String data = "\nmovq " + leftData.getSource() + ", %r11"
    							+ "\ncmpq " + rightData.getSource() + ", %r11"
    							+ "\npushf"
    							+ "\nmovq $192, %r12"
    							+ "\npopq %r13"
    							+ "\nandq %r13, %r12"
    							+ "\ncmpq $192, %r12"
    							+ "\npushf"
    							+ "\npopq %r13"
    							+ "\nmovq $64, %r12"
    							+ "\nandq %r13, %r12"
    							+ "\nsarq $6, %r12"
    							+ "\nmovq %r12, " + store;
    				bestTile = new AssemInstr(leftData.getData() + rightData.getData() + push + data, store,
    										  leftData.getCost() + rightData.getCost() + 13); break;
    			}
    			case GT: {
    				String data = "\nmovq " + leftData.getSource() + ", %r11"
								+ "\ncmpq " + rightData.getSource() + ", %r11"
								+ "\npushf"
								+ "\nmovq $192, %r12"
								+ "\npopq %r13"
								+ "\nandq %r13, %r12"
								+ "\ncmpq $192, %r12"
								+ "\npushf"
								+ "\npopq %r13"
								+ "\nmovq $64, %r12"
								+ "\nandq %r13, %r12"
								+ "\nsarq $6, %r12" //same as LEQ but flipped (XOR with 1)
								+ "\nxorq $1, %r12"
								+ "\nmovq %r12, " + store;
    				bestTile = new AssemInstr(leftData.getData() + rightData.getData() + push + data, store,
										      leftData.getCost() + rightData.getCost() + 14); break;
    			}
    			}; break;
    		} //end case 0 (mintile)
    		}
    	}
    	return bestTile;
    }
}
