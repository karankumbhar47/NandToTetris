package com.vmTranslator;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.vmTranslator.Parser.CommandType;
import com.vmTranslator.VMExceptions.SyntaxExceptions;
import com.vmTranslator.VMExceptions.SyntaxExceptions.*;

public class VMTranslator {
    public static void main(String[] args) throws IOException,SyntaxExceptions {
        Path filePath = Paths.get(args[0]);
        firstPass(filePath);
    }


    private static void firstPass(Path filePath) throws IOException, SyntaxExceptions {
        System.out.println("Input File "+filePath.toAbsolutePath());

        Parser parser = new Parser(filePath);
        CodeWriter codeWriter = new CodeWriter(filePath.getFileName().toString().replace("vm", "asm"));

        while (parser.hasMoreLines()) {
            parser.advance();
            CommandType type = parser.commandType();
            if (type == CommandType.C_NULL)
                continue;

            switch (type) {
                case C_PUSH:
                    codeWriter.writePushPop(CommandType.C_PUSH, parser.arg1(), parser.arg2());
                    break;
                case C_POP:
                    codeWriter.writePushPop(CommandType.C_POP, parser.arg1(), parser.arg2());
                    break;
                case C_ARITHMETIC:
                    codeWriter.writeArithmetic(parser.arg1());
                    break;
                case C_NULL:
                    throw new NuLLCommandFoundException();
                default:
                    throw new InstructionNotHandled(type.name());
            }
        }

        codeWriter.close();
        parser.close();
    }

}