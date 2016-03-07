0. Unzip the PA2Submission.zip and move into directory "PA2Submission"

1. Run Xic
- Execute "./xth/xth -compilerpath $(pwd) -testpath xth/tests/pa2 xth/tests/pa2/xthScript"

Note:
- "xic-build" executable file would compile src/compiler_ww424/lexer.flex using jflex and generate src/compiler_ww424/Lexer.java
- Then it would compile src/compiler_ww424/parser.cup using cup and generate src/compiler_ww424/parser.java & src/compiler_ww424/sym.java
- Finally it would compile all the .java file and generate .jar and then run the testing
