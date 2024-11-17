package com.compiler.CompilationClasses;

import com.compiler.CompilationEngine;
import com.compiler.Utils.SyntaxExceptions;
import com.compiler.JackTokenizer;
import com.compiler.SymbolTable;
import com.compiler.Utils.EnumClass;
import com.compiler.VMWriter;

import java.io.IOException;

public class CompileTerm {
    private final CompilationEngine parent;

    public CompileTerm(CompilationEngine parent) {
        this.parent = parent;
    }

    public void compile() throws IOException, SyntaxExceptions {
        SymbolTable symbolTable = parent.symbolTable;
        JackTokenizer tokenizer = parent.tokenizer;
        VMWriter vmWriter = parent.vmWriter;

        if (tokenizer.tokenType() == EnumClass.TokenType.INT_CONST) {
            vmWriter.writePush("constant", tokenizer.intVal());
            tokenizer.advance();
        }
        else if (tokenizer.tokenType() == EnumClass.TokenType.STRING_CONST) {
            String str = tokenizer.stringVal();
            vmWriter.writePush("constant", str.length());
            vmWriter.writeCall("String.new", 1);
            for (char c : str.toCharArray()) {
                vmWriter.writePush("constant", c);
                vmWriter.writeCall("String.appendChar", 2);
            }
            tokenizer.advance();
        }
        else if (tokenizer.tokenType() == EnumClass.TokenType.KEYWORD &&
                (tokenizer.keyWord() == EnumClass.KeywordType.TRUE || tokenizer.keyWord() == EnumClass.KeywordType.FALSE ||
                        tokenizer.keyWord() == EnumClass.KeywordType.NULL || tokenizer.keyWord() == EnumClass.KeywordType.THIS)
        ) {
            switch (tokenizer.keyWord()) {
                case TRUE:
                    vmWriter.writePush("constant", 0);
                    vmWriter.writeArithmetic("not");
                    break;
                case FALSE:
                case NULL:
                    vmWriter.writePush("constant", 0);
                    break;
                case THIS:
                    vmWriter.writePush("pointer", 0);
                    break;
            }
            tokenizer.advance();
        }
        else if (tokenizer.tokenType() == EnumClass.TokenType.SYMBOL &&
                (tokenizer.symbol() == '~' || tokenizer.symbol() == '-')) {
            char unaryOp = tokenizer.symbol();
            tokenizer.advance();
            parent.compileTerm();
            if (unaryOp == '~') {
                vmWriter.writeArithmetic("not");
            } else if (unaryOp == '-') {
                vmWriter.writeArithmetic("neg");
            }
        }
        else if (tokenizer.tokenType() == EnumClass.TokenType.SYMBOL && tokenizer.symbol() == '(') {
            tokenizer.advance();
            parent.compileExpression();

            parent.ensureSymbol(')',"Expected `)` at the end of expression");
            tokenizer.advance();
        }
        else if (tokenizer.tokenType() == EnumClass.TokenType.IDENTIFIER) {
            String name = tokenizer.identifier();
            tokenizer.advance(); // . or [ or (

            if (tokenizer.tokenType() == EnumClass.TokenType.SYMBOL && tokenizer.symbol() == '[') {
                vmWriter.writePush(symbolTable.kindOf(name).toString().toLowerCase(), symbolTable.indexOf(name));

                tokenizer.advance();
                parent.compileExpression();
                parent.ensureSymbol(']', "Expected `]` at the end of array Expression");

                vmWriter.writeArithmetic("add");
                vmWriter.writePop("pointer", 1);
                vmWriter.writePush("that", 0);
                tokenizer.advance();
            }
            else if (tokenizer.tokenType() == EnumClass.TokenType.SYMBOL && tokenizer.symbol() == '.') {
                tokenizer.advance(); // name of the function

                parent.ensureIdentifier("Expected valid identifier as function Name");
                String subroutineName = tokenizer.identifier();
                int argCount = 0;

                if (symbolTable.kindOf(name) != SymbolTable.Kind.NONE) {
                    vmWriter.writePush(symbolTable.kindOf(name).toString().toLowerCase(), symbolTable.indexOf(name));
                    name = symbolTable.typeOf(name);
                    argCount++;
                }
                name += "." + subroutineName;

                tokenizer.advance(); //(
                parent.ensureSymbol('(', "Expected `(` in declaration function call");

                tokenizer.advance();
                argCount += parent.compileExpressionList();

                vmWriter.writeCall(name , argCount);
            }
            else if (tokenizer.tokenType() == EnumClass.TokenType.SYMBOL && tokenizer.symbol() == '(') {
                tokenizer.advance();
                int argCount = parent.compileExpressionList();

                vmWriter.writeCall(name, argCount);
                tokenizer.advance();
            }
            else {
                vmWriter.writePush(symbolTable.kindOf(name).toString().toLowerCase(), symbolTable.indexOf(name));
            }
        }
    }
}
