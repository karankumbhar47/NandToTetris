package com.compiler.CompilationClasses;

import com.compiler.CompilationEngine;
import com.compiler.CustomExceptions.SyntaxExceptions;
import com.compiler.JackTokenizer;
import com.compiler.Utils.EnumClass;
import com.compiler.VMWriter;

import java.io.IOException;

public class CompileExpression {
    private final CompilationEngine parent;

    public CompileExpression(CompilationEngine parent) {
        this.parent = parent;
    }

    public void compile() throws IOException, SyntaxExceptions {
        JackTokenizer tokenizer = parent.tokenizer;
        VMWriter vmWriter = parent.vmWriter;

        parent.compileTerm();

        while (tokenizer.tokenType() == EnumClass.TokenType.SYMBOL &&
                (tokenizer.symbol() == '+' || tokenizer.symbol() == '-' || tokenizer.symbol() == '/'
                        || tokenizer.symbol() == '*' || tokenizer.symbol() == '&' || tokenizer.symbol() == '|'
                        || tokenizer.symbol() == '>' || tokenizer.symbol() == '<' || tokenizer.symbol() == '=')) {

            char operator = tokenizer.symbol();
            tokenizer.advance(); // Move to the next term
            parent.compileTerm();

            // Write VM command for the operator
            switch (operator) {
                case '+':
                    vmWriter.writeArithmetic("add");
                    break;
                case '-':
                    vmWriter.writeArithmetic("sub");
                    break;
                case '*':
                    vmWriter.writeCall("Math.multiply", 2);
                    break;
                case '/':
                    vmWriter.writeCall("Math.divide", 2);
                    break;
                case '&':
                    vmWriter.writeArithmetic("and");
                    break;
                case '|':
                    vmWriter.writeArithmetic("or");
                    break;
                case '<':
                    vmWriter.writeArithmetic("lt");
                    break;
                case '>':
                    vmWriter.writeArithmetic("gt");
                    break;
                case '=':
                    vmWriter.writeArithmetic("eq");
                    break;
            }
        }
    }
}
