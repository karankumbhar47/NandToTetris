package com.compiler.CompilationClasses;

import com.compiler.CompilationEngine;
import com.compiler.CustomExceptions.SyntaxExceptions;
import com.compiler.JackTokenizer;
import com.compiler.SymbolTable;
import com.compiler.Utils.EnumClass;
import com.compiler.Utils.VMUtils;
import com.compiler.VMWriter;

import java.io.IOException;

public class CompileWhile {
    private final CompilationEngine parent;
    private final String className;

    public CompileWhile(CompilationEngine parent,String className) {
        this.parent = parent;
        this.className = className;
    }

    public void compile() throws IOException, SyntaxExceptions {
        SymbolTable symbolTable = parent.symbolTable;
        JackTokenizer tokenizer = parent.tokenizer;
        VMWriter vmWriter = parent.vmWriter;

        // while
        parent.indentationLevel++;

        String labelStart = "WHILE_START_" + VMUtils.generateUniqueLabel();
        String labelEnd = "WHILE_END_" + VMUtils.generateUniqueLabel();

        vmWriter.writeLabel(labelStart);

        tokenizer.advance(); //(
        if (!(tokenizer.tokenType() == EnumClass.TokenType.SYMBOL && tokenizer.symbol() == '('))
            throw new SyntaxExceptions.InvalidClosingBracketsException();

        tokenizer.advance(); // expression inside bracket
        new CompileExpression(parent).compile();

        vmWriter.writeArithmetic("not");
        vmWriter.writeIf(labelEnd);

        if (!(tokenizer.tokenType() == EnumClass.TokenType.SYMBOL && tokenizer.symbol() == ')'))
            throw new SyntaxExceptions.InvalidClosingBracketsException();

        tokenizer.advance(); // {
        if (!(tokenizer.tokenType() == EnumClass.TokenType.SYMBOL && tokenizer.symbol() == '{'))
            throw new SyntaxExceptions.InvalidClosingBracketsException();

        tokenizer.advance(); // statements inside while
        new CompileStatements(parent,className).compile();

        if (!(tokenizer.tokenType() == EnumClass.TokenType.SYMBOL && tokenizer.symbol() == '}'))
            throw new SyntaxExceptions.InvalidClosingBracketsException();

        vmWriter.writeGoto(labelStart);
        vmWriter.writeLabel(labelEnd);
        tokenizer.advance();
        parent.indentationLevel--;
    }
}
