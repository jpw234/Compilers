package commandLine;

import commandLine.Lexer;
import commandLine.Lexer.Token;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class xic {
	public static final String INPUT_HELPER = "--help";
	public static final String INPUT_LEX = "--lex";
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
		}

		public String getRoot() {
			return root == null ? null : root.toAbsolutePath().toString();
		}
		public String getFile() {
			return file.toAbsolutePath().toString();
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


	public static final void main(String[] args) throws IOException {
		ArrayList<CodePath> pathArgs = new ArrayList<>();
		ArrayList<CodePath> codeToCompile = new ArrayList<>();
		String currentRoot = directory;
		Boolean toCompile = false;

		// Use All The Arguments
		for(int i = 0;i < args.length;i++) {
			switch(args[i].toLowerCase()) {
			case "-p" :
				i++;
				if(i < args.length) currentRoot = args[i];
				break;
			case INPUT_HELPER :
				//help function
				printUsage();
				break;
			case INPUT_LEX:
				// Use The CWD
				toCompile = true;
				break;
			default:
				// This Must Be A File
				pathArgs.add(new CodePath(currentRoot, args[i]));
				break;
			}
		}
		if (toCompile){
			if(pathArgs.size() < 1) {
				// Attempt To Compile All the Codes
				pathArgs.add(new CodePath(currentRoot, "."));

				// Display What's Going To Go Down
				System.out.println("\nAttempting To Compile All Files");
			}
			// Expand All The Possible Codes
			for(CodePath p : pathArgs) {
				// Add All The Files In The
				File f = p.file.toFile();
				if(f.isDirectory()) {
					for(File _f : f.listFiles()) {
						// We Only Want XML Files
						if(!_f.getPath().endsWith(".xi")) continue;
						codeToCompile.add(new CodePath(p.getRoot(), _f.toPath().toAbsolutePath().toString()));
					}
				}
				else {
					// We Only Want XML Files
					if(!f.getPath().endsWith(".xi")) continue;
					// Just A Single Scene
					codeToCompile.add(p);
				}
			}
			System.out.println("Attempting To Compile " + codeToCompile.size() + " File(s)");

			for (CodePath p: codeToCompile){
				System.out.println(p.getFile());
				
				Reader fr = new FileReader(p.getFile());
				System.out.println(fr.read());
				Lexer lexer = new Lexer(fr);
				while (lexer.yylex() != null){
					Token tok = lexer.yylex();
					System.out.println(tok.getLine() + ":" + tok.getCol() + " " + tok.getType().toString());
				}
			}
		}

		
	}

	public static void printUsage() {
		System.out.println("xic");
		System.out.println("SYNOPSIS");
		System.out.println("xic [--lex] [--help] [-testpath path] [file ...]");
		System.out.println("DESCRIPTION");
		System.out.println("Compile programming code. Transfer input string into a set of tokens.");
		System.out.println("--help: A synopsis of options lists all possible options along with brief descriptions.");
		System.out.println("--lex: Generate output from lexical analysis. For each source file named filename.xi,");
		System.out.println("an output file named filename.lexed is generated to provide the result of lexing");
		System.out.println("the source file.");

	}
}
