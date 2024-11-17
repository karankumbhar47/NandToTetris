package com.compiler.CompilationClasses;

import com.compiler.CompilationEngine;
import com.compiler.CustomExceptions.SyntaxExceptions;
import com.compiler.JackTokenizer;
import com.compiler.SymbolTable;
import com.compiler.Utils.EnumClass;
import com.compiler.VMWriter;

import java.io.IOException;

public class CompileExpressionList {
    private final CompilationEngine parent;

    public CompileExpressionList(CompilationEngine parent) {
        this.parent = parent;
    }

    public int compile() throws IOException, SyntaxExceptions {
        SymbolTable symbolTable = parent.symbolTable;
        JackTokenizer tokenizer = parent.tokenizer;
        VMWriter vmWriter = parent.vmWriter;

        int argCount = 0;
        parent.indentationLevel++;

        if (!(tokenizer.tokenType() == EnumClass.TokenType.SYMBOL && tokenizer.symbol() == ')')) {
            new CompileExpression(parent).compile();
            argCount++;

            while (tokenizer.tokenType() == EnumClass.TokenType.SYMBOL && tokenizer.symbol() == ',') {
                tokenizer.advance();
                new CompileExpression(parent).compile();
                argCount++;
            }
        }


        if (!(tokenizer.tokenType() == EnumClass.TokenType.SYMBOL && tokenizer.symbol() == ')'))
            throw new SyntaxExceptions.InvalidClosingBracketsException();

        tokenizer.advance();
        parent.indentationLevel--;
        return argCount;
    }
}
