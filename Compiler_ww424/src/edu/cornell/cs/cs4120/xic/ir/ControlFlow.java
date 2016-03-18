package edu.cornell.cs.cs4120.xic.ir;

import java.util.ArrayList;
import java.util.List;

import compiler_ww424.Expr;
import compiler_ww424.LabelMaker;

public class ControlFlow {
	
	private Object e;
	private String t,f;
	public ControlFlow(Object e ,String t, String f) {
		String _t = LabelMaker.Generate_Unique_Label(t);
		String _f = LabelMaker.Generate_Unique_Label(f);
		this.e = e;
		this.t = _t;
		this.f = _f; 
	}
	
	public IRStmt convert() {
		if (e instanceof Boolean ){
			if ((Boolean) e == true)
				return new IRJump( new IRName(t));
			else 
				return new IRJump(new IRName(f));
		}
		else if (e instanceof IRExpr){
			return new IRCJump((IRExpr)e, t , f);
		}else if (e instanceof IRBinOp){
			String _e1 = LabelMaker.Generate_Unique_Label("_True_e1");
			ControlFlow left = new ControlFlow (((IRBinOp)e).left() ,_e1,f);
			ControlFlow right= new ControlFlow (((IRBinOp)e).right() ,t,f);
			return new IRSeq(left.convert(), new IRLabel(_e1), right.convert());
		}
		return null;
		
			
	}
}
