package compiler_ww424;

import java.util.List;
import java.util.ArrayList;

public class MultiDecl extends Stmt{
	private List<Decl> declList = null;
	private boolean isGlobal = false;

	public MultiDecl(List<IDExpr> idList, Decl varType, int lineNum, int colNum){
		declList = new ArrayList<Decl>();
		for(IDExpr id : idList){
			declList.add(new Decl(id, varType, id.getLine(), id.getColumn()));
		}
		line = lineNum;
		column = colNum;
	}

	public List<Decl> getDeclList(){
		return declList;
	}

	public void setGlobal(boolean v) {
		isGlobal = v;
	}

	public boolean isGlobal() {
		return isGlobal;
	}

	public Type typecheck(SymTab s){
		for(Decl d : declList) {
			d.typecheck(s);
		}

		return new Type("unit");
	}

	public String toString(){
		String s_declList = "";
		if (declList != null){
			for (int i = 0; i<declList.size();i++){
				s_declList = s_declList + " " + declList.get(i).toString();
			}
		}
		return String.format("( %s )", s_declList);
	}
	
	@Override
	public IRStmt buildIRStmt() {
		if(isGlobal == true){
			for(Decl d : declList){
				String assembly = "";//y_final
				String mngName = Decl.globalVariableMangler(d.getName().getName(), d.getType());//_I_g_y__final_i
				assembly += "\n.globl " + mngName;//.globl _I_g_y__final_i
				assembly += "\n" + mngName + ":";//_I_g_y__final_i:
				assembly += "\n\t.zero 8";//	.zero 8
				assembly += "\n\t.text";//	.text
				assembly += "\n";
				if(d.getType().getDepth() != 0){
					assembly += "\n.section .ctors";//.section .ctors
					assembly += "\n\t.align 8";//	.align 8
					assembly += "\n\t.quad " + "_I_init_" + mngName.substring(5);//	.align 8
					assembly += "\n";
				}
				assembly += "\n\t.bss";//	.bss
				assembly += "\n\t.align 8";//	.align 8
				IRCompUnit.addGlobal(assembly);
			}
		}
		return null;
	}
}
