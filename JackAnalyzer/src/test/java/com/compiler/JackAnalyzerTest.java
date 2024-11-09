package com.compiler;

import com.compiler.CustomExceptions.SyntaxExceptions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JackAnalyzerTest {

    private static final Path RESOURCES_PATH = Paths.get("src/main/resources");
    private static final Path OUTPUT_BASE_PATH = Paths.get("").toAbsolutePath();

    @BeforeEach
    void setUp() throws IOException{
//        if (Files.exists(OUTPUT_BASE_PATH)) {
//            Files.walk(OUTPUT_BASE_PATH)
//                    .map(Path::toFile)
//                    .forEach(File::delete);
//        }
//        Files.createDirectories(OUTPUT_BASE_PATH);
    }

    @AfterEach
    void tearDown() throws IOException{
//        Files.walk(OUTPUT_BASE_PATH)
//                .map(Path::toFile)
//                .forEach(File::delete);
    }


    @ParameterizedTest
    @ValueSource(strings = {
        "ArrayTest",
        "ExpressionLessSquare",
        "Square"
    })
    void testJackAnalyzerWithDirectories(String folderName) throws IOException, SyntaxExceptions {
        List<Path> directories = Files.list(RESOURCES_PATH)
                .filter(Files::isDirectory)
                .collect(Collectors.toList());

        for (Path folder : directories) {
            System.out.println("Testing directory: " + folder.getFileName());
            // Run JackAnalyzer on each folder
            JackAnalyzer.main(new String[]{folder.toString()});

            // Compare each .xml output file with the expected .xml file
            List<Path> expectedFiles = Files.walk(folder)
                    .filter(path -> path.toString().endsWith(".xml"))
                    .collect(Collectors.toList());

            for (Path expectedFile : expectedFiles) {
                Path generatedFile = OUTPUT_BASE_PATH.resolve(folder.getFileName() + "_out").resolve(expectedFile.getFileName());
                assertTrue(Files.exists(generatedFile), "Generated file not found: " + generatedFile);

                // Compare contents
                String expectedContent = Files.readString(expectedFile);
                String generatedContent = Files.readString(generatedFile);
                assertEquals(expectedContent, generatedContent, "Mismatch in contents for file: " + generatedFile);
            }
        }
    }

}