package edu.cornell.cs.cs4120.xic.ir;

import edu.cornell.cs.cs4120.util.InternalCompilerError;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import edu.cornell.cs.cs4120.xic.ir.visit.AggregateVisitor;
import edu.cornell.cs.cs4120.xic.ir.visit.IRVisitor;
import compiler_ww424.LabelMaker;

/**
 * An intermediate representation for a memory location
 * MEM(e)
 */
public class IRMem extends IRExpr {
    public enum MemType {
        NORMAL, IMMUTABLE;

        @Override
        public String toString() {
            switch (this) {
            case NORMAL:
                return "MEM";
            case IMMUTABLE:
                return "MEM_I";
            }
            throw new InternalCompilerError("Unknown mem type!");
        }
    };

    private IRExpr expr;
    private MemType memType;
    private IRTemp x;
    private IRTemp y;
    private long w;
    private long k;


    /**
     *
     * @param expr the address of this memory location
     */
    public IRMem(IRExpr expr) {
        this(expr, MemType.NORMAL);
    }

    public IRMem(IRExpr expr, MemType memType) {
        this.expr = expr;
        this.memType = memType;
    }

    public IRExpr expr() {
        return expr;
    }

    public MemType memType() {
        return memType;
    }

    @Override
    public String label() {
        return memType.toString();
    }

    public IRESeq IRLower() {
        IRESeq temp = expr.IRLower();

        return new IRESeq(temp.stmt(), new IRMem(temp.expr()));
    }

    @Override
    public IRNode visitChildren(IRVisitor v) {
        IRExpr expr = (IRExpr) v.visit(this, this.expr);

        if (expr != this.expr) return new IRMem(expr, memType);

        return this;
    }

    @Override
    public <T> T aggregateChildren(AggregateVisitor<T> v) {
        T result = v.unit();
        result = v.bind(result, v.visit(expr));
        return result;
    }

    @Override
    public void printSExp(SExpPrinter p) {
        p.startList();
        p.printAtom(memType.toString());
        expr.printSExp(p);
        p.endList();
    }

