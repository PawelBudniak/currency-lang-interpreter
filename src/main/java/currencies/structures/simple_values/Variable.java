package currencies.structures.simple_values;

import currencies.ExecutionException;
import currencies.executor.Scope;
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

    public Variable(String name, Class<?> type, CType value) {
        this.name = name;
        this.type = type;
        setValue(value);
    }

    boolean isDefined(){
        return type == null;
    }

    public String getName() {
        return name;
    }

    @Override
    public CType getValue(Scope scope){
        Variable lookedUpVal = scope.getVariable(name);
        if (lookedUpVal == null)
            throw new ExecutionException("Attempting to reference undefined variable", null);
        return lookedUpVal.getSavedValue();
    }

    public Class<?> getType() {
        return type;
    }

    public CType getSavedValue() {
        return value;
    }

    public void setValue(CType value) {
//        if (value.getClass() != this.getType())
//            throw new ExecutionException("Can't assign " + value.getClass() + " to " + this.getType(), null);
//        this.value = value;
        this.value = CType.assign(value, type);
    }

    @Override
    public String toString() {
        return name;
    }

}
