Code for an IR implementation, including an IR simulator.

For Cornell CS 4120, spring 2016.

A test program that builds an IR tree, and then executes it, is
found in src/edu/cornell/cs/cs4120/xic/ir/interpret/Main.java.

Updates:

3/9: Removed the NOT operation. This can be simulated with XOR or NEQ,
     depending on what flavor of NOT is wanted.
3/8: New version released with support for multiple returns and
     s-expression parsing.
