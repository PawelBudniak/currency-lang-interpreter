package currencies.structures.simple_values;

import currencies.lexer.TokenType;
import currencies.structures.expressions.RValue;

public class Variable extends RValue {

    private String name;
    private Object value;
    private TokenType type;

    public Variable(String name) {
        this.name = name;
    }

    public Variable(String name, TokenType type) {
        this.name = name;
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return name;
    }

}
