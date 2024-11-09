package com.compiler;

import com.compiler.CustomExceptions.SyntaxExceptions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class JackAnalyzer {
    public static void main(String[] args) throws IOException, SyntaxExceptions {
        if (args.length != 1) {
            System.out.println("Usage: JackAnalyzer <input>");
            return;
        }

        Path inputPath = Paths.get(args[0]);
        Path currentDirectory = Paths.get("");

        if (Files.isDirectory(inputPath)) {
            Path outputFolderPath = currentDirectory.toAbsolutePath().resolve(inputPath.getFileName() + "_Out");
            Files.createDirectories(outputFolderPath);

            List<Path> jackFiles = Files.walk(inputPath)
                    .filter(path -> path.toString().endsWith(".jack"))
                    .collect(Collectors.toList());

            for (Path filePath : jackFiles) {
                processFile(filePath,outputFolderPath);
            }
        } else if (Files.isRegularFile(inputPath) && inputPath.toString().endsWith(".jack")) {
            processFile(inputPath, currentDirectory.toAbsolutePath());
        } else {
            System.out.println("Invalid input. Provide a .jack file or a folder containing .jack files.");
        }
    }

    private static void processFile(Path filePath,Path outputFolderPath) throws IOException, SyntaxExceptions {
        String outputFileName = filePath.getFileName().toString().replace(".jack", ".xml");
        Path outputFilePath = outputFolderPath.resolve(outputFileName);

        JackTokenizer tokenizer = new JackTokenizer(filePath);
        CompilationEngine engine = new CompilationEngine(outputFilePath, tokenizer);

        tokenizer.advance();
        engine.compileClass();
        engine.closeFile();

        System.out.println("Processed " + filePath + " -> " + outputFilePath);
    }
}
