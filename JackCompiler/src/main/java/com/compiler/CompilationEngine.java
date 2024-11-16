package com.compiler;

import com.compiler.CustomExceptions.SyntaxExceptions;
import com.compiler.CustomExceptions.SyntaxExceptions.*;
import com.compiler.Utils.EnumClass.KeywordType;
import com.compiler.Utils.EnumClass.TokenType;
import com.compiler.Utils.VMUtils;

import java.io.IOException;
import java.nio.file.Path;

public class CompilationEngine {
    public final JackTokenizer tokenizer;
    private int indentationLevel;

    private final VMWriter vmWriter;
    private final SymbolTable symbolTable;


    public CompilationEngine(Path outputVMFilePath, JackTokenizer tokenizer) throws IOException {
        this.indentationLevel = 0;
        this.tokenizer = tokenizer;
        this.symbolTable = new SymbolTable();
        this.vmWriter = new VMWriter(outputVMFilePath.toString());
    }

    public void compileClass() throws IOException, SyntaxExceptions {
        indentationLevel++;

        if (!(tokenizer.tokenType() == TokenType.KEYWORD && tokenizer.keyWord() == KeywordType.CLASS))
            throw new IllegalClassKeywordException();

        tokenizer.advance(); // Class name
        String className;
        if (tokenizer.tokenType() == TokenType.IDENTIFIER)
            className = tokenizer.identifier();
        else throw new IllegalClassNameException();

        tokenizer.advance(); // '{'
        if (!(tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol() == '{'))
            throw new InvalidOpeningBracketsException();

        tokenizer.advance(); // class var dec
        while (tokenizer.tokenType() == TokenType.KEYWORD &&
                (tokenizer.keyWord() == KeywordType.STATIC || tokenizer.keyWord() == KeywordType.FIELD)) {
            compileClassVarDec();
        }

        // Compile subroutine declarations
        while (tokenizer.tokenType() == TokenType.KEYWORD &&
                (tokenizer.keyWord() == KeywordType.CONSTRUCTOR ||
                        tokenizer.keyWord() == KeywordType.FUNCTION ||
                        tokenizer.keyWord() == KeywordType.METHOD)) {
            compileSubroutine(className);
        }

        // '}'
        if (!(tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol() == '}')) {
            throw new InvalidClosingBracketsException();
        }

        indentationLevel--;
        tokenizer.advance();
    }

    public void compileClassVarDec() throws IOException, SyntaxExceptions {
        indentationLevel++;

        //'static' or 'field'
        SymbolTable.Kind varKind;
        if (tokenizer.tokenType() == TokenType.KEYWORD &&
                (tokenizer.keyWord() == KeywordType.STATIC || tokenizer.keyWord() == KeywordType.FIELD)) {
            String kind = tokenizer.keyWord().toString().toLowerCase();
            varKind = kind.equals("static") ? SymbolTable.Kind.STATIC : SymbolTable.Kind.THIS;
        } else {
            throw new InvalidClassVariableKeywordException();
        }

        // var type
        tokenizer.advance();
        if (!(tokenizer.tokenType() == TokenType.KEYWORD || tokenizer.tokenType() == TokenType.IDENTIFIER))
            throw new InvalidVariableTypeException();
        String varType = tokenizer.tokenType() == TokenType.KEYWORD ? tokenizer.keyWord().toString().toLowerCase() : tokenizer.identifier();

        // first var name
        tokenizer.advance();
        if (!(tokenizer.tokenType() == TokenType.IDENTIFIER))
            throw new InvalidVariableNameException();

        symbolTable.define(tokenizer.identifier(), varType, varKind);

        // Process additional variable names if separated by commas
        while (true) {
            tokenizer.advance();
            if (!(tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol() == ','))
                break;

            tokenizer.advance(); // var name
            if (!(tokenizer.tokenType() == TokenType.IDENTIFIER))
                throw new VariableNameNotFoundException();

            symbolTable.define(tokenizer.identifier(), varType, varKind);
        }

        // ';'
        if (!(tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol() == ';'))
            throw new SemicolonNotFoundException();

        indentationLevel--;
        tokenizer.advance();
    }

