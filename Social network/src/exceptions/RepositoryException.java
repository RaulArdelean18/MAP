package exceptions;

/** Custom exception for the corresponding application layer. */
public class RepositoryException extends RuntimeException{
    public RepositoryException(String message){super(message);}
}
