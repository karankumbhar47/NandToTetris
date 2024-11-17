package com.compiler.CompilationClasses;

import com.compiler.CompilationEngine;
import com.compiler.CustomExceptions.SyntaxExceptions;
import com.compiler.JackTokenizer;
import com.compiler.SymbolTable;
import com.compiler.Utils.EnumClass;
import com.compiler.VMWriter;

import java.io.IOException;

public class CompileLet {
    private final CompilationEngine parent;

    public CompileLet(CompilationEngine parent) {
        this.parent = parent;
    }

    public void compile() throws IOException, SyntaxExceptions {
        SymbolTable symbolTable = parent.symbolTable;
        JackTokenizer tokenizer = parent.tokenizer;
        VMWriter vmWriter = parent.vmWriter;


        // let
        parent.indentationLevel++;

        tokenizer.advance(); // variable name
        if (tokenizer.tokenType() != EnumClass.TokenType.IDENTIFIER)
            throw new SyntaxExceptions.VariableNameNotFoundException();
        String varName = tokenizer.identifier();

        tokenizer.advance();
        boolean isArray = false;
        if (tokenizer.tokenType() == EnumClass.TokenType.SYMBOL && tokenizer.symbol() == '[') { // [
            isArray = true;
            vmWriter.writePush(symbolTable.kindOf(varName).toString().toLowerCase(), symbolTable.indexOf(varName));

            tokenizer.advance(); // expression inside []
            new CompileExpression(parent).compile();

            // skip ]
            if (!(tokenizer.tokenType() == EnumClass.TokenType.SYMBOL && tokenizer.symbol() == ']'))
                throw new SyntaxExceptions.InvalidClosingBracketsException();

            vmWriter.writeArithmetic("add");

            tokenizer.advance(); // =
        }

        // expression after assignment =
        if (!(tokenizer.tokenType() == EnumClass.TokenType.SYMBOL && tokenizer.symbol() == '='))
            throw new SyntaxExceptions.InvalidClosingBracketsException();

        tokenizer.advance();
        new CompileExpression(parent).compile();

        if (isArray) {
            vmWriter.writePop("temp", 0);
            vmWriter.writePop("pointer", 1);
            vmWriter.writePush("temp", 0);
            vmWriter.writePop("that", 0);
        } else
            vmWriter.writePop(symbolTable.kindOf(varName).toString().toLowerCase(), symbolTable.indexOf(varName));

        // ;
        if (!(tokenizer.tokenType() == EnumClass.TokenType.SYMBOL && tokenizer.symbol() == ';'))
            throw new SyntaxExceptions.InvalidClosingBracketsException();

        tokenizer.advance();
        parent.indentationLevel--;
    }

}
