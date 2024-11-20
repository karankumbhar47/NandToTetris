package com.compiler.Utils;

public class SyntaxExceptions extends Exception{
    public SyntaxExceptions(String message,Context context){
        super(getFormattedMessage(message,context));
    }

    private static String getFormattedMessage(String message,Context context){
        return "\nSyntax Error in file: " + context.getFileName() + "\n" +
                "Line "+context.getLineNumber()+": " + context.getCurrentLine() + "\n" +
                "Message: " + message + "\n" +
                "Offending Token: " + context.getCurrentToken() +"\n";
    }

    public static class InvalidOpeningBracketsException extends SyntaxExceptions{
        public InvalidOpeningBracketsException(Context context){
            super("Expected '{' at the start of class body.",context);
        }
    }

    public static class InvalidFunctionVariableKeywordException extends SyntaxExceptions{
        public InvalidFunctionVariableKeywordException(Context context){
            super("Expected 'var' or 'arg' at start of Function Variable.",context);
        }
    }
}
