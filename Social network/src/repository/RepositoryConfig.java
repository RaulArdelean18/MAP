package repository;

import exceptions.RepositoryException;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Persistence settings, read from a properties file.
 */
public class RepositoryConfig {
    /** Property that selects the persistence type. */
    public static final String TYPE_KEY = "repository.type";

    private final String source;
    private final Properties properties = new Properties();

    /**
     * Loads the settings.
     *
     * @param fileName relative path of the properties file
     * @throws RepositoryException if the file does not exist or cannot be read
     */
    public RepositoryConfig(String fileName) {
        this.source = fileName;
        Path path = Paths.get(fileName);
        if (!Files.exists(path)) {
            throw new RepositoryException("Configuration file " + fileName + " does not exist.");
        }
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            properties.load(reader);
        } catch (IOException e) {
            throw new RepositoryException("Cannot read configuration file " + fileName, e);
        }
    }

    /**
     * Returns the configured persistence type.
     *
     * @return the configured persistence type
     * @throws RepositoryException if the property is missing or its value is not a known type
     */
    public RepositoryType getType() {
        String value = require(TYPE_KEY);
        for (RepositoryType type : RepositoryType.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        StringBuilder known = new StringBuilder();
        for (RepositoryType type : RepositoryType.values()) {
            if (known.length() > 0) known.append(", ");
            known.append(type.name());
        }
        throw new RepositoryException("Unknown value '" + value + "' for property '" + TYPE_KEY
                + "' in " + source + ". Expected one of: " + known + ".");
    }

    /**
     * Returns the value of a required property.
     *
     * @param key name of the property
     * @return its value, trimmed
     * @throws RepositoryException if the property is missing or blank
     */
    public String require(String key) {
        String value = properties.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new RepositoryException("Missing property '" + key + "' in " + source + ".");
        }
        return value.trim();
    }
}
