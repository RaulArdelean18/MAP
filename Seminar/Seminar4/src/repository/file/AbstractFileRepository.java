package repository.file;

import domain.Entity;
import domain.validators.ValidationException;
import domain.validators.Validator;
import repository.InMemoryRepository;

import java.io.*;

/**
 * Repository that keeps the entities in memory and in a text file (one entity per line).
 * Template Method: loading and saving are written here, once; subclasses only say
 * how an entity is converted from / to a line of text.
 *
 * @param <ID> - type E must have an attribute of type ID
 * @param <E>  - type of entities saved in repository
 */
public abstract class AbstractFileRepository<ID, E extends Entity<ID>> extends InMemoryRepository<ID, E> {

    private final String fileName;

    /**
     * creates the repository and loads the entities from the file
     *
     * @param validator - the validation strategy for the entities
     * @param fileName  - the path of the text file
     */
    public AbstractFileRepository(Validator<E> validator, String fileName) {
        super(validator);
        this.fileName = fileName;
        loadData();
    }

    /**
     * creates an entity from a line of the file
     *
     * @param line - a line of the file
     * @return the entity described by the line
     */
    protected abstract E extractEntity(String line);

    /**
     * converts an entity into a line of the file
     *
     * @param entity - the entity to be converted
     * @return the line of text that describes the entity
     */
    protected abstract String createEntityAsString(E entity);

    /**
     * reads all the lines of the file and saves the entities in memory;
     * if the file does not exist, the repository starts empty
     *
     * @throws RuntimeException if the file cannot be read
     */
    private void loadData() {
        // decorator: BufferedReader (buffer + readLine) peste FileReader (primitiv)
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank())
                    continue;
                E entity = extractEntity(line);
                super.save(entity);
            }
        } catch (FileNotFoundException e) {
            System.out.println("The file " + fileName + " doesn't exist. Empty repository.");
        } catch (IOException e) {
            throw new RuntimeException("Error while reading the file " + fileName, e);
        }
    }

    /**
     * rewrites the whole file with the entities currently in memory
     *
     * @throws RuntimeException if the file cannot be written
     */
    private void writeToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (E entity : findAll()) {
                writer.write(createEntityAsString(entity));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error while writing in the file " + fileName, e);
        }
    }

    /**
     * saves the entity in memory and, if it was saved, rewrites the file
     *
     * @param entity entity must be not null
     * @return null - if the given entity is saved
     * otherwise returns the entity (id already exists)
     * @throws ValidationException if the entity is not valid
     * @throws IllegalArgumentException                   if the given entity is null.
     */
    @Override
    public E save(E entity) {
        E result = super.save(entity);
        if (result == null)
            writeToFile();
        return result;
    }

    /**
     * removes the entity from memory and, if it existed, rewrites the file
     *
     * @param id id must be not null
     * @return the removed entity or null if there is no entity with the given id
     */
    @Override
    public E delete(ID id) {
        E result = super.delete(id);
        if (result != null)
            writeToFile();
        return result;
    }

    /**
     * updates the entity in memory and, if it was updated, rewrites the file
     *
     * @param entity entity must not be null
     * @return null - if the entity is updated,
     * otherwise returns the entity - (e.g id does not exist).
     * @throws IllegalArgumentException                   if the given entity is null.
     * @throws ValidationException if the entity is not valid.
     */
    @Override
    public E update(E entity) {
        E result = super.update(entity);
        if (result == null)
            writeToFile();
        return result;
    }
}
