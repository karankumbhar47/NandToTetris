package com.compiler.CompilationClasses;

import com.compiler.CompilationEngine;
import com.compiler.CustomExceptions.SyntaxExceptions;
import com.compiler.JackTokenizer;
import com.compiler.Utils.EnumClass;

import java.io.IOException;

public class CompileStatements {
    private final CompilationEngine parent;
    private final String className;

    public CompileStatements(CompilationEngine parent,String className) {
        this.parent = parent;
        this.className = className;
    }

    public void compile() throws IOException, SyntaxExceptions {
        JackTokenizer tokenizer = parent.tokenizer;

        statementsLoop:
        while (tokenizer.hasMoreTokens() && tokenizer.tokenType() == EnumClass.TokenType.KEYWORD) {
            switch (tokenizer.keyWord()) {
                case LET:
                    parent.compileLet();
                    break;
                case DO:
                    parent.compileDo(className);
                    break;
                case WHILE:
                    parent.compileWhile(className);
                    break;
                case RETURN:
                    parent.compileReturn();
                    break;
                case IF:
                    parent.compileIf(className);
                    break;
                default:
                    break statementsLoop;
            }
        }
    }
}
