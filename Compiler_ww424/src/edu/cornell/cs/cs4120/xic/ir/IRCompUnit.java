package edu.cornell.cs.cs4120.xic.ir;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import compiler_ww424.Wrapper;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import edu.cornell.cs.cs4120.xic.ir.visit.AggregateVisitor;
import edu.cornell.cs.cs4120.xic.ir.visit.IRVisitor;

/**
 * An intermediate representation for a compilation unit
 */
public class IRCompUnit extends IRNode {
    private String name;
    private Map<String, IRFuncDecl> functions;
    private static List<String> globalVar2Assembly = new ArrayList<String>();
   
    public IRCompUnit(String name) {
        this.name = name;
        functions = new LinkedHashMap<>();
    }

    public IRCompUnit(String name, Map<String, IRFuncDecl> functions) {
        this.name = name;
        this.functions = functions;
    }

    public void appendFunc(IRFuncDecl func) {
        functions.put(func.name(), func);
    }

    public String name() {
        return name;
    }

    public static List<String> classConstr2Assembly(HashMap<String,Wrapper> DispachTable){
    	List<String> ret = new ArrayList<String>();
    	for (String s : DispachTable.keySet()){
    		String size = "_I_size_"+s;
    		String vt = "_I_vt_"+s;
    		String init = "_I_init_"+s;
    		int fieldNum = DispachTable.get(s).getFields().size();
    		int methodNum= DispachTable.get(s).getMethods().getMethod().size();
    		
    		ret.add(".globl "+size);
    		ret.add(size+":");
    		int quad = (fieldNum+1)*8;
    		ret.add("    .quad " + quad );
    		ret.add("    .text \n");
    		ret.add("    .bss");
    		ret.add("    .align 8");
    		ret.add(".globl "+vt);
    		ret.add(vt+":");
    		int zero = methodNum * 8;
    		ret.add("    .zero " + zero );
    		ret.add("    .text \n");
    		ret.add(".section .ctors");
    		ret.add("    .align 8");
    		ret.add("    .quad " +init);
    		ret.add("    .text \n");
    		ret.add("    .bss");
    		ret.add("    .align 8");
    	}
    	return ret;
    }
    
    public Map<String, IRFuncDecl> functions() {
        return functions;
    }

    public IRFuncDecl getFunction(String name) {
        return functions.get(name);
    }
    
    public static List<String> getGlobalVar2Assembly(){
    	return globalVar2Assembly;
    }
    
    public static void addGlobal(String s){
    	globalVar2Assembly.add(s);
    }

    
    @Override
    public String label() {
        return "COMPUNIT";
    }

    @Override
    public IRNode visitChildren(IRVisitor v) {
        boolean modified = false;

        Map<String, IRFuncDecl> results = new LinkedHashMap<>();
        for (IRFuncDecl func : functions.values()) {
            IRFuncDecl newFunc = (IRFuncDecl) v.visit(this, func);
            if (newFunc != func) modified = true;
            results.put(newFunc.name(), newFunc);
        }

        if (modified) return new IRCompUnit(name, results);

        return this;
    }

    @Override
    public <T> T aggregateChildren(AggregateVisitor<T> v) {
        T result = v.unit();
        for (IRFuncDecl func : functions.values())
            result = v.bind(result, v.visit(func));
        return result;
    }

    @Override
    public void printSExp(SExpPrinter p) {
        p.startList();
        p.printAtom("COMPUNIT");
        p.printAtom(name);
        for (IRFuncDecl func : functions.values())
            func.printSExp(p);
        p.endList();
    }
}
