package com.compiler.CompilationClasses;

import com.compiler.CompilationEngine;
import com.compiler.CustomExceptions.SyntaxExceptions;
import com.compiler.JackTokenizer;
import com.compiler.Utils.EnumClass;
import com.compiler.Utils.VMUtils;
import com.compiler.VMWriter;

import java.io.IOException;

public class CompileIf {
    private final CompilationEngine parent;
    private final String className;

    public CompileIf(CompilationEngine parent, String className) {
        this.parent = parent;
        this.className = className;
    }

    public void compile() throws IOException, SyntaxExceptions {
        JackTokenizer tokenizer = parent.tokenizer;
        VMWriter vmWriter = parent.vmWriter;

        // if
        String labelFalse = "IF_FALSE_" + VMUtils.generateUniqueLabel();
        String labelEnd = "IF_END_" + VMUtils.generateUniqueLabel();

        tokenizer.advance(); // (
        parent.ensureSymbol('(', "Expected `(` after the `IF` keyword");

        tokenizer.advance(); // expression
        parent.compileExpression();

        vmWriter.writeArithmetic("not");
        vmWriter.writeIf(labelFalse);

        parent.ensureSymbol(')', "Expected `)` at the end of IF condition");

        tokenizer.advance(); // {
        parent.ensureSymbol('{', "Expected `{` at start of the if Body");

        tokenizer.advance();
        parent.compileStatements(className); // statements

        parent.ensureSymbol('}', "Expected `}` at end of the if Body");

        vmWriter.writeGoto(labelEnd); // Jump to end
        vmWriter.writeLabel(labelFalse); // False label

        // Check for 'else'
        if (tokenizer.hasMoreTokens()) {
            tokenizer.advance();
            if (tokenizer.tokenType() == EnumClass.TokenType.KEYWORD && tokenizer.keyWord().equals(EnumClass.KeywordType.ELSE)) {
                tokenizer.advance(); // '{'
                parent.ensureSymbol('{', "Expected `{` at start of the else Body");

                tokenizer.advance(); // statements inside else
                parent.compileStatements(className);

                parent.ensureSymbol('}', "Expected `{` at end of the else Body");
                tokenizer.advance();
            }
        }

        vmWriter.writeLabel(labelEnd);
    }

}
