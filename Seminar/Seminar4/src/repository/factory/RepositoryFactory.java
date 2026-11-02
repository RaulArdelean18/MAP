package repository.factory;

import domain.User;
import domain.validators.Validator;
import repository.InMemoryRepository;
import repository.Repository;
import repository.db.UserDbRepository;
import repository.file.UserFileRepository;
import java.util.Properties;

public class RepositoryFactory {

    public static final String TYPE_KEY = "repository.type";
    public static final String FILE_KEY = "repository.file";
    public static final String DB_URL_KEY = "db.url";
    public static final String DB_USER_KEY = "db.user";
    public static final String DB_PASSWORD_KEY = "db.password";
    private final Properties properties;

    public RepositoryFactory(Properties properties) {
        this.properties=properties;
    }

    public Repository<Long, User> createUserRepository(Validator<User> validator){
        String property=properties.getProperty("repository.type");
        if (property == null || property.isBlank())
            throw new IllegalArgumentException("Lipseste proprietatea repository.type");

        RepositoryType type= RepositoryType.valueOf(property.trim().toUpperCase());

        switch (type){
            case MEMORY:
                return new InMemoryRepository<>(validator);
            case FILE:
                return new UserFileRepository(validator,properties.getProperty("file.name"));
            case DATABASE:
                return new UserDbRepository(validator,properties.getProperty("db.url"),
                        properties.getProperty("db.username"),
                        properties.getProperty("db.password"));
            default:
                throw new IllegalArgumentException("Invalid repository type");
        }
    }

    private String required(String key) {
        String value = properties.getProperty(key);
        if (value == null || value.isBlank())
            throw new IllegalArgumentException("Missing property: " + key);
        return value;
    }

    private RepositoryType readType() {
        String text = required(TYPE_KEY).trim().toUpperCase();
        try {
            return RepositoryType.valueOf(text);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown repository type '" + text
                    + "' for property " + TYPE_KEY, e);
        }
    }
}
