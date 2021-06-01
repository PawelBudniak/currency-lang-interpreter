package currencies.lexer;

import currencies.InterpreterException;
import currencies.reader.CharPosition;

public class TooLongTokenException extends RuntimeException implements InterpreterException {
    private CharPosition position;

    public TooLongTokenException(String message, CharPosition position) {
        super(message);
        this.position = position;
    }

    @Override
    public CharPosition getPosition() {
        return position;
    }
}
