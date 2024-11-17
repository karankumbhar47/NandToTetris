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
        JackTokenizer tokenizer = parent.tokenizer;

        parent.ensureKeyword(EnumClass.KeywordType.CLASS,
                "Expected Keyword `class` at the start of the class declaration");

        tokenizer.advance(); // Class name
        parent.ensureIdentifier("Expected a valid class Name as  a Identifier");
        String className = tokenizer.identifier();

        tokenizer.advance(); // '{'
        parent.ensureSymbol('{',"Expected an opening curly brace `{` after the class declaration");

        tokenizer.advance(); // class var dec
        while (tokenizer.tokenType() == EnumClass.TokenType.KEYWORD &&
                (tokenizer.keyWord() == EnumClass.KeywordType.STATIC || tokenizer.keyWord() == EnumClass.KeywordType.FIELD)) {
            parent.compileClassVarDec();
        }

        // Compile subroutine declarations
        while (tokenizer.tokenType() == EnumClass.TokenType.KEYWORD &&
                (tokenizer.keyWord() == EnumClass.KeywordType.CONSTRUCTOR ||
                        tokenizer.keyWord() == EnumClass.KeywordType.FUNCTION ||
                        tokenizer.keyWord() == EnumClass.KeywordType.METHOD)) {
            parent.compileSubroutine(className);
        }

        // '}'
        parent.ensureSymbol('}',"Expected a closing curly brace `}` to end the class declaration");
        tokenizer.advance();
    }

}
