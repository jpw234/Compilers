package compiler_ww424;

import java.util.List;
import java.util.ArrayList;
import edu.cornell.cs.cs4120.xic.ir.*;

public class DefaultClass extends Expr {
	private String className;
	
	public DefaultClass(String cn, int l, int c) {
		className = cn;
		line = l;
		column = c;
	}
	
	public String getClassType() {
		return className;
	}
	
	public Type typecheck(SymTab s) {
		type = new Type(className);
		return type;
	}
	
	//TODO: implement buildIRExpr()
	public IRExpr buildIRExpr() {
		String _this = LabelMaker.Generate_Unique_Label("_this");
		String _temp = LabelMaker.Generate_Unique_Label("_temp");
		String sz = "_I_size_" + className;
		String vt = "_I_vt_" + className;
		List<IRStmt> seqList = new ArrayList<IRStmt>();
		seqList.add(new IRMove(new IRTemp(_this), new IRCall(new IRName("_I_alloc_i"), 
				new IRBinOp(IRBinOp.OpType.ADD, new IRTemp(sz), new IRTemp("_reg_%rip")))));//allocate fields space & return "this" value
		seqList.add(new IRMove(new IRTemp(_temp), new IRBinOp(IRBinOp.OpType.ADD, new IRTemp(vt), new IRTemp("_reg_%rip"))));//compute dispatch vector address
		seqList.add(new IRMove(new IRMem(new IRTemp(_this)), new IRTemp(_temp)));
		return new IRESeq(new IRSeq(seqList), new IRTemp(_this));
	}
	@Override
	public String toString(){
		return "(" + className + ")";
	}
}
