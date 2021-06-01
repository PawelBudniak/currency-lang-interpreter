package currencies.structures.simple_values;

import currencies.types.CCurrency;
import currencies.lexer.TokenType;
import currencies.structures.expressions.RValue;

public class Literal extends RValue {
    Object value;
    TokenType type;

    public Literal(Object value, TokenType type) {
        this.value = value;
        this.type = type;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public Object getValue() {
        return value;
    }
}
