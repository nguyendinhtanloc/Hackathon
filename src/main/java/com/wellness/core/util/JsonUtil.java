package com.wellness.core.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

/**
 * JSON Utility for parsing and serialization
 */
public class JsonUtil {
    
    private static final Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
            .setPrettyPrinting()
            .create();
    
    /**
     * Convert object to JSON string
     */
    public static String toJson(Object obj) {
        return gson.toJson(obj);
    }
    
    /**
     * Parse JSON string to object
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return gson.fromJson(json, clazz);
        } catch (JsonSyntaxException e) {
            throw new RuntimeException("Invalid JSON format", e);
        }
    }
    
    /**
     * Check if string is valid JSON
     */
    public static boolean isValidJson(String json) {
        try {
            gson.fromJson(json, Object.class);
            return true;
        } catch (JsonSyntaxException e) {
            return false;
        }
    }
}
