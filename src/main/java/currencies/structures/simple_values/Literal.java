package currencies.structures.simple_values;

import currencies.execution.Scope;
import currencies.structures.expressions.RValue;
import currencies.types.CType;

public class Literal extends RValue {
    CType value;

    public Literal(CType value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value.debugStr();
    }

    @Override
    public CType getValue(Scope scope) {
        return value;
    }

    public CType getValue(){
        return value;
    }
}
