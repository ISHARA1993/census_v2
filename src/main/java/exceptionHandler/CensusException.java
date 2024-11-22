package exceptionHandler;

public class CensusException extends RuntimeException {

    public CensusException(String message) {
        super(message);
    }

    public CensusException(String message, Throwable cause) {
        super(message, cause);
    }
}
