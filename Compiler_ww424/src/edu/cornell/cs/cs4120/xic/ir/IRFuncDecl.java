package edu.cornell.cs.cs4120.xic.ir;

import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import compiler_ww424.LabelMaker;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import edu.cornell.cs.cs4120.xic.ir.IRBinOp.OpType;
import edu.cornell.cs.cs4120.xic.ir.visit.AggregateVisitor;
import edu.cornell.cs.cs4120.xic.ir.visit.IRVisitor;
import edu.cornell.cs.cs4120.xic.ir.visit.InsnMapsBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/** An IR function declaration */
public class IRFuncDecl extends IRNode {
	private String name;
	private IRStmt body;

	public IRFuncDecl(String name, IRStmt stmt) {
		this.name = name;
		body = stmt;
	}

	public String name() {
		return name;
	}

	public IRStmt body() {
		return body;
	}

	@Override
	public String label() {
		return "FUNC " + name;
	}

	@Override
	public IRNode visitChildren(IRVisitor v) {
		IRStmt stmt = (IRStmt) v.visit(this, body);

		if (stmt != body) return new IRFuncDecl(name, stmt);

		return this;
	}

	@Override
	public <T> T aggregateChildren(AggregateVisitor<T> v) {
		T result = v.unit();
		result = v.bind(result, v.visit(body));
		return result;
	}

	@Override
	public InsnMapsBuilder buildInsnMapsEnter(InsnMapsBuilder v) {
		v.addNameToCurrentIndex(name);
		v.addInsn(this);
		return v;
	}

	@Override
	public IRNode buildInsnMaps(InsnMapsBuilder v) {
		return this;
	}

	@Override
	public void printSExp(SExpPrinter p) {
		p.startList();
		p.printAtom("FUNC");
		p.printAtom(name);
		body.printSExp(p);
		p.endList();
	}

