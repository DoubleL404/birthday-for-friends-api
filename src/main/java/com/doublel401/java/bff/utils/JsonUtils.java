package com.doublel401.java.bff.utils;

import com.doublel401.java.bff.exception.InternalServerException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

final public class JsonUtils {
    private JsonUtils() {}

    /**
     * Reuse object mapper to improve performance
     */
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Convert an object to JSON string
     * @param obj generic object
     * @return JSON string
     * @throws InternalServerException 500 Internal Server Exception
     */
    public static String toJsonString(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new InternalServerException("Convert object to json error", e);
        }
    }

    /**
     * Convert json string to object
     * @param json JSON string
     * @param clazz target type
     * @return object of class {@link T}
     * @param <T> target type
     */
    public static <T> T toObjectFromJson(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new InternalServerException("Convert json to object error", e);
        }
    }
}
