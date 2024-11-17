package com.compiler;

import com.compiler.Utils.SyntaxExceptions;

import java.io.IOException;
import java.nio.file.*;

public class JackCompiler {
    public static void main(String[] args) throws SyntaxExceptions,IOException {
        if (args.length != 1) {
            System.out.println("Usage: JackCompiler <input>");
            return;
        }

        Path inputPath = Paths.get(args[0]);
        if (Files.isDirectory(inputPath)) {
            Path outputDir = inputPath.resolveSibling(inputPath.getFileName() + "_out");
            Files.createDirectories(outputDir);

            Files.walk(inputPath)
                    .filter(path -> path.toString().endsWith(".jack"))
                    .forEach(filePath -> {
                        processFile(filePath, outputDir);
                    });
        } else
            processFile(inputPath, inputPath.getParent());
    }

    private static void processFile(Path filePath, Path outputDir){
        try {
            String outputVMFileName = filePath.getFileName().toString().replace(".jack", ".vm");
            Path outputVMPath = outputDir.resolve(outputVMFileName);

            System.out.println("Input File    : "+filePath.toAbsolutePath());
            JackTokenizer tokenizer = new JackTokenizer(filePath);
            CompilationEngine engine = new CompilationEngine(outputVMPath,tokenizer);
            if(tokenizer.hasMoreTokens()) {
                tokenizer.advance();
                engine.compileClass();
            }
            engine.closeFile();
            System.out.println("Compiled File : " + outputVMPath.toAbsolutePath());
        } catch (IOException|SyntaxExceptions exception) {
            System.err.println(exception.getMessage());
        }
    }
}

