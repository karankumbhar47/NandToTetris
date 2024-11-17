package com.compiler.CompilationClasses;

import com.compiler.CompilationEngine;
import com.compiler.CustomExceptions.SyntaxExceptions;
import com.compiler.JackTokenizer;
import com.compiler.SymbolTable;
import com.compiler.Utils.EnumClass;

import java.io.IOException;

public class CompileSubroutine {
    private final CompilationEngine parent;
    private final String className;

    public CompileSubroutine(CompilationEngine parent, String className) {
        this.parent = parent;
        this.className = className;
    }

    public void compile() throws IOException, SyntaxExceptions{
        SymbolTable symbolTable = parent.symbolTable;
        JackTokenizer tokenizer = parent.tokenizer;

        symbolTable.reset();
        // 'constructor', 'function', or 'method'
        parent.ensureFunctionType();
        String subroutineType = tokenizer.keyWord().toString().toLowerCase();

        // return type
        tokenizer.advance();
        if (!((tokenizer.tokenType() == EnumClass.TokenType.KEYWORD &&
                (tokenizer.keyWord().equals(EnumClass.KeywordType.VOID)
                        || (tokenizer.keyWord().equals(EnumClass.KeywordType.INT))
                        || (tokenizer.keyWord().equals(EnumClass.KeywordType.CHAR))
                        || (tokenizer.keyWord().equals(EnumClass.KeywordType.BOOLEAN))
                )) || (tokenizer.tokenType() == EnumClass.TokenType.IDENTIFIER))
        )
            throw new IllegalArgumentException("Expected return type (void, int, char, boolean, or class name) in subroutineDec.");

        // Subroutine name
        tokenizer.advance();
        parent.ensureIdentifier("Expected subroutine name identifier in subroutineDec.");
        String subroutineName = tokenizer.identifier();

        // '(' and parameter list
        tokenizer.advance();
        parent.ensureSymbol('(',"Expected '(' before parameterList in subroutineDec.");

        if (subroutineType.equals("method"))
            symbolTable.define("this", className, SymbolTable.Kind.ARGUMENT);

        // parameter list
        tokenizer.advance();
        parent.compileParameterList();

        // Process ")"
        parent.ensureSymbol(')',"Expected ')' after parameterList in subroutineDec.");

        // Subroutine body
        tokenizer.advance();
        parent.compileSubroutineBody(className, subroutineName, subroutineType);
    }
}