    public void compileSubroutine(String className) throws IOException, SyntaxExceptions {
        symbolTable.reset();
        indentationLevel++;

        // 'constructor', 'function', or 'method'
        if (!(tokenizer.tokenType() == TokenType.KEYWORD &&
                (tokenizer.keyWord().equals(KeywordType.CONSTRUCTOR) ||
                        tokenizer.keyWord().equals(KeywordType.FUNCTION) ||
                        tokenizer.keyWord().equals(KeywordType.METHOD)))) {
            throw new IllegalArgumentException("Expected 'constructor', 'function', or 'method' at start of subroutineDec.");
        }
        String subroutineType = tokenizer.keyWord().toString().toLowerCase();

        // return type
        tokenizer.advance();
        if (!((tokenizer.tokenType() == TokenType.KEYWORD &&
                (tokenizer.keyWord().equals(KeywordType.VOID)
                        || (tokenizer.keyWord().equals(KeywordType.INT))
                        || (tokenizer.keyWord().equals(KeywordType.CHAR))
                        || (tokenizer.keyWord().equals(KeywordType.BOOLEAN))
                )) || (tokenizer.tokenType() == TokenType.IDENTIFIER))
        )
            throw new IllegalArgumentException("Expected return type (void, int, char, boolean, or class name) in subroutineDec.");

        // Subroutine name
        tokenizer.advance();
        if (!(tokenizer.tokenType() == TokenType.IDENTIFIER)) {
            throw new IllegalArgumentException("Expected subroutine name identifier in subroutineDec.");
        }
        String subroutineName = tokenizer.identifier();

        // '(' and parameter list
        tokenizer.advance();
        if (!(tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol().equals('(')))
            throw new IllegalArgumentException("Expected '(' before parameterList in subroutineDec.");

        if (subroutineType.equals("method"))
            symbolTable.define("this", className, SymbolTable.Kind.ARGUMENT);

        // parameter list
        tokenizer.advance();
        compileParameterList();

        // Process ")"
        if (!(tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol().equals(')')))
            throw new IllegalArgumentException("Expected ')' after parameterList in subroutineDec.");


        // Subroutine body
        tokenizer.advance();
        compileSubroutineBody(className, subroutineName, subroutineType);

        indentationLevel--;
    }

    public void compileParameterList() throws IOException {
        indentationLevel++;

        // Parameter list begins
        if (tokenizer.tokenType() == TokenType.KEYWORD || tokenizer.tokenType() == TokenType.IDENTIFIER) {
            while (true) {
                // Type
                String type = tokenizer.tokenType() == TokenType.KEYWORD
                        ? tokenizer.keyWord().toString()
                        : tokenizer.identifier();

                tokenizer.advance(); // Variable name
                String varName = tokenizer.identifier();

                symbolTable.define(varName, type, SymbolTable.Kind.ARGUMENT); // Add to symbol table
                tokenizer.advance();

                //','
                if (!(tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol() == ','))
                    break;
                tokenizer.advance(); // var type next
            }
        }

        indentationLevel--;
    }

    public void compileSubroutineBody(String className,String subroutineName, String subroutineType) throws IOException, SyntaxExceptions {
        indentationLevel++;

        // {
        if (!(tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol() == '{'))
            throw new InvalidOpeningBracketsException();

        // check if any function var
        tokenizer.advance();
        while (tokenizer.tokenType() == TokenType.KEYWORD &&
                tokenizer.keyWord().equals(KeywordType.VAR))
            compileVarDec();

        // Write VM function declaration after parsing all local variables
        int localCount = symbolTable.varCount(SymbolTable.Kind.LOCAL);
        vmWriter.writeFunction(className + "." + subroutineName, localCount);

        if (subroutineType.equals("constructor")) {
            vmWriter.writePush("constant", symbolTable.varCount(SymbolTable.Kind.THIS));
            vmWriter.writeCall("Memory.alloc", 1);
            vmWriter.writePop("pointer", 0);
        } else if (subroutineType.equals("method")) {
            vmWriter.writePush("argument", 0);
            vmWriter.writePop("pointer", 0);
        }

        // Compile statements
        compileStatements(className);

        // Closing '}'
        if (!(tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol() == '}'))
            throw new InvalidClosingBracketsException();

        indentationLevel--;
        tokenizer.advance();
    }

