package currencies.structures.simple_values;

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
        return value.toString();
    }

    @Override
    public CType getValue() {
        return value;
    }
}
