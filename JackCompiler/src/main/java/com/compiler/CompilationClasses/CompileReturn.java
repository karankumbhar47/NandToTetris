package com.compiler.CompilationClasses;

import com.compiler.CompilationEngine;
import com.compiler.Utils.SyntaxExceptions;
import com.compiler.JackTokenizer;
import com.compiler.Utils.EnumClass;
import com.compiler.VMWriter;

import java.io.IOException;

public class CompileReturn {
    private final CompilationEngine parent;

    public CompileReturn(CompilationEngine parent) {
        this.parent = parent;
    }

    public void compile() throws IOException, SyntaxExceptions {
        // return
        JackTokenizer tokenizer = parent.tokenizer;
        VMWriter vmWriter = parent.vmWriter;

        tokenizer.advance(); // either ";" or expression
        if (!(tokenizer.tokenType() == EnumClass.TokenType.SYMBOL && tokenizer.symbol() == ';'))
            parent.compileExpression();
        else
            vmWriter.writePush("constant",0);

        parent.ensureSymbol(';',"Expected `;` at the end of return statement");
        vmWriter.writeReturn();
        tokenizer.advance();
    }
}