    public void compileVarDec() throws IOException, SyntaxExceptions {
        //var
        indentationLevel++;

        tokenizer.advance(); // type of variable
        if (!(tokenizer.tokenType() == TokenType.KEYWORD || tokenizer.tokenType() == TokenType.IDENTIFIER))
            throw new InvalidFunctionVariableKeywordException();

        String type = tokenizer.tokenType() == TokenType.KEYWORD ? tokenizer.keyWord().toString() : tokenizer.identifier();

        tokenizer.advance(); // name of variable
        if (tokenizer.tokenType() != TokenType.IDENTIFIER)
            throw new VariableNameNotFoundException();

        String varName = tokenizer.identifier();
        symbolTable.define(varName, type, SymbolTable.Kind.LOCAL);

        tokenizer.advance(); //, or ;
        while (tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol()==',') {
            tokenizer.advance(); // var name

            if (tokenizer.tokenType() != TokenType.IDENTIFIER)
                throw new VariableNameNotFoundException();
            varName = tokenizer.identifier();
            symbolTable.define(varName, type, SymbolTable.Kind.LOCAL);
            tokenizer.advance(); //, or ;
        }

        tokenizer.advance();
        indentationLevel--;
    }

    public void compileStatements(String className) throws IOException, SyntaxExceptions {
        indentationLevel++;

        statementsLoop:
        while (tokenizer.hasMoreTokens() && tokenizer.tokenType() == TokenType.KEYWORD) {
            switch (tokenizer.keyWord()) {
                case LET:
                    compileLet();
                    break;
                case DO:
                    compileDo(className);
                    break;
                case WHILE:
                    compileWhile(className);
                    break;
                case RETURN:
                    compileReturn();
                    break;
                case IF:
                    compileIf(className);
                    break;
                default:
                    break statementsLoop;
            }
        }

        indentationLevel--;
    }

    public void compileLet() throws IOException, SyntaxExceptions {
        // let
        indentationLevel++;

        tokenizer.advance(); // variable name
        if (tokenizer.tokenType() != TokenType.IDENTIFIER)
            throw new VariableNameNotFoundException();
        String varName = tokenizer.identifier();

        tokenizer.advance();
        boolean isArray = false;
        if (tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol() == '[') { // [
            isArray = true;
            vmWriter.writePush(symbolTable.kindOf(varName).toString().toLowerCase(), symbolTable.indexOf(varName));

            tokenizer.advance(); // expression inside []
            compileExpression();

            // skip ]
            if (!(tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol() == ']'))
                throw new InvalidClosingBracketsException();

            vmWriter.writeArithmetic("add");

            tokenizer.advance(); // =
        }

        // expression after assignment =
        if (!(tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol() == '='))
            throw new InvalidClosingBracketsException();

        tokenizer.advance();
        compileExpression();

        if (isArray) {
            vmWriter.writePop("temp", 0);
            vmWriter.writePop("pointer", 1);
            vmWriter.writePush("temp", 0);
            vmWriter.writePop("that", 0);
        } else
            vmWriter.writePop(symbolTable.kindOf(varName).toString().toLowerCase(), symbolTable.indexOf(varName));

        // ;
        if (!(tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol() == ';'))
            throw new InvalidClosingBracketsException();

        tokenizer.advance();
        indentationLevel--;
    }

