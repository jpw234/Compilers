package compiler_ww424;

import edu.cornell.cs.cs4120.xic.ir.*;

public class BinaryExpr extends Expr {
	private Expr left;
	private Expr right;
	private BinaryOp op;
	private int leftLine;
	private int leftCol;
	private int rightLine;
	private int rightCol;
	
	public BinaryExpr(Expr l, Expr r, BinaryOp o,int ellineNum,int elcolNum,int erlineNum,int ercolNum) {
		left = l;
		right = r;
		op = o;
		leftLine = ellineNum;
		leftCol = elcolNum;
		rightLine = erlineNum;
		rightCol = ercolNum;
		line = leftLine;
		column = leftCol;
	}
	
	public Expr getLeft() {
		return left;
	}
	
	public Expr getRight() {
		return right;
	}
	
	public BinaryOp getOp() {
		return op;
	}
	
	public Type typecheck(SymTab s) {
		Type ltype = left.typecheck(s);
		Type rtype = right.typecheck(s);
		if(!rtype.equals(ltype)) throw new Error(line + ":" + column + " error: " + "2 sides of binaryexpr do not match types");
		
		if(ltype.getDepth() > 0) {
			if(op == BinaryOp.PLUS || op == BinaryOp.EQEQ || op == BinaryOp.NEQ) {
				type = ltype;
				return type;
			}
			else throw new Error(line + ":" + column + " error: " + "that binary operation does not work on arrays");
		}
		
		else if(ltype.getType() == "bool") {
			if(op == BinaryOp.EQEQ || op == BinaryOp.NEQ || op == BinaryOp.AND ||
					op == BinaryOp.OR) {
				type = ltype;
				return type;
			}
			else throw new Error(line + ":" + column + " error: " + "that binary operation does not work on booleans");
		}
		
		else if(ltype.getType() == "int") {
			if(op == BinaryOp.PLUS || op == BinaryOp.MINUS || op == BinaryOp.TIMES || 
			   op == BinaryOp.DIV || op == BinaryOp.MOD || op == BinaryOp.HIGHMUL) {
				type = new Type("int");
				return type;
			}
			else if(op == BinaryOp.EQEQ || op == BinaryOp.NEQ || op == BinaryOp.LT ||
					op == BinaryOp.LEQ || op == BinaryOp.GT || op == BinaryOp.GEQ) {
				type =  new Type("bool");
				return type;
			}
			else throw new Error(line + ":" + column + " error: " + "that binary operation does not work on integers");
		}
		
		else throw new Error(line + ":" + column + " error: " + "this shouldn't happen but the BinaryExpr got messed up");
	}
	
	@Override
	public Expr constantFold() {
		//TODO: finish this
		left = left.constantFold();
		right = right.constantFold();
		
		if((left instanceof BoolExpr) && (right instanceof BoolExpr)) {
			switch(op) {
			case EQEQ: 
				return new BoolExpr(((BoolExpr) left).getVal() == ((BoolExpr) right).getVal(), line, column);
			case NEQ:
				return new BoolExpr(((BoolExpr) left).getVal() != ((BoolExpr) right).getVal(), line, column);
			case AND:
				return new BoolExpr(((BoolExpr) left).getVal() && ((BoolExpr) right).getVal(), line, column);
			case OR:
				return new BoolExpr(((BoolExpr) left).getVal() || ((BoolExpr) right).getVal(), line, column);
			default:
				return this;
			}
		}
		else if((left instanceof NumExpr) && (right instanceof NumExpr)) {
			switch(op) {
			case EQEQ:
				return new BoolExpr(((NumExpr) left).getIntVal() == ((NumExpr) right).getIntVal(), line, column);
			case NEQ:
				return new BoolExpr(((NumExpr) left).getIntVal() != ((NumExpr) right).getIntVal(), line, column);
			case LT:
				return new BoolExpr(((NumExpr) left).getIntVal() < ((NumExpr) right).getIntVal(), line, column);
			case LEQ:
				return new BoolExpr(((NumExpr) left).getIntVal() <= ((NumExpr) right).getIntVal(), line, column);
			case GT:
				return new BoolExpr(((NumExpr) left).getIntVal() > ((NumExpr) right).getIntVal(), line, column);
			case GEQ:
				return new BoolExpr(((NumExpr) left).getIntVal() >= ((NumExpr) right).getIntVal(), line, column);
			case PLUS:
				return new NumExpr(((NumExpr) left).getIntVal() + ((NumExpr) right).getIntVal(), line, column);
			case MINUS:
				return new NumExpr(((NumExpr) left).getIntVal() - ((NumExpr) right).getIntVal(), line, column);
			case TIMES:
				return new NumExpr(((NumExpr) left).getIntVal() * ((NumExpr) right).getIntVal(), line, column);
			case DIV:
				return new NumExpr(((NumExpr) left).getIntVal() / ((NumExpr) right).getIntVal(), line, column);
			case MOD:
				return new NumExpr(((NumExpr) left).getIntVal() % ((NumExpr) right).getIntVal(), line, column);
			case HIGHMUL:
				//TODO: THIS IS WRONG IMPLEMENT THIS CORRECTLY
				return new NumExpr(((NumExpr) left).getIntVal() * ((NumExpr) right).getIntVal(), line, column);
			default: return this;
			}
		}
		return this;
	}
	
