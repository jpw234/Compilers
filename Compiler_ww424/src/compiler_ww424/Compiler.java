package compiler_ww424;
import java.io.BufferedReader;
import java.io.File;
import java_cup.runtime.ComplexSymbolFactory.ComplexSymbol;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import compiler_ww424.Lexer.Token;
import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import edu.cornell.cs.cs4120.xic.ir.*;
import edu.cornell.cs.cs4120.xic.ir.interpret.IRSimulator;
import edu.cornell.cs.cs4120.xic.ir.visit.CheckCanonicalIRVisitor;
import edu.cornell.cs.cs4120.xic.ir.visit.CheckConstFoldedIRVisitor;
import java_cup.runtime.*;

public class Compiler {
	public static final String INPUT_HELPER = "--help";
	public static final String INPUT_REPORTOPT = "--report-opts";
	public static final String INPUT_LEX = "--lex";
	public static final String INPUT_PARSE = "--parse";
	public static final String INPUT_TYPECHECK = "--typecheck";
	public static final String INPUT_SOURCEPATH = "-sourcepath";
	public static final String INPUT_DIAGNOSIS_PATH = "-D";
	public static final String INPUT_ASSEMBLY_PATH = "-d";
	public static final String INPUT_LIBRARY_PATH = "-libpath";
	public static final String INPUT_IRGEN = "--irgen";
	public static final String INPUT_IRRUN = "--irrun";
	public static final String INPUT_OPTIR = "--optir";
	public static final String INPUT_OPTCFG = "--optcfg";
	public static final String INPUT_OPTIMIZATION = "-O";
	public static final String INPUT_TARGET = "-target";
	public static final String NO_CF = "-O-no-cf";
	public static final String NO_CSE = "-O-no-cse";
	public static final String NO_UCE = "-O-no-uce";
	public static final String NO_CP = "-O-no-cp";
	public static final String NO_VN = "-O-no-vn";
	public static int MINIMUM_ARG_COUNT = 2; 
	public static String opt_phase =null;
	public static Boolean toCF = true;
	public static Boolean toCSE = true;
	public static Boolean toUCE = true;
	public static Boolean toCP = true;
	public static Boolean toVN = true;
	public static Boolean toDrawCFG = false;
	public static Boolean toGenOPTIR= false;
	public static HashMap<String,Wrapper> DispachTable;



	public static class CodePath {
		/**
		 * The Code's File
		 */
		public Path file;
		/**
		 * The Folder Containing The Scene
		 */
		public Path codeRoot;
		/**
		 * The Root Workspace Path
		 */
		public Path root;
		public String OriginFileName;
		public CodePath(String r, String f) {
			if(r == null) {
				root = null;
				file = Paths.get(f);
			}
			else  {
				root = Paths.get(r);
				file = root.resolve(f);
			}
			codeRoot = file.getParent();
			OriginFileName = f;
		}

		public String getRoot() {
			return root == null ? null : root.toAbsolutePath().toString();
		}
		public String getFile() {
			return file.toAbsolutePath().toString();
		}

		public String getFileName() {
			return file.getFileName().toString();
		}

		/**
		 * Attempt To Search The Code And Program Workspace For A File
		 * @param f The File To Search For
		 * @return The Absolute File Path That Is Resolved (Or null)
		 */
		public String resolve(String f) {
			Path p = root != null ? root.resolve(f) : null;
			if(p == null) p = codeRoot.resolve(f);
			if(p == null) p = Paths.get(f);
			return p == null ? null : p.toAbsolutePath().toString();
		}
	}
	/**
	 * The Workspace For The Scene
	 */
	public static CodePath codeWorkspace = null;

	/**
	 * This directory precedes the arguments passed in via the command line.
	 */
	public static final String directory = "";