    public void compileIf(String className) throws IOException, SyntaxExceptions {
        String labelTrue = "IF_TRUE_" + VMUtils.generateUniqueLabel();
        String labelFalse = "IF_FALSE_" + VMUtils.generateUniqueLabel();
        String labelEnd = "IF_END_" + VMUtils.generateUniqueLabel();

        // if
        indentationLevel++;

        tokenizer.advance(); // (
        if (!(tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol() == '('))
            throw new InvalidClosingBracketsException();

        tokenizer.advance(); // expression
        compileExpression();

        vmWriter.writeArithmetic("not");
        vmWriter.writeIf(labelFalse);

        if (!(tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol() == ')'))
            throw new InvalidClosingBracketsException();

        tokenizer.advance(); // {
        if (!(tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol() == '{'))
            throw new InvalidClosingBracketsException();

        tokenizer.advance();
        compileStatements(className); // statements

        if (!(tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol() == '}'))
            throw new InvalidClosingBracketsException();

        vmWriter.writeGoto(labelEnd); // Jump to end
        vmWriter.writeLabel(labelFalse); // False label

        // Check for 'else'
        if (tokenizer.hasMoreTokens()) {
            tokenizer.advance();
            if (tokenizer.tokenType() == TokenType.KEYWORD && tokenizer.keyWord().equals(KeywordType.ELSE)) {
                tokenizer.advance(); // '{'
                if (!(tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol() == '{'))
                    throw new InvalidClosingBracketsException();

                tokenizer.advance(); // statements inside else
                compileStatements(className);

                if (!(tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol() == '}'))
                    throw new InvalidClosingBracketsException();

                tokenizer.advance();
            }
        }

        vmWriter.writeLabel(labelEnd);
        indentationLevel--;
    }

    public void compileWhile(String className) throws IOException, SyntaxExceptions {
        // while
        indentationLevel++;

        String labelStart = "WHILE_START_" + VMUtils.generateUniqueLabel();
        String labelEnd = "WHILE_END_" + VMUtils.generateUniqueLabel();

        vmWriter.writeLabel(labelStart);

        tokenizer.advance(); //(
        if (!(tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol() == '('))
            throw new InvalidClosingBracketsException();

        tokenizer.advance(); // expression inside bracket
        compileExpression();

        vmWriter.writeArithmetic("not");
        vmWriter.writeIf(labelEnd);

        if (!(tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol() == ')'))
            throw new InvalidClosingBracketsException();

        tokenizer.advance(); // {
        if (!(tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol() == '{'))
            throw new InvalidClosingBracketsException();

        tokenizer.advance(); // statements inside while
        compileStatements(className);

        if (!(tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol() == '}'))
            throw new InvalidClosingBracketsException();

        vmWriter.writeGoto(labelStart);
        vmWriter.writeLabel(labelEnd);
        tokenizer.advance();
        indentationLevel--;
    }

    public void compileDo(String className) throws IOException, SyntaxExceptions {
        // do
        indentationLevel++;

        tokenizer.advance(); // name of the function / class
        if (tokenizer.tokenType() != TokenType.IDENTIFIER)
            throw new InvalidIdentifierException();

        String classNameMethod = tokenizer.identifier();
        boolean isMethod = false;
        int argCount = 0;

        tokenizer.advance(); // . or (
        if (tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol() == '.') {
            tokenizer.advance(); // name of the function
            if (tokenizer.tokenType() != TokenType.IDENTIFIER)
                throw new InvalidIdentifierException();


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
            if (!(tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol() == '('))
                throw new InvalidOpeningBracketsException();
        } else if (tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol() == '(') {
            // Implicit method call on `this`
            vmWriter.writePush("pointer", 0); // Push `this` as the first argument
            classNameMethod = className + "." + classNameMethod; // Add class context to the method name
            isMethod = true;
            argCount++;
        } else
            throw new InvalidOpeningBracketsException();

        tokenizer.advance();
        argCount += compileExpressionList();

        if (!(tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol() == ';'))
            throw new SemicolonNotFoundException();

        vmWriter.writeCall(classNameMethod, argCount);

        // Discard the return value for a `do` statement
        vmWriter.writePop("temp", 0);

        indentationLevel--;
        tokenizer.advance();
    }

    public void compileReturn() throws IOException, SyntaxExceptions {
        // return
        indentationLevel++;

        tokenizer.advance(); // either ";" or expression
        if (!(tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol() == ';'))
            compileExpression();
        else
            vmWriter.writePush("constant",0);

        if (!(tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol() == ';'))
            throw new InvalidClosingBracketsException();

        vmWriter.writeReturn();
        tokenizer.advance();
        indentationLevel--;
    }

