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
        // let
        SymbolTable symbolTable = parent.symbolTable;
        JackTokenizer tokenizer = parent.tokenizer;
        VMWriter vmWriter = parent.vmWriter;

        tokenizer.advance(); // variable name
        parent.ensureIdentifier("Expected valid identifier for variable name");
        String varName = tokenizer.identifier();

        tokenizer.advance();
        boolean isArray = false;
        if (tokenizer.tokenType() == EnumClass.TokenType.SYMBOL && tokenizer.symbol() == '[') { // [
            isArray = true;
            vmWriter.writePush(symbolTable.kindOf(varName).toString().toLowerCase(), symbolTable.indexOf(varName));

            tokenizer.advance(); // expression inside []
            parent.compileExpression();

            // skip ]
            parent.ensureSymbol(']', "Expected `]` at the end of array Expression");
            vmWriter.writeArithmetic("add");
            tokenizer.advance(); // =
        }

        // expression after assignment =
        parent.ensureSymbol('=', "Expected `=` in the let statement assignment");
        tokenizer.advance();
        parent.compileExpression();

        if (isArray) {
            vmWriter.writePop("temp", 0);
            vmWriter.writePop("pointer", 1);
            vmWriter.writePush("temp", 0);
            vmWriter.writePop("that", 0);
        } else
            vmWriter.writePop(symbolTable.kindOf(varName).toString().toLowerCase(), symbolTable.indexOf(varName));

        // ;
        parent.ensureSymbol(';', "Expected `;` at the end of let statement");
        tokenizer.advance();
    }

}