	public void IRLower(){
		IRStmt stmtLow = body.IRLower();
		List<IRStmt> listLow = unwrapIRStmt(stmtLow);
		body = new IRSeq(listLow);

	}
	public static List<IRStmt>  constantPropagation(List<IRStmt> IRlists){
		List<List<IRStmt>> boxes = boxAssign(IRlists);
		for (int b=0; b < boxes.size(); b++){
			List<IRStmt> box = boxes.get(b);
			HashMap<String, Long> constantmap = new HashMap<String, Long>();
			for (int i=0; i < box.size(); i++){
				IRStmt s = box.get(i);
				if (s instanceof IRMove){
					IRExpr left = ((IRMove)s).target();
					IRExpr right= ((IRMove)s).expr();
					if (left instanceof IRTemp && right instanceof IRConst){
						String x = ((IRTemp)left).name();
						Long number = ((IRConst)right).value();
						if (!constantmap.containsKey(x)){
							constantmap.put(x, number);
						}
						else{
							constantmap.replace(x, number);
						}
						box = constantPropagationHelper(x,number,i,box);
						boxes.remove(b);
						boxes.add(b,box);
					}
				}
			}
		}
		List<IRStmt> ret = new ArrayList<IRStmt>();
		for (int b=0; b < boxes.size(); b++){
			for (int i = 0 ; i < boxes.get(b).size();i++){
				ret.add(boxes.get(b).get(i));
			}
		}
		return ret;
	}
	public static List<List<IRStmt>> boxAssign(List<IRStmt> IRlists){
		List<List<IRStmt>> ret = new ArrayList<List<IRStmt>>();
		List<IRStmt> curList = new ArrayList<IRStmt>();
		for(IRStmt s:IRlists){
			if ((s instanceof IRCJump) || (s instanceof IRLabel)|| (s instanceof IRReturn)){
				curList.add(s);
				ret.add(curList);
				curList = new ArrayList<IRStmt>();}
			else{
				curList.add(s);
			}
		}
		return ret;
	}
	public static List<IRStmt> constantPropagationHelper(String x, Long c,int i, List<IRStmt> stmts){
		for(int j = i+1 ; j < stmts.size(); j++){
			IRStmt s = stmts.get(j);
			if (s instanceof IRMove){
				IRExpr left = ((IRMove)s).target();
				IRExpr right= ((IRMove)s).expr();
				//REASSIGN
				if (left instanceof IRTemp && right instanceof IRConst){
					if (((IRTemp)left).name().equals(x)   &&  ((IRConst)right).value()== c){
						stmts.remove(j);        
					}
					else if (((IRTemp)left).name().equals(x)   &&  ((IRConst)right).value() != c){
						return stmts;       
					}
				}
				else if (left instanceof IRTemp && right instanceof IRTemp 
						&& ((IRTemp)right).name().equals(x) ){
					stmts.remove(j);
					stmts.add(j, new IRMove(left, new IRConst(c)));
				}
				else if (left instanceof IRTemp && right instanceof IRBinOp){
					if(!((IRTemp)left).name().equals(x)){
						OpType type = ((IRBinOp)right).opType();
						IRExpr binleft = ((IRBinOp)right).left();
						IRExpr binright= ((IRBinOp)right).right();
						if ((binleft instanceof IRTemp && ((IRTemp)binleft).name().equals(x))){
							IRStmt t = stmts.remove(j);
							stmts.add(j, new IRMove(left, new IRBinOp(type, new IRConst(c),
									((IRBinOp)(((IRMove)t).expr())).right())));
						}
						if (binright instanceof IRTemp && ((IRTemp)binright).name().equals(x))  {
							IRStmt t = stmts.remove(j);
							stmts.add(j, new IRMove(left, 
									new IRBinOp(type, 
											((IRBinOp)(((IRMove)t).expr())).left(),
											new IRConst(c))));
						}
					}
				}
			}
			//(CJUMP (XOR (EQ (TEMP x) (TEMP n)) (CONST 1)) endif1)
			else if (s instanceof IRCJump && ((IRCJump)s).expr() instanceof IRBinOp){
				if (((IRBinOp)((IRCJump)s).expr()).left() instanceof IRBinOp){
					IRExpr templ = ((IRBinOp)((IRCJump)s).expr()).left() ;
					IRExpr tempr = ((IRBinOp)((IRCJump)s).expr()).right();
					if (((IRBinOp)templ).right() instanceof IRTemp && 
							((IRTemp)(((IRBinOp)templ).right() )).name().equals(x) ){
						IRStmt t = stmts.remove(j);
						stmts.add(j, new IRCJump(
								new IRBinOp( ((IRBinOp)((IRCJump)t).expr()).opType(),
										new IRBinOp(((IRBinOp)templ).opType(), ((IRBinOp)templ).left(), new IRConst(c)), 
										tempr   ),
								((IRCJump)t).trueLabel()
								));}
				}
			}
		}
		return stmts;
	}

