package com.automation.framework.utils;

import com.automation.framework.config.ConfigurationManager;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class for managing test data
 * Supports JSON, CSV, and properties file formats
 * 
 * @author Automation Framework
 * @version 1.0
 */
public class TestDataUtils {
    
    private static final Logger logger = LoggerFactory.getLogger(TestDataUtils.class);
    private final ConfigurationManager config;
    private final ObjectMapper objectMapper;
    
    /**
     * Constructor
     */
    public TestDataUtils() {
        this.config = ConfigurationManager.getInstance();
        this.objectMapper = new ObjectMapper();
        logger.debug("TestDataUtils initialized");
    }
    
    /**
     * Load test data from JSON file
     * @param fileName JSON file name (relative to test data path)
     * @param dataClass Class type to deserialize to
     * @param <T> Generic type
     * @return Deserialized object
     */
    public <T> T loadJsonData(String fileName, Class<T> dataClass) {
        logger.info("Loading JSON data from file: {}", fileName);
        
        try {
            String filePath = getTestDataFilePath(fileName);
            File file = new File(filePath);
            
            if (!file.exists()) {
                logger.error("Test data file not found: {}", filePath);
                throw new RuntimeException("Test data file not found: " + filePath);
            }
            
            T data = objectMapper.readValue(file, dataClass);
            logger.debug("JSON data loaded successfully from: {}", fileName);
            return data;
            
        } catch (IOException e) {
            logger.error("Failed to load JSON data from {}: {}", fileName, e.getMessage(), e);
            throw new RuntimeException("Failed to load JSON data from " + fileName, e);
        }
    }
    
    /**
     * Load test data from JSON file as Map
     * @param fileName JSON file name
     * @return Map representation of JSON data
     */
    public Map<String, Object> loadJsonDataAsMap(String fileName) {
        logger.info("Loading JSON data as Map from file: {}", fileName);
        
        try {
            String filePath = getTestDataFilePath(fileName);
            File file = new File(filePath);
            
            if (!file.exists()) {
                logger.error("Test data file not found: {}", filePath);
                throw new RuntimeException("Test data file not found: " + filePath);
            }
            
            TypeReference<Map<String, Object>> typeRef = new TypeReference<Map<String, Object>>() {};
            Map<String, Object> data = objectMapper.readValue(file, typeRef);
            
            logger.debug("JSON data loaded as Map successfully from: {}", fileName);
            return data;
            
        } catch (IOException e) {
            logger.error("Failed to load JSON data as Map from {}: {}", fileName, e.getMessage(), e);
            throw new RuntimeException("Failed to load JSON data as Map from " + fileName, e);
        }
    }
    
    /**
     * Load test data from JSON file as List
     * @param fileName JSON file name
     * @param elementClass Class type of list elements
     * @param <T> Generic type
     * @return List of deserialized objects
     */
    public <T> List<T> loadJsonDataAsList(String fileName, Class<T> elementClass) {
        logger.info("Loading JSON data as List from file: {}", fileName);
        
        try {
            String filePath = getTestDataFilePath(fileName);
            File file = new File(filePath);
            
            if (!file.exists()) {
                logger.error("Test data file not found: {}", filePath);
                throw new RuntimeException("Test data file not found: " + filePath);
            }
            
            TypeReference<List<T>> typeRef = new TypeReference<List<T>>() {};
            List<T> data = objectMapper.readValue(file, typeRef);
            
            logger.debug("JSON data loaded as List successfully from: {}", fileName);
            return data;
            
        } catch (IOException e) {
            logger.error("Failed to load JSON data as List from {}: {}", fileName, e.getMessage(), e);
            throw new RuntimeException("Failed to load JSON data as List from " + fileName, e);
        }
    }
    
    /**
     * Load test data from JSON resource (classpath)
     * @param resourcePath Resource path
     * @param dataClass Class type to deserialize to
     * @param <T> Generic type
     * @return Deserialized object
     */
    public <T> T loadJsonDataFromResource(String resourcePath, Class<T> dataClass) {
        logger.info("Loading JSON data from resource: {}", resourcePath);
        
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                logger.error("Test data resource not found: {}", resourcePath);
                throw new RuntimeException("Test data resource not found: " + resourcePath);
            }
            
