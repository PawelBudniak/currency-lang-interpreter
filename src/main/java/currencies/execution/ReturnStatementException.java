package currencies.execution;

import currencies.reader.CharPosition;
import currencies.types.CType;

/** Thrown when a return statement is executed, so the main function Block can be found easily.
 *  Contains information about the returned value and the position of the return statement.
 */
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
