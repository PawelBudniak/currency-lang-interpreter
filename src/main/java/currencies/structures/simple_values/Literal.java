package currencies.structures.simple_values;

import currencies.executor.Scope;
import currencies.types.CCurrency;
import currencies.lexer.TokenType;
import currencies.structures.expressions.RValue;
import currencies.types.CType;

public class Literal extends RValue {
    CType value;
    TokenType type;

    public Literal(CType value, TokenType type) {
        this.value = value;
        this.type = type;
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