	public static List<IRStmt> valueNumbering(List<IRStmt> IRlists){
		List<List<IRStmt>> boxes = boxAssign(IRlists);
		for (int b=0; b < boxes.size(); b++){
			List<IRStmt> box = boxes.get(b);
			HashMap<IRTemp,Long> hashmap = new HashMap<IRTemp,Long>();
			HashMap<Long,Integer> valuenumberingmap = new HashMap<Long,Integer>();
			Integer number = 0;
			for (int i=0; i < box.size(); i++){
				IRStmt s = box.get(i);

				if (s instanceof IRMove){
					IRExpr left = ((IRMove)s).target();
					IRExpr right= ((IRMove)s).expr();
					if (left instanceof IRTemp && right instanceof IRConst){
						if (hashmap.containsKey(left)){
							hashmap.replace((IRTemp)left, ((IRConst)right).value());
						}
						else{
							hashmap.put((IRTemp)left, ((IRConst)right).value());
						}
					}
					else if (left instanceof IRTemp && right instanceof IRBinOp){
						OpType type = ((IRBinOp)right).opType();
						IRExpr binopLeft = ((IRBinOp)right).left();
						IRExpr binopRight= ((IRBinOp)right).right();
						Long value = hashnumber(type,binopLeft,binopRight,hashmap);
						if(!hashmap.containsKey((IRTemp)left)){
							hashmap.put((IRTemp)left, value);
						}else{
							hashmap.replace((IRTemp)left, value);
						}
						if (!valuenumberingmap.containsKey(value)){
							valuenumberingmap.put(value, number);
							number += 1; 
							box = valueNumberingHelper((IRTemp)left,value,i,box,hashmap);
							boxes.remove(b);
							boxes.add(b,box);}
					}
				}
			}
		}
		List<IRStmt> ret = new ArrayList<IRStmt>();
		for (int b=0; b < boxes.size(); b++){
			for (int i = 0 ; i < boxes.get(b).size();i++){
				ret.add(boxes.get(b).get(i));
			}
		}
		return ret;
	}
	public static List<IRStmt> valueNumberingHelper(IRTemp n,Long value, int index,
			List<IRStmt> lists,HashMap<IRTemp,Long> hashmap){
		//TODO
		for(int i = index+1 ; i < lists.size(); i++){
			IRStmt s = lists.get(i);
			if (s instanceof IRMove){
				IRExpr left = ((IRMove)s).target();
				IRExpr right= ((IRMove)s).expr();
				if (left instanceof IRTemp && right instanceof IRConst){
					//REASSIGN
					if((((IRTemp)left).name()).equals(n.name()) && ((Long)((IRConst)right).value()).equals(value)){
						lists.remove(i);
					}
					return lists;
				}
				else if (left instanceof IRTemp && right instanceof IRBinOp){
					//TODO
					IRExpr binleft = ((IRBinOp)right).left();
					IRExpr binright= ((IRBinOp)right).right();
					OpType optype = ((IRBinOp)right).opType();
					//REASSIGN
					if (((IRTemp)left).name().equals(n.name()) && 
							hashnumber(optype,binleft,binright,hashmap).equals(value) ){
						lists.remove(i);
					}
					else if (((IRTemp)left).name().equals(n.name()) && 
							!hashnumber(optype,binleft,binright,hashmap).equals(value)){
						return lists;
					}
					//REASSIGN
					else if (!((IRTemp)left).name().equals(n.name()) &&
							hashnumber(optype,binleft,binright,hashmap).equals(value)){
						lists.remove(i);
						lists.add(i, new IRMove(left, n));
					}
					/*else if(){

					}*/
				}
			}
		}
		return lists;

	}