	public static final void main(String[] args) throws Exception {
		ArrayList<CodePath> pathArgs = new ArrayList<>();
		ArrayList<CodePath> codeToCompile = new ArrayList<>();
		String currentRoot = directory;
		String sourceRoot = null;
		String libRoot = null;
		String diagnosisRoot = null;
		String assemblyRoot = null;
		String target = null;
		Boolean toLex = false;
		Boolean useHelp = false;
		Boolean toParse = false;
		Boolean toTypecheck = false;
		Boolean toGenIR = false;
		Boolean toRunIR = false;
		Boolean toOptimize = true;
		Boolean toAssembly = false;


		// Use All The Arguments
		for(int i = 0;i < args.length;i++) {
			switch(args[i]) {
			case INPUT_SOURCEPATH :
				i++;
				if(i < args.length) sourceRoot = args[i];
				break;
			case INPUT_DIAGNOSIS_PATH :
				i++;
				if(i < args.length) diagnosisRoot = args[i];
				break;
			case INPUT_ASSEMBLY_PATH:
				i++;
				if(i < args.length) assemblyRoot = args[i];
				break;
			case INPUT_LIBRARY_PATH :
				i++;
				if(i < args.length) libRoot = args[i];
				break;
			case INPUT_HELPER :
				//help function
				useHelp = true;
				printUsage();
				break;
			case INPUT_REPORTOPT :
				//help function
				useHelp = true;
				System.out.println("% xic --report-opts");
				System.out.println(" reg \n uce \n cse \n cf \n cp \n vn \n%");
				break;
			case INPUT_LEX:
				// Use The CWD
				toLex = true;
				break;
			case INPUT_PARSE:
				toParse = true;
				break;
			case INPUT_TYPECHECK:
				toTypecheck = true;
				break;
			case INPUT_IRGEN:
				toGenIR = true;
				break;
			case INPUT_IRRUN:
				toRunIR = true;
				break;
			case INPUT_OPTIR:
				i ++;
				toGenIR = true;
				toGenOPTIR = true;
				if(i < args.length) opt_phase = args[i];
				break;
			case INPUT_OPTCFG:
				i ++;
				toGenIR = true;
				toDrawCFG = true;
				if(i < args.length) opt_phase = args[i];
				break;
			case INPUT_OPTIMIZATION:
				i++;
				String ss = "";
				if(i < args.length){
					ss = args[i];
					if(ss.equals("cf")) {
						toCF = true;
						toCSE = false;
						toUCE = false;
						toCP = false;
						toVN = false;}
					else if(ss.equals("cse")) {
						toCSE = true;
						toCF = false;
						toUCE = false;
						toCP = false;
						toVN = false;}
					else if(ss.equals("uce")) {
						toUCE = true;
						toCSE = false;
						toCF = false;
						toCP = false;
						toVN = false;}
					else if(ss.equals("cp")) {
						toCP = true;
						toUCE = false;
						toCSE = false;
						toCF = false;
						toVN = false;
					}
					else if(ss.equals("vn")) {
						toVN = true;
						toCP = true;
						toUCE = false;
						toCSE = false;
						toCF = false;}

					else {i--; toOptimize = false;}
				}
				else{i--; toOptimize = false;}
				break;
			case INPUT_TARGET:
				toAssembly = true;
				i++;
				if(i < args.length) target = args[i];
				break;
			case NO_CF:
				toCF = false;
				break;
			case NO_CSE:
				toCSE = false;
				break;
			case NO_UCE:
				toUCE = false;
				break;
			case NO_CP:
				toCP = false;
				break;
			case NO_VN:
				toVN = false;
				break;
			default:
				// This Must Be A File
				pathArgs.add(new CodePath(currentRoot, args[i]));
				break;
			}
		}
		if ((toLex || toParse || toTypecheck || toGenIR || toRunIR || toOptimize || toAssembly) && !useHelp){
			lex_parse(toLex, toParse,toTypecheck,toGenIR,toRunIR,toOptimize,toAssembly,
					pathArgs,codeToCompile,sourceRoot,diagnosisRoot,assemblyRoot,libRoot);
		}



	}
	public static void lex_parse(
			Boolean toLex, Boolean toParse,Boolean toTypecheck,
			Boolean toGenIR, Boolean toRunIR, Boolean toOptimize,Boolean toAssembly,
			ArrayList<CodePath> pathArgs,ArrayList<CodePath> codeToCompile ,
			String sourceRoot,String diagnosisRoot,String assemblyRoot, String libRoot) throws Exception{

		if(pathArgs.size() < 1) {
			pathArgs.add(new CodePath(sourceRoot, "."));
		}
		// Expand All The Possible Codes
		for(CodePath p : pathArgs) {
			// Add All The Files In The List

			File f = p.file.toFile();
			if(f.isDirectory()) {
				for(File _f : f.listFiles()) {
					// We Only Want XI Files
					if(!_f.getPath().endsWith(".xi")) continue;
					if (sourceRoot == null){
						codeToCompile.add(new CodePath(p.getRoot(), _f.getName().toString()));;
					}
					else {
						codeToCompile.add(new CodePath(sourceRoot, _f.getName().toString()));
					}
				}
			}
			else {
				// We Only Want XI Files
				if(!f.getPath().endsWith(".xi")) continue;
				// Just A Single Scene
				if (sourceRoot == null){
					codeToCompile.add(p);
				}
				else {
					codeToCompile.add(new CodePath(sourceRoot, f.toString()));
				}
			}

		}

		//System.out.println("Attempting To Compile " + codeToCompile.size() + " File(s)");

		for (CodePath p: codeToCompile){

			////////////////////////////////////////////////		
			/* check if file exists or file empty or not */
			///////////////////////////////////////////////	
			try {
				Reader abc = new FileReader(p.getFile());
				abc.close();}
			catch(IOException ioe){
				System.err.println("No such File in Directory");
				return;}

			Boolean isempty = emptyFile(p.OriginFileName,toLex,toParse, toGenIR ,toRunIR,toAssembly);
			if (isempty) break;

			//////////////////////////////////////		
			/* to Lex and generate lexed files */
			/////////////////////////////////////	

			if (toLex){
				String fileName = p.OriginFileName.substring(0,p.OriginFileName.length()-2)+"lexed";
				if(diagnosisRoot != null){
					fileName = diagnosisRoot + "/" +fileName;}
				Reader fr = new FileReader(p.getFile());
				Lexer lexer = new Lexer(fr);
				FileWriter fw = new FileWriter(fileName);
				for(Token tok = (Token) lexer.next_token(); tok.sym!=0; tok = (Token)lexer.next_token()){
					int numLine = tok.getLine() + 1 ;
					int numCol = tok.getCol() + 1; 
					String lineVal = new String ();
					if (tok.sym == sym.INTEGER){ lineVal = "integer"+" "+ lexer.yytext();}
					else if (tok.sym == sym.STRING){lineVal = "string"+" "+ tok.value;}
					else if (tok.sym == sym.CHARACTER) { lineVal = "character"+" "+ tok.value;}
					else if (tok.sym == sym.ID) {lineVal = "id"+" "+ tok.value;}
					else if (tok.sym == sym.error){lineVal = (String) tok.value;
					String unparsedString = "";
					for (char unparseChar : lineVal.toCharArray()) {
						if(unparseChar == '\t') {
							unparsedString += "\\t";
						}
						else if(unparseChar == '\n') {
							unparsedString += "\\n";
						}
						else {
							unparsedString += unparseChar;
						}
					}
					lineVal = unparsedString;
					fw.write(String.format("%d:%d %s\n", numLine, numCol, lineVal));
					break;}
					else{lineVal = lexer.yytext();}
					//WRITE IN THE FILES
					String unparsedString = "";
					for (char unparseChar : lineVal.toCharArray()) {
						if(unparseChar == '\t') {
							unparsedString += "\\t";
						}
						else if(unparseChar == '\n') {
							unparsedString += "\\n";
						}
						else {
							unparsedString += unparseChar;
						}
					}
					lineVal = unparsedString;
					String s = String.format("%d:%d %s\n", numLine, numCol, lineVal);
					fw.write(s);	
				}
				fw.close();
			}

			/////////////////////////////////////////		
			/* to Parse and generate parsed files */
			////////////////////////////////////////	
			if(toParse){
				String fN = p.OriginFileName.substring(0,p.OriginFileName.length()-2)+"parsed";
				if(diagnosisRoot != null){fN = diagnosisRoot + "/" +fN;}
				Reader fr = new FileReader(p.getFile());
				Lexer lexer = new Lexer(fr);
				FileWriter fw = new FileWriter(fN);
				@SuppressWarnings("deprecation")
				parser par = new parser(lexer);
				try{
					Program program = (Program) par.parse().value;
					//System.out.println(program.toString());
					fw.write(program.toString());
				}catch(Error e ){		
					//System.out.println(e.getMessage());
					fw.write(e.getMessage());
				}catch(ArrayInitException ex) {
					fw.write(ex.getMessage());
				}
				fw.close();
			}

			//////////////////////////////////////////		
			/* Type Check and generate typed files */
			/////////////////////////////////////////			

			if(toTypecheck) {
				String fN = p.OriginFileName.substring(0,p.OriginFileName.length()-2)+"typed";
				if(diagnosisRoot != null){fN = diagnosisRoot + "/" +fN;}
				Reader fr = new FileReader(p.getFile());
				Lexer lexer = new Lexer(fr);
				FileWriter fw = new FileWriter(fN);
				parser par = new parser(lexer);

				SymTab table = new SymTab(null);
				if(libRoot ==null)
				{
					libRoot = new String("");
				}
				try{
					Program program = (Program) par.parse().value;
					for(int a = 0; a < program.getImports().size(); a++) {
						Reader impReader = null;
						try {
							impReader = new FileReader(libRoot + "/" + program.getImports().get(a).getString()+".ixi");
						}
						catch(Exception excp) {
							throw new Error("Interface file " + libRoot + "/" + program.getImports().get(a).getString()+".ixi not found.");
						}
						Lexer impLexer = new Lexer(impReader);
						parser impPar = new parser(impLexer);
						impPar.setState(true);
						Program impProgram = (Program) impPar.parse().value;
						impProgram.firstPass(table);
						impReader.close();
					}
					DispachTable = program.buildDispachTable();
					//Test DispachTable
					/*for (String s:DispachTable.keySet()){
						System.out.println(s);
						System.out.println("-----------");
						// Here is the Filds 
						for(int i = 0 ; i < DispachTable.get(s).getFields().size();i++){
							System.out.println(DispachTable.get(s).getFields().get(i));
						}
						System.out.println("-----------");
						// Here is the Normal Name Methods 
						for(int i = 0 ; i < DispachTable.get(s).getMethods().getMethod().size();i++){
							System.out.println( DispachTable.get(s).getMethods().getMethod().get(i));
						}
						System.out.println("-----------");
						// Here is the Mangling Name Methods 
						for(int i = 0 ; i < DispachTable.get(s).getMethods().getMangleMethod().size();i++){
							System.out.println(  DispachTable.get(s).getMethods().getMangleMethod().get(i));
						}
					}*/
					program.firstPass(table);
					program.secondPass(table);
					program.returnPass();
					fw.write("Valid Xi Program\r\n");
				}
				catch(Error e) {
					System.out.println(e.getMessage());
					fw.write(e.getMessage()+"\r\n");
				}catch(ArrayInitException ex) {
					fw.write(ex.getMessage());
				}
				fw.close();
			}
			////////////////////////////////////////////		
			/* Generate IR File and Run IR Simulator
			 *  and generate .ir file*/
			///////////////////////////////////////////
			if(toGenIR || toRunIR) {
				String fN = p.OriginFileName.substring(0,p.OriginFileName.length()-2)+"ir";
				String fileDot = "";
				if(diagnosisRoot != null){fN = diagnosisRoot + "/" +fN;}
				if(opt_phase != null && opt_phase.equals("initial")){
					if (toGenOPTIR){fN = p.OriginFileName.substring(0,p.OriginFileName.length()-3)+"_initial.ir";}
					if (toDrawCFG){fileDot = p.OriginFileName.substring(0,p.OriginFileName.length()-3)+"_f_initial.dot";}
				}else if (opt_phase != null && opt_phase.equals("final")){
					if (toGenOPTIR){fN = p.OriginFileName.substring(0,p.OriginFileName.length()-3)+"_final.ir";}
					if (toDrawCFG){fileDot = p.OriginFileName.substring(0,p.OriginFileName.length()-3)+"_f_final.dot";}
				}
				Reader fr = new FileReader(p.getFile());
				Lexer lexer = new Lexer(fr);
				FileWriter fw = new FileWriter(fN);
				parser par = new parser(lexer);

				SymTab table = new SymTab(null);
				if(libRoot ==null)
				{
					libRoot = new String("");
				}
				try{
					Program program = (Program) par.parse().value;
					for(int a = 0; a < program.getImports().size(); a++) {
						Reader impReader = null;
						try {
							impReader = new FileReader(libRoot + "/" + program.getImports().get(a).getString()+".ixi");
						}
						catch(Exception excp) {
							throw new Error("Interface file " + libRoot + "/" + program.getImports().get(a).getString()+".ixi not found.");
						}
						Lexer impLexer = new Lexer(impReader);
						parser impPar = new parser(impLexer);
						impPar.setState(true);
						Program impProgram = (Program) impPar.parse().value;
						impProgram.firstPass(table);
						impReader.close();
					}
					DispachTable = program.buildDispachTable();
					program.firstPass(table);
					program.secondPass(table);
					program.returnPass();

					if(opt_phase != null && opt_phase.equals("final")){
						program.constantFold();
						program.unreachableCodeRemove();
					}

					if(opt_phase == null && toOptimize){
						if (toCF) program.constantFold();
						if (toUCE) program.unreachableCodeRemove();
					}

					IRCompUnit compUnit = new IRCompUnit("test");
					int nodeNumber = 0;
					if(toDrawCFG){
						FileWriter fwCFG = new FileWriter(fileDot);
						fwCFG.write("digraph CFG {\n");
						fwCFG.write(" \"\" [shape = none] \n");
						fwCFG.close();
					}
					for (Function f: program.getFunctions()){
						IRFuncDecl F = f.buildIR();
						F.IRLower();
						if(opt_phase != null &&opt_phase.equals("final")){ //Do all the optimizations
							F.CSE();
							List<IRStmt> a = ((IRSeq)(F.body())).stmts();
							F = new IRFuncDecl(F.name(),new IRSeq(IRFuncDecl.constantPropagation(a)));
							a = ((IRSeq)(F.body())).stmts();
							F = new IRFuncDecl(F.name(),new IRSeq(IRFuncDecl.valueNumbering(a)));
						}
						if(opt_phase == null && toOptimize){
							if (toCSE) {F.CSE();}
							if (toCP) {
								List<IRStmt> a = ((IRSeq)(F.body())).stmts();
								F = new IRFuncDecl(F.name(),new IRSeq(IRFuncDecl.constantPropagation(a)));
							}
							if (toVN) {
								List<IRStmt> a = ((IRSeq)(F.body())).stmts();
								F = new IRFuncDecl(F.name(),new IRSeq(IRFuncDecl.valueNumbering(a)));
							}
						}
						if(toDrawCFG){
							FileWriter fwCFG = new FileWriter(fileDot, true);
							nodeNumber = F.drawCFG(fwCFG,nodeNumber,opt_phase);
							fwCFG.close();
						}
						compUnit.appendFunc(F);
					}
					if(toDrawCFG){
						FileWriter fwCFG = new FileWriter(fileDot, true);
						fwCFG.write("}\n");
						fwCFG.close();
					}
					StringWriter sw = new StringWriter();
					try (PrintWriter pw = new PrintWriter(sw);
							SExpPrinter sp = new CodeWriterSExpPrinter(pw)) {
						compUnit.printSExp(sp);
					}
					{
						CheckConstFoldedIRVisitor cv = new CheckConstFoldedIRVisitor();
						System.out.print("Constant-folded?: ");
						System.out.println(cv.visit(compUnit));
					}

					if (toRunIR){
						// IR interpreter demo
						{
							IRSimulator sim = new IRSimulator(compUnit);
							long result = sim.call("_Imain_paai");
							//System.out.println("RESULT:  " + result);
						}
					}
					fw.write(sw.toString());
				}
				catch(Error e) {
					System.out.println(e.getMessage());
					fw.write(e.getMessage()+"\r\n");

				}
				catch(ArrayInitException ex) {
					fw.write(ex.getMessage());

				}
				fw.close();
			}

			/////////////////////////////////////
			/* Run Target and generate .s file */
			/////////////////////////////////////
			if(toAssembly) {
				String fN = p.OriginFileName.substring(0,p.OriginFileName.length()-2)+"s";
				String fileDot = "";
				if(opt_phase != null && opt_phase.equals("initial")){
					fileDot = p.OriginFileName.substring(0,p.OriginFileName.length()-3)+"_f_initial.dot";
				}else if (opt_phase != null && opt_phase.equals("final")){
					fileDot = p.OriginFileName.substring(0,p.OriginFileName.length()-3)+"_f_final.dot";
				}
				if(assemblyRoot != null){fN = diagnosisRoot + "/" +fN;}
				Reader fr = new FileReader(p.getFile());
				Lexer lexer = new Lexer(fr);
				FileWriter fw = new FileWriter(fN);
				parser par = new parser(lexer);

				SymTab table = new SymTab(null);
				if(libRoot ==null)
				{
					libRoot = new String("");
				}
				try{
					Program program = (Program) par.parse().value;
					for(int a = 0; a < program.getImports().size(); a++) {
						Reader impReader = null;
						try {
							impReader = new FileReader(libRoot + "/" + program.getImports().get(a).getString()+".ixi");
						}
						catch(Exception excp) {
							throw new Error("Interface file " + libRoot + "/" + program.getImports().get(a).getString()+".ixi not found.");
						}
						Lexer impLexer = new Lexer(impReader);
						parser impPar = new parser(impLexer);
						impPar.setState(true);
						Program impProgram = (Program) impPar.parse().value;
						impProgram.firstPass(table);
						impReader.close();
					}
					DispachTable = program.buildDispachTable();
					program.firstPass(table);
					program.secondPass(table);
					program.returnPass();
					if(opt_phase == null && toOptimize){
						if (toCF) program.constantFold();
						if (toUCE) program.unreachableCodeRemove();
					}
					String assembly= ".text";
					IRCompUnit compUnit = new IRCompUnit("test");
					int nodeNumber = 0;
					if(toDrawCFG){
						FileWriter fwCFG = new FileWriter(fileDot);
						fwCFG.write("digraph CFG {\n");
						fwCFG.write(" \"\" [shape = none] \n");
						fwCFG.close();
					}
					for (Function f: program.getFunctions()){
						IRFuncDecl F = f.buildIR();
						F.IRLower();
						if(opt_phase == null && toOptimize){
							if (toCSE) F.CSE();
							if (toCP) {
								List<IRStmt> a = ((IRSeq)(F.body())).stmts();
								F = new IRFuncDecl(F.name(),new IRSeq(IRFuncDecl.constantPropagation(a)));
							}
							if (toVN) {
								List<IRStmt> a = ((IRSeq)(F.body())).stmts();
								F = new IRFuncDecl(F.name(),new IRSeq(IRFuncDecl.valueNumbering(a)));
							}
						}
						compUnit.appendFunc(F);
						assembly += F.getBestTile().getData();

						if(toDrawCFG){
							FileWriter fwCFG = new FileWriter(fileDot, true);
							nodeNumber = F.drawCFG(fwCFG,nodeNumber,opt_phase);
							fwCFG.close();
						}
					}
					for(String s : IRCompUnit.getGlobalVar2Assembly()){
						assembly += s;
					}
					for(String s: IRCompUnit.classConstr2Assembly(DispachTable)){
						assembly += s+"\n";
					}
					for(String s: IRCompUnit.classInit2Assembly(DispachTable)){
						assembly += s+"\n";
					}
					fw.write(assembly);
					if(toDrawCFG){
						FileWriter fwCFG = new FileWriter(fileDot, true);
						fwCFG.write("}\n");
						fwCFG.close();
					}
				}
				catch(Error e) {
					System.out.println(e.getMessage());
					fw.write(e.getMessage()+"\r\n");
				}
				catch(ArrayInitException ex) {
					fw.write(ex.getMessage());
				}
				fw.close();
			}
		}
	}


