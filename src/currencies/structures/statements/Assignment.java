package currencies.structures.statements;

import currencies.lexer.Token;
import currencies.structures.expressions.RValue;
import currencies.structures.simple_values.Variable;


public class Assignment implements Statement{
    private Token type;
    private Variable var;
    private RValue value;

    public Assignment(Token type, Variable var, RValue value) {
        this.type = type;
        this.var = var;
        this.value = value;
    }

    @Override
    public String toString() {
        if (type != null){
            return type.valueStr() + " " + var + " = " + value +";";
        }
        return var + " = " + value + ";";
    }

    public Token getType() {
        return type;
    }

    public Variable getVar() {
        return var;
    }

    public RValue getValue() {
        return value;
    }
}
