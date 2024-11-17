package com.compiler;

import com.compiler.CompilationClasses.*;
import com.compiler.Utils.EnumClass;
import com.compiler.Utils.EnumClass.KeywordType;
import com.compiler.Utils.EnumClass.TokenType;
import com.compiler.Utils.SyntaxExceptions;
import com.compiler.Utils.VMUtils;

import java.io.IOException;
import java.nio.file.Path;

public class CompilationEngine {
    public final VMWriter vmWriter;
    public final JackTokenizer tokenizer;
    public final SymbolTable symbolTable;


    public CompilationEngine(Path outputVMFilePath, JackTokenizer tokenizer) throws IOException {
        this.tokenizer = tokenizer;
        this.symbolTable = new SymbolTable();
        this.vmWriter = new VMWriter(outputVMFilePath.toString());
    }

    public void compileClass() throws IOException, SyntaxExceptions {
        new CompileClass(this).compile();
    }

    public void compileClassVarDec() throws IOException,SyntaxExceptions {
        new CompileClassVarDec(this).compile();
    }

    public void compileSubroutine(String className) throws IOException, SyntaxExceptions {
        new CompileSubroutine(this,className).compile();
    }

    public void compileParameterList() throws IOException, SyntaxExceptions{
        new CompileParameterList(this).compile();
    }

    public void compileSubroutineBody(String className,String subroutineName, String subroutineType) throws IOException, SyntaxExceptions {
        new CompileSubroutineBody(this,className,subroutineName,subroutineType).compile();
    }

    public void compileVarDec() throws IOException,SyntaxExceptions {
        new CompileVarDec(this).compile();
    }

    public void compileStatements(String className) throws IOException, SyntaxExceptions {
        new CompileStatements(this,className).compile();
    }

    public void compileLet() throws IOException, SyntaxExceptions {
        new CompileLet(this).compile();
    }

    public void compileIf(String className) throws IOException, SyntaxExceptions {
        new CompileIf(this,className).compile();
    }

    public void compileWhile(String className) throws IOException, SyntaxExceptions {
        new CompileWhile(this,className).compile();
    }

    public void compileDo(String className) throws IOException, SyntaxExceptions {
        new CompileDo(this,className).compile();
    }

    public void compileReturn() throws IOException, SyntaxExceptions {
        new CompileReturn(this).compile();
    }

    public void compileExpression() throws IOException, SyntaxExceptions {
        new CompileExpression(this).compile();
    }

    public void compileTerm() throws IOException, SyntaxExceptions {
        new CompileTerm(this).compile();
    }

    public int compileExpressionList() throws IOException, SyntaxExceptions {
        return new CompileExpressionList(this).compile();
    }

    public void closeFile() throws IOException {
        vmWriter.close();
    }

    public void ensureSymbol(char expectedSymbol,String errorMessage) throws SyntaxExceptions {
        if (!(tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol() == expectedSymbol)) {
            throw new SyntaxExceptions(errorMessage,tokenizer.getContext());
        }
    }

    public void ensureKeyword(KeywordType expectedKeyword, String errorMessage) throws SyntaxExceptions {
        if (!(tokenizer.tokenType() == TokenType.KEYWORD && tokenizer.keyWord() == expectedKeyword)) {
            throw new SyntaxExceptions(errorMessage,tokenizer.getContext());
        }
    }

    public void ensureIdentifier(String errorMessage) throws SyntaxExceptions {
        if (tokenizer.tokenType() != TokenType.IDENTIFIER) {
            throw new SyntaxExceptions(errorMessage,tokenizer.getContext());
        }
    }

    public void ensureType(String errorMessage) throws SyntaxExceptions {
        if (!(tokenizer.tokenType() == TokenType.KEYWORD &&
                (tokenizer.keyWord() == KeywordType.INT ||
                        tokenizer.keyWord() == KeywordType.CHAR ||
                        tokenizer.keyWord() == KeywordType.BOOLEAN)) &&
                tokenizer.tokenType() != TokenType.IDENTIFIER) {
            throw new SyntaxExceptions(errorMessage,tokenizer.getContext());
        }
    }

    public void ensureClassVarType(String errorMessage) throws SyntaxExceptions {
        if(!(tokenizer.tokenType() == EnumClass.TokenType.KEYWORD &&
                (tokenizer.keyWord() == EnumClass.KeywordType.STATIC
                        || tokenizer.keyWord() == EnumClass.KeywordType.FIELD))) {
            throw new SyntaxExceptions(errorMessage,tokenizer.getContext());
        }
    }

    public void ensureFunctionType() throws SyntaxExceptions{
        if (!(tokenizer.tokenType() == EnumClass.TokenType.KEYWORD &&
                (tokenizer.keyWord().equals(EnumClass.KeywordType.CONSTRUCTOR) ||
                        tokenizer.keyWord().equals(EnumClass.KeywordType.FUNCTION) ||
                        tokenizer.keyWord().equals(EnumClass.KeywordType.METHOD)))) {
            throw new SyntaxExceptions("Expected 'constructor', 'function', or 'method' at start of subroutineDec."
                    ,tokenizer.getContext());
        }
    }
}
