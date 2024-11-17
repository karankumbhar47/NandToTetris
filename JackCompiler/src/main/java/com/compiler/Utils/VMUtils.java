package com.compiler.Utils;

import com.compiler.CustomExceptions.SyntaxExceptions;

public class VMUtils {
    static private int labelCounter = 0;

    public static String generateUniqueLabel() {
        return String.valueOf(labelCounter++);

    }

    public static void throwError(String message) throws SyntaxExceptions {
//        String currentToken = tokenizer.getCurrentToken();
//        int lineNumber = tokenizer.getCurrentLineNumber(); // Ensure tokenizer tracks line numbers
//        String currentLine = tokenizer.getCurrentLineContent(); // Ensure tokenizer tracks full lines

        StringBuilder errorDetails = new StringBuilder();
//        errorDetails.append("Syntax Error in file: ").append(fileName).append("\n");
//        errorDetails.append("Line ").append(lineNumber).append(": ").append(currentLine).append("\n");
        errorDetails.append("Message: ").append(message).append("\n");
//        errorDetails.append("Offending Token: ").append(currentToken);

        throw new SyntaxExceptions(errorDetails.toString());
    }
}
