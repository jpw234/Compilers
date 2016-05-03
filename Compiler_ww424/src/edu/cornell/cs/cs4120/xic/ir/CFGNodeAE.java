package edu.cornell.cs.cs4120.xic.ir;

import java.util.HashSet;
import java.util.List;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import edu.cornell.cs.cs4120.util.SExpPrinter;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;


//node for available expression in control flow graph
public class CFGNodeAE {
	private List<IRStmt> predecessor = null;
	private List<IRStmt> successor = null;
	private HashSet<String> availExprs = null;
	
	public CFGNodeAE(HashSet<String> ae){
		availExprs = new HashSet<String>(ae);
		predecessor = new ArrayList<IRStmt>();
		successor = new ArrayList<IRStmt>();
	}
	
	public List<IRStmt> successor(){
		return successor;
	}
	
	public List<IRStmt> predecessor(){
		return predecessor;
	}
	
	public HashSet<String> availExprs(){
		return availExprs;
	}
	
	public boolean transferFunction(IRStmt s){
		HashSet<String> in;
		//meet operation
		if(predecessor.size() == 0) {in = new HashSet<String>(availExprs);}
		else if(predecessor.size() == 1) {in = new HashSet<String>(predecessor.get(0).nodeAE().availExprs());}
		else { //size() >= 2
			in = new HashSet<String>();
			HashSet<String> pre0 = predecessor.get(0).nodeAE().availExprs();
			HashSet<String> pre1 = predecessor.get(1).nodeAE().availExprs();
			for(String str : pre0){
				if(pre1.contains(str)){in.add(new String(str));}
			}
		}
		//flow function Fn()
		//compute exprs(n)
		String selfExprPrint = "";
		if(s instanceof IRMove && !(((IRMove)s).expr() instanceof IRCall) && !(((IRMove)s).expr() instanceof IRTemp)){
			StringWriter sw = new StringWriter();
	        try (PrintWriter pw = new PrintWriter(sw);
	             SExpPrinter sp = new CodeWriterSExpPrinter(pw)) {
	        	((IRMove)s).expr().printSExp(sp);
	        }
	        selfExprPrint = new String(sw.toString());
		}
		else if(s instanceof IRCJump && !(((IRCJump)s).expr() instanceof IRCall) && !(((IRCJump)s).expr() instanceof IRTemp)){
			StringWriter sw = new StringWriter();
	        try (PrintWriter pw = new PrintWriter(sw);
	             SExpPrinter sp = new CodeWriterSExpPrinter(pw)) {
	        	((IRCJump)s).expr().printSExp(sp);
	        }
	        selfExprPrint = new String(sw.toString());
		}
		//union with exprs(n)
		if(!(in.contains(selfExprPrint))){in.add(selfExprPrint);}
		//kill(n)
		if(s instanceof IRMove){
			StringWriter sw = new StringWriter();
	        try (PrintWriter pw = new PrintWriter(sw);
	             SExpPrinter sp = new CodeWriterSExpPrinter(pw)) {
	        	((IRMove)s).target().printSExp(sp);
	        }
	        String targetStr = new String(sw.toString());
	        for (Iterator<String> it = in.iterator(); it.hasNext();) {
	            String element = it.next();
	            if (element.contains(targetStr)) {
	                it.remove();
	            }
	        }
		}
		//check isChanged()
		boolean isChanged = checkChanged(in, availExprs);
		if(isChanged) {availExprs = new HashSet<String>(in);}
		return isChanged;
	}
	
	private boolean checkChanged(HashSet<String> outNew, HashSet<String> outOld){
		boolean isChanged = false;
		for(String s : outNew){
			if(!(outOld.contains(s))){
				isChanged = true;
				break;
			}
		}
		if(isChanged == false){
			for(String s : outOld){
				if(!(outNew.contains(s))){
					isChanged = true;
					break;
				}
			}
		}
		return isChanged;
	}
}
