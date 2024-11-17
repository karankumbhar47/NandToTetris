package com.compiler.CompilationClasses;

import com.compiler.CompilationEngine;
import com.compiler.Utils.SyntaxExceptions;
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

        tokenizer.advance(); // name of the function / class
        parent.ensureIdentifier("Expected valid identifier as function name or class name");

        String classNameMethod = tokenizer.identifier();
        int argCount = 0;

        tokenizer.advance(); // . or (
        if (tokenizer.tokenType() == EnumClass.TokenType.SYMBOL && tokenizer.symbol() == '.') {
            tokenizer.advance(); // name of the function
            parent.ensureIdentifier("Expected valid identifier as function Name");

            if (symbolTable.kindOf(classNameMethod) != SymbolTable.Kind.NONE) {
                vmWriter.writePush(symbolTable.kindOf(classNameMethod).toString().toLowerCase(), symbolTable.indexOf(classNameMethod));
                classNameMethod = symbolTable.typeOf(classNameMethod) +"." + tokenizer.identifier();
                argCount++;
            } else
                classNameMethod += "." + tokenizer.identifier();

            tokenizer.advance(); // (
            parent.ensureSymbol('(', "Expected `(` in declaration function call");
        } else if (tokenizer.tokenType() == EnumClass.TokenType.SYMBOL && tokenizer.symbol() == '(') {
            // Implicit method call on `this`
            vmWriter.writePush("pointer", 0); // Push `this` as the first argument
            classNameMethod = className + "." + classNameMethod; // Add class context to the method name
            argCount++;
        } else
            throw new SyntaxExceptions.InvalidOpeningBracketsException(tokenizer.getContext());

        tokenizer.advance();
        argCount += parent.compileExpressionList();

        parent.ensureSymbol(';', "Expected `;` at the end of function call declaration");

        vmWriter.writeCall(classNameMethod, argCount);
        // Discard the return value for a `do` statement
        vmWriter.writePop("temp", 0);
        tokenizer.advance();
    }
}
