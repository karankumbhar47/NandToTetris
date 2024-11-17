package com.compiler.CompilationClasses;

import com.compiler.CompilationEngine;
import com.compiler.Utils.SyntaxExceptions;
import com.compiler.JackTokenizer;
import com.compiler.SymbolTable;
import com.compiler.Utils.EnumClass;

import java.io.IOException;

public class CompileClassVarDec {
    private final CompilationEngine parent;

    public CompileClassVarDec(CompilationEngine parent) {
        this.parent = parent;
    }

    public void compile() throws IOException, SyntaxExceptions{
        SymbolTable symbolTable = parent.symbolTable;
        JackTokenizer tokenizer = parent.tokenizer;

        //'static' or 'field'
        parent.ensureClassVarType("Expected a class variable type, such as `static` or `field`");
        String kind = tokenizer.keyWord().toString().toLowerCase();
        SymbolTable.Kind varKind = kind.equals("static") ? SymbolTable.Kind.STATIC : SymbolTable.Kind.THIS;

        // var type
        tokenizer.advance();
        parent.ensureType("Expected variable type like `int` or `boolean` or `char` or valid type identifier");
        String varType = tokenizer.tokenType() == EnumClass.TokenType.KEYWORD ? tokenizer.keyWord().toString().toLowerCase() : tokenizer.identifier();

        // first var name
        tokenizer.advance();
        parent.ensureIdentifier("Expected valid identifier for variable name");
        symbolTable.define(tokenizer.identifier(), varType, varKind);

        // Process additional variable names if separated by commas
        while (true) {
            tokenizer.advance();
            if (!(tokenizer.tokenType() == EnumClass.TokenType.SYMBOL && tokenizer.symbol() == ','))
                break;

            tokenizer.advance(); // var name
            parent.ensureIdentifier("Expected valid identifier for variable name");

            symbolTable.define(tokenizer.identifier(), varType, varKind);
        }

        // ';'
        parent.ensureSymbol(';', "Expected `;` at the end of the statement(variable declaration)");
        tokenizer.advance();
    }
}
