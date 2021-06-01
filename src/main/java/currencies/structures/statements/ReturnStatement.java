package currencies.structures.statements;

import currencies.structures.expressions.RValue;

public class ReturnStatement implements Statement{
    RValue value;

    public ReturnStatement(RValue val) {
        this.value = val;
    }

    @Override
    public String toString() {
        return "return " + value + ";";
    }
}
