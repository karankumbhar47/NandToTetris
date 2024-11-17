package com.compiler.CompilationClasses;

import com.compiler.CompilationEngine;
import com.compiler.CustomExceptions.SyntaxExceptions;
import com.compiler.JackTokenizer;
import com.compiler.SymbolTable;
import com.compiler.Utils.EnumClass;
import com.compiler.VMWriter;

import java.io.IOException;

public class CompileReturn {
    private final CompilationEngine parent;

    public CompileReturn(CompilationEngine parent) {
        this.parent = parent;
    }

    public void compile() throws IOException, SyntaxExceptions {
        SymbolTable symbolTable = parent.symbolTable;
        JackTokenizer tokenizer = parent.tokenizer;
        VMWriter vmWriter = parent.vmWriter;

        // return
        parent.indentationLevel++;

        tokenizer.advance(); // either ";" or expression
        if (!(tokenizer.tokenType() == EnumClass.TokenType.SYMBOL && tokenizer.symbol() == ';'))
            new CompileExpression(parent).compile();
        else
            vmWriter.writePush("constant",0);

        if (!(tokenizer.tokenType() == EnumClass.TokenType.SYMBOL && tokenizer.symbol() == ';'))
            throw new SyntaxExceptions.InvalidClosingBracketsException();

        vmWriter.writeReturn();
        tokenizer.advance();
        parent.indentationLevel--;

    }
}