            T data = objectMapper.readValue(inputStream, dataClass);
            logger.debug("JSON data loaded successfully from resource: {}", resourcePath);
            return data;
            
        } catch (IOException e) {
            logger.error("Failed to load JSON data from resource {}: {}", resourcePath, e.getMessage(), e);
            throw new RuntimeException("Failed to load JSON data from resource " + resourcePath, e);
        }
    }
    
    /**
     * Load test data from JSON resource as Map
     * @param resourcePath Resource path
     * @return Map representation of JSON data
     */
    public Map<String, Object> loadJsonDataFromResourceAsMap(String resourcePath) {
        logger.info("Loading JSON data as Map from resource: {}", resourcePath);
        
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                logger.error("Test data resource not found: {}", resourcePath);
                throw new RuntimeException("Test data resource not found: " + resourcePath);
            }
            
            TypeReference<Map<String, Object>> typeRef = new TypeReference<Map<String, Object>>() {};
            Map<String, Object> data = objectMapper.readValue(inputStream, typeRef);
            
            logger.debug("JSON data loaded as Map successfully from resource: {}", resourcePath);
            return data;
            
        } catch (IOException e) {
            logger.error("Failed to load JSON data as Map from resource {}: {}", resourcePath, e.getMessage(), e);
            throw new RuntimeException("Failed to load JSON data as Map from resource " + resourcePath, e);
        }
    }
    
    /**
     * Save object as JSON file
     * @param fileName Output file name
     * @param data Object to serialize
     * @param <T> Generic type
     */
    public <T> void saveJsonData(String fileName, T data) {
        logger.info("Saving data as JSON to file: {}", fileName);
        
        try {
            String filePath = getTestDataFilePath(fileName);
            File file = new File(filePath);
            
            // Create parent directories if they don't exist
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                boolean created = parentDir.mkdirs();
                if (!created) {
                    logger.warn("Could not create parent directories for: {}", filePath);
                }
            }
            
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, data);
            logger.debug("Data saved as JSON successfully to: {}", fileName);
            
        } catch (IOException e) {
            logger.error("Failed to save JSON data to {}: {}", fileName, e.getMessage(), e);
            throw new RuntimeException("Failed to save JSON data to " + fileName, e);
        }
    }
    
    /**
     * Get specific value from JSON data by key path
     * @param fileName JSON file name
     * @param keyPath Dot-separated key path (e.g., "user.credentials.username")
     * @return Value at the specified key path
     */
    public Object getValueByKeyPath(String fileName, String keyPath) {
        logger.debug("Getting value by key path '{}' from file: {}", keyPath, fileName);
        
        Map<String, Object> data = loadJsonDataAsMap(fileName);
        String[] keys = keyPath.split("\\.");
        Object current = data;
        
        for (String key : keys) {
            if (current instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> map = (Map<String, Object>) current;
                current = map.get(key);
                if (current == null) {
                    logger.warn("Key '{}' not found in path '{}'", key, keyPath);
                    return null;
                }
            } else {
                logger.warn("Cannot navigate further in key path '{}' at key '{}'", keyPath, key);
                return null;
            }
        }
        
        logger.debug("Value found for key path '{}': {}", keyPath, current);
        return current;
    }
    
    /**
     * Get string value from JSON data by key path
     * @param fileName JSON file name
     * @param keyPath Dot-separated key path
     * @return String value at the specified key path
     */
    public String getStringValue(String fileName, String keyPath) {
        Object value = getValueByKeyPath(fileName, keyPath);
        return value != null ? value.toString() : null;
    }
    
    /**
     * Get integer value from JSON data by key path
     * @param fileName JSON file name
     * @param keyPath Dot-separated key path
     * @return Integer value at the specified key path
     */
    public Integer getIntegerValue(String fileName, String keyPath) {
        Object value = getValueByKeyPath(fileName, keyPath);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        } else if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException e) {
                logger.warn("Cannot parse '{}' as integer", value);
                return null;
            }
        }
        return null;
    }
    
    /**
     * Get boolean value from JSON data by key path
     * @param fileName JSON file name
     * @param keyPath Dot-separated key path
     * @return Boolean value at the specified key path
     */
    public Boolean getBooleanValue(String fileName, String keyPath) {
        Object value = getValueByKeyPath(fileName, keyPath);
        if (value instanceof Boolean) {
            return (Boolean) value;
        } else if (value instanceof String) {
            return Boolean.parseBoolean((String) value);
        }
        return null;
    }
    
    /**
     * Create test data object with builder pattern
     * @return TestDataBuilder instance
     */
    public TestDataBuilder createDataBuilder() {
        return new TestDataBuilder();
    }
    
    /**
     * Get full file path for test data file
     * @param fileName File name
     * @return Full file path
     */
    private String getTestDataFilePath(String fileName) {
        String testDataPath = config.getTestDataPath();
        if (!testDataPath.endsWith("/") && !testDataPath.endsWith("\\")) {
            testDataPath += File.separator;
        }
        return testDataPath + fileName;
    }
    
    /**
     * Builder class for creating test data objects
     */
    public static class TestDataBuilder {
        private final Map<String, Object> data = new HashMap<>();
        
        public TestDataBuilder add(String key, Object value) {
            data.put(key, value);
            return this;
        }
        
        public TestDataBuilder addString(String key, String value) {
            data.put(key, value);
            return this;
        }
        
        public TestDataBuilder addInteger(String key, Integer value) {
            data.put(key, value);
            return this;
        }
        
        public TestDataBuilder addBoolean(String key, Boolean value) {
            data.put(key, value);
            return this;
        }
        
        public TestDataBuilder addList(String key, List<?> value) {
            data.put(key, value);
            return this;
        }
        
        public TestDataBuilder addMap(String key, Map<?, ?> value) {
            data.put(key, value);
            return this;
        }
        
        public Map<String, Object> build() {
            return new HashMap<>(data);
        }
        
        public String buildAsJson() {
            try {
                ObjectMapper mapper = new ObjectMapper();
                return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(data);
            } catch (Exception e) {
                throw new RuntimeException("Failed to convert to JSON", e);
            }
        }
    }
}
