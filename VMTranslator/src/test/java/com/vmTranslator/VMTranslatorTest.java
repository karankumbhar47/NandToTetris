package com.vmTranslator;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class VMTranslatorTest {
    private static final Path VM_FILES_DIR = Paths.get("/home/karan/Project/working/NandToTetris/VMTranslator/src/main/resources/VMFiles");
    private static final Path ASM_FILES_DIR = Paths.get("src/main/resources/ASMFiles");

    private String outputFileName;

    @BeforeEach
    void setup() {}

    @AfterEach
    void tearDown() throws IOException {
        Path outputFilePath = ASM_FILES_DIR.resolve(outputFileName).toAbsolutePath();
//        Files.deleteIfExists(outputFilePath);
    }

    @ParameterizedTest
    @ValueSource(strings = {
           "BasicTest.vm",
           "PointerTest.vm",
           "SimpleAdd.vm",
           "StackTest.vm",
           "StaticTest.vm",
    })
    void testHackAssembler(String fileName) throws IOException , InterruptedException {
        outputFileName = fileName.replace(".vm", ".asm");
        String outputTstFileName = fileName.replace(".vm", ".tst");
        Path outputFilePath = VM_FILES_DIR.resolve(outputFileName);
        Path outputTstFilePath = VM_FILES_DIR.resolve(outputTstFileName);
        Path inputFilePath = VM_FILES_DIR.resolve(fileName);

        VMTranslator.main(new String[]{inputFilePath.toString()});

        String pathToTestScript = outputTstFilePath.toString();
        ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", "CPUEmulator.sh " + pathToTestScript);

        Process process = processBuilder.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder output = new StringBuilder();

        String line;
        while ((line = reader.readLine()) != null)
            output.append(line).append("\n");
        int exitCode = process.waitFor();

        String successMessage = "End of script - Comparison ended successfully";
        assertTrue(output.toString().contains(successMessage), "Test failed, success message not found in output : "+output);
        assertEquals(0, exitCode, "Process exited with non-zero exit code: " + exitCode);
    }
}