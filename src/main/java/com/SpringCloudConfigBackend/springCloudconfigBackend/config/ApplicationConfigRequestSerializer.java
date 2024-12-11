package com.SpringCloudConfigBackend.springCloudconfigBackend.config;

import com.SpringCloudConfigBackend.springCloudconfigBackend.model.ApplicationConfigRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.io.IOException;

public class ApplicationConfigRequestSerializer implements RedisSerializer<ApplicationConfigRequest> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] serialize(ApplicationConfigRequest applicationConfigRequest) throws SerializationException {
        try {
            return objectMapper.writeValueAsBytes(applicationConfigRequest);
        } catch (JsonProcessingException e) {
            throw new SerializationException("Error serializing object", e);
        }
    }

    @Override
    public ApplicationConfigRequest deserialize(byte[] bytes) throws SerializationException {
        try {
            return objectMapper.readValue(bytes, ApplicationConfigRequest.class);
        } catch (IOException e) {
            throw new SerializationException("Error deserializing object", e);
        }
    }
}

