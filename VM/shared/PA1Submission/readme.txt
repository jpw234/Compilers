0. Before starting, make sure the JDK and JRE are installed as javac, and java are needed. 
JFlex is included in the submition and it is stored in the jflex directory.

1. Generate the lexer source file using jlfex:
- move to the jflex binary location using "cd jflex/bin/"
- use the command "jflex ../../compiler_ww424/lexer.flex" to generate Lexer.java.
- move back to the original directory ("cd ../../") and check to see that Lexer.java 
has been created in the commandLine directory.

2. Compile the java files into java bytecode.
- use the command "javac compiler_ww424/Compiler.java compiler_ww424/Lexer.java" to compile the program

3. Run Xic
- use the bash wrapper "xic" in the root on this directory to run the lexer.
- the main/ entrypoint class is compiler_ww424.Compiler