    public int bestCost() {
        if(bestTile != null) return bestTile.getCost();
        else {//min tiling
            bestTileNum = 0;
            bestCost = expr.bestCost() + 2;
        }
        //bestTileNum = 1, IRMem(IRBinOp(ADD, IRTemp(x), IRTemp(y)))
        if(expr instanceof IRBinOp && 
                ((IRBinOp) expr).opType() == IRBinOp.OpType.ADD && 
                ((IRBinOp) expr).left() instanceof IRTemp && 
                ((IRBinOp) expr).right() instanceof IRTemp ){
            if(bestCost > 4){
                bestCost = 4;
                bestTileNum = 1;
            }
        }
        //bestTileNum = 2, IRMem(IRBinOp(ADD, IRTemp(x), IRConst(k)))
        if(expr instanceof IRBinOp && 
                ((IRBinOp) expr).opType() == IRBinOp.OpType.ADD && 
                ( (((IRBinOp) expr).left() instanceof IRTemp && ((IRBinOp) expr).right() instanceof IRConst) || 
                        ((((IRBinOp) expr).left() instanceof IRConst && ((IRBinOp) expr).right() instanceof IRTemp)) )
                ){
            if(bestCost > 4){
                bestCost = 4;
                bestTileNum = 2;
            }
        }
        //bestTileNum = 3, IRMem(IRBinOp(ADD, Temp(x), IRBinOp(MUL, Temp(y), Const(w))))
        //bestTileNum = 3, IRMem(IRBinOp(ADD, Temp(x), IRBinOp(MUL, Const(w), Temp(y))))
        //bestTileNum = 3, IRMem(IRBinOp(ADD, IRBinOp(MUL, Temp(y), Const(w)), Temp(x)))
        //bestTileNum = 3, IRMem(IRBinOp(ADD, IRBinOp(MUL, Const(w), Temp(y)), Temp(x)))
        if(expr instanceof IRBinOp && ((IRBinOp) expr).opType() == IRBinOp.OpType.ADD){
            boolean tileMatch = false;
            if(     ((IRBinOp) expr).left() instanceof IRTemp && 
                    ((IRBinOp) expr).right() instanceof IRBinOp && ((IRBinOp) ((IRBinOp) expr).right()).opType() == IRBinOp.OpType.MUL && 
                    ((IRBinOp) ((IRBinOp) expr).right()).left() instanceof IRTemp && 
                    ((IRBinOp) ((IRBinOp) expr).right()).right() instanceof IRConst && 
                    isPowerOf2( ((IRConst)((IRBinOp) ((IRBinOp) expr).right()).right()).value() )
                    ){
                x = (IRTemp)(((IRBinOp) expr).left());
                y = (IRTemp)(((IRBinOp) ((IRBinOp) expr).right()).left());
                w = ((IRConst)((IRBinOp) ((IRBinOp) expr).right()).right()).value();
                tileMatch = true;
            }
            if(     ((IRBinOp) expr).left() instanceof IRTemp && 
                    ((IRBinOp) expr).right() instanceof IRBinOp && ((IRBinOp) ((IRBinOp) expr).right()).opType() == IRBinOp.OpType.MUL && 
                    ((IRBinOp) ((IRBinOp) expr).right()).left() instanceof IRConst && 
                    ((IRBinOp) ((IRBinOp) expr).right()).right() instanceof IRTemp && 
                    isPowerOf2( ((IRConst)((IRBinOp) ((IRBinOp) expr).right()).left()).value() )
                    ){
                x = (IRTemp)(((IRBinOp) expr).left());
                y = (IRTemp)(((IRBinOp) ((IRBinOp) expr).right()).right());
                w = ((IRConst)((IRBinOp) ((IRBinOp) expr).right()).left()).value();
                tileMatch = true;
            }
            if(     ((IRBinOp) expr).right() instanceof IRTemp && 
                    ((IRBinOp) expr).left() instanceof IRBinOp && ((IRBinOp) ((IRBinOp) expr).left()).opType() == IRBinOp.OpType.MUL && 
                    ((IRBinOp) ((IRBinOp) expr).left()).left() instanceof IRTemp && 
                    ((IRBinOp) ((IRBinOp) expr).left()).right() instanceof IRConst && 
                    isPowerOf2( ((IRConst)((IRBinOp) ((IRBinOp) expr).left()).right()).value() )
                    ){
                x = (IRTemp)(((IRBinOp) expr).right());
                y = (IRTemp)(((IRBinOp) ((IRBinOp) expr).left()).left());
                w = ((IRConst)((IRBinOp) ((IRBinOp) expr).left()).right()).value();
                tileMatch = true;
            }
            if(     ((IRBinOp) expr).right() instanceof IRTemp && 
                    ((IRBinOp) expr).left() instanceof IRBinOp && ((IRBinOp) ((IRBinOp) expr).left()).opType() == IRBinOp.OpType.MUL && 
                    ((IRBinOp) ((IRBinOp) expr).left()).left() instanceof IRConst && 
                    ((IRBinOp) ((IRBinOp) expr).left()).right() instanceof IRTemp && 
                    isPowerOf2( ((IRConst)((IRBinOp) ((IRBinOp) expr).left()).left()).value() )     
                    ){
                x = (IRTemp)(((IRBinOp) expr).right());
                y = (IRTemp)(((IRBinOp) ((IRBinOp) expr).left()).right());
                w = ((IRConst)((IRBinOp) ((IRBinOp) expr).left()).left()).value();
                tileMatch = true;
            }
            if(tileMatch && bestCost > 5){
                bestCost = 5;
                bestTileNum = 3;
            }
        }
        if (expr instanceof IRBinOp && ((IRBinOp)expr).opType() == IRBinOp.OpType.ADD ){
            IRExpr left = ((IRBinOp)expr).left();
            IRExpr right= ((IRBinOp)expr).right();
            
            if (left instanceof IRBinOp && ((IRBinOp)left).opType() == IRBinOp.OpType.ADD  && right instanceof IRConst){
                IRExpr ll = ((IRBinOp)left).left();
                IRExpr lr = ((IRBinOp)left).right();
                if (ll instanceof IRTemp && lr instanceof IRBinOp && ((IRBinOp)lr).opType() == IRBinOp.OpType.MUL &&
                        ((IRBinOp)lr).left() instanceof IRTemp && ((IRBinOp)lr).right() instanceof IRConst ){
                    k = ((IRConst) right).value();
                    w = ((IRConst) ((IRBinOp)lr).right()).value();
                    if ( isPowerOf2(w) && bestCost>6){
                        x = (IRTemp)ll;
                        y = (IRTemp)((IRBinOp)lr).left() ;
                        bestCost = 6;
                        bestTileNum = 4;
                    }
                    
                }
                if (ll instanceof IRTemp && lr instanceof IRBinOp && ((IRBinOp)lr).opType() == IRBinOp.OpType.MUL &&
                        ((IRBinOp)lr).left() instanceof IRConst && ((IRBinOp)lr).right() instanceof IRTemp ){
                    k = ((IRConst) right).value();
                    w = ((IRConst) ((IRBinOp)lr).left()).value();
                    if ( isPowerOf2(w) && bestCost>6){
                        x = (IRTemp)ll;
                        y = (IRTemp)((IRBinOp)lr).right();
                        bestCost = 6;
                        bestTileNum = 4;
                    }
                }
                if (lr instanceof IRTemp && ll instanceof IRBinOp && ((IRBinOp)ll).opType() == IRBinOp.OpType.MUL &&
                        ((IRBinOp)ll).left() instanceof IRTemp && ((IRBinOp)ll).right() instanceof IRConst ){
                    k = ((IRConst) right).value();
                    w = ((IRConst) ((IRBinOp)ll).right()).value();
                    if ( isPowerOf2(w) && bestCost>6){
                        x = (IRTemp)lr;
                        y = (IRTemp)((IRBinOp)ll).left() ;
                        bestCost = 6;
                        bestTileNum = 4;
                    }
                    
                }
                if (lr instanceof IRTemp && ll instanceof IRBinOp && ((IRBinOp)ll).opType() == IRBinOp.OpType.MUL &&
                        ((IRBinOp)ll).left() instanceof IRConst && ((IRBinOp)ll).right() instanceof IRTemp ){
                    k = ((IRConst) right).value();
                    w = ((IRConst) ((IRBinOp)ll).left()).value();
                    if ( isPowerOf2(w) && bestCost>6){
                        x = (IRTemp)lr;
                        y = (IRTemp)((IRBinOp)ll).right();
                        bestCost = 6;
                        bestTileNum = 4;
                    }
                }
                
            }
        
            if (left instanceof IRConst && right instanceof IRBinOp && ((IRBinOp)right).opType() == IRBinOp.OpType.ADD ){
                IRExpr rl = ((IRBinOp)right).left();
                IRExpr rr = ((IRBinOp)right).right();
                if (rl instanceof IRTemp && rr instanceof IRBinOp && ((IRBinOp)rr).opType() == IRBinOp.OpType.MUL &&
                        ((IRBinOp)rr).left() instanceof IRTemp && ((IRBinOp)rr).right() instanceof IRConst ){
                    k = ((IRConst) left).value();
                    w = ((IRConst) ((IRBinOp)rr).right() ).value();
                    if ( isPowerOf2(w) && bestCost>6){
                        x = (IRTemp)rl;
                        y = (IRTemp) ((IRBinOp)rr).left() ;
                        bestCost = 6;
                        bestTileNum = 4;
                    }
                }
                if (rl instanceof IRTemp && rr instanceof IRBinOp && ((IRBinOp)rr).opType() == IRBinOp.OpType.MUL &&
                        ((IRBinOp)rr).left() instanceof IRConst && ((IRBinOp)rr).right() instanceof IRTemp ){
                    k = ((IRConst) left).value();
                    w = ((IRConst) ((IRBinOp)rr).left()).value();
                    if ( isPowerOf2(w) && bestCost>6){
                        x = (IRTemp)rl;
                        y = (IRTemp)((IRBinOp)rr).right();
                        bestCost = 6;
                        bestTileNum = 4;
                    }
                }
                if (rr instanceof IRTemp && rl instanceof IRBinOp && ((IRBinOp)rl).opType() == IRBinOp.OpType.MUL &&
                        ((IRBinOp)rl).left() instanceof IRTemp && ((IRBinOp)rl).right() instanceof IRConst ){
                    k = ((IRConst) left).value();
                    w = ((IRConst) ((IRBinOp)rl).right() ).value();
                    if ( isPowerOf2(w) && bestCost>6){
                        x = (IRTemp)rr;
                        y = (IRTemp)((IRBinOp)rl).left() ;
                        bestCost = 6;
                        bestTileNum = 4;
                    }
                }
                if (rr instanceof IRTemp && rl instanceof IRBinOp && ((IRBinOp)rl).opType() == IRBinOp.OpType.MUL &&
                        ((IRBinOp)rl).left() instanceof IRConst && ((IRBinOp)rl).right() instanceof IRTemp ){
                    k = ((IRConst) left).value();
                    w = ((IRConst) ((IRBinOp)rl).left()).value();
                    if ( isPowerOf2(w) && bestCost>6){
                        x = (IRTemp)rr;
                        y = (IRTemp)((IRBinOp)rl).right() ;
                        bestCost = 6;
                        bestTileNum = 4;
                    }
                }
            }
        }
        return bestCost;
    }

