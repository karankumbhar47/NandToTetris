package com.compiler.CompilationClasses;

import com.compiler.CompilationEngine;
import com.compiler.Utils.SyntaxExceptions;
import com.compiler.JackTokenizer;
import com.compiler.Utils.EnumClass;

import java.io.IOException;

public class CompileExpressionList {
    private final CompilationEngine parent;

    public CompileExpressionList(CompilationEngine parent) {
        this.parent = parent;
    }

    public int compile() throws IOException, SyntaxExceptions {
        JackTokenizer tokenizer = parent.tokenizer;
        int argCount = 0;

        if (!(tokenizer.tokenType() == EnumClass.TokenType.SYMBOL && tokenizer.symbol() == ')')) {
            parent.compileExpression();
            argCount++;

            while (tokenizer.tokenType() == EnumClass.TokenType.SYMBOL && tokenizer.symbol() == ',') {
                tokenizer.advance();
                parent.compileExpression();
                argCount++;
            }
        }

        parent.ensureSymbol(')',"Expected `)` at the end of argument list");
        tokenizer.advance();
        return argCount;
    }
}
