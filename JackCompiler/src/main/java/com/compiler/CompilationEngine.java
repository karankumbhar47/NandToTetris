package com.compiler;

import com.compiler.CompilationClasses.*;
import com.compiler.CustomExceptions.SyntaxExceptions;
import com.compiler.CustomExceptions.SyntaxExceptions.*;
import com.compiler.Utils.EnumClass.KeywordType;
import com.compiler.Utils.EnumClass.TokenType;
import com.compiler.Utils.VMUtils;

import java.io.IOException;
import java.nio.file.Path;

public class CompilationEngine {
    public final VMWriter vmWriter;
    public final JackTokenizer tokenizer;
    public final SymbolTable symbolTable;
    public int indentationLevel;


    public CompilationEngine(Path outputVMFilePath, JackTokenizer tokenizer) throws IOException {
        this.indentationLevel = 0;
        this.tokenizer = tokenizer;
        this.symbolTable = new SymbolTable();
        this.vmWriter = new VMWriter(outputVMFilePath.toString());
    }

    public void compileClass() throws IOException, SyntaxExceptions {
        new CompileClass(this).compile();
    }

    public void compileClassVarDec() throws IOException,SyntaxExceptions {
        new CompileVarDec(this).compile();
    }

    public void compileSubroutine(String className) throws IOException, SyntaxExceptions {
        new CompileSubroutine(this,className).compile();
    }

    public void compileParameterList() throws IOException, SyntaxExceptions{
        new CompileParameterList(this).compile();
    }

    public void compileSubroutineBody(String className,String subroutineName, String subroutineType) throws IOException, SyntaxExceptions {
        new CompileSubroutineBody(this,className,subroutineName,subroutineType);
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

}
