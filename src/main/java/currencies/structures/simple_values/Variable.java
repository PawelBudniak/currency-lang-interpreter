package currencies.structures.simple_values;

import currencies.lexer.TokenType;
import currencies.structures.expressions.RValue;
import currencies.types.CType;

public class Variable extends RValue {

    private String name;
    private CType value;
    private TokenType type;

    public Variable(String name) {
        this.name = name;
    }

    public Variable(String name, TokenType type) {
        this.name = name;
        this.type = type;
    }

    public CType getValue() {
        return value;
    }

    public void setValue(CType value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return name;
    }

}
