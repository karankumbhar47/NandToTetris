package com.compiler.CompilationClasses;

import com.compiler.CompilationEngine;
import com.compiler.CustomExceptions.SyntaxExceptions;
import com.compiler.JackTokenizer;
import com.compiler.SymbolTable;
import com.compiler.Utils.EnumClass;
import com.compiler.VMWriter;

import java.io.IOException;

public class CompileStatements {
    private final CompilationEngine parent;
    private final String className;

    public CompileStatements(CompilationEngine parent,String className) {
        this.parent = parent;
        this.className = className;
    }

    public void compile() throws IOException, SyntaxExceptions {
        SymbolTable symbolTable = parent.symbolTable;
        JackTokenizer tokenizer = parent.tokenizer;
        VMWriter vmWriter = parent.vmWriter;


        parent.indentationLevel++;

        statementsLoop:
        while (tokenizer.hasMoreTokens() && tokenizer.tokenType() == EnumClass.TokenType.KEYWORD) {
            switch (tokenizer.keyWord()) {
                case LET:
                    new CompileLet(parent).compile();
                    break;
                case DO:
                    new CompileDo(parent,className).compile();
                    break;
                case WHILE:
                    new CompileWhile(parent,className).compile();
                    break;
                case RETURN:
                    new CompileReturn(parent).compile();
                    break;
                case IF:
                    new CompileIf(parent,className).compile();
                    break;
                default:
                    break statementsLoop;
            }
        }

        parent.indentationLevel--;
    }
}