	public static Long hashnumber(OpType type, IRExpr left, IRExpr right,HashMap<IRTemp,Long> map){
		Long value = (long) 0;
		switch(type){
		case ADD: //*10
			if (left instanceof IRTemp && right instanceof IRTemp){
				if(!map.containsKey(left) || !map.containsKey(right)) break;
				value = 10*map.get(((IRTemp)left)) * map.get((IRTemp)right) + 100;
			}
			else if (left instanceof IRConst && right instanceof IRTemp){
				if( !map.containsKey(right)) break;
				value = 10* ((IRConst)left).value() * map.get(right) + 100;
			}
			else if (left instanceof IRTemp && right instanceof IRConst){
				if( !map.containsKey(left)) break;
				value = 10*map.get(((IRTemp)left))* ((IRConst)right).value() + 100;
			}
			break;
		case MUL://*20
			if (left instanceof IRTemp && right instanceof IRTemp){
				if(!map.containsKey(left) || !map.containsKey(right)) break;
				value = 20*map.get(((IRTemp)left)) * map.get((IRTemp)right) + 200;
			}
			else if (left instanceof IRConst && right instanceof IRTemp){
				if( !map.containsKey(right)) break;
				value = 20* ((IRConst)left).value() * map.get((IRTemp)right) + 200;
			}
			else if (left instanceof IRTemp && right instanceof IRConst){
				if( !map.containsKey(left)) break;
				value = 20* map.get(((IRTemp)left))* ((IRConst)right).value() + 200;
			}
			break;
		case SUB://*30
			if (left instanceof IRTemp && right instanceof IRTemp){
				if(!map.containsKey(left) || !map.containsKey(right)) break;
				value = 30*map.get(((IRTemp)left)) * map.get((IRTemp)right) + 300;
			}
			else if (left instanceof IRConst && right instanceof IRTemp){
				if( !map.containsKey(right)) break;
				value = 30* ((IRConst)left).value() * map.get((IRTemp)right) + 300;
			}
			else if (left instanceof IRTemp && right instanceof IRConst){
				if( !map.containsKey(left)) break;
				value = 30* map.get(((IRTemp)left))* ((IRConst)right).value() + 300;
			}

			break;
		case DIV://*40
			if (left instanceof IRTemp && right instanceof IRTemp){
				if(!map.containsKey(left) || !map.containsKey(right)) break;
				value = 40*map.get(((IRTemp)left)) * map.get((IRTemp)right) + 400;
			}
			else if (left instanceof IRConst && right instanceof IRTemp){
				if( !map.containsKey(right)) break;
				value = 40* ((IRConst)left).value() * map.get((IRTemp)right) + 400;
			}
			else if (left instanceof IRTemp && right instanceof IRConst){
				if( !map.containsKey(left)) break;
				value = 40* map.get(((IRTemp)left))* ((IRConst)right).value() + 400;
			}

			break;
		case MOD://*50
			if (left instanceof IRTemp && right instanceof IRTemp){
				if(!map.containsKey(left) || !map.containsKey(right)) break;
				value = 50*map.get(((IRTemp)left)) * map.get((IRTemp)right) + 500;
			}
			else if (left instanceof IRConst && right instanceof IRTemp){
				if( !map.containsKey(right)) break;
				value = 50* ((IRConst)left).value() * map.get((IRTemp)right) + 500;
			}
			else if (left instanceof IRTemp && right instanceof IRConst){
				if( !map.containsKey(left)) break;
				value = 50* map.get(((IRTemp)left))* ((IRConst)right).value() + 500;
			}

			break;
		case HMUL:
			if (left instanceof IRTemp && right instanceof IRTemp){
				if(!map.containsKey(left) || !map.containsKey(right)) break;
				value = 60*map.get(((IRTemp)left)) * map.get((IRTemp)right) + 600;
			}
			else if (left instanceof IRConst && right instanceof IRTemp){
				if( !map.containsKey(right)) break;
				value = 60* ((IRConst)left).value() * map.get((IRTemp)right) + 600;
			}
			else if (left instanceof IRTemp && right instanceof IRConst){
				if( !map.containsKey(left)) break;
				value = 60* map.get(((IRTemp)left))* ((IRConst)right).value() + 600;
			}
			break;
		}

		return value;
	}

	public ArrayList<String> getTemps() {
		return body.getTemps();
	}

	public List<IRStmt> unwrapIRStmt(IRStmt s){
		List<IRStmt> unwrapS = new ArrayList<IRStmt>();
		for (IRStmt st:((IRSeq) s).stmts()){
			if(st instanceof IRSeq){
				List<IRStmt> tmp = unwrapIRStmt(st);
				for(IRStmt b:tmp){
					unwrapS.add(b);
				}
			}
			else{unwrapS.add(st);}
		}
		return unwrapS;
	}

	public int bestCost() {
		if(bestTile != null) return bestTile.getCost();
		else {
			bestTileNum = 0;
			bestCost = body.bestCost() + 2;
		}
		return bestCost;
	}

