package com.vmTranslator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.vmTranslator.Parser.CommandType;
import com.vmTranslator.VMExceptions.SyntaxExceptions.*;

import static com.vmTranslator.Parser.CommandType.*;

public class VMTranslator {
    public static void main(String[] args) throws IOException {
//        Path filePath = Paths.get(args[0]);
//        firstPass(filePath);

        Path inputPath = Paths.get(args[0]);

        if (Files.isDirectory(inputPath)) {
            try (Stream<Path> paths = Files.walk(inputPath)) {
                List<Path> vmFiles = paths
                        .filter(p -> p.toString().endsWith(".vm"))
                        .collect(Collectors.toList());
                if (vmFiles.isEmpty()) {
                    System.out.println("No .vm files found in the directory.");
                    return;
                }
                String directoryName = inputPath.getFileName().toString();
                Path outputAsmFile = inputPath.resolve(directoryName + ".asm");

                processFiles(vmFiles, outputAsmFile);
            }
        } else {
            Path outputFilePath = inputPath.resolveSibling(inputPath.getFileName().toString()
                    .replace(".vm", ".asm"));
            processFiles(List.of(inputPath), outputFilePath);
        }
    }

    private static void processFiles(List<Path> vmFiles, Path outputAsmFile) throws IOException {
        System.out.println("Generating output file: " + outputAsmFile.toAbsolutePath());
        int retIndex = 0;

        boolean hasError = false;
        CodeWriter codeWriter = new CodeWriter(outputAsmFile.toString());
        codeWriter.setFileName(outputAsmFile.getFileName().toString().replace(".asm", ""));

        if (vmFiles.stream().anyMatch(f -> f.getFileName().toString().equals("Sys.vm")))
            codeWriter.writeInit(retIndex++);

        for (Path vmFile : vmFiles) {
            Parser parser = new Parser(vmFile);
            codeWriter.setFileName(vmFile.getFileName().toString().replace(".vm", ""));
            System.out.println("Processing file: " + vmFile.getFileName());

            while (parser.hasMoreLines()) {
                try {
                    parser.advance();
                    CommandType type = parser.commandType();
                    if (type == C_NULL) continue;

                    switch (type) {
                        case C_PUSH ->
                                codeWriter.writePushPop(C_PUSH, parser.arg1(), parser.arg2(), parser.getContext());
                        case C_POP ->
                                codeWriter.writePushPop(C_POP, parser.arg1(), parser.arg2(), parser.getContext());
                        case C_ARITHMETIC ->
                                codeWriter.writeArithmetic(parser.arg1(), parser.getContext());
                        case C_LABEL ->
                                codeWriter.writeLabel(parser.arg1());
                        case C_GOTO ->
                                codeWriter.writeGoTo(parser.arg1());
                        case C_IF ->
                                codeWriter.writeIf(parser.arg1());
                        case C_CALL ->
                                codeWriter.writeCall(parser.arg1(), parser.arg2(),retIndex++);
                        case C_FUNCTION ->
                                codeWriter.writeFunction(parser.arg1(), parser.arg2());
                        case C_RETURN ->
                                codeWriter.writeReturn();
                        case C_NULL ->
                                throw new NuLLCommandFoundException(parser.getLineNumber(), parser.getCurrent_line());
                        default ->
                                throw new InstructionNotHandled(type.name(), parser.getLineNumber(), parser.getCurrent_line());
                    }
                } catch (Exception exception) {
                    hasError = true;
                    System.err.println(exception.getMessage());
                }
            }

            parser.close();
        }

        codeWriter.close(hasError);
    }
}


//private static void firstPass(Path filePath) throws IOException {
//    System.out.println("Input File " + filePath.toAbsolutePath());
//    String fileName = filePath.getFileName().toString()
//            .replace("vm", "asm");
//
//    boolean hasError = false;
//
//    Parser parser = new Parser(filePath);
//    CodeWriter codeWriter = new CodeWriter(fileName);
//    codeWriter.setFileName(fileName.replace(".asm",""));
//
//    while (parser.hasMoreLines()) {
//        try {
//            parser.advance();
//            CommandType type = parser.commandType();
//            if (type == C_NULL)
//                continue;
//
//            switch (type) {
//                case C_PUSH ->
//                        codeWriter.writePushPop(C_PUSH, parser.arg1(), parser.arg2(), parser.getContext());
//                case C_POP ->
//                        codeWriter.writePushPop(C_POP, parser.arg1(), parser.arg2(), parser.getContext());
//                case C_ARITHMETIC ->
//                        codeWriter.writeArithmetic(parser.arg1(), parser.getContext());
//                case C_LABEL ->
//                        codeWriter.writeLabel(parser.arg1());
//                case C_GOTO ->
//                        codeWriter.writeGoTo(parser.arg1());
//                case C_IF ->
//                        codeWriter.writeIf(parser.arg1());
//                case C_CALL ->
//                        codeWriter.writeCall(parser.arg1(),parser.arg2());
//                case C_FUNCTION ->
//                        codeWriter.writeFunction(parser.arg1(),parser.arg2());
//                case C_RETURN ->
//                        codeWriter.writeReturn();
//                case C_NULL ->
//                        throw new NuLLCommandFoundException(parser.getLineNumber(), parser.getCurrent_line());
//                default ->
//                        throw new InstructionNotHandled(type.name(), parser.getLineNumber(), parser.getCurrent_line());
//            }
//        } catch (Exception exception) {
//            hasError = true;
//            System.err.println(exception.getMessage());
//        }
//    }
//
//    codeWriter.close(hasError);
//    parser.close();
//}

