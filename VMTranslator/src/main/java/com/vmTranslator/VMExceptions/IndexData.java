package com.vmTranslator.VMExceptions;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class IndexData {
    public static Map<String, List<Integer>> indexMap;
    static{
        indexMap.put("local", Arrays.asList(0,239));
        indexMap.put("argument", Arrays.asList(0,239));
        indexMap.put("this", Arrays.asList(0,239));
        indexMap.put("that", Arrays.asList(0,239));
        indexMap.put("constant", Arrays.asList(0,239));

        indexMap.put("static", Arrays.asList(0,239));
        indexMap.put("pointer", Arrays.asList(0,1));
        indexMap.put("temp", Arrays.asList(0,7));
    }
}
