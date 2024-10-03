package ar.edu.utn.dds.k3003.model;

import io.javalin.json.JsonMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.InputStream;
import java.io.IOException;
import java.lang.reflect.Type;

public class CustomJsonMapper implements JsonMapper {
    private final ObjectMapper objectMapper;

    public CustomJsonMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String toJsonString(Object obj, Type type) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            // Handle or log the exception
            return ""; // Or return a default value, or rethrow as a RuntimeException
        }
    }

    @Override
    public <T> T fromJsonString(String json, Type type) {
        try {
            return objectMapper.readValue(json, objectMapper.constructType(type));
        } catch (JsonProcessingException e) {
            // Handle or log the exception
            return null; // Or return a default value, or rethrow as a RuntimeException
        }
    }

    @Override
    public <T> T fromJsonStream(InputStream inputStream, Type type) {
        try {
            return objectMapper.readValue(inputStream, objectMapper.constructType(type));
        } catch (IOException e) {
            // Handle or log the exception
            return null; // Or return a default value, or rethrow as a RuntimeException
        }
    }
}
