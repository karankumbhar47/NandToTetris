package com.compiler;

import com.compiler.Utils.Context;
import com.compiler.Utils.EnumClass.TokenType;
import com.compiler.Utils.EnumClass.KeywordType;
import com.compiler.Utils.TokenizerUtils;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import static com.compiler.Utils.TokenizerUtils.keywordMap;

public class JackTokenizer {
    private String fileName;
    private String currentToken;
    public int currentTokenIndex;
    private final List<String> tokens;
    private final List<String> tokenLines;
    private final List<Integer> tokenLineNumbers;

    public JackTokenizer(Path filePath) throws IOException {
        this.tokenLineNumbers = new ArrayList<>();
        this.tokenLines = new ArrayList<>();
        this.tokens = new ArrayList<>();
        this.currentTokenIndex = -1;
        this.fileName = filePath.getFileName().toString();
        tokenize(filePath);
    }

    private void tokenize(Path filePath) throws IOException {
        StringBuilder fileContent = new StringBuilder();
        BufferedReader reader = new BufferedReader(new FileReader(String.valueOf(filePath)));

        String currentLine;
        while ((currentLine = reader.readLine()) != null) {
            fileContent.append(currentLine).append("\n");
        }

        String input = fileContent
                .toString()
                .replaceAll("//.*|/\\*\\*?[\\s\\S]*?\\*/", "#");
        BufferedReader cleanReader = new BufferedReader(new StringReader(input));

        int lineNumber = 0;
        while ((currentLine = cleanReader.readLine()) != null) {
            lineNumber++;
            Matcher lineMatcher = TokenizerUtils.TOKEN_PATTERN.matcher(currentLine);
            while (lineMatcher.find()) {
                tokens.add(lineMatcher.group());
                tokenLineNumbers.add(lineNumber);
                tokenLines.add(currentLine);
            }
        }
        cleanReader.close();
    }

    public boolean hasMoreTokens() {
        return currentTokenIndex < tokens.size() - 1;
    }

    public void advance() {
        if(hasMoreTokens()){
            currentTokenIndex++;
            currentToken = tokens.get(currentTokenIndex);
        }
    }

    public TokenType tokenType() {
        if (currentToken.matches(TokenizerUtils.keyWordRegX)) {
            return TokenType.KEYWORD;
        } else if (currentToken.matches(TokenizerUtils.symbolRegX)) {
            return TokenType.SYMBOL;
        } else if (currentToken.matches(TokenizerUtils.integerConstRegX)) {
            return TokenType.INT_CONST;
        } else if (currentToken.matches(TokenizerUtils.stringConstRegX)) {
            return TokenType.STRING_CONST;
        } else if (currentToken.matches(TokenizerUtils.identifiersRegX)) {
            return TokenType.IDENTIFIER;
        }
        return null;
    }

    public KeywordType keyWord() {
        if (tokenType() == TokenType.KEYWORD)
            return keywordMap.get(currentToken);
        return null;
    }

    public Character symbol() {
        if (tokenType() == TokenType.SYMBOL)
            return currentToken.charAt(0);
        return null;
    }

    public String identifier() {
        if (tokenType() == TokenType.IDENTIFIER)
            return currentToken;
        return null;
    }

    public Integer intVal() {
        if (tokenType() == TokenType.INT_CONST)
            return Integer.parseInt(currentToken);
        return null;
    }

    public String stringVal() {
        if (tokenType() == TokenType.STRING_CONST)
            return currentToken.substring(1,currentToken.length()-1);
        return null;
    }

    public Context getContext() {
        int lineNumber = tokenLineNumbers.get(currentTokenIndex);
        String currentLine = tokenLines.get(currentTokenIndex);
        return new Context(lineNumber, currentLine, fileName,currentToken);
    }
}
