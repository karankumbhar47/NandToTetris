package com.assembler;

import java.util.HashMap;
import java.util.Map;

public class Code {
    Map<String, Integer> destMap;
    Map<String, Integer> compMap;
    Map<String, Integer> jumpMap;

    Code() {
        initializeMaps();
    }

    public String dest(String destination) {
        return Utils.toBinaryString(destMap.get(destination), 3);
    }

    public String comp(String computation) {
        return Utils.toBinaryString(compMap.get(computation), 7);
    }

    public String jump(String jumpCondition) {
        return Utils.toBinaryString(jumpMap.get(jumpCondition), 3);
    }

    private void initializeMaps() {
        jumpMap = new HashMap<String,Integer>() {{
            put(null, 0);
            put("JGT", 1);
            put("JEQ", 2);
            put("JGE", 3);
            put("JLT", 4);
            put("JNE", 5);
            put("JLE", 6);
            put("JMP", 7);
        }};

        compMap = new HashMap<String,Integer>() {{
            put("0", 42);
            put("1", 63);
            put("-1", 58);
            put("D", 12);
            put("A", 48);
            put("M", 112);
            put("!D", 13);
            put("!A", 49);
            put("!M", 113);
            put("-D", 15);
            put("-A", 51);
            put("-M", 115);
            put("D+1", 31);
            put("A+1", 55);
            put("M+1", 119);
            put("D-1", 14);
            put("A-1", 50);
            put("M-1", 114);
            put("D+A", 2);
            put("D+M", 66);
            put("D-A", 19);
            put("D-M", 83);
            put("A-D", 7);
            put("M-D", 71);
            put("D&A", 0);
            put("D&M", 64);
            put("D|A", 21);
            put("D|M", 85);
        }};

        destMap = new HashMap<String,Integer>() {{
            put(null, 0);
            put("M", 1);
            put("D", 2);
            put("MD", 3);
            put("DM", 3);
            put("A", 4);
            put("AM", 5);
            put("AD", 6);
            put("ADM", 7);
            put("AMD", 7);
        }};

    }
}