    public void compileExpression() throws IOException, SyntaxExceptions {
        indentationLevel++;

        compileTerm();

        while (tokenizer.tokenType() == TokenType.SYMBOL &&
                (tokenizer.symbol() == '+' || tokenizer.symbol() == '-' || tokenizer.symbol() == '/'
                        || tokenizer.symbol() == '*' || tokenizer.symbol() == '&' || tokenizer.symbol() == '|'
                        || tokenizer.symbol() == '>' || tokenizer.symbol() == '<' || tokenizer.symbol() == '=')) {

            char operator = tokenizer.symbol();
            tokenizer.advance(); // Move to the next term
            compileTerm();

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

        indentationLevel--;
    }

    public void compileTerm() throws IOException, SyntaxExceptions {
        indentationLevel++;

        if (tokenizer.tokenType() == TokenType.INT_CONST) {
            vmWriter.writePush("constant", tokenizer.intVal());
            tokenizer.advance();
        }
        else if (tokenizer.tokenType() == TokenType.STRING_CONST) {
            String str = tokenizer.stringVal();
            vmWriter.writePush("constant", str.length());
            vmWriter.writeCall("String.new", 1);
            for (char c : str.toCharArray()) {
                vmWriter.writePush("constant", c);
                vmWriter.writeCall("String.appendChar", 2);
            }
            tokenizer.advance();
        }
        else if (tokenizer.tokenType() == TokenType.KEYWORD &&
                (tokenizer.keyWord() == KeywordType.TRUE || tokenizer.keyWord() == KeywordType.FALSE ||
                        tokenizer.keyWord() == KeywordType.NULL || tokenizer.keyWord() == KeywordType.THIS)
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
        else if (tokenizer.tokenType() == TokenType.SYMBOL &&
                (tokenizer.symbol() == '~' || tokenizer.symbol() == '-')) {
            char unaryOp = tokenizer.symbol();
            tokenizer.advance();
            compileTerm();
            if (unaryOp == '~') {
                vmWriter.writeArithmetic("not");
            } else if (unaryOp == '-') {
                vmWriter.writeArithmetic("neg");
            }
        }
        else if (tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol() == '(') {
            tokenizer.advance();
            compileExpression();

            if (!(tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol() == ')'))
                throw new InvalidClosingBracketsException();
            tokenizer.advance();
        }
        else if (tokenizer.tokenType() == TokenType.IDENTIFIER) {
            String name = tokenizer.identifier();
            tokenizer.advance(); // . or [ or (

            if (tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol() == '[') {
                vmWriter.writePush(symbolTable.kindOf(name).toString().toLowerCase(), symbolTable.indexOf(name));

                tokenizer.advance();
                compileExpression();

                if (!(tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol() == ']'))
                    throw new InvalidClosingBracketsException();

                vmWriter.writeArithmetic("add");
                vmWriter.writePop("pointer", 1);
                vmWriter.writePush("that", 0);
                tokenizer.advance();
            } else if (tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol() == '.') {
                tokenizer.advance(); // name of the function
                if (tokenizer.tokenType() != TokenType.IDENTIFIER)
                    throw new VariableNameNotFoundException();
                String subroutineName = tokenizer.identifier();

                tokenizer.advance();
                if (!(tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol() == '('))
                    throw new InvalidClosingBracketsException();

                tokenizer.advance();
                int argCount = compileExpressionList();
                vmWriter.writeCall(name + "." + subroutineName, argCount);

            } else if (tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol() == '(') {
                tokenizer.advance();
                int argCount = compileExpressionList();
                vmWriter.writeCall(name, argCount);

                if (!(tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol() == ')'))
                    throw new InvalidClosingBracketsException();
                tokenizer.advance();
            } else {
                vmWriter.writePush(symbolTable.kindOf(name).toString().toLowerCase(), symbolTable.indexOf(name));
            }
        }

        indentationLevel--;
    }

    public int compileExpressionList() throws IOException, SyntaxExceptions {
        int argCount = 0;
        indentationLevel++;

        if (!(tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol() == ')')) {
            compileExpression();
            argCount++;

            while (tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol() == ',') {
                tokenizer.advance();
                compileExpression();
                argCount++;
            }
        }


        if (!(tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol() == ')'))
            throw new InvalidClosingBracketsException();

        tokenizer.advance();
        indentationLevel--;
        return argCount;
    }

    public void closeFile() throws IOException {
        vmWriter.close();

    }

}
