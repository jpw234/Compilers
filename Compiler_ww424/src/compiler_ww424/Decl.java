package compiler_ww424;

import java.util.ArrayList;
import java.util.List;

import edu.cornell.cs.cs4120.xic.ir.*;

public class Decl extends Stmt {
	private IDExpr name;
	private Type type;
	private List<Expr> accesses = null;
	private boolean isGlobal = false;
	
	public Decl(IDExpr n, Type t,int lineNum,int colNum) {
		name = n;
		type = t;
		line = lineNum;
		column = colNum;
	}
	
	public Decl(IDExpr n, Type t, List<Expr> a, int lineNum, int colNum) {
		name = n;
		type = t;
		accesses = a;
		line = lineNum;
		column = colNum;
	}
	
	public Decl(IDExpr n, Decl d, int lineNum, int colNum){
		name = n;
		type = new Type(d.getType().getType(), d.getType().getDepth());
		accesses = new ArrayList<Expr>();
		for(Expr e : d.getAccesses()){accesses.add(e);}// not deep copy, watch out!!!
		line = lineNum;
		column = colNum;
	}
	
	public void setID (IDExpr n){
		name = n ;
	}
	public IDExpr getName() {
		return name;
	}
	
	public Type getType() {
		return type;
	}
	
	public void addAccess(Expr a) {
		accesses.add(a);
	}
	
	public List<Expr> getAccesses() {
		return accesses;
	}
	
	public void setGlobal(boolean v) {
		isGlobal = v;
	}
	
	public boolean isGlobal() {
		return isGlobal;
	}
	
	public Type typecheck(SymTab s) {
		try {
			if(s.lookup(name.getName()) != null) throw new Error("redeclared variable error");
		}
		catch(Error e) {
			if(e.getMessage() == "Semantic Error: var does not exist") {
				//do nothing
			}
			else throw new Error(line + ":" + column + " error: " + e.getMessage());
		}
			
		if(type.getType() != "underscore") s.add(name.getName(), type);
		
		Type dummyType = new Type("int");
		
		if(accesses != null) {
			for(int a = 0; a < accesses.size(); a++) {
				if(!dummyType.equals(accesses.get(a).typecheck(s))) {
					throw new Error(line + ":" + column + " error: " + "non-integer expression used as array access");
				}
			}
		}
		
		return new Type("unit");
	}
	
	@Override
	public void constantFold() {
		if(accesses != null) {
			for(int a = 0; a < accesses.size(); a++) {
				accesses.set(a, accesses.get(a).constantFold());
			}
		}
	}
	
	@Override
	public String toString(){
		String s = type.getType();
		if(type.getDepth() > 0) {//array type variable
			for(int i = type.getDepth()-1; i >= 0; i--){
				if (accesses == null) {
					s = "( [] " + s +")";
 				}
				else {
					if(i < accesses.size()) { s = "( [] " + s + " " + accesses.get(i).toString() + " )";}
					else {s = "( [] " + s + " )";}
				}

			}
		}
		s = "( " + name.toString() + " " + s + " )";
		return (type.getType()=="underscore")? "_" : s ;
	}
	