	public IRExpr buildIRExpr() {
		switch(op) {
		case PLUS: {
			//TODO: this logic
		}
		case MINUS: return new IRBinOp(IRBinOp.OpType.SUB,
									   left.buildIRExpr(),
									   right.buildIRExpr());
		case TIMES: return new IRBinOp(IRBinOp.OpType.MUL,
									   left.buildIRExpr(),
									   right.buildIRExpr());
		case HIGHMUL: return new IRBinOp(IRBinOp.OpType.HMUL,
										 left.buildIRExpr(),
										 right.buildIRExpr());
		case DIV: return new IRBinOp(IRBinOp.OpType.DIV,
									 left.buildIRExpr(),
									 right.buildIRExpr());
		case MOD: return new IRBinOp(IRBinOp.OpType.MOD,
									 left.buildIRExpr(),
									 right.buildIRExpr());
		case LT: return new IRBinOp(IRBinOp.OpType.LT,
									left.buildIRExpr(),
									right.buildIRExpr());
		case LEQ: return new IRBinOp(IRBinOp.OpType.LEQ,
									 left.buildIRExpr(),
									 right.buildIRExpr());
		case GEQ: return new IRBinOp(IRBinOp.OpType.GEQ,
									 left.buildIRExpr(),
									 right.buildIRExpr());
		case GT: return new IRBinOp(IRBinOp.OpType.GT,
									left.buildIRExpr(),
									right.buildIRExpr());
		case EQEQ: return new IRBinOp(IRBinOp.OpType.EQ,
									  left.buildIRExpr(),
									  right.buildIRExpr());
		case NEQ: return new IRBinOp(IRBinOp.OpType.NEQ,
									 left.buildIRExpr(),
									 right.buildIRExpr());
		case AND: return new IRBinOp(IRBinOp.OpType.AND,
									 left.buildIRExpr(),
									 right.buildIRExpr());
		case OR: return new IRBinOp(IRBinOp.OpType.OR,
									left.buildIRExpr(),
									right.buildIRExpr());
		default: return null;
		}
	}
	
	@Override
	public String toString(){
		String s = "";
		String operand = "";
		switch(op){
			case PLUS: operand = "+";
				break;
			case MINUS: operand = "-";
				break;
			case TIMES: operand = "*";
				break;
			case HIGHMUL: operand = "*>>";
				break;
			case DIV: operand = "/";
				break;
			case MOD: operand = "%";
				break;
			case LT: operand = "<";
				break;
			case LEQ: operand = "<=";
				break;
			case GEQ: operand = ">=";
				break;
			case GT: operand = ">";
				break;
			case EQEQ: operand = "==";
				break;
			case NEQ: operand = "!=";
				break;
			case AND: operand = "&";
				break;
			case OR: operand = "|";
				break;
		}
		s = "( " + operand + " " + left.toString() + " " + right.toString() + " )";
		return s ;
	}
}
