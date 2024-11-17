package com.compiler.CompilationClasses;

import com.compiler.CompilationEngine;
import com.compiler.CustomExceptions.SyntaxExceptions;
import com.compiler.JackTokenizer;
import com.compiler.SymbolTable;
import com.compiler.Utils.EnumClass;
import com.compiler.Utils.VMUtils;
import com.compiler.VMWriter;

import java.io.IOException;

public class CompileIf {
    private final CompilationEngine parent;
    private final String className;

    public CompileIf(CompilationEngine parent,String className) {
        this.parent = parent;
        this.className = className;
    }

    public void compile() throws IOException, SyntaxExceptions {
        SymbolTable symbolTable = parent.symbolTable;
        JackTokenizer tokenizer = parent.tokenizer;
        VMWriter vmWriter = parent.vmWriter;


        String labelTrue = "IF_TRUE_" + VMUtils.generateUniqueLabel();
        String labelFalse = "IF_FALSE_" + VMUtils.generateUniqueLabel();
        String labelEnd = "IF_END_" + VMUtils.generateUniqueLabel();

        // if
        parent.indentationLevel++;

        tokenizer.advance(); // (
        if (!(tokenizer.tokenType() == EnumClass.TokenType.SYMBOL && tokenizer.symbol() == '('))
            throw new SyntaxExceptions.InvalidClosingBracketsException();

        tokenizer.advance(); // expression
        new CompileExpression(parent).compile();

        vmWriter.writeArithmetic("not");
        vmWriter.writeIf(labelFalse);

        if (!(tokenizer.tokenType() == EnumClass.TokenType.SYMBOL && tokenizer.symbol() == ')'))
            throw new SyntaxExceptions.InvalidClosingBracketsException();

        tokenizer.advance(); // {
        if (!(tokenizer.tokenType() == EnumClass.TokenType.SYMBOL && tokenizer.symbol() == '{'))
            throw new SyntaxExceptions.InvalidClosingBracketsException();

        tokenizer.advance();
        new CompileStatements(parent,className).compile(); // statements

        if (!(tokenizer.tokenType() == EnumClass.TokenType.SYMBOL && tokenizer.symbol() == '}'))
            throw new SyntaxExceptions.InvalidClosingBracketsException();

        vmWriter.writeGoto(labelEnd); // Jump to end
        vmWriter.writeLabel(labelFalse); // False label

        // Check for 'else'
        if (tokenizer.hasMoreTokens()) {
            tokenizer.advance();
            if (tokenizer.tokenType() == EnumClass.TokenType.KEYWORD && tokenizer.keyWord().equals(EnumClass.KeywordType.ELSE)) {
                tokenizer.advance(); // '{'
                if (!(tokenizer.tokenType() == EnumClass.TokenType.SYMBOL && tokenizer.symbol() == '{'))
                    throw new SyntaxExceptions.InvalidClosingBracketsException();

                tokenizer.advance(); // statements inside else
                new CompileStatements(parent,className).compile();

                if (!(tokenizer.tokenType() == EnumClass.TokenType.SYMBOL && tokenizer.symbol() == '}'))
                    throw new SyntaxExceptions.InvalidClosingBracketsException();

                tokenizer.advance();
            }
        }

        vmWriter.writeLabel(labelEnd);
        parent.indentationLevel--;
    }

}