	@Override
	public IRStmt buildIRStmt() {
		if(type.getDepth() == 0) {return null;}//just normal declaration, e.g. x:int
		else{ //array declaration
			if(accesses.size() == 0) {return null;}
			List<IRStmt> seqList = new ArrayList<IRStmt>();
			//allocate memory for list of Expression
			String _tD = LabelMaker.Generate_Unique_Label("_totalDim");
			IRTemp totalDim = new IRTemp(_tD);
			String _eI = LabelMaker.Generate_Unique_Label("_exprIdx");
			IRTemp exprIdx = new IRTemp(_eI);
			String _tmp = LabelMaker.Generate_Unique_Label("_tmp");
			IRTemp tmp = new IRTemp(_tmp);// temp register
			seqList.add(new IRMove(new IRTemp(_tD), new IRConst(accesses.size())));
			seqList.add(new IRMove(new IRTemp(_eI), 
						new IRCall(
								new IRName("_I_alloc_i"), new IRBinOp(IRBinOp.OpType.MUL, new IRTemp(_tD), new IRConst(8)))));
			seqList.add(new IRMove(new IRTemp(_tmp), new IRTemp(_eI)));
			seqList.add(new IRMove(new IRMem(new IRTemp(_eI)), accesses.get(0).buildIRExpr()));//exprIdx[i] = accesses[i]
			for(int i = 1; i < accesses.size(); i++){
				seqList.add(new IRMove(new IRTemp(_eI), new IRBinOp(IRBinOp.OpType.ADD, new IRTemp(_eI), new IRConst(8))));//exprIdx += 8
				seqList.add(new IRMove(new IRMem(new IRTemp(_eI)), accesses.get(i).buildIRExpr()));//exprIdx[i] = accesses[i]
			}
			seqList.add(new IRMove(new IRTemp(_eI), new IRTemp(_tmp)));//reset exprIdx pointer location
			//create registers
			String _pDN = LabelMaker.Generate_Unique_Label("_preDimNum");
			IRTemp preDimNum = new IRTemp(_pDN);//previous dimension number
			String _cDN = LabelMaker.Generate_Unique_Label("_curDimNum");
			IRTemp curDimNum = new IRTemp(_cDN);//current dimension number
			String _sL = LabelMaker.Generate_Unique_Label("_stLocation");
			IRTemp stLocation = new IRTemp(_sL);//starting location
			String _rF = LabelMaker.Generate_Unique_Label("_recurFlag");
			IRTemp recurFlag = new IRTemp(_rF);//recursive flag
			String _dI = LabelMaker.Generate_Unique_Label("_dimIdx");
			IRTemp dimIdx = new IRTemp(_dI);//dimension index
			String _lI = LabelMaker.Generate_Unique_Label("_loopIdx");
			IRTemp loopIdx = new IRTemp(_lI);//loop index
			String _slI = LabelMaker.Generate_Unique_Label("_subloopIdx");
			IRTemp subloopIdx = new IRTemp(_slI);//sub loop index
			String _alloc = LabelMaker.Generate_Unique_Label("_ALLOCATE");
			String _link = LabelMaker.Generate_Unique_Label("_LINK");
			String _linkLoop = LabelMaker.Generate_Unique_Label("_LINKLOOP");
			String _length = LabelMaker.Generate_Unique_Label("_LENGTH");//length assignment
			String _checkRecursion = LabelMaker.Generate_Unique_Label("_CHECKRECURSION");//check recursion
			String _checkEnd = LabelMaker.Generate_Unique_Label("_CHECKEND");//check end
			String _exitInit = LabelMaker.Generate_Unique_Label("_EXITINIT");//exit array initialization
			String _skipLenLoc = LabelMaker.Generate_Unique_Label("_SLIPLENLOC");//skip length location
			String _aP = LabelMaker.Generate_Unique_Label("_arrPointer");
			IRTemp arrPointer = new IRTemp(_aP);
			String _pAP = LabelMaker.Generate_Unique_Label("_preArrPointer");
			IRTemp preArrPointer = new IRTemp(_pAP);
			String _cU = LabelMaker.Generate_Unique_Label("_cycleUnit");//indicate each link operation cycle, cuz need to skip length memory location
			IRTemp cycleUnit = new IRTemp(_cU);
			//initial setting
			seqList.add(new IRMove(new IRTemp(_pDN), new IRConst(1)));
			seqList.add(new IRMove(new IRTemp(_cDN), new IRMem(new IRTemp(_eI))));
			seqList.add(new IRMove(new IRTemp(_rF), new IRConst(0)));
			seqList.add(new IRMove(new IRTemp(_dI), new IRConst(0)));
			//allocate memory for each dimension
			seqList.add(new IRLabel(_alloc));//label _alloc
			seqList.add(new IRMove(new IRTemp(_tmp), 
						new IRBinOp(IRBinOp.OpType.MUL, new IRTemp(_pDN), 
								new IRBinOp(IRBinOp.OpType.ADD, new IRTemp(_cDN), new IRConst(1)))));//tmp = allocate size = prevDim*(curDimNum+1)
			seqList.add(new IRMove(new IRTemp(_aP), new IRCall(new IRName("_I_alloc_i"), new IRBinOp(IRBinOp.OpType.MUL, new IRTemp(_tmp), new IRConst(8)))));
			seqList.add(new IRMove(new IRMem(new IRTemp(_aP)), new IRTemp(_cDN)));//set length
			seqList.add(new IRMove(new IRTemp(_lI), new IRConst(1)));//set loopIdx = 1
			seqList.add(new IRMove(new IRTemp(_tmp), new IRBinOp(IRBinOp.OpType.ADD, new IRTemp(_aP), new IRConst(8))));//tmp = arrPointer + 8
			seqList.add(new IRMove(new IRTemp(_dI), new IRBinOp(IRBinOp.OpType.ADD, new IRTemp(_dI), new IRConst(1))));//dimIdx++
			
			//store starting position
			seqList.add(new IRCJump(new IRBinOp(IRBinOp.OpType.EQ, new IRTemp(_rF), new IRConst(1)), _length));//if recurFlag == 0, then record location
			seqList.add(new IRMove(new IRTemp(_sL), new IRBinOp(IRBinOp.OpType.ADD, new IRTemp(_aP), new IRConst(8))));//stLocation = arrPointer + 8
			
			//for loop for assign length
			seqList.add(new IRLabel(_length));//label assign length
			seqList.add(new IRCJump(new IRBinOp(IRBinOp.OpType.EQ, new IRTemp(_lI), new IRTemp(_pDN)), _checkRecursion));
			seqList.add(new IRMove(new IRTemp(_aP), 
						new IRBinOp(IRBinOp.OpType.ADD, new IRTemp(_aP), 
						new IRBinOp(IRBinOp.OpType.MUL, new IRConst(8), 
						new IRBinOp(IRBinOp.OpType.ADD, new IRTemp(_cDN), new IRConst(1))))));//arrPointer += 8*(curDimNum+1)
			seqList.add(new IRMove(new IRMem(new IRTemp(_aP)), new IRTemp(_cDN)));//set length
			seqList.add(new IRMove(new IRTemp(_lI), new IRBinOp(IRBinOp.OpType.ADD, new IRTemp(_lI), new IRConst(1))));//loopIdx++
			seqList.add(new IRJump(new IRName(_length)));
			
			seqList.add(new IRLabel(_checkRecursion));
			seqList.add(new IRMove(new IRTemp(_aP), new IRTemp(_tmp)));//restore arrPointer original location
			seqList.add(new IRCJump(new IRBinOp(IRBinOp.OpType.EQ, new IRTemp(_rF), new IRConst(1)), _link));//if recurFlag==1 , jump to link operation
			seqList.add(new IRJump(new IRName(_checkEnd)));//jump to checkEnd
			
			//for loop for link array pointer
			seqList.add(new IRLabel(_link));
			seqList.add(new IRMove(new IRMem(new IRTemp(_pAP)), new IRTemp(_aP)));//link Mem(preArrPointer) = arrPointer
			seqList.add(new IRMove(new IRTemp(_lI), new IRConst(1)));//set loopIdx = 1
			seqList.add(new IRMove(new IRTemp(_slI), new IRConst(0)));//set subloopIdx = 0
			seqList.add(new IRLabel(_linkLoop));
			seqList.add(new IRCJump(new IRBinOp(IRBinOp.OpType.EQ, new IRTemp(_lI), new IRTemp(_pDN)), _checkEnd));//if loopIdx==preDimNum (finish linking), jump to checkEnd
			seqList.add(new IRMove(new IRTemp(_pAP), new IRBinOp(IRBinOp.OpType.ADD, new IRTemp(_pAP), new IRConst(8))));//preArrPointer += 8
			seqList.add(new IRMove(new IRTemp(_slI), new IRBinOp(IRBinOp.OpType.ADD, new IRTemp(_slI), new IRConst(1))));//subloopIdx++
			seqList.add(new IRCJump(new IRBinOp(IRBinOp.OpType.NEQ, new IRTemp(_slI), new IRTemp(_cU)), _skipLenLoc));//if subloopIdx != cycleUnit, jump to _skipLenLoc
			seqList.add(new IRMove(new IRTemp(_pAP), new IRBinOp(IRBinOp.OpType.ADD, new IRTemp(_pAP), new IRConst(8))));//preArrPointer += 8
			seqList.add(new IRMove(new IRTemp(_slI), new IRConst(0)));//set subloopIdx = 0
			seqList.add(new IRLabel(_skipLenLoc));//label _skipLenLoc
			seqList.add(new IRMove(new IRTemp(_aP), 
						new IRBinOp(IRBinOp.OpType.ADD, new IRTemp(_aP), 
						new IRBinOp(IRBinOp.OpType.MUL, new IRBinOp(IRBinOp.OpType.ADD, new IRTemp(_cDN), new IRConst(1)), new IRConst(8))
						)));//arrPointer = arrPointer + (curDimNum + 1) * 8
			seqList.add(new IRMove(new IRMem(new IRTemp(_pAP)), new IRTemp(_aP)));//link Mem(preArrPointer) = arrPointer
			seqList.add(new IRMove(new IRTemp(_lI), new IRBinOp(IRBinOp.OpType.ADD, new IRTemp(_lI), new IRConst(1))));//loopIdx++
			seqList.add(new IRJump(new IRName(_linkLoop)));
			
			seqList.add(new IRLabel(_checkEnd));//determine whether end the initialization
			seqList.add(new IRCJump(new IRBinOp(IRBinOp.OpType.EQ, new IRTemp(_dI), new IRTemp(_tD)), _exitInit));
			seqList.add(new IRMove(new IRTemp(_rF), new IRConst(1)));//recurFlag = 1
			seqList.add(new IRMove(new IRTemp(_cU), new IRTemp(_cDN)));//cycleUnit = curDimNum
			seqList.add(new IRMove(new IRTemp(_pDN), new IRBinOp(IRBinOp.OpType.MUL, new IRTemp(_pDN), new IRTemp(_cDN))));//preDimNum = preDimNum * curDimNum
			seqList.add(new IRMove(new IRTemp(_eI), new IRBinOp(IRBinOp.OpType.ADD, new IRTemp(_eI), new IRConst(8))));//exprIdx += 8
			seqList.add(new IRMove(new IRTemp(_cDN), new IRMem(new IRTemp(_eI))));//curDimNum = Mem(exprIdx)
			seqList.add(new IRMove(new IRTemp(_aP), new IRTemp(_tmp)));//arrPointer = tmp
			seqList.add(new IRMove(new IRTemp(_pAP), new IRTemp(_aP)));//preArrPointer = arrPointer
			seqList.add(new IRJump(new IRName(_alloc)));
			
			seqList.add(new IRLabel(_exitInit));
			seqList.add(new IRMove(new IRTemp(name.getName()), new IRTemp(_sL)));//set variable to starting location
			//delete exprIdx Mem
			//exit
			return new IRSeq(seqList);
		}
	}
}
