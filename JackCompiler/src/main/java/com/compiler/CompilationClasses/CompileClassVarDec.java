package com.compiler.CompilationClasses;

import com.compiler.CompilationEngine;
import com.compiler.CustomExceptions.SyntaxExceptions;
import com.compiler.JackTokenizer;
import com.compiler.SymbolTable;
import com.compiler.Utils.EnumClass;

import java.io.IOException;

public class CompileClassVarDec {
    private final CompilationEngine parent;
    private final String className;

    public CompileClassVarDec(CompilationEngine parent, String className) {
        this.parent = parent;
        this.className = className;
    }

    public void compile() throws IOException, SyntaxExceptions{
        SymbolTable symbolTable = parent.symbolTable;
        JackTokenizer tokenizer = parent.tokenizer;
        parent.indentationLevel++;

        //'static' or 'field'
        SymbolTable.Kind varKind;
        if (tokenizer.tokenType() == EnumClass.TokenType.KEYWORD &&
                (tokenizer.keyWord() == EnumClass.KeywordType.STATIC || tokenizer.keyWord() == EnumClass.KeywordType.FIELD)) {
            String kind = tokenizer.keyWord().toString().toLowerCase();
            varKind = kind.equals("static") ? SymbolTable.Kind.STATIC : SymbolTable.Kind.THIS;
        } else {
            throw new SyntaxExceptions.InvalidClassVariableKeywordException();
        }

        // var type
        tokenizer.advance();
        if (!(tokenizer.tokenType() == EnumClass.TokenType.KEYWORD || tokenizer.tokenType() == EnumClass.TokenType.IDENTIFIER))
            throw new SyntaxExceptions.InvalidVariableTypeException();
        String varType = tokenizer.tokenType() == EnumClass.TokenType.KEYWORD ? tokenizer.keyWord().toString().toLowerCase() : tokenizer.identifier();

        // first var name
        tokenizer.advance();
        if (!(tokenizer.tokenType() == EnumClass.TokenType.IDENTIFIER))
            throw new SyntaxExceptions.InvalidVariableNameException();

        symbolTable.define(tokenizer.identifier(), varType, varKind);

        // Process additional variable names if separated by commas
        while (true) {
            tokenizer.advance();
            if (!(tokenizer.tokenType() == EnumClass.TokenType.SYMBOL && tokenizer.symbol() == ','))
                break;

            tokenizer.advance(); // var name
            if (!(tokenizer.tokenType() == EnumClass.TokenType.IDENTIFIER))
                throw new SyntaxExceptions.VariableNameNotFoundException();

            symbolTable.define(tokenizer.identifier(), varType, varKind);
        }

        // ';'
        if (!(tokenizer.tokenType() == EnumClass.TokenType.SYMBOL && tokenizer.symbol() == ';'))
            throw new SyntaxExceptions.SemicolonNotFoundException();

        parent.indentationLevel--;
        tokenizer.advance();
    }
}
