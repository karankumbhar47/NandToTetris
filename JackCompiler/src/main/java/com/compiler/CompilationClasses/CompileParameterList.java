package com.compiler.CompilationClasses;

import com.compiler.CompilationEngine;
import com.compiler.CustomExceptions.SyntaxExceptions;
import com.compiler.JackTokenizer;
import com.compiler.SymbolTable;
import com.compiler.Utils.EnumClass;
import com.compiler.VMWriter;

import java.io.IOException;

public class CompileParameterList {
    private final CompilationEngine parent;

    public CompileParameterList(CompilationEngine parent) {
        this.parent = parent;
    }

    public void compile() throws IOException, SyntaxExceptions {
        SymbolTable symbolTable = parent.symbolTable;
        JackTokenizer tokenizer = parent.tokenizer;
        VMWriter vmWriter = parent.vmWriter;

        parent.indentationLevel++;

        // Parameter list begins
        if (tokenizer.tokenType() == EnumClass.TokenType.KEYWORD || tokenizer.tokenType() == EnumClass.TokenType.IDENTIFIER) {
            while (true) {
                // Type
                String type = tokenizer.tokenType() == EnumClass.TokenType.KEYWORD
                        ? tokenizer.keyWord().toString()
                        : tokenizer.identifier();

                tokenizer.advance(); // Variable name
                String varName = tokenizer.identifier();

                symbolTable.define(varName, type, SymbolTable.Kind.ARGUMENT); // Add to symbol table
                tokenizer.advance();

                //','
                if (!(tokenizer.tokenType() == EnumClass.TokenType.SYMBOL && tokenizer.symbol() == ','))
                    break;
                tokenizer.advance(); // var type next
            }
        }

        parent.indentationLevel--;
    }
}
