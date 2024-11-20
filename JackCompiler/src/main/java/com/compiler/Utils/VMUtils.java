package com.compiler.Utils;

public class VMUtils {
    static private int labelCounter = 0;
    static private int ifTrue_labelCounter = 0;
    static private int ifFalse_labelCounter = 0;
    static private int whileExp_labelCounter = 0;
    static private int whileEnd_labelCounter = 0;

    public static String generateUniqueLabel(EnumClass.Label label) {
        return switch (label) {
            case IF_FALSE -> String.valueOf(ifFalse_labelCounter++);
            case IF_TRUE -> String.valueOf(ifTrue_labelCounter++);
            case WHILE_EXP -> String.valueOf(whileExp_labelCounter++);
            case WHILE_END -> String.valueOf(whileEnd_labelCounter++);
        };
    }

    public static void setLabelCounter(){
        ifFalse_labelCounter = 0;
        ifTrue_labelCounter = 0;
        whileEnd_labelCounter = 0;
        whileExp_labelCounter = 0;
    }

}
