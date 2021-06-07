package currencies.execution;

import currencies.error.InterpreterException;
import currencies.reader.CharPosition;

public class ExecutionException extends InterpreterException {
    public ExecutionException() {
    }

    public ExecutionException(CharPosition position) {
        super(position);
    }

    public ExecutionException(String message, CharPosition position) {
        super(message, position);
    }

    public ExecutionException(String message, Throwable cause, CharPosition position) {
        super(message, cause, position);
    }

    public ExecutionException(Throwable cause, CharPosition position) {
        super(cause, position);
    }

    public ExecutionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, CharPosition position) {
        super(message, cause, enableSuppression, writableStackTrace, position);
    }
}


