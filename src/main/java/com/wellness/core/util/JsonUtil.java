package com.wellness.core.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * JSON Utility for parsing and serialization with Java 17+ compatibility
 */
public class JsonUtil {
    
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            .setPrettyPrinting()
            .create();
    
    /**
     * Custom TypeAdapter for LocalDateTime
     */
    private static class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
        @Override
        public void write(JsonWriter out, LocalDateTime value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(value.format(DATE_TIME_FORMATTER));
            }
        }

        @Override
        public LocalDateTime read(JsonReader in) throws IOException {
            if (in.peek() == null) {
                in.nextNull();
                return null;
            }
            return LocalDateTime.parse(in.nextString(), DATE_TIME_FORMATTER);
        }
    }
    
    /**
     * Custom TypeAdapter for LocalDate
     */
    private static class LocalDateAdapter extends TypeAdapter<LocalDate> {
        @Override
        public void write(JsonWriter out, LocalDate value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(value.format(DATE_FORMATTER));
            }
        }

        @Override
        public LocalDate read(JsonReader in) throws IOException {
            if (in.peek() == null) {
                in.nextNull();
                return null;
            }
            return LocalDate.parse(in.nextString(), DATE_FORMATTER);
        }
    }
    
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
