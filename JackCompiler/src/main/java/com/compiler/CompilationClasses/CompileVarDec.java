package com.compiler.CompilationClasses;

import com.compiler.CompilationEngine;
import com.compiler.CustomExceptions.SyntaxExceptions;
import com.compiler.JackTokenizer;
import com.compiler.SymbolTable;
import com.compiler.Utils.EnumClass;
import com.compiler.VMWriter;

import java.io.IOException;

public class CompileVarDec {
    private final CompilationEngine parent;

    public CompileVarDec(CompilationEngine parent) {
        this.parent = parent;
    }

    public void compile() throws IOException, SyntaxExceptions {
        SymbolTable symbolTable = parent.symbolTable;
        JackTokenizer tokenizer = parent.tokenizer;
        VMWriter vmWriter = parent.vmWriter;

        //var
        parent.indentationLevel++;

        tokenizer.advance(); // type of variable
        if (!(tokenizer.tokenType() == EnumClass.TokenType.KEYWORD || tokenizer.tokenType() == EnumClass.TokenType.IDENTIFIER))
            throw new SyntaxExceptions.InvalidFunctionVariableKeywordException();

        String type = tokenizer.tokenType() == EnumClass.TokenType.KEYWORD ? tokenizer.keyWord().toString() : tokenizer.identifier();

        tokenizer.advance(); // name of variable
        if (tokenizer.tokenType() != EnumClass.TokenType.IDENTIFIER)
            throw new SyntaxExceptions.VariableNameNotFoundException();

        String varName = tokenizer.identifier();
        symbolTable.define(varName, type, SymbolTable.Kind.LOCAL);

        tokenizer.advance(); //, or ;
        while (tokenizer.tokenType() == EnumClass.TokenType.SYMBOL && tokenizer.symbol()==',') {
            tokenizer.advance(); // var name

            if (tokenizer.tokenType() != EnumClass.TokenType.IDENTIFIER)
                throw new SyntaxExceptions.VariableNameNotFoundException();
            varName = tokenizer.identifier();
            symbolTable.define(varName, type, SymbolTable.Kind.LOCAL);
            tokenizer.advance(); //, or ;
        }

        tokenizer.advance();
        parent.indentationLevel--;
    }
}