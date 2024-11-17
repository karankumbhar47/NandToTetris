package com.compiler.CompilationClasses;

import com.compiler.CompilationEngine;
import com.compiler.CustomExceptions.SyntaxExceptions;
import com.compiler.JackTokenizer;
import com.compiler.SymbolTable;
import com.compiler.Utils.EnumClass;
import com.compiler.VMWriter;

import java.io.IOException;

public class CompileDo {
    private final CompilationEngine parent;
    private final String className;

    public CompileDo(CompilationEngine parent,String className) {
        this.parent = parent;
        this.className = className;
    }

    public void compile() throws IOException, SyntaxExceptions {
        SymbolTable symbolTable = parent.symbolTable;
        JackTokenizer tokenizer = parent.tokenizer;
        VMWriter vmWriter = parent.vmWriter;

        // do
        parent.indentationLevel++;

        tokenizer.advance(); // name of the function / class
        if (tokenizer.tokenType() != EnumClass.TokenType.IDENTIFIER)
            throw new SyntaxExceptions.InvalidIdentifierException();

        String classNameMethod = tokenizer.identifier();
        boolean isMethod = false;
        int argCount = 0;

        tokenizer.advance(); // . or (
        if (tokenizer.tokenType() == EnumClass.TokenType.SYMBOL && tokenizer.symbol() == '.') {
            tokenizer.advance(); // name of the function
            if (tokenizer.tokenType() != EnumClass.TokenType.IDENTIFIER)
                throw new SyntaxExceptions.InvalidIdentifierException();


            if (symbolTable.kindOf(classNameMethod) != SymbolTable.Kind.NONE) {
                // `name` is an object, push the object as the first argument
                vmWriter.writePush(symbolTable.kindOf(classNameMethod).toString().toLowerCase(), symbolTable.indexOf(classNameMethod));
                classNameMethod = symbolTable.typeOf(classNameMethod) +"." + tokenizer.identifier();
                isMethod = true; // Object calls are methods
                argCount++;
            } else {
                // `name` is a class
                classNameMethod += "." + tokenizer.identifier();
            }
            tokenizer.advance(); // (
            if (!(tokenizer.tokenType() == EnumClass.TokenType.SYMBOL && tokenizer.symbol() == '('))
                throw new SyntaxExceptions.InvalidOpeningBracketsException();
        } else if (tokenizer.tokenType() == EnumClass.TokenType.SYMBOL && tokenizer.symbol() == '(') {
            // Implicit method call on `this`
            vmWriter.writePush("pointer", 0); // Push `this` as the first argument
            classNameMethod = className + "." + classNameMethod; // Add class context to the method name
            isMethod = true;
            argCount++;
        } else
            throw new SyntaxExceptions.InvalidOpeningBracketsException();

        tokenizer.advance();
        argCount += new CompileExpressionList(parent).compile();

        if (!(tokenizer.tokenType() == EnumClass.TokenType.SYMBOL && tokenizer.symbol() == ';'))
            throw new SyntaxExceptions.SemicolonNotFoundException();

        vmWriter.writeCall(classNameMethod, argCount);

        // Discard the return value for a `do` statement
        vmWriter.writePop("temp", 0);

        parent.indentationLevel--;
        tokenizer.advance();
    }
}
