package com.compiler.Utils;

public class Context {
    private int lineNumber;
    private String currentLine;
    private String fileName;
    private String currentToken;

    public Context(int lineNumber, String currentLine, String fileName, String currentToken) {
        this.fileName = fileName;
        this.lineNumber = lineNumber;
        this.currentLine = currentLine;
        this.currentToken = currentToken;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public String getCurrentLine() {
        return currentLine;
    }

    public String getFileName() {
        return fileName;
    }

    public String getCurrentToken() {
        return currentToken;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public void setCurrentLine(String currentLine) {
        this.currentLine = currentLine;
    }

    public void setCurrentToken(String currentToken) {
        this.currentToken = currentToken;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}

