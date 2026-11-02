package repository;

import domain.Entity;
import domain.validators.Validator;
import exceptions.RepositoryException;
import exceptions.ValidationException;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * In-memory repository whose content is persisted in a text file, one entity per line.
 *
 * <p>The conversion between a line and an entity is given by two functions passed to the
 * constructor (composition instead of the Template Method of laboratory 3, where subclasses
 * overrode {@code extractEntity} and {@code createEntityAsString}).</p>
 *
 * <p>A corrupt or invalid line is reported on the error stream (file, line number and reason)
 * and ignored; the file is rewritten without it at the next successful change.</p>
 *
 * @param <ID> type of the entity id
 * @param <E>  type of the entity
 */
public final class FileRepository<ID, E extends Entity<ID>> extends InMemoryRepository<ID, E> {
    private final Path path;
    private final Function<String, E> lineToEntity;
    private final Function<E, String> entityToLine;

    /**
     * Creates the repository and loads the data from the file, if it exists.
     * A missing file is not an error: the repository starts empty and the file
     * is created at the first save.
     *
     * @param validator    the validation strategy
     * @param fileName     relative path of the file
     * @param lineToEntity converts a non-blank line to an entity; it throws
     *                     {@link RepositoryException} if the line is malformed
     * @param entityToLine converts an entity to its line, without line terminator
     * @throws RepositoryException if the file exists but cannot be read
     */
    public FileRepository(Validator<E> validator, String fileName,
                          Function<String, E> lineToEntity, Function<E, String> entityToLine) {
        super(validator);
        this.path = Paths.get(fileName);
        this.lineToEntity = lineToEntity;
        this.entityToLine = entityToLine;
        loadFromFile();
    }

    private void loadFromFile() {
        if (!Files.exists(path)) {
            return;
        }
        // the stream keeps the file open, so it is closed here; the lines are copied in a list
        // because the line numbers are needed for the warnings
        List<String> lines;
        try (Stream<String> fileLines = Files.lines(path, StandardCharsets.UTF_8)) {
            lines = fileLines.collect(Collectors.toList());
        } catch (IOException | UncheckedIOException e) {
            throw new RepositoryException("Cannot read file " + path, e);
        }
        IntStream.range(0, lines.size())
                .filter(i -> !lines.get(i).isBlank())
                .forEach(i -> loadLine(lines.get(i), i + 1));
    }

    private void loadLine(String line, int lineNumber) {
        try {
            // validates and stores in memory, but does NOT rewrite the file
            super.save(lineToEntity.apply(line));
        } catch (RepositoryException | ValidationException e) {
            System.err.println("Warning: " + path + ", line " + lineNumber
                    + " ignored: " + e.getMessage());
        }
    }

    private void saveToFile() {
        try {
            Path parent = path.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            List<String> lines = entities.values().stream()
                    .map(entityToLine)
                    .collect(Collectors.toList());
            Files.write(path, lines, StandardCharsets.UTF_8);
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
