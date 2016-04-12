package compiler_ww424;

import edu.cornell.cs.cs4120.xic.ir.*;
import java.util.ArrayList;
import java.math.BigInteger;

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
		
		String ltypeVal = ltype.getType();
		if(ltypeVal == "tuple") {
			ltypeVal = ((Tuple) ltype).getArgs().get(0).getType();
			ltype = ((Tuple) ltype).getArgs().get(0);
		}
		
		if(ltype.getDepth() > 0) {
			if(op == BinaryOp.PLUS || op == BinaryOp.EQEQ || op == BinaryOp.NEQ) {
				type = ltype;
				return type;
			}
			else throw new Error(line + ":" + column + " error: " + "that binary operation does not work on arrays");
		}
		
		else if(ltypeVal == "bool") {
			if(op == BinaryOp.EQEQ || op == BinaryOp.NEQ || op == BinaryOp.AND ||
					op == BinaryOp.OR) {
				type = ltype;
				return type;
			}
			else throw new Error(line + ":" + column + " error: " + "that binary operation does not work on booleans");
		}
		
		else if(ltypeVal == "int") {
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
				BigInteger lb = BigInteger.valueOf(((NumExpr) left).getIntVal());
				BigInteger rb = BigInteger.valueOf(((NumExpr) left).getIntVal());
				//TODO: THIS IS WRONG IMPLEMENT THIS CORRECTLY
				return new NumExpr(lb.multiply(rb).shiftRight(32).longValue(), line, column);
			default: return this;
			}
		}
		else if((left instanceof ArrLiteralExpr) && (right instanceof ArrLiteralExpr)
				&& (((ArrLiteralExpr) left).getAccesses().size() == 0)
				&& (((ArrLiteralExpr) right).getAccesses().size() == 0)){
			/* Last potential constant folding: array concatenation. Can only be folded if
			 * we have 2 ArrLiteralExprs. **BY THE DEFINITION OF ARRLITERALEXPR'S CONSTANTFOLD**,
			 * if getAccesses.size() > 0, then the first access is a non-constant, so constant-folding
			 * this concat is out of the picture - this justifies the guard. If both are shallow arrays,
			 * we constant-fold concatenation by simply making an ArrLiteralExpr with the values
			 * of both left and right, and returning that ArrLiteralExpr.
			 */
			ArrLiteralExpr ret = new ArrLiteralExpr(new ArrayList<Expr>(), line, column);
			//first add all the values in left
			for(int a = 0; a < ((ArrLiteralExpr) left).getValues().size(); a++) {
				ret.addValue(((ArrLiteralExpr) left).getValues().get(a));
			}
			for(int a = 0; a < ((ArrLiteralExpr) right).getValues().size(); a++) {
				ret.addValue(((ArrLiteralExpr) right).getValues().get(a));
			}
			
			return ret;
		}
		return this;
	}
	
	public IRExpr buildIRExpr() {
		switch(op) {
		case PLUS: {
			if(left.getType() != null && left.getType().equals(new Type("int"))){
				return new IRBinOp(IRBinOp.OpType.ADD,
								   left.buildIRExpr(),
								   right.buildIRExpr());
			}
			ArrayList <IRStmt> sequence = new ArrayList<IRStmt>();
			String leftside = LabelMaker.Generate_Unique_Label("_STRING_CONCAT_LEFT");
			String rightside = LabelMaker.Generate_Unique_Label("_STRING_CONCAT_RIGHT");
			sequence.add(new IRMove(
							new IRTemp(leftside),
							left.buildIRExpr()));
			sequence.add(new IRMove(
							new IRTemp(rightside),
							right.buildIRExpr()));
			sequence.add(new IRMove(
							new IRTemp("_STRING_CONCAT_LLEN"),
							new IRMem(
								new IRBinOp(
									IRBinOp.OpType.SUB,
									new IRTemp(leftside),
									new IRConst(8)))));
			sequence.add(new IRMove(
							new IRTemp("_STRING_CONCAT_RLEN"),
							new IRMem(
								new IRBinOp(
									IRBinOp.OpType.SUB,
									new IRTemp(rightside),
									new IRConst(8)))));
			sequence.add(new IRMove(
							new IRTemp("_STRING_CONCAT_LENGTH"),
							new IRBinOp(
										IRBinOp.OpType.ADD,
										new IRTemp("_STRING_CONCAT_LLEN"),
										new IRTemp("_STRING_CONCAT_RLEN"))));
			sequence.add(new IRMove(
							new IRTemp("_STRING_CONCAT_LOC"),
							new IRCall(
								new IRName("_I_alloc_i"),
								new IRBinOp(
										IRBinOp.OpType.MUL,
										new IRBinOp(IRBinOp.OpType.ADD,
													new IRTemp("_STRING_CONCAT_LENGTH"),
													new IRConst(1)),
										new IRConst(8)))));
										
			sequence.add(new IRMove(
							new IRMem(
								new IRTemp("_STRING_CONCAT_LOC")),
							new IRTemp("_STRING_CONCAT_LENGTH")));
			sequence.add(new IRMove(new IRTemp("_STRING_CONCAT_COUNTER"),new IRConst(8)));
			sequence.add(new IRMove(new IRTemp("_STRING_CONCAT_LCOUNTER"),new IRConst(0)));
			sequence.add(new IRMove(new IRTemp("_STRING_CONCAT_RCOUNTER"),new IRConst(0)));
			String uleft_label_start 	= LabelMaker.Generate_Unique_Label("_STRING_CONCAT_LLOOP_START");
			String uright_label_start 	= LabelMaker.Generate_Unique_Label("_STRING_CONCAT_RLOOP_START");
			String udone_label 			= LabelMaker.Generate_Unique_Label("_STRING_CONCAT_DONE");
			
			sequence.add(new IRLabel(uleft_label_start));
			sequence.add(new IRCJump(
							new IRBinOp(
								IRBinOp.OpType.GEQ,
								new IRTemp("_STRING_CONCAT_LCOUNTER"),
								new IRTemp("_STRING_CONCAT_LLEN")),
							uright_label_start));
			sequence.add(new IRMove(
								new IRMem(
									new IRBinOp(
										IRBinOp.OpType.ADD,
										new IRTemp("_STRING_CONCAT_LOC"),
										new IRTemp("_STRING_CONCAT_COUNTER"))),
								new IRMem(
									new IRBinOp(
										IRBinOp.OpType.ADD,
										new IRTemp(leftside),
										new IRBinOp(
											IRBinOp.OpType.MUL,
											new IRTemp("_STRING_CONCAT_LCOUNTER"),
											new IRConst(8))))));
			
			sequence.add(new IRMove(new IRTemp("_STRING_CONCAT_COUNTER"),new IRBinOp(
																			IRBinOp.OpType.ADD,
																			new IRTemp("_STRING_CONCAT_COUNTER"),
																			new IRConst(8))));

			sequence.add(new IRMove(new IRTemp("_STRING_CONCAT_LCOUNTER"),new IRBinOp(
																			IRBinOp.OpType.ADD,
																			new IRTemp("_STRING_CONCAT_LCOUNTER"),
																			new IRConst(1))));
			sequence.add(new IRJump(new IRName(uleft_label_start)));
			
			sequence.add(new IRLabel(uright_label_start));
			sequence.add(new IRCJump(
							new IRBinOp(
								IRBinOp.OpType.GEQ,
								new IRTemp("_STRING_CONCAT_RCOUNTER"),
								new IRTemp("_STRING_CONCAT_RLEN")),
							udone_label));
			sequence.add(new IRMove(
								new IRMem(
									new IRBinOp(
										IRBinOp.OpType.ADD,
										new IRTemp("_STRING_CONCAT_LOC"),
										new IRTemp("_STRING_CONCAT_COUNTER"))),
								new IRMem(
									new IRBinOp(
										IRBinOp.OpType.ADD,
										new IRTemp(rightside),
										new IRBinOp(
											IRBinOp.OpType.MUL,
											new IRTemp("_STRING_CONCAT_RCOUNTER"),
											new IRConst(8))))));
			sequence.add(new IRMove(new IRTemp("_STRING_CONCAT_COUNTER"),new IRBinOp(
																			IRBinOp.OpType.ADD,
																			new IRTemp("_STRING_CONCAT_COUNTER"),
																			new IRConst(8))));
			sequence.add(new IRMove(new IRTemp("_STRING_CONCAT_RCOUNTER"),new IRBinOp(
																			IRBinOp.OpType.ADD,
																			new IRTemp("_STRING_CONCAT_RCOUNTER"),
																			new IRConst(1))));
			sequence.add(new IRJump(new IRName(uright_label_start)));
			sequence.add(new IRLabel(udone_label));
			return new IRESeq(new IRSeq(sequence),new IRBinOp(
													IRBinOp.OpType.ADD,
													new IRTemp("_STRING_CONCAT_LOC"),
													new IRConst(8)));
			
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
		case AND: {
			String tempLabel = LabelMaker.Generate_Unique_Label("lf");
			
			ArrayList<IRStmt> k = new ArrayList<IRStmt>();
			k.add(new IRMove(new IRTemp("x"), new IRConst(0)));
			k.add(new IRCJump(new IRBinOp(IRBinOp.OpType.XOR,
										  left.buildIRExpr(),
										  new IRConst(1)),
							  tempLabel));
			k.add(new IRCJump(new IRBinOp(IRBinOp.OpType.XOR,
										  right.buildIRExpr(),
										  new IRConst(1)),
							  tempLabel));
			k.add(new IRMove(new IRTemp("x"), new IRConst(1)));
			k.add(new IRLabel(tempLabel));
			
			return new IRESeq(new IRSeq(k), new IRTemp("x"));
		}
		case OR: {
			String tempLabel = LabelMaker.Generate_Unique_Label("lf");
			
			ArrayList<IRStmt> k = new ArrayList<IRStmt>();
			k.add(new IRMove(new IRTemp("x"), new IRConst(1)));
			k.add(new IRCJump(left.buildIRExpr(),
							  tempLabel));
			k.add(new IRCJump(right.buildIRExpr(),
							  tempLabel));
			k.add(new IRMove(new IRTemp("x"), new IRConst(0)));
			k.add(new IRLabel(tempLabel));
			
			return new IRESeq(new IRSeq(k), new IRTemp("x"));
		}
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
