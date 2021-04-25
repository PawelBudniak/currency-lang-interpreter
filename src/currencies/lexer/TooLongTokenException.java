package currencies.lexer;

public class TooLongTokenException extends RuntimeException {
    public TooLongTokenException() {
    }

    public TooLongTokenException(String message) {
        super(message);
    }

    public TooLongTokenException(String message, Throwable cause) {
        super(message, cause);
    }

    public TooLongTokenException(Throwable cause) {
        super(cause);
    }
}