	///////////////////////////////////////
	/* Empty file checker helper function */
	///////////////////////////////////////

	public static Boolean emptyFile(String fileName, Boolean toLex,Boolean toparse,
			Boolean toGenIR,Boolean toRunIR,Boolean toAssembly) throws IOException{
		FileReader fr = new FileReader(fileName);
		String outFile = fileName.substring(0,fileName.length()-2)+"typed";;
		if (toLex){outFile = fileName.substring(0,fileName.length()-2)+"lexed";}
		else if (toparse){outFile = fileName.substring(0,fileName.length()-2)+"parsed";}
		else if (toGenIR || toRunIR){outFile = fileName.substring(0,fileName.length()-2)+"ir";}
		else if (toAssembly) {outFile = fileName.substring(0,fileName.length()-2)+"s";}
		BufferedReader br = new BufferedReader(fr); 
		FileWriter fw = new FileWriter(outFile); 
		String line;
		while((line = br.readLine()) != null)
		{   line = line.trim(); // remove leading and trailing whitespace
		if (!line.equals("")) // don't write out blank lines
		{fw.write(line, 0, line.length());}
		}
		fr.close();
		fw.close();
		@SuppressWarnings("resource")
		Reader reader = new FileReader(outFile);
		if (reader.read() == -1) { 
			return true;
		}
		return false; 

	}
	//REMEMBER!!! ADD IRRUN
	public static void printUsage() {
		System.out.println("xic");
		System.out.println("SYNOPSIS");
		System.out.println("xic [--lex] [--parse] [--typecheck] [--irgen] [--help] [-sourcepath path] [-libpath path] [-D] [-O] [file ...]");
		System.out.println("DESCRIPTION");
		System.out.println("Compile programming code. Transfer input string into a set of tokens.");
		System.out.println("--help: A synopsis of options lists all possible options along with brief descriptions.");
		System.out.println("--lex: Generate output from lexical analysis. For each source file named filename.xi,");
		System.out.println("an output file named filename.lexed is generated to provide the result of lexing");
		System.out.println("the source file.");
		System.out.println("--parse: Generate output from syntactic analysis.");
		System.out.println("--irgen: Generate intermediate code.");
		System.out.println("--irrun Generate and interpret intermediate code.");
		System.out.println("--typecheck: Generate output from semantic analysis.");
		System.out.println("-sourcepath <path>: specify the file path of the xi files to be compiled");
		System.out.println("-libpath <path>: specify the file path of the library interfaces files that will be used");
		System.out.println("-D <path>: specify the file path where diagnostic files will be generated");
		System.out.println("-O: Turn off optimizations.");
		System.out.println("-d <path>: Specify where to place generated assembly output files");
		System.out.println("-target <OS>: Specify the operating system for which to generate code.");
		System.out.println("OS may be one of linux, windows, and macos.");
	}

}
