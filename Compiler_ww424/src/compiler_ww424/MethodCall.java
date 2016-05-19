package compiler_ww424;

import java.util.ArrayList;
import java.util.List;

import compiler_ww424.Compiler;
import edu.cornell.cs.cs4120.xic.ir.*;

public class MethodCall extends FunCall {
	private ArrExpr ownerClass;
	private String classType;
	
	public MethodCall(ArrExpr cl, IDExpr name, ArrayList<Expr> args, int lineNum, int colNum) {
		super(name, args, lineNum, colNum);
		
		ownerClass = cl;
	}
	
	public String getClassType() {
		return classType;
	}
	
	public Type typecheck(SymTab s) {
		//first typecheck ArrExpr to ensure it's a class
		Type varType = ownerClass.typecheck(s); //this must be a class type
		//if it's another type throw an error
		if(varType.getType() == "int" || varType.getType() == "bool" || varType.getType() == "empty") {
			throw new Error(line + ":" + column + " error: " + "used non-class variable like a class (bad method access)");
		}
		
		classType = varType.getType();
		
		try {
			ft = (FunType) s.lookupFunction(varType.getType() + "." + name.getName());

			if(args.size() != ft.getInputs().getArgs().size()) throw new Error(line + ":" + column + " error: " + "incorrect # of args to fun");

			for(int a = 0; a < args.size(); a++) {
				if(!args.get(a).typecheck(s).equals(ft.getInputs().getArgs().get(a))) 
					throw new Error(line + ":" + column + " error: " + "incorrect type of arg to fun");
			}

			type = ft.getOutputs();
			return type;
		}
		catch(Error e) {
			if(e.getMessage() == "Semantic Error: var does not exist") throw new Error(line + ":" + column + " error: " + e.getMessage());
			else throw e;
		}
	}
	
	public String toString(){
		return String.format("(%s)", ownerClass.toString());
	}
	//TODO: reimplement buildIRExpr
	public IRExpr buildIRExpr() {
		String _this = LabelMaker.Generate_Unique_Label("_this");
		String _temp = LabelMaker.Generate_Unique_Label("_temp");
		List<IRStmt> seqList = new ArrayList<IRStmt>();
		seqList.add(new IRMove(new IRTemp(_this), ownerClass.buildIRExpr()));
		seqList.add(new IRMove(new IRTemp(_temp), new IRMem(new IRTemp(_this))));
		int offset = (Compiler.DispachTable.get(classType).getMethods().getMethod().indexOf(this.getName().getName())) * 8;
		seqList.add(new IRMove(new IRTemp(_temp), new IRBinOp(IRBinOp.OpType.ADD, new IRTemp(_temp), new IRConst(offset))));
		ArrayList<IRExpr> irargs = new ArrayList<IRExpr>();
		for(int a = 0; a < args.size(); a++) {
			irargs.add(args.get(a).buildIRExpr());
		}
		return new IRESeq(new IRSeq(seqList), new IRCall(new IRMem(new IRTemp(_temp)), irargs));
	}
}
