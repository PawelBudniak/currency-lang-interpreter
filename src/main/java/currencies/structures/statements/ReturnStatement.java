package currencies.structures.statements;

import currencies.execution.Scope;
import currencies.execution.ReturnStatementException;
import currencies.reader.CharPosition;
import currencies.structures.expressions.RValue;
import currencies.types.CType;

public class ReturnStatement implements Statement{
    RValue value;
    CType result;
    CharPosition position;

    public ReturnStatement(RValue val, CharPosition position) {
        this.value = val;
        this.position = position;
    }

    @Override
    public void execute(Scope scope){
        result = value.getValue(scope);
        throw new ReturnStatementException(result, position);
    }

    public CType getResult(){
        return result;
    }

    @Override
    public String toString() {
        return "return " + value + ";";
    }
}
