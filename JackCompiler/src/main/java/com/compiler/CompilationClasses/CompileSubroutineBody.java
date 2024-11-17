package com.compiler.CompilationClasses;

import com.compiler.CompilationEngine;
import com.compiler.CustomExceptions.SyntaxExceptions;
import com.compiler.JackTokenizer;
import com.compiler.SymbolTable;
import com.compiler.Utils.EnumClass;
import com.compiler.VMWriter;

import java.io.IOException;

public class CompileSubroutineBody {
    private final CompilationEngine parent;
    private final String className;
    private final String subroutineName;
    private final String subroutineType;

    public CompileSubroutineBody(CompilationEngine parent, String className, String subroutineName, String subroutineType) {
        this.parent = parent;
        this.className = className;
        this.subroutineName = subroutineName;
        this.subroutineType = subroutineType;
    }

    public void compile() throws IOException, SyntaxExceptions {
        SymbolTable symbolTable = parent.symbolTable;
        JackTokenizer tokenizer = parent.tokenizer;
        VMWriter vmWriter = parent.vmWriter;

        parent.indentationLevel++;

        // {
        if (!(tokenizer.tokenType() == EnumClass.TokenType.SYMBOL && tokenizer.symbol() == '{'))
            throw new SyntaxExceptions.InvalidOpeningBracketsException();

        // check if any function var
        tokenizer.advance();
        while (tokenizer.tokenType() == EnumClass.TokenType.KEYWORD &&
                tokenizer.keyWord().equals(EnumClass.KeywordType.VAR))
            new CompileVarDec(parent).compile();

        // Write VM function declaration after parsing all local variables
        int localCount = symbolTable.varCount(SymbolTable.Kind.LOCAL);
        vmWriter.writeFunction(className + "." + subroutineName, localCount);

        if (subroutineType.equals("constructor")) {
            vmWriter.writePush("constant", symbolTable.varCount(SymbolTable.Kind.THIS));
            vmWriter.writeCall("Memory.alloc", 1);
            vmWriter.writePop("pointer", 0);
        } else if (subroutineType.equals("method")) {
            vmWriter.writePush("argument", 0);
            vmWriter.writePop("pointer", 0);
        }

        // Compile statements
        new CompileStatements(parent,className).compile();

        // Closing '}'
        if (!(tokenizer.tokenType() == EnumClass.TokenType.SYMBOL && tokenizer.symbol() == '}'))
            throw new SyntaxExceptions.InvalidClosingBracketsException();

        parent.indentationLevel--;
        tokenizer.advance();
    }
}
