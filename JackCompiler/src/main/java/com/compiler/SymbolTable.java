package com.compiler;

import java.util.HashMap;

public class SymbolTable {
    private static class Symbol {
        String type;
        Kind kind;
        int index;

        Symbol(String type, Kind kind, int index) {
            this.type = type;
            this.kind = kind;
            this.index = index;
        }
    }

    public enum Kind {
        STATIC, THIS, ARGUMENT, LOCAL, NONE
    }

    private final HashMap<String, Symbol> classScope;
    private final HashMap<String, Symbol> subroutineScope;
    private int staticIndex, fieldIndex, argIndex, varIndex;

    public SymbolTable() {
        classScope = new HashMap<>();
        subroutineScope = new HashMap<>();
        staticIndex = fieldIndex = argIndex = varIndex = 0;
    }

    public void reset() {
        subroutineScope.clear();
        argIndex = varIndex = 0;
    }

    public void define(String name, String type, Kind kind) {
        switch (kind) {
            case STATIC:
                classScope.put(name, new Symbol(type, kind,staticIndex++));
                break;
            case THIS:
                classScope.put(name, new Symbol(type, kind, fieldIndex++));
                break;
            case ARGUMENT:
                subroutineScope.put(name, new Symbol(type, kind,argIndex++));
                break;
            case LOCAL:
                subroutineScope.put(name, new Symbol(type, kind,varIndex++));
                break;
        }
    }

    public int varCount(Kind kind) {
        return switch (kind) {
            case STATIC -> staticIndex;
            case THIS -> fieldIndex;
            case ARGUMENT -> argIndex;
            case LOCAL -> varIndex;
            default -> 0;
        };
    }

    public Kind kindOf(String name) {
        if (subroutineScope.containsKey(name)) {
            return subroutineScope.get(name).kind;
        } else if (classScope.containsKey(name)) {
            return classScope.get(name).kind;
        }
        return Kind.NONE;
    }

    public String typeOf(String name) {
        if (subroutineScope.containsKey(name)) {
            return subroutineScope.get(name).type;
        } else if (classScope.containsKey(name)) {
            return classScope.get(name).type;
        }
        return null;
    }

    public int indexOf(String name) {
        if (subroutineScope.containsKey(name)) {
            return subroutineScope.get(name).index;
        } else if (classScope.containsKey(name)) {
            return classScope.get(name).index;
        }
        return -1;
    }
}



