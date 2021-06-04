package currencies;

import currencies.reader.CharPosition;
//
//public interface InterpreterException {
//    CharPosition getPosition();
//    String getMessage();
//}

public class InterpreterException extends RuntimeException{

    private CharPosition position;

    public InterpreterException(){
        super();
    }

    public InterpreterException(CharPosition position) {
        this.position = position;
    }

    public InterpreterException(String message, CharPosition position) {
        super(message);
        this.position = position;
    }

    public InterpreterException(String message, Throwable cause, CharPosition position) {
        super(message, cause);
        this.position = position;
    }

    public InterpreterException(Throwable cause, CharPosition position) {
        super(cause);
        this.position = position;
    }

    public InterpreterException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, CharPosition position) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.position = position;
    }

    public CharPosition getPosition() {
        return position;
    }

    public void setPosition(CharPosition position) {
        this.position = position;
    }

    public static void setPositionAndRethrow(InterpreterException e, CharPosition position){
        if (e.getPosition() == null)
            e.setPosition(position);
        throw e;
    }
}
