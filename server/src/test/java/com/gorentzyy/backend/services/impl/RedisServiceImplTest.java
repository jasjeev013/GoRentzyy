package com.gorentzyy.backend.services.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.lang.reflect.Field;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RedisServiceImplTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private RedisServiceImpl redisService;

    @BeforeEach
    void setUp() throws Exception {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        Field objectMapperField = RedisServiceImpl.class.getDeclaredField("objectMapper");
        objectMapperField.setAccessible(true);
        objectMapperField.set(redisService, objectMapper);
    }

//    @Test
    void get_WhenKeyExists_ShouldReturnValue() {
        // Arrange
        String key = "testKey";
        String value = "testValue";
        when(valueOperations.get(key)).thenReturn(value);

        // Act
        Optional<String> result = redisService.get(key, String.class);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(value, result.get());
        verify(valueOperations, times(1)).get(key);
    }

    @Test
    void get_WhenKeyDoesNotExist_ShouldReturnEmpty() {
        // Arrange
        String key = "nonExistentKey";
        when(valueOperations.get(key)).thenReturn(null);

        // Act
        Optional<String> result = redisService.get(key, String.class);

        // Assert
        assertFalse(result.isPresent());
        verify(valueOperations, times(1)).get(key);
    }

    @Test
    void get_WhenRedisFails_ShouldReturnEmptyAndLogError() {
        // Arrange
        String key = "failingKey";
        when(valueOperations.get(key)).thenThrow(new RuntimeException("Redis error"));

        // Act
        Optional<String> result = redisService.get(key, String.class);

        // Assert
        assertFalse(result.isPresent());
        verify(valueOperations, times(1)).get(key);
    }

    @Test
    void set_WithTTL_ShouldCallSetWithExpiration() {
        // Arrange
        String key = "testKey";
        String value = "testValue";
        Duration ttl = Duration.ofMinutes(10);

        // Act
        redisService.set(key, value, ttl);

        // Assert
        verify(valueOperations, times(1)).set(eq(key), eq(value), eq(ttl));
    }

    @Test
    void set_WithoutTTL_ShouldCallSetWithoutExpiration() {
        // Arrange
        String key = "testKey";
        String value = "testValue";

        // Act
        redisService.set(key, value, null);

        // Assert
        verify(valueOperations, times(1)).set(eq(key), eq(value));
    }

    @Test
    void set_WhenRedisFails_ShouldThrowException() {
        // Arrange
        String key = "failingKey";
        String value = "testValue";

        doThrow(new RuntimeException("Redis error"))
                .when(valueOperations).set(anyString(), any());

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                redisService.set(key, value, null));

        verify(valueOperations, times(1)).set(eq(key), eq(value));
    }


//    @Test
    void getList_WhenKeyExists_ShouldReturnList() throws Exception {
        // Arrange
        String key = "testListKey";
        String json = "[{\"name\":\"test\"}]";
        List<TestObject> expectedList = Collections.singletonList(new TestObject("test"));

        when(valueOperations.get(key)).thenReturn(json);
        when(objectMapper.readValue(eq(json), any(TypeReference.class))).thenReturn(expectedList);

        // Act
        Optional<List<TestObject>> result = redisService.getList(key, TestObject.class);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(1, result.get().size());
        assertEquals("test", result.get().get(0).name);
        verify(valueOperations, times(1)).get(key);
    }

    @Test
    void getList_WhenKeyDoesNotExist_ShouldReturnEmpty() {
        // Arrange
        String key = "nonExistentListKey";
        when(valueOperations.get(key)).thenReturn(null);

        // Act
        Optional<List<TestObject>> result = redisService.getList(key, TestObject.class);

        // Assert
        assertFalse(result.isPresent());
        verify(valueOperations, times(1)).get(key);
    }

    @Test
    void getList_WhenInvalidData_ShouldReturnEmpty() {
        // Arrange
        String key = "invalidDataKey";
        when(valueOperations.get(key)).thenReturn("invalid json");

        // Act
        Optional<List<TestObject>> result = redisService.getList(key, TestObject.class);

        // Assert
        assertFalse(result.isPresent());
        verify(valueOperations, times(1)).get(key);
    }

    @Test
    void setList_WithTTL_ShouldCallSetWithExpiration() throws Exception {
        // Arrange
        String key = "testListKey";
        List<TestObject> value = Collections.singletonList(new TestObject("test"));
        Duration ttl = Duration.ofMinutes(10);
        String json = "[{\"name\":\"test\"}]";

        when(objectMapper.writeValueAsString(value)).thenReturn(json);

        // Act
        redisService.setList(key, value, ttl);

        // Assert
        verify(valueOperations, times(1)).set(eq(key), eq(json), eq(ttl));
    }

    @Test
    void setList_WithoutTTL_ShouldCallSetWithoutExpiration() throws Exception {
        // Arrange
        String key = "testListKey";
        List<TestObject> value = Collections.singletonList(new TestObject("test"));
        String json = "[{\"name\":\"test\"}]";

        when(objectMapper.writeValueAsString(value)).thenReturn(json);

        // Act
        redisService.setList(key, value, null);

        // Assert
        verify(valueOperations, times(1)).set(eq(key), eq(json));
    }

//    @Test
    void setList_WhenSerializationFails_ShouldThrowException() throws Exception {
        String key = "failingListKey";
        List<TestObject> value = Collections.singletonList(new TestObject("test"));

        when(objectMapper.writeValueAsString(value)).thenThrow(new RuntimeException("Serialization error"));

        assertThrows(RuntimeException.class, () ->
                redisService.setList(key, value, null));
    }

    // Helper class for testing list operations
    private static class TestObject {
        public String name;

        public TestObject(String name) {
            this.name = name;
        }
    }
}