package currencies.structures.simple_values;

import currencies.Currency;
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

        if (type == TokenType.T_KW_STRING)
            return "'" + value + "'";

        if (type == TokenType.T_KW_CURRENCY)
            return ((Currency)value).getValue().toString() + ((Currency)value).getTypeStr();

        return value.toString();
    }

    @Override
    public Object getValue() {
        return value;
    }
}
