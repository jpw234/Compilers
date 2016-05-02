package edu.cornell.cs.cs4120.xic.ir;

import java.util.ArrayList;
import java.util.List;

import edu.cornell.cs.cs4120.util.SExpPrinter;
import edu.cornell.cs.cs4120.xic.ir.visit.AggregateVisitor;
import edu.cornell.cs.cs4120.xic.ir.visit.IRVisitor;
import edu.cornell.cs.cs4120.xic.ir.visit.InsnMapsBuilder;

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
                                ));
                    }
                }
            }

        }
        return stmts;
    }

    public static void valueNumbering(List<IRStmt> IRlists){
        List<List<IRStmt>> boxes = boxAssign(IRlists);
        for (int b=0; b < boxes.size(); b++){
            List<IRStmt> box = boxes.get(b);
            HashMap<IRTemp,Long> hashmap = new HashMap<IRTemp,Long>();
            HashMap<Long,Integer> valuenumberingmap = new HashMap<Long,Integer>();
            int number = 0;
            for (int i=0; i < box.size(); i++){
                IRStmt s = box.get(i);
                if (s instanceof IRMove){
                    IRExpr left = ((IRMove)s).target();
                    IRExpr right= ((IRMove)s).expr();
                    if (left instanceof IRTemp && right instanceof IRConst){
                        Long value = ((IRConst)right).value();
                        //valuenumbering map
                        if (!valuenumberingmap.containsKey(value)){
                            valuenumberingmap.put(value, number);
                            number += 1; }
                        else{
                            //TODO
                            List<IRStmt> temp = new ArrayList<IRStmt>();
                            for(int j = i+1; j<box.size();j++){temp.add(box.get(j));}
                            valueNumberingHelper((IRTemp)left,value,temp,hashmap);
                            for(int j = i+1; j<box.size();j++){box.remove(j);}
                            for(int j = 0; j<temp.size();j++){box.addAll(temp);}
                        }
                        //For hashmap
                        if(!hashmap.containsKey((IRTemp)left)){
                            hashmap.put((IRTemp)left, value);
                        }else{
                            hashmap.replace((IRTemp)left, value);
                        }
                    }
                    else if (left instanceof IRTemp && right instanceof IRBinOp){
                        OpType type = ((IRBinOp)right).opType();
                        IRExpr binopLeft = ((IRBinOp)right).left();
                        IRExpr binopRight= ((IRBinOp)right).right();
                        Long value = hashnumber(type,binopLeft,binopRight,hashmap);
                        if (!valuenumberingmap.containsKey(value)){
                            valuenumberingmap.put(value, number);
                            number += 1; }
                        else{
                            //TODO
                            List<IRStmt> temp = new ArrayList<IRStmt>();
                            for(int j = i+1; j<box.size();j++){temp.add(box.get(j));}
                            valueNumberingHelper((IRTemp)left,value, temp,hashmap);
                            for(int j = i+1; j<box.size();j++){box.remove(j);}
                            for(int j = 0; j<temp.size();j++){box.addAll(temp);}
                        }
                        //For hashmap
                        if(!hashmap.containsKey((IRTemp)left)){
                            hashmap.put((IRTemp)left, value);
                        }else{
                            hashmap.replace((IRTemp)left, value);
                        }
                    }
                }
            }
        }
    }
    public static void valueNumberingHelper(IRTemp n,Long value, List<IRStmt> lists,HashMap<IRTemp,Long> hashmap){
        //TODO
        for(int i = 0 ; i < lists.size(); i++){
            IRStmt s = lists.get(i);
            if (s instanceof IRMove){
                IRExpr left = ((IRMove)s).target();
                IRExpr right= ((IRMove)s).expr();
                //REASSIGN
                if (left instanceof IRTemp && right instanceof IRConst){
                    //TODO
                    if (((IRConst)right).value()== value) {
                        lists.remove(i);
                        lists.add(i, new IRMove(n, new IRConst(value)));
                    }
                }
                else if (left instanceof IRTemp && right instanceof IRBinOp){
                    //TODO
                    IRExpr binleft = ((IRBinOp)right).left();
                    IRExpr binright= ((IRBinOp)right).right();
                    OpType optype = ((IRBinOp)right).opType();
                    if (hashnumber(optype,binleft,binright,hashmap) == value){
                        lists.remove(i);
                        lists.add(i, new IRMove((IRTemp)left, n));
                    }
                }

            }
        }
    }

    public static Long hashnumber(OpType type, IRExpr left, IRExpr right,HashMap<IRTemp,Long> map){
        Long value = (long) 0;
        switch(type){
        case ADD: //*10
            if (left instanceof IRTemp && right instanceof IRTemp){
                value = 10*map.get(((IRTemp)left)) * map.get((IRTemp)right) + 100;
            }
            else if (left instanceof IRConst && right instanceof IRTemp){
                value = 10* ((IRConst)left).value() * map.get((IRTemp)right) + 100;
            }
            else if (left instanceof IRTemp && right instanceof IRConst){
                value = 10* map.get(((IRTemp)left))* ((IRConst)right).value() + 100;
            }
            else if (left instanceof IRConst && right instanceof IRConst){
                value = 10* ((IRConst)left).value() * ((IRConst)right).value() + 100;
            }
            break;
        case MUL://*20
            if (left instanceof IRTemp && right instanceof IRTemp){
                value = 20*map.get(((IRTemp)left)) * map.get((IRTemp)right) + 200;
            }
            else if (left instanceof IRConst && right instanceof IRTemp){
                value = 20* ((IRConst)left).value() * map.get((IRTemp)right) + 200;
            }
            else if (left instanceof IRTemp && right instanceof IRConst){
                value = 20* map.get(((IRTemp)left))* ((IRConst)right).value() + 200;
            }
            else if (left instanceof IRConst && right instanceof IRConst){
                value = 20* ((IRConst)left).value() * ((IRConst)right).value() + 200;
            }
            break;
        case SUB://*30
            if (left instanceof IRTemp && right instanceof IRTemp){
                value = 30*map.get(((IRTemp)left)) * map.get((IRTemp)right) + 300;
            }
            else if (left instanceof IRConst && right instanceof IRTemp){
                value = 30* ((IRConst)left).value() * map.get((IRTemp)right) + 300;
            }
            else if (left instanceof IRTemp && right instanceof IRConst){
                value = 30* map.get(((IRTemp)left))* ((IRConst)right).value() + 300;
            }
            else if (left instanceof IRConst && right instanceof IRConst){
                value = 30* ((IRConst)left).value() * ((IRConst)right).value() + 300;
            }
            break;
        case DIV://*40
            if (left instanceof IRTemp && right instanceof IRTemp){
                value = 40*map.get(((IRTemp)left)) * map.get((IRTemp)right) + 400;
            }
            else if (left instanceof IRConst && right instanceof IRTemp){
                value = 40* ((IRConst)left).value() * map.get((IRTemp)right) + 400;
            }
            else if (left instanceof IRTemp && right instanceof IRConst){
                value = 40* map.get(((IRTemp)left))* ((IRConst)right).value() + 400;
            }
            else if (left instanceof IRConst && right instanceof IRConst){
                value = 40* ((IRConst)left).value() * ((IRConst)right).value() + 400;
            }
            break;
        case MOD://*50
            if (left instanceof IRTemp && right instanceof IRTemp){
                value = 50*map.get(((IRTemp)left)) * map.get((IRTemp)right) + 500;
            }
            else if (left instanceof IRConst && right instanceof IRTemp){
                value = 50* ((IRConst)left).value() * map.get((IRTemp)right) + 500;
            }
            else if (left instanceof IRTemp && right instanceof IRConst){
                value = 50* map.get(((IRTemp)left))* ((IRConst)right).value() + 500;
            }
            else if (left instanceof IRConst && right instanceof IRConst){
                value = 50* ((IRConst)left).value() * ((IRConst)right).value() + 500;
            }
            break;
        case HMUL:
            if (left instanceof IRTemp && right instanceof IRTemp){
                value = 60*map.get(((IRTemp)left)) * map.get((IRTemp)right) + 600;
            }
            else if (left instanceof IRConst && right instanceof IRTemp){
                value = 60* ((IRConst)left).value() * map.get((IRTemp)right) + 600;
            }
            else if (left instanceof IRTemp && right instanceof IRConst){
                value = 60* map.get(((IRTemp)left))* ((IRConst)right).value() + 600;
            }
            else if (left instanceof IRConst && right instanceof IRConst){
                value = 60* ((IRConst)left).value() * ((IRConst)right).value() + 600;
            }
            break;
        }

        return value;
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
}
