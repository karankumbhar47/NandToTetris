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
        parent.indentationLevel++;

        // 'constructor', 'function', or 'method'
        if (!(tokenizer.tokenType() == EnumClass.TokenType.KEYWORD &&
                (tokenizer.keyWord().equals(EnumClass.KeywordType.CONSTRUCTOR) ||
                        tokenizer.keyWord().equals(EnumClass.KeywordType.FUNCTION) ||
                        tokenizer.keyWord().equals(EnumClass.KeywordType.METHOD)))) {
            throw new IllegalArgumentException("Expected 'constructor', 'function', or 'method' at start of subroutineDec.");
        }
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
        if (!(tokenizer.tokenType() == EnumClass.TokenType.IDENTIFIER)) {
            throw new IllegalArgumentException("Expected subroutine name identifier in subroutineDec.");
        }
        String subroutineName = tokenizer.identifier();

        // '(' and parameter list
        tokenizer.advance();
        if (!(tokenizer.tokenType() == EnumClass.TokenType.SYMBOL && tokenizer.symbol().equals('(')))
            throw new IllegalArgumentException("Expected '(' before parameterList in subroutineDec.");

        if (subroutineType.equals("method"))
            symbolTable.define("this", className, SymbolTable.Kind.ARGUMENT);

        // parameter list
        tokenizer.advance();
        new CompileParameterList(parent).compile();

        // Process ")"
        if (!(tokenizer.tokenType() == EnumClass.TokenType.SYMBOL && tokenizer.symbol().equals(')')))
            throw new IllegalArgumentException("Expected ')' after parameterList in subroutineDec.");


        // Subroutine body
        tokenizer.advance();
        new CompileSubroutineBody(parent,className, subroutineName, subroutineType).compile();

        parent.indentationLevel--;

    }

}
