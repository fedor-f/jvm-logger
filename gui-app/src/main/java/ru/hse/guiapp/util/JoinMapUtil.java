package ru.hse.guiapp.util;

import java.util.HashMap;
import java.util.Map;

public class JoinMapUtil {

    public static Map<String, Pair<Integer, String>> innerJoin(Map<String, String> firstMap,
                                                               Map<String, Integer> secondMap) {
        Map<String, Pair<Integer, String>> joinedMap = new HashMap<>();

        for (Map.Entry<String, Integer> entry : secondMap.entrySet()) {
            String key = entry.getKey();
            Integer value1 = entry.getValue();
            if (firstMap.containsKey(key)) {
                joinedMap.put(key, new Pair<>(value1, firstMap.get(key)));
            }
        }

        return joinedMap;
    }
}
