package com.m4l0n.hitchride.utility;

import java.util.HashMap;
import java.util.Map;

public class UtilityMethods {

    public static <K, V> Map<K, V> deepCopyMap(Map<K, V> original) {
        Map<K, V> copy = new HashMap<>();

        for (Map.Entry<K, V> entry : original.entrySet()) {
            K key = entry.getKey();
            V value = entry.getValue();

            // Perform a deep copy of the value if it's mutable
            if (value instanceof Cloneable) {
                try {
                    @SuppressWarnings("unchecked")
                    V clonedValue = (V) value.getClass().getMethod("clone").invoke(value);
                    copy.put(key, clonedValue);
                } catch (Exception e) {
                    // Handle the exception or fallback to shallow copy
                    copy.put(key, value);
                }
            } else {
                // Fallback to shallow copy for immutable values
                copy.put(key, value);
            }
        }

        return copy;
    }

}
