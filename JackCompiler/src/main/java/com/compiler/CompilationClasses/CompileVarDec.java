package com.compiler.CompilationClasses;

import com.compiler.CompilationEngine;
import com.compiler.Utils.SyntaxExceptions;
import com.compiler.JackTokenizer;
import com.compiler.SymbolTable;
import com.compiler.Utils.EnumClass;

import java.io.IOException;

public class CompileVarDec {
    private final CompilationEngine parent;

    public CompileVarDec(CompilationEngine parent) {
        this.parent = parent;
    }

    public void compile() throws IOException, SyntaxExceptions {
        //var
        SymbolTable symbolTable = parent.symbolTable;
        JackTokenizer tokenizer = parent.tokenizer;

        tokenizer.advance(); // type of variable
        if (!(tokenizer.tokenType() == EnumClass.TokenType.KEYWORD || tokenizer.tokenType() == EnumClass.TokenType.IDENTIFIER))
            throw new SyntaxExceptions.InvalidFunctionVariableKeywordException(tokenizer.getContext());
        String type = tokenizer.tokenType() == EnumClass.TokenType.KEYWORD ? tokenizer.keyWord().toString() : tokenizer.identifier();

        tokenizer.advance(); // name of variable
        parent.ensureIdentifier("Expected valid identifier for variable name");
        String varName = tokenizer.identifier();
        symbolTable.define(varName, type, SymbolTable.Kind.LOCAL);

        tokenizer.advance(); //, or ;
        while (tokenizer.tokenType() == EnumClass.TokenType.SYMBOL && tokenizer.symbol()==',') {
            tokenizer.advance(); // var name

            parent.ensureIdentifier("Expected valid identifier for variable name");
            varName = tokenizer.identifier();
            symbolTable.define(varName, type, SymbolTable.Kind.LOCAL);

            tokenizer.advance(); //, or ;
        }

        parent.ensureSymbol(';',"Expected `;` at the end of Variable declaration");
        tokenizer.advance();
    }
}
