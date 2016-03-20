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
	public static final String INPUT_LEX = "--lex";
	public static final String INPUT_PARSE = "--parse";
	public static final String INPUT_TYPECHECK = "--typecheck";
	public static final String INPUT_SOURCEPATH = "-sourcepath";
	public static final String INPUT_DIAGNOSIS_PATH = "-d";
	public static final String INPUT_LIBRARY_PATH = "-libpath";
	public static final String INPUT_IRGEN = "--irgen";
	public static final String INPUT_IRRUN = "--irrun";
	public static final String INPUT_OPTIMIZATION = "-O";
	public static int MINIMUM_ARG_COUNT = 2; 


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
		Boolean toLex = false;
		Boolean useHelp = false;
		Boolean toParse = false;
		Boolean toTypecheck = false;
		Boolean toGenIR = false;
		Boolean toRunIR = false;
		Boolean toOptimize = false;

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
			case INPUT_LIBRARY_PATH :
				i++;
				if(i < args.length) libRoot = args[i];
				break;
			case INPUT_HELPER :
				//help function
				useHelp = true;
				printUsage();
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
			case INPUT_OPTIMIZATION:
				toOptimize = true;
				break;
			default:
				// This Must Be A File
				pathArgs.add(new CodePath(currentRoot, args[i]));
				break;
			}
		}
		if ((toLex || toParse || toTypecheck || toGenIR || toRunIR || toOptimize) && !useHelp){
			lex_parse(toLex, toParse,toTypecheck,toGenIR,toRunIR,toOptimize,
					  pathArgs,codeToCompile,sourceRoot,diagnosisRoot,libRoot);
		}



	}
	public static void lex_parse(
			Boolean toLex, Boolean toParse,Boolean toTypecheck,
			Boolean toGenIR, Boolean toRunIR, Boolean toOptimize,
			ArrayList<CodePath> pathArgs,ArrayList<CodePath> codeToCompile ,
			String sourceRoot,String diagnosisRoot,String libRoot) throws Exception{
		if(pathArgs.size() < 1) {
			// Attempt To Compile All the Codes
			pathArgs.add(new CodePath(sourceRoot, "."));

			// Display What's Going To Go Down
			//System.out.println("\nAttempting To Compile All Files");
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
			//DETECT IF THIS EXISTS 
			try {
				Reader abc = new FileReader(p.getFile());
				abc.close();}
			catch(IOException ioe){
				System.err.println("No such File in Directory");
				return;}
			Boolean isempty = emptyFile(p.OriginFileName,toLex,toParse, toGenIR );
			if (isempty) break;
			
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
						fw.write(String.format("%d:%d %s\n", numLine, numCol, lineVal));
						break;}
					else{lineVal = lexer.yytext();}
					//WRITE IN THE FILES
					String s = String.format("%d:%d %s\n", numLine, numCol, lineVal);
					fw.write(s);	
				}
				fw.close();
			}
			if(toParse){
				String fN = p.OriginFileName.substring(0,p.OriginFileName.length()-2)+"parsed";
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
			
			if(toTypecheck) {
				String fN = p.OriginFileName.substring(0,p.OriginFileName.length()-2)+"typed";
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
							impReader = new FileReader(libRoot + program.getImports().get(a).getString()+".ixi");
						}
						catch(Exception excp) {
							throw new Error("Interface file " + libRoot + program.getImports().get(a).getString()+".ixi not found.");
						}
						Lexer impLexer = new Lexer(impReader);
						parser impPar = new parser(impLexer);
						impPar.setState(true);
						Program impProgram = (Program) impPar.parse().value;
						impProgram.firstPass(table);
						impReader.close();
					}
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
			
			
			if(toGenIR) {
				String fN = p.OriginFileName.substring(0,p.OriginFileName.length()-2)+"ir";
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
							impReader = new FileReader(libRoot + program.getImports().get(a).getString()+".ixi");
						}
						catch(Exception excp) {
							throw new Error("Interface file " + libRoot + program.getImports().get(a).getString()+".ixi not found.");
						}
						Lexer impLexer = new Lexer(impReader);
						parser impPar = new parser(impLexer);
						impPar.setState(true);
						Program impProgram = (Program) impPar.parse().value;
						impProgram.firstPass(table);
						impReader.close();
					}
					program.firstPass(table);
					program.secondPass(table);
					program.returnPass();
					if(toOptimize){
						program.constantFold();
					}
			        IRCompUnit compUnit = new IRCompUnit(p.getFileName());
			        for (Function f: program.getFunctions()){
			        	IRFuncDecl F = f.buildIR();
			        	F.IRLower();
			        	compUnit.appendFunc(F);
			        }
			        StringWriter sw = new StringWriter();
			        try (PrintWriter pw = new PrintWriter(sw);
			             SExpPrinter sp = new CodeWriterSExpPrinter(pw)) {
			            compUnit.printSExp(sp);
			        }
			        {
			            CheckCanonicalIRVisitor cv = new CheckCanonicalIRVisitor();
			            System.out.print("Canonical?: ");
			            System.out.println(cv.visit(compUnit));
			        }
					fw.write(sw.toString());
					System.out.println(sw.toString());
			        // IR constant-folding checker demo
			        {
			            CheckConstFoldedIRVisitor cv = new CheckConstFoldedIRVisitor();
			            System.out.print("Constant-folded?: ");
			            System.out.println(cv.visit(compUnit));
			        }
					// IR interpreter demo
			        {
			            IRSimulator sim = new IRSimulator(compUnit);
			            long result = sim.call("main");
			            System.out.println("b(2,1) == " + result);
			        }
				}
				catch(Error e) {
					System.out.println(e.getMessage());
					fw.write(e.getMessage()+"\r\n");
				}
				catch(ArrayInitException ex) {
					fw.write(ex.getMessage());
				}
				//System.out.println("IRGEN file(s) generated!"); XIC should not send anything to STDOUT IF THERE ARE NO ERRORS
				fw.close();
			}
			
			
		}

	}

	
	
	
	public static Boolean emptyFile(String fileName, Boolean toLex,Boolean toparse,
								   Boolean toGenIR) throws IOException{
		FileReader fr = new FileReader(fileName);
		String outFile = fileName.substring(0,fileName.length()-2)+"typed";;
		if (toLex){outFile = fileName.substring(0,fileName.length()-2)+"lexed";}
		else if (toparse){outFile = fileName.substring(0,fileName.length()-2)+"parsed";}
		else if (toGenIR){outFile = fileName.substring(0,fileName.length()-2)+"ir";}
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
	}
}