    public AssemInstr getBestTile() {
        if(bestTile != null) return bestTile;
        else {
            this.bestCost();
            switch(bestTileNum) {
            case 0: {//mintile
                AssemInstr child = expr.getBestTile();

                String newData = "\n movq " + child.getSource() + ", %r10\n";
                String store = StackAssigner.getLocation(LabelMaker.Generate_Unique_Label("_MEM_TEMP"));
                newData += "subq $8, %rsp\nmovq %r10, " + store;

                bestTile = new AssemInstr(child.getData() + newData, store, child.getCost() + 2);
            }
            case 1: {
                x = (IRTemp)(((IRBinOp) expr).left());
                y = (IRTemp)(((IRBinOp) expr).right());
                String store = StackAssigner.getLocation(LabelMaker.Generate_Unique_Label("_MEM_TEMP"));
                String newData = "\n movq " + x.getBestTile().getSource() + ", %r10\n";
                newData += "movq " + y.getBestTile().getSource() + ", %r11\n";
                newData += "subq $8, %rsp\nmovq (%r10, %r11), " + store;
                
                bestTile = new AssemInstr(newData, store, this.bestCost);
            }
            case 2: {
                /* movq x.src, %r10 
                 * movq k(%10), ___
                 * AssemInstr source field =___
                 */
                String store = StackAssigner.getLocation(LabelMaker.Generate_Unique_Label("_MEM_TEMP"));
                String newData = "\n movq " + x.getBestTile().getSource()+", %r10 \n"+ "subq $8, %rsp\n" + 
                                 "movq " + k + "(%10), " +store;

                bestTile = new AssemInstr(newData, store, bestCost);
            }
            case 3: {
                /* movq x.src, %r10
                movq y.src, %r11
                movq (%10,%11,w), ____
                AssemInstr source field = ____*/
                String store = StackAssigner.getLocation(LabelMaker.Generate_Unique_Label("_MEM_TEMP"));
                String newData = "\n movq " + x.getBestTile().getSource()+", %r10 \n"+
                                 "movq " + y.getBestTile().getSource()+", %r11 \n"+ "subq $8, %rsp\n" + 
                                 "movq (%10, %11, " + w +"), " +store;

                bestTile = new AssemInstr(newData, store, bestCost);
            }
            case 4:{
                /*movq x.src, %r10
                    movq y.src, %r11
                    movq k(%10,%11,w), ____
                    AssemInstr source field =____*/
                String store = StackAssigner.getLocation(LabelMaker.Generate_Unique_Label("_MEM_TEMP"));
                String newData = "\n movq " + x.getBestTile().getSource()+", %r10 \n"+
                                 "movq " + y.getBestTile().getSource()+", %r11 \n"+ "subq $8, %rsp\n" + 
                                 "movq " + k + "(%10, %11, " + w +"), " +store;

                bestTile = new AssemInstr(newData, store, bestCost);

            }
            }
        }
        return bestTile;
    }

    public boolean isPowerOf2(long n){
        return (n <= 0)? false: ((n & (n-1)) == 0);
    }
}
