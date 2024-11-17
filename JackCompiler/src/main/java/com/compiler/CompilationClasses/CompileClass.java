package com.compiler.CompilationClasses;

import com.compiler.CompilationEngine;
import com.compiler.CustomExceptions.SyntaxExceptions;
import com.compiler.JackTokenizer;
import com.compiler.Utils.EnumClass;

import java.io.IOException;

public class CompileClass {
    private final CompilationEngine parent;

    public CompileClass(CompilationEngine parent){
        this.parent = parent;
    }

    public void compile() throws IOException, SyntaxExceptions{
        parent.indentationLevel++;
        JackTokenizer tokenizer = parent.tokenizer;


        if (!(tokenizer.tokenType() == EnumClass.TokenType.KEYWORD && tokenizer.keyWord() == EnumClass.KeywordType.CLASS))
            throw new SyntaxExceptions.IllegalClassKeywordException();

        tokenizer.advance(); // Class name
        String className;
        if (tokenizer.tokenType() == EnumClass.TokenType.IDENTIFIER)
            className = tokenizer.identifier();
        else throw new SyntaxExceptions.IllegalClassNameException();

        tokenizer.advance(); // '{'
        if (!(tokenizer.tokenType() == EnumClass.TokenType.SYMBOL && tokenizer.symbol() == '{'))
            throw new SyntaxExceptions.InvalidOpeningBracketsException();

        tokenizer.advance(); // class var dec
        while (tokenizer.tokenType() == EnumClass.TokenType.KEYWORD &&
                (tokenizer.keyWord() == EnumClass.KeywordType.STATIC || tokenizer.keyWord() == EnumClass.KeywordType.FIELD)) {
            new CompileClassVarDec(parent,className).compile();
        }

        // Compile subroutine declarations
        while (tokenizer.tokenType() == EnumClass.TokenType.KEYWORD &&
                (tokenizer.keyWord() == EnumClass.KeywordType.CONSTRUCTOR ||
                        tokenizer.keyWord() == EnumClass.KeywordType.FUNCTION ||
                        tokenizer.keyWord() == EnumClass.KeywordType.METHOD)) {
            new CompileSubroutine(parent,className).compile();
        }

        // '}'
        if (!(tokenizer.tokenType() == EnumClass.TokenType.SYMBOL && tokenizer.symbol() == '}')) {
            throw new SyntaxExceptions.InvalidClosingBracketsException();
        }

        parent.indentationLevel--;
        tokenizer.advance();
    }

}
