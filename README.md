# LL-1-Compiler-for-C-Language-Generating-Assembly-Code-and-LLVM-Parsing

Description:
This project is an LL(1) Compiler implementation for the C language, written in Java. It encompasses a scanner and parser that effectively handle the grammar of the C language and generate corresponding Assembly codes. In addition, the LL(1) parser also utilizes an LLVM parser specifically designed to handle boolean expressions that cannot be parsed by the LL(1) parser.

Key Features:

Grammar Handling: The LL(1) Compiler handles the fundamental features of the C language, implementing a complete set of grammar rules defined by the C language specification.
Assembly Code Generation: The compiler generates Assembly code as an output, providing a low-level representation of the C program.
Boolean Expressions with LLVM Parser: To overcome limitations in parsing boolean expressions with the LL(1) parser, the project incorporates an LLVM parser dedicated to parsing and handling boolean expressions effectively
