package com.compiler.Utils;

public class VMUtils {
    static private int labelCounter = 0;

    public static String generateUniqueLabel() {
        return String.valueOf(labelCounter++);
    }
}
