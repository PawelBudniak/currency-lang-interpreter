package currencies.lexer;

import currencies.error.InterpreterException;
import currencies.reader.CharPosition;

public class LexerException extends InterpreterException {
    public LexerException() {
    }

    public LexerException(CharPosition position) {
        super(position);
    }

    public LexerException(String message, CharPosition position) {
        super(message, position);
    }

    public LexerException(String message, Throwable cause, CharPosition position) {
        super(message, cause, position);
    }

    public LexerException(Throwable cause, CharPosition position) {
        super(cause, position);
    }

    public LexerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, CharPosition position) {
        super(message, cause, enableSuppression, writableStackTrace, position);
    }
}