	public AssemInstr getBestTile() {
		if(bestTile != null) return bestTile;
		else {
			StackAssigner.clear();
			this.bestCost();
			switch(bestTileNum) {
			case 0: {//mintile
				AssemInstr child = body.getBestTile();
				String data = "\n.global " + name + "\n"+
						".align 4\n" + 
						name + ": \n"+
						"pushq %rbp \n"+
						"movq %rsp, %rbp";
				data += child.getData();
				bestTile = new AssemInstr(data, "", child.getCost() + 2);
			}
			}
		}
		return bestTile;
	}
	
    //Optimization : common subexpression elimination
    public void CSE(){
    	HashMap<String, Integer> labelPos = new HashMap<String, Integer>();
    	HashSet<String> allExprs = new HashSet<String>();
    	HashMap<String, List<IRStmt>> allExprsMap = new HashMap<String, List<IRStmt>>();
    	//first pass: record Label position & compute all exprs
    	for(int i = 0; i < ((IRSeq)body).stmts().size(); i++){
    		IRStmt s = ((IRSeq)body).stmts().get(i);
    		if(s instanceof IRLabel){
    			labelPos.put( ((IRLabel)(s)).name(), i);
    		}
    		else if(s instanceof IRMove && 
    				!(((IRMove)s).expr() instanceof IRCall) && 
    				!(((IRMove)s).expr() instanceof IRTemp) && 
    				!(((IRMove)s).expr() instanceof IRConst)){
    			StringWriter sw = new StringWriter();
    	        try (PrintWriter pw = new PrintWriter(sw);
    	             SExpPrinter sp = new CodeWriterSExpPrinter(pw)) {
    	        	((IRMove)s).expr().printSExp(sp);
    	        }
    	        String exprTmp = sw.toString().trim();
    	        allExprs.add(exprTmp);
    	        if(allExprsMap.containsKey(exprTmp)){
    	        	allExprsMap.get(exprTmp).add(s);
    	        }
    	        else {
    	        	List<IRStmt> tmp = new ArrayList<IRStmt>();
    	        	tmp.add(s);
    	        	allExprsMap.put(exprTmp, tmp);
    	        }
    		}
    		else if(s instanceof IRCJump && 
    				!(((IRCJump)s).expr() instanceof IRCall) && 
    				!(((IRCJump)s).expr() instanceof IRTemp)){
    			StringWriter sw = new StringWriter();
    	        try (PrintWriter pw = new PrintWriter(sw);
    	             SExpPrinter sp = new CodeWriterSExpPrinter(pw)) {
    	        	((IRCJump)s).expr().printSExp(sp);
    	        }
    	        String exprTmp = sw.toString().trim();
    	        allExprs.add(exprTmp);
    	        if(allExprsMap.containsKey(exprTmp)){
    	        	allExprsMap.get(exprTmp).add(s);
    	        }
    	        else {
    	        	List<IRStmt> tmp = new ArrayList<IRStmt>();
    	        	tmp.add(s);
    	        	allExprsMap.put(exprTmp, tmp);
    	        }
    		}
    	}
    	//DEBUG
//    	System.out.println("Size of allExprsMap = " + allExprsMap.size());
//    	System.out.println("Size of allExprs = " + allExprs.size());
//    	for(Map.Entry<String, List<IRStmt>> entry : allExprsMap.entrySet()){
//    		System.out.print(entry.getKey() + ": ");
//    		for(IRStmt ss : entry.getValue()){
//    			System.out.print(((IRSeq)body).stmts().indexOf(ss) + " ");
//    		}
//    		System.out.print("\n");
//    	}
    	//build graph(define predecessor & successor)
    	for(int i = 0; i < ((IRSeq)body).stmts().size(); i++){
    		IRStmt s = ((IRSeq)body).stmts().get(i);
    		if(s.nodeAE() == null) {s.nodeAE_init(allExprs);}
    	}
    	for(int i = 0; i < ((IRSeq)body).stmts().size(); i++){
    		IRStmt s = ((IRSeq)body).stmts().get(i);
    		if(!(s instanceof IRReturn)){//if not IRReturn, must have successor
    			if(s instanceof IRJump){
    				String jmpLabel = ((IRName)(((IRJump)s).target())).name();
    				int sucIdx = -1;//successor index
    				if(labelPos.containsKey(jmpLabel)){sucIdx = labelPos.get(jmpLabel);}
    				else {System.out.println("jmpLabel not found in labelPos!");}
    				s.nodeAE().successor().add(((IRSeq)body).stmts().get(sucIdx));//successor
    				((IRSeq)body).stmts().get(sucIdx).nodeAE().predecessor().add(s);//predecessor
    			}
    			else if(s instanceof IRCJump){
    				String cjmpLabel = ((IRCJump)s).trueLabel();
    				int sucIdx = -1;//successor index
    				if(labelPos.containsKey(cjmpLabel)){sucIdx = labelPos.get(cjmpLabel);}
    				else {System.out.println("jmpLabel not found in labelPos!");}
    				s.nodeAE().successor().add(((IRSeq)body).stmts().get(sucIdx));//successor
    				((IRSeq)body).stmts().get(sucIdx).nodeAE().predecessor().add(s);//predecessor
    				if(sucIdx != i+1){
    					s.nodeAE().successor().add(((IRSeq)body).stmts().get(i+1));//successor
        				((IRSeq)body).stmts().get(i+1).nodeAE().predecessor().add(s);//predecessor
    				}
    			}
    			else {
    				s.nodeAE().successor().add(((IRSeq)body).stmts().get(i+1));//successor
    				((IRSeq)body).stmts().get(i+1).nodeAE().predecessor().add(s);//predecessor
    			}
    		}
    	}
    	//DEBUG
//    	System.out.println("Graph : ");
//    	List<IRStmt> funcStmtList = ((IRSeq)body).stmts();
//    	for(int i = 0; i < funcStmtList.size(); i++){
//    		System.out.print(i + "th stmt --> ");
//    		System.out.print("pre: ");
//    		for(int j = 0; j < funcStmtList.get(i).nodeAE().predecessor().size(); j++){
//    			System.out.print(funcStmtList.indexOf(funcStmtList.get(i).nodeAE().predecessor().get(j)) + " ");
//    		}
//    		System.out.print("suc: ");
//    		for(int j = 0; j < funcStmtList.get(i).nodeAE().successor().size(); j++){
//    			System.out.print(funcStmtList.indexOf(funcStmtList.get(i).nodeAE().successor().get(j)) + " ");
//    		}
//    		System.out.print("\n");
//    	}
    	//data flow analysis : worklist algorithm for available expression
    	ArrayDeque<IRStmt> irFIFO = new ArrayDeque<IRStmt>();
    	//initially add all nodes
    	for(int i = 0; i < ((IRSeq)body).stmts().size(); i++){
    		irFIFO.addLast(((IRSeq)body).stmts().get(i));
    	}
    	//run until no change
    	while(!irFIFO.isEmpty()){
    		IRStmt s = irFIFO.removeFirst();
    		boolean isChanged = s.nodeAE().transferFunction(s);
    		if(isChanged){
    			if(s.nodeAE().successor().size() == 1){
    				IRStmt suc0 = s.nodeAE().successor().get(0);
    				if(!irFIFO.contains(suc0)) {irFIFO.addLast(suc0);}
    			}
    			else if(s.nodeAE().successor().size() == 2){
    				IRStmt suc0 = s.nodeAE().successor().get(0);
    				IRStmt suc1 = s.nodeAE().successor().get(1);
    				if(!irFIFO.contains(suc0)) {irFIFO.addLast(suc0);}
    				if(!irFIFO.contains(suc1)) {irFIFO.addLast(suc1);}
    			}
    		}
    	}
    	//DEBUG
//    	System.out.println("Available Expression : ");
//    	for(int i = 0; i < funcStmtList.size(); i++){
//    		System.out.print(i + "th stmt --> ");
//    		for(String ss : funcStmtList.get(i).nodeAE().availExprs()){
//    			System.out.print(ss + " , ");
//    		}
//    		System.out.print("\n");
//    	}
    	//DFS to validate common subexpression
    	for(String str : allExprsMap.keySet()){
    		if(allExprsMap.get(str).size() >= 2){
    			int n = allExprsMap.get(str).size();
    			boolean isFirst = true;
    			String _cse = "";
    			for(int i = 0; i < n-1; i++){
    				if(DFS_validCommonExpr(allExprsMap.get(str).get(i+1), allExprsMap.get(str).get(i), str)){
    					if(isFirst){
    						_cse = LabelMaker.Generate_Unique_Label("_CSETemp");
    						isFirst = false;
    						int idx = ((IRSeq)body).stmts().indexOf(allExprsMap.get(str).get(i));
    						IRExpr commonExpr = (allExprsMap.get(str).get(i) instanceof IRMove)? ((IRMove)allExprsMap.get(str).get(i)).expr(): 
    							((IRCJump)allExprsMap.get(str).get(i)).expr();
    						((IRSeq)body).stmts().add(idx, new IRMove(new IRTemp(_cse), commonExpr));
    					}
    					if(allExprsMap.get(str).get(i) instanceof IRMove){((IRMove)allExprsMap.get(str).get(i)).CSE_modifyExpr(new IRTemp(_cse));}
    					else{((IRCJump)allExprsMap.get(str).get(i)).CSE_modifyExpr(new IRTemp(_cse));}
    					if(allExprsMap.get(str).get(i+1) instanceof IRMove){((IRMove)allExprsMap.get(str).get(i+1)).CSE_modifyExpr(new IRTemp(_cse));}
    					else{((IRCJump)allExprsMap.get(str).get(i+1)).CSE_modifyExpr(new IRTemp(_cse));}
    				}
    			}
    		}
    	}
    }
    
