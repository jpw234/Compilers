package compiler_ww424;
import java.io.File;
import java_cup.runtime.ComplexSymbolFactory.ComplexSymbol;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import compiler_ww424.Lexer.Token;
import java_cup.runtime.*;

public class Compiler {
	public static final String INPUT_HELPER = "--help";
	public static final String INPUT_LEX = "--lex";
	public static final String INPUT_PARSE = "--parse";
	public static final String INPUT_SOURCEPATH = "-sourcepath";
	public static final String INPUT_DIAGNOSIS_PATH = "-d";
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
		String diagnosisRoot = null;
		Boolean toCompile = false;
		Boolean useHelp = false;
		Boolean toParse = false;

		// Use All The Arguments
		for(int i = 0;i < args.length;i++) {
			switch(args[i].toLowerCase()) {
			case INPUT_SOURCEPATH :
				i++;
				if(i < args.length) sourceRoot = args[i];
				break;
			case INPUT_DIAGNOSIS_PATH :
				i++;
				if(i < args.length) diagnosisRoot = args[i];
				break;
			case INPUT_HELPER :
				//help function
				useHelp = true;
				printUsage();
				break;
			case INPUT_LEX:
				// Use The CWD
				toCompile = true;
				break;
			case INPUT_PARSE:
				toCompile = true;
				toParse = true;
				break;

			default:
				// This Must Be A File
				pathArgs.add(new CodePath(currentRoot, args[i]));
				break;
			}
		}
		if (toCompile && !useHelp){
			lex_parse(toParse,pathArgs,codeToCompile,sourceRoot,diagnosisRoot);
		}



	}
	public static void lex_parse(Boolean toParse,ArrayList<CodePath> pathArgs,ArrayList<CodePath> codeToCompile ,
			String sourceRoot,String diagnosisRoot) throws Exception{
		if(pathArgs.size() < 1) {
			// Attempt To Compile All the Codes
			pathArgs.add(new CodePath(sourceRoot, "."));

			// Display What's Going To Go Down
			System.out.println("\nAttempting To Compile All Files");
		}
		// Expand All The Possible Codes
		for(CodePath p : pathArgs) {
			// Add All The Files In The List

			File f = p.file.toFile();
			if(f.isDirectory()) {
				for(File _f : f.listFiles()) {
					// We Only Want XML Files
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
				// We Only Want XML Files
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

		System.out.println("Attempting To Compile " + codeToCompile.size() + " File(s)");

		for (CodePath p: codeToCompile){
			//DETECT IF THIS EXISTS 
			try {
				Reader abc = new FileReader(p.getFile());
				abc.close();
			}
			catch(IOException ioe){
				System.err.println("No such File in Directory");
				return;
			}
			String fileName = p.OriginFileName.substring(0,p.OriginFileName.length()-2)+"lexed";
			if(diagnosisRoot != null){
				fileName = diagnosisRoot + "/" +fileName;
			}

			Reader fr = new FileReader(p.getFile());
			FileWriter fw = new FileWriter(fileName);
			Lexer lexer = new Lexer(fr);


			for(Token tok = (Token) lexer.next_token(); tok.sym!=0; tok = (Token)lexer.next_token()){
				int numLine = tok.getLine() + 1 ;
				int numCol = tok.getCol() + 1; 
				String lineVal = new String ();

				if (tok.sym == sym.INTEGER){
					lineVal = "integer"+" "+ lexer.yytext();
				}
				else if (tok.sym == sym.STRING){
					lineVal = "string"+" "+ tok.value;
				}
				else if (tok.sym == sym.CHARACTER) {
					lineVal = "character"+" "+ tok.value;
				}
				else if (tok.sym == sym.ID) {
					lineVal = "id"+" "+ tok.value;
				}
				else if (tok.sym == sym.error){
					lineVal = (String) tok.value;
					fw.write(String.format("%d:%d %s\n", numLine, numCol, lineVal));
					break;
				}
				else{
					lineVal = lexer.yytext();
				}
				//WRITE IN THE FILES

				String s = String.format("%d:%d %s\n", numLine, numCol, lineVal);
				//System.out.println(s);
				fw.write(s);


			}
			if(toParse){
				fr = new FileReader(p.getFile());
				String fN = p.OriginFileName.substring(0,p.OriginFileName.length()-2)+"parsed";
				FileWriter fwp = new FileWriter(fN);
				lexer = new Lexer(fr);

				try{
					parser par = new parser(lexer);
					//System.out.println(par.parse().value);
					fwp.write(par.parse().value.toString());
				}catch(Error e ){		
					//System.out.println(e.getMessage());
					fwp.write(e.getMessage());
				}
				fwp.close();
			}
			fw.close();

		}

	}





	public static void printUsage() {
		System.out.println("xic");
		System.out.println("SYNOPSIS");
		System.out.println("xic [--lex] [--help] [-p path] [file ...]");
		System.out.println("DESCRIPTION");
		System.out.println("Compile programming code. Transfer input string into a set of tokens.");
		System.out.println("--help: A synopsis of options lists all possible options along with brief descriptions.");
		System.out.println("--lex: Generate output from lexical analysis. For each source file named filename.xi,");
		System.out.println("an output file named filename.lexed is generated to provide the result of lexing");
		System.out.println("the source file.");
		System.out.println("-p setting the file path of the xi files to be compiled");

	}
}