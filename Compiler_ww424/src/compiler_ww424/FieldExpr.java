package compiler_ww424;

import java.util.List;
import java.util.ArrayList;
import edu.cornell.cs.cs4120.xic.ir.*;

public class FieldExpr extends Expr {
	private ArrExpr preDot;
	private ArrExpr postDot;
	private String classType;
	
	public FieldExpr(ArrExpr pre, ArrExpr post,int lineNum, int colNum) {
		preDot = pre;
		postDot = post;
		line = lineNum;
		column = colNum;
	}
	
	@Override
	public Type typecheck(SymTab s) {
		Type varType = preDot.typecheck(s); //this must be a class type
		//if it's another type throw an error
		if(varType.getType() == "int" || varType.getType() == "bool" || varType.getType() == "empty") {
			throw new Error(line + ":" + column + " error: " + "used non-class variabe like a class (bad field access)");
		}
		
		classType = varType.getType();
		
		/* Currently postDot is an ArrExpr that is kinda broken. Take example FieldExpr a.var, a:Point, var:int[]
		 * postDot currently has name as an IDExpr with name "var", but "var" isn't a variable
		 * Construct a new ArrExpr with postDot's accesses but prepend the classname, and typecheck it
		 */
		ArrExpr real = new ArrExpr(new IDExpr(varType.getType() + "." + postDot.getName().getName(), line, column),
								   postDot.getAccesses(), postDot.getLine(), postDot.getColumn());
		
		//if this typechecks, the full FieldExpr is good
		type = real.typecheck(s);
		return type;
	}
	
	public ArrExpr getLeft() {
		return preDot;
	}
	
	public ArrExpr getRight() {
		return postDot;
	}
	
	public String getClassType() {
		return classType;
	}
	
	public IRExpr buildIRExpr() {
		String _temp = LabelMaker.Generate_Unique_Label("_temp");
		List<IRStmt> seqList = new ArrayList<IRStmt>();
		seqList.add(new IRMove(new IRTemp(_temp), preDot.buildIRExpr()));
		long offset = (Compiler.DispachTable.get(classType).getFields().indexOf(postDot.getName().getName()) + 1) * 8;//+1 for "this" pointer
		seqList.add(new IRMove(new IRTemp(_temp), new IRBinOp(IRBinOp.OpType.ADD, new IRTemp(_temp), new IRConst(offset))));
		return new IRESeq(new IRSeq(seqList), new IRMem(new IRTemp(_temp)));
	}
	
	
	public String toString(){
		String s = "";
		String s_pre = "";
		String s_post = "";
	
		// PreDot 
		if (preDot!=null) s_pre = preDot.toString();
		if (postDot!= null) s_post = postDot.toString();
		
		s_pre = "(" + s_pre + ")";
		s_post= "(" +s_post + ")";	
		s = "(" + s_pre + s_post +")";
		return s ;
	}
	
}
