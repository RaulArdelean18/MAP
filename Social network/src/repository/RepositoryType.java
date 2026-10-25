package repository;

/**
 * The kinds of persistence the application can use.
 */
public enum RepositoryType {
    /** Data lives only in memory and is lost when the application stops. */
    MEMORY,
    /** Data is stored in text files. */
    FILE,
    /** Data is stored in a database (not implemented yet). */
    DATABASE
}
