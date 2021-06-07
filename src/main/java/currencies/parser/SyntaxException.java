package currencies.parser;

import currencies.error.InterpreterException;
import currencies.reader.CharPosition;

public class SyntaxException extends InterpreterException {
    public SyntaxException() {
    }

    public SyntaxException(CharPosition position) {
        super(position);
    }

    public SyntaxException(String message, CharPosition position) {
        super(message, position);
    }

    public SyntaxException(String message, Throwable cause, CharPosition position) {
        super(message, cause, position);
    }

    public SyntaxException(Throwable cause, CharPosition position) {
        super(cause, position);
    }

    public SyntaxException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, CharPosition position) {
        super(message, cause, enableSuppression, writableStackTrace, position);
    }
}
