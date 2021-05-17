package currencies;

import currencies.reader.CharPosition;

public interface InterpreterException {
    CharPosition getPosition();
    String getMessage();
}
