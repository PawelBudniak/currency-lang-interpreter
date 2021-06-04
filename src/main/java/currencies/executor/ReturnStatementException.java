package currencies.executor;

import currencies.reader.CharPosition;
import currencies.types.CType;

public class ReturnStatementException extends RuntimeException{
    CType returnedValue;
    CharPosition position;

    public ReturnStatementException(CType returnedValue, CharPosition position) {
        this.returnedValue = returnedValue;
        this.position = position;
    }

    public CType getReturnedValue() {
        return returnedValue;
    }

    public CharPosition getPosition() { return position; }
}
