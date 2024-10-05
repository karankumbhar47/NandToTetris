package com.vmTranslator.VMExceptions;

import com.vmTranslator.Parser;

public class SyntaxExceptions extends Exception {
    public SyntaxExceptions(String message) {
        super(message);
    }

    public static class InvalidCommandException extends SyntaxExceptions {
        public InvalidCommandException(String command) {
            super("Invalid command: " + command);
        }
    }


    public static class InvalidSegmentException extends SyntaxExceptions {
        public InvalidSegmentException(String segment) {
            super("Invalid memory segment: " + segment);
        }
    }


    public static class InvalidPushPopAddSegmentException extends SyntaxExceptions {
        public InvalidPushPopAddSegmentException(String segment,Parser.CommandType commandType) {
            super("Searching for Segment among {local, argument, this, that},for command type "+commandType+" But found "+segment);
        }
    }

    public static class InvalidPushPopSegmentException extends SyntaxExceptions {
        public InvalidPushPopSegmentException(String segment, Parser.CommandType commandType) {
            super("Searching for Segment among {temp, static, pointer} for command type "+commandType+", But found "+segment);
        }
    }

    public static class InvalidIndexException extends SyntaxExceptions {
        public InvalidIndexException(int index, int min, int max) {
            super("Invalid index: " + index + ". Expected between " + min + " and " + max);
        }
    }

    public static class InstructionNotHandled extends SyntaxExceptions {
        public InstructionNotHandled(String instruction) {
            super("Instruction Not handled" +instruction);
        }
    }

    public static class MissingFirstArgumentException extends SyntaxExceptions {
        public MissingFirstArgumentException(String command) {
            super("Missing First argument for command: " + command);
        }
    }

    public static class MissingSecondArgumentException extends SyntaxExceptions {
        public MissingSecondArgumentException(String command) {
            super("Missing Second argument for command: " + command);
        }
    }

    public static class InvalidArithmeticCommandException extends SyntaxExceptions {
        public InvalidArithmeticCommandException(String command) {
            super("Invalid arithmetic command: " + command);
        }
    }

    public static class InvalidBinaryOperationException extends SyntaxExceptions {
        public InvalidBinaryOperationException(String operation) {
            super("Searching for binary operators among {add,sub,and,or}, but found " + operation);
        }
    }

    public static class InvalidCompOperationException extends SyntaxExceptions {
        public InvalidCompOperationException(String operation) {
            super("Searching for Comparison operators among {gt, lt, eq} , but found "+operation);
        }
    }

    public static class InvalidUnaryOperationException extends SyntaxExceptions {
        public InvalidUnaryOperationException(String operation) {
            super("Searching for Unary operators among {not, neg}, but found "+operation);
        }
    }

    public static class InvalidFunctionCallException extends SyntaxExceptions {
        public InvalidFunctionCallException(String command) {
            super("Invalid function call format: " + command);
        }
    }

    public static class UnexpectedEndOfFileException extends SyntaxExceptions {
        public UnexpectedEndOfFileException() {
            super("Unexpected end of file.");
        }
    }

    public static class InvalidLabelNameException extends SyntaxExceptions {
        public InvalidLabelNameException(String label) {
            super("Invalid label name: " + label);
        }
    }

    public static class ExcessiveArgumentsException extends SyntaxExceptions {
        public ExcessiveArgumentsException(String command) {
            super("Too many arguments for command: " + command);
        }
    }

    public static class InvalidGotoCommandException extends SyntaxExceptions {
        public InvalidGotoCommandException(String command) {
            super("Invalid 'goto' command format: " + command);
        }
    }

    public static class NuLLCommandFoundException extends SyntaxExceptions {
        public NuLLCommandFoundException() {
            super("No command found on current line.");
        }
    }

}


