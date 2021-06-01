package currencies.parser;

import currencies.InterpreterException;
import currencies.reader.CharPosition;

public class SyntaxException extends RuntimeException implements InterpreterException {
    private CharPosition position;

    public SyntaxException(String message, CharPosition position) {
        super(message);
        this.position = position;
    }

    @Override
    public CharPosition getPosition() {
        return position;
    }
}