    boolean DFS_validCommonExpr(IRStmt start, IRStmt end, String conmmonExpr){//since backtrace, start node is successor, end node is predecessor
    	if(!(start.nodeAE().availExprs().contains(conmmonExpr))) {return false;}
    	else if(start == end) {return true;}
    	else if(start.nodeAE().predecessor().size() == 0){return false;}
    	boolean isCommon = true;
    	for(IRStmt s : start.nodeAE().predecessor()){
    		isCommon = (isCommon && DFS_validCommonExpr(s, end, conmmonExpr));
    		if(isCommon == false) {break;}
    	}
    	return isCommon;
    }
    
    public void drawCFG(FileWriter fw , int nodeNumber) throws IOException {
		//FileWriter fw = new FileWriter(fileName);
		HashMap<String, Integer> labelPos = new HashMap<String, Integer>();
		HashSet<String> allExprs = new HashSet<String>();
		//first pass: record Label position & compute all exprs
		for(int i = 0; i < ((IRSeq)body).stmts().size(); i++){
			IRStmt s = ((IRSeq)body).stmts().get(i);
			if(s instanceof IRLabel){
				labelPos.put( ((IRLabel)(s)).name(), i);
			}
			else if(s instanceof IRMove && 
					!(((IRMove)s).expr() instanceof IRCall) && 
					!(((IRMove)s).expr() instanceof IRTemp)){
				StringWriter sw = new StringWriter();
				try (PrintWriter pw = new PrintWriter(sw);
						SExpPrinter sp = new CodeWriterSExpPrinter(pw)) {
					((IRMove)s).expr().printSExp(sp);
				}
				allExprs.add(sw.toString());
			}
			else if(s instanceof IRCJump && 
					!(((IRCJump)s).expr() instanceof IRCall) && 
					!(((IRCJump)s).expr() instanceof IRTemp)){
				StringWriter sw = new StringWriter();
				try (PrintWriter pw = new PrintWriter(sw);
						SExpPrinter sp = new CodeWriterSExpPrinter(pw)) {
					((IRCJump)s).expr().printSExp(sp);
				}
				allExprs.add(sw.toString());
			}
		}
		//build graph(define predecessor & successor)
		for(int i = 0; i < ((IRSeq)body).stmts().size(); i++){
			IRStmt s = ((IRSeq)body).stmts().get(i);
			if(s.nodeAE() == null) {s.nodeAE_init(allExprs);}
		}
		for(int i = 0; i < ((IRSeq)body).stmts().size(); i++){
			IRStmt s = ((IRSeq)body).stmts().get(i);
			if(!(s instanceof IRReturn)){//if not IRReturn, must have successor
				if(s instanceof IRJump){
					String jmpLabel = ((IRName)(((IRJump)s).target())).name();
					int sucIdx = -1;//successor index
					if(labelPos.containsKey(jmpLabel)){sucIdx = labelPos.get(jmpLabel);}
					else {System.out.println("jmpLabel not found in labelPos!");}
					s.nodeAE().successor().add(((IRSeq)body).stmts().get(sucIdx));//successor
					((IRSeq)body).stmts().get(sucIdx).nodeAE().predecessor().add(s);//predecessor
				}
				else if(s instanceof IRCJump){
					String cjmpLabel = ((IRCJump)s).trueLabel();
					int sucIdx = -1;//successor index
					if(labelPos.containsKey(cjmpLabel)){sucIdx = labelPos.get(cjmpLabel);}
					else {System.out.println("jmpLabel not found in labelPos!");}
					s.nodeAE().successor().add(((IRSeq)body).stmts().get(sucIdx));//successor
					((IRSeq)body).stmts().get(sucIdx).nodeAE().predecessor().add(s);//predecessor
					if(sucIdx != i+1){
						s.nodeAE().successor().add(((IRSeq)body).stmts().get(i+1));//successor
						((IRSeq)body).stmts().get(i+1).nodeAE().predecessor().add(s);//predecessor
					}
				}
				else {
					s.nodeAE().successor().add(((IRSeq)body).stmts().get(i+1));//successor
					((IRSeq)body).stmts().get(i+1).nodeAE().predecessor().add(s);//predecessor
				}
			}
		}
	
		HashMap<Integer, ArrayList<Integer>> edges = new HashMap<Integer, ArrayList<Integer>>();		
		//fw.write("digraph CFG {\n");
		//fw.write(" \"\" [shape = none] \n");
		for(int i = 0; i < ((IRSeq)body).stmts().size(); i++){
			IRStmt s = ((IRSeq)body).stmts().get(i);
			try{
				if (s.nodeAE() == null ){s.nodeAE_init(allExprs);} 
				CFGNodeAE curNode = s.nodeAE();
				for(int j = 0 ; j < curNode.successor().size();j++){
					String curNodeS = printStmt(curNode.successor().get(j));
					int num = nodeNumber; 
					if (curNodeS.length() >2) {
						nodeNumber += 1;
						curNodeS = curNodeS.substring(0, curNodeS.length()-1);
						String append = ""+nodeNumber + " [label=\" " +curNodeS +"\"]\n";
						if (edges.containsKey(num)){
							edges.get(num).add(nodeNumber);
						}else{
							ArrayList<Integer> temp = new ArrayList<Integer>();
							temp.add(nodeNumber);
							edges.put(num, temp);
						}
						fw.write(append);
					}
				}
			}
			catch(Error e) {
				System.out.println(e.getMessage());
				fw.write(e.getMessage()+"\r\n");
			}
		}
		fw.write("\"\" -> 1");
		for (Integer a : edges.keySet()){
			for (int j = 0 ; j < edges.get(a).size();j++){
				fw.write(""+a+"->"+edges.get(a).get(j)+"\n");
			}
		}
		fw.write("}");
		//fw.close();

	}

	public String printStmt(IRStmt s){
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
		return selfExprPrint;
	}
}
