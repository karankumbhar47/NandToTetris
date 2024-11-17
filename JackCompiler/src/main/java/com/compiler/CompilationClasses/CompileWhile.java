package com.compiler.CompilationClasses;

import com.compiler.CompilationEngine;
import com.compiler.Utils.SyntaxExceptions;
import com.compiler.JackTokenizer;
import com.compiler.Utils.VMUtils;
import com.compiler.VMWriter;

import java.io.IOException;

public class CompileWhile {
    private final CompilationEngine parent;
    private final String className;

    public CompileWhile(CompilationEngine parent,String className) {
        this.parent = parent;
        this.className = className;
    }

    public void compile() throws IOException, SyntaxExceptions {
        // while
        JackTokenizer tokenizer = parent.tokenizer;
        VMWriter vmWriter = parent.vmWriter;

        String labelStart = "WHILE_START_" + VMUtils.generateUniqueLabel();
        String labelEnd = "WHILE_END_" + VMUtils.generateUniqueLabel();

        vmWriter.writeLabel(labelStart);

        tokenizer.advance(); //(
        parent.ensureSymbol('(', "Expected `(` after the `while` keyword");

        tokenizer.advance(); // expression inside bracket
        parent.compileExpression();

        vmWriter.writeArithmetic("not");
        vmWriter.writeIf(labelEnd);

        parent.ensureSymbol(')', "Expected `)` at the end of while condition");

        tokenizer.advance(); // {
        parent.ensureSymbol('{', "Expected `{` at start of the while Body");

        tokenizer.advance(); // statements inside while
        parent.compileStatements(className);

        parent.ensureSymbol('}', "Expected `}` at end of the while Body");
        vmWriter.writeGoto(labelStart);
        vmWriter.writeLabel(labelEnd);
        tokenizer.advance();
    }
}
