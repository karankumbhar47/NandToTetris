# <Center> JackCompiler Project</Center>

### Project Overview

JackCompiler is a Java-based project that compiles programs written in the Jack programming language into intermediate
virtual machine (VM) code. It tokenizes, parses, and converts Jack code into `.vm` files, which can then be executed using
a virtual machine. The project uses Java for implementation, Maven for build automation, and JUnit for testing.

JackCompiler supports both single `.jack` files and directories containing multiple `.jack` files. The output `.vm` files are
saved in the specified output directory or, by default, in the current working directory.

### Tools Used

- **Java** : The programming language used for implementation.
- **Maven** : For project management and build automation.
- **JUnit** : For unit testing.

### Installation

To install Maven on your system, use the following command:
```bash
sudo apt install maven
```

### Building the Project

To build and package the JackCompiler project, follow these steps:

1. **Ensure Maven is Installed**
 
   Verify that Maven is installed on your system by running:
    
   ```bash
   mvn -version
   ```
   
2. **Build the Project**

   In the root directory of the project, execute the following Maven command to compile the code and package it into a JAR file:
 
   ```bash
    mvn clean package
   ```

    This command will compile the project, run tests, and create a JAR file in the target directory.

### Running the Project Using the JAR Package

After building the project, you can run the JackCompiler tool using the generated JAR file. Here’s how:

1. **Navigate to the Target Directory**

    The JAR file is located in the target directory. Navigate to this directory:
   ```bash
   cd target
   ```

2. Run the JAR File

   Use the following command to run the JackCompiler tool,replacing <file-path> with the path to your .jack file or directory of .jack files:

   ```bash
    java -jar JackCompiler-1.0.jar <file-path>
   ```

   **Parameters**:
   - `<file-path>` : Path to a single .jack file or a directory containing multiple .jack files.

   **Note**:
    1. Each .jack file in /src/main/resources/JackFiles/ is used for testing and verification.
    2. Output will be generated in the directory of the input file.
    3. If input is directory the output will be stored in directory with name `<directory-name>_out` in directory of parent directory of the input directory

3. **Important Notes**
- For single .jack files, provide the file path. For an entire directory, ensure it contains .jack files.
- The output files will have the same name as the .jack files but with the .vm extension.
- You can find test .jack files in the /src/main/resources/JackFiles/ directory, useful for verifying the generated vm files.

### Testing
- For this project, use the description given in [PDF](./project.pdf) for testing each program
- Read the description from [PDF](./project.pdf).
- Run the generated vm code in VMEmulator
- Match the output mentioned in [PDF](./project.pdf).

### Using JackCompiler Without Maven

If you prefer not to use Maven, you can compile and run the project manually using a provided script. Ensure you are in the root directory of the project and follow these steps:

1. **Change Directory**
 
   Change the current directory to the root directory of the project.

2. **Ensure the Script is Executable**

    The root directory contains a script named `JackCompiler`. Change the script file permissions to make it executable:
   ```bash
   chmod +x JackCompiler
   ```

3. **Run the Script**

   Use the script to compile and run the JackCompiler tool:
   
   ```bash
    ./JackCompiler <file-path> 
   ```

   This command will compile the project and run the JackCompiler tool with the specified .jack file or directory as
   input and output the vm files accordingly.

