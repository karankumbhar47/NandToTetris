package com.vmTranslator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.cert.TrustAnchor;
import java.util.Objects;

import com.vmTranslator.Parser.CommandType;

public class CodeWriter {
    private final BufferedWriter writer;
    private int labelID = 0;
    private static final Path VM_FILES_DIR = Paths.get("src/main/resources/VMFiles");
    private boolean isGTSet;

    CodeWriter(String fileName) throws IOException {
        Path outputFilePath = VM_FILES_DIR.resolve(fileName);
        writer = new BufferedWriter(new FileWriter(outputFilePath.toFile()));
        isGTSet = false;
    }

    public void writeArithmetic(String command) throws IOException{
        String program = switch (command) {
            case "add" -> Utils.binaryOprProgram(0);
            case "sub" -> Utils.binaryOprProgram(1);
            case "and" -> Utils.binaryOprProgram(2);
            case "or" -> Utils.binaryOprProgram(3);
            case "neg" -> Utils.unaryOprProgram(0);
            case "not" -> Utils.unaryOprProgram(1);
            case "gt" -> Utils.compOpProgram(0, labelID++);
            case "lt" -> Utils.compOpProgram(1, labelID++);
            case "eq" -> Utils.compOpProgram(2, labelID++);
            default -> throw new IllegalArgumentException("Invalid Command " + command);
        };
        if(!program.isEmpty()) {
            writer.write(program);
            writer.flush();
        }
    }

    public void writePushPop(CommandType commandType, String segment, int index) throws IOException{
        String program;
        boolean isAdderAddition = Objects.equals(segment, "local") ||
                Objects.equals(segment, "argument") ||
                Objects.equals(segment, "this") ||
                Objects.equals(segment, "that");

        if(commandType==CommandType.C_PUSH) {
            if(Objects.equals(segment, "constant"))
                program = Utils.pushConstProgram(segment,index);
            else if (isAdderAddition)
                program = Utils.pushAddProgram(segment, index);
            else
                program = Utils.pushProgram(segment, index);
        }
        else{
            if (isAdderAddition)
                program = Utils.popAddProgram(segment, index);
            else
                program = Utils.popProgram(segment, index);
        }

        writer.write(program);
        writer.flush();
    }

    public void close() throws IOException{
        if(writer!=null){
            writer.flush();
            writer.close();
        }
    }
}
