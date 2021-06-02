package currencies.structures;

import currencies.types.CType;

public class ReturnStatementException extends RuntimeException{
    CType returnedValue;

    public ReturnStatementException(CType returnedValue) {
        this.returnedValue = returnedValue;
    }

    public CType getReturnedValue() {
        return returnedValue;
    }
}
