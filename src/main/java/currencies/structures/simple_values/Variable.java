package currencies.structures.simple_values;

import currencies.lexer.TokenType;
import currencies.structures.expressions.RValue;
import currencies.types.CType;

public class Variable extends RValue {

    private String name;
    private CType value;
    private Class<?> type;

    public Variable(String name) {
        this.name = name;
    }

    public Variable(String name, Class<?> type) {
        this.name = name;
        this.type = type;
    }

    boolean isDefined(){
        return type == null;
    }

    public String getName() {
        return name;
    }

    public Class<?> getType() {
        return type;
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
