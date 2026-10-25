package repository;

import domain.Entity;
import domain.validators.Validator;
import exceptions.RepositoryException;
import exceptions.ValidationException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * In-memory repository whose content is persisted in a text file, one entity per line.
 *
 * <p>Template Method: this class implements loading and saving; subclasses only
 * define how a line is converted to an entity ({@link #extractEntity(String)}) and back
 * ({@link #createEntityAsString(Entity)}).</p>
 *
 * <p>A corrupt or invalid line is reported on the error stream (file, line number and reason)
 * and ignored; the file is rewritten without it at the next successful change.</p>
 *
 * @param <ID> type of the entity id
 * @param <E>  type of the entity
 */
public abstract class AbstractFileRepository<ID, E extends Entity<ID>> extends InMemoryRepository<ID, E> {
    private final Path path;

    /**
     * Creates the repository and loads the data from the file, if it exists.
     * A missing file is not an error: the repository starts empty and the file
     * is created at the first save.
     *
     * @param validator the validation strategy
     * @param fileName  relative path of the file
     * @throws RepositoryException if the file exists but cannot be read
     */
    protected AbstractFileRepository(Validator<E> validator, String fileName) {
        super(validator);
        this.path = Paths.get(fileName);
        loadFromFile();
    }

    /**
     * Converts a line of the file to an entity.
     *
     * <p>It is called while the constructor of this class is running, before the fields of the
     * subclass are initialized, so implementations must not use fields of the subclass.</p>
     *
     * @param line a non-blank line
     * @return the entity
     * @throws RepositoryException if the line is malformed
     */
    protected abstract E extractEntity(String line);

    /**
     * Converts an entity to its line in the file.
     *
     * @param entity the entity
     * @return the line, without line terminator
     */
    protected abstract String createEntityAsString(E entity);

    private void loadFromFile() {
        if (!Files.exists(path)) {
            return;
        }
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.isBlank()) {
                    continue;
                }
                try {
                    // validates and stores in memory, but does NOT rewrite the file
                    super.save(extractEntity(line));
                } catch (RepositoryException | ValidationException e) {
                    System.err.println("Warning: " + path + ", line " + lineNumber
                            + " ignored: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new RepositoryException("Cannot read file " + path, e);
        }
    }

    private void saveToFile() {
        try {
            Path parent = path.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
                for (E entity : entities.values()) {
                    writer.write(createEntityAsString(entity));
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            throw new RepositoryException("Cannot write file " + path, e);
        }
    }

    @Override
    public void save(E entity) {
        super.save(entity);
        try {
            saveToFile();
        } catch (RepositoryException e) {
            entities.remove(entity.getId());   // keep memory and file consistent
            throw e;
        }
    }

    @Override
    public E delete(ID id) {
        E removed = super.delete(id);
        try {
            saveToFile();
        } catch (RepositoryException e) {
            entities.put(id, removed);         // undo
            throw e;
        }
        return removed;
    }
}
