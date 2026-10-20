package repository;

import domain.Entity;
import domain.validators.Validator;
import exceptions.RepositoryException;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public abstract class FileRepository<ID, E extends Entity<ID>> extends InMemoryRepository<ID, E> {
    private final Path path;

    public FileRepository(Validator<E> validator, String fileName) {
        super(validator);
        this.path = Paths.get(fileName);
        load();
    }

    protected abstract E fromLine(String line);

    protected abstract String toLine(E entity);

    private void load() {
        try {
            if (path.getParent() != null) Files.createDirectories(path.getParent());
            if (!Files.exists(path)) {
                Files.createFile(path);
                return;
            }
            List<String> lines = Files.readAllLines(path);
            for (String line : lines) {
                if (!line.isBlank())
                    super.save(fromLine(line));
            }
        } catch (IOException e) {
            throw new RepositoryException("Cannot read file " + path + ": " + e.getMessage());
        }
    }

    private void store() {
        try (BufferedWriter w = Files.newBufferedWriter(path)) {
            for (E e : entities.values()) {
                w.write(toLine(e));
                w.newLine();
            }
        } catch (IOException e) {
            throw new RepositoryException("Cannot write file " + path + ": " + e.getMessage());
        }
    }

    @Override
    public void save(E entity) {
        super.save(entity);
        store();
    }

    @Override
    public E delete(ID id) {
        E removed = super.delete(id);
        store();
        return removed;
    }
}
