package currencies.structures.statements;

import currencies.executor.Scope;
import currencies.structures.expressions.RValue;
import currencies.types.CType;

public class ReturnStatement implements Statement{
    RValue value;
    CType result;

    public ReturnStatement(RValue val) {
        this.value = val;
    }

    @Override
    public void execute(Scope scope){
        result = value.getValue();
    }

    public CType getResult(){
        return result;
    }

    @Override
    public String toString() {
        return "return " + value + ";";
    }
}
