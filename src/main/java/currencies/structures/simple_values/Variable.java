package currencies.structures.simple_values;

import currencies.execution.ExecutionException;
import currencies.execution.Scope;
import currencies.structures.expressions.RValue;
import currencies.types.CType;

public class Variable extends RValue {

    private Identifier id;
    private CType value;
    private Class<?> type;

    public Variable(Identifier id) {
        this.id = id;
    }

    public Variable(Identifier id, Class<?> type) {
        this.id = id;
        this.type = type;
    }

    public Variable(Identifier id, Class<?> type, CType value) {
        this.id = id;
        this.type = type;
        setValue(value);
    }

    boolean isDefined(){
        return type == null;
    }

    public String getName() {
        return id.getName();
    }

    public Identifier getId() { return id; }


    @Override
    public CType getValue(Scope scope){
        Variable lookedUpVal = scope.getVariable(id.getName());
        if (lookedUpVal == null)
            throw new ExecutionException("Attempting to reference undefined variable", id.getPosition());
        return lookedUpVal.getSavedValue();
    }

    public Class<?> getType() {
        return type;
    }

    public CType getSavedValue() {
        return value;
    }

    public void setValue(CType value) {
        this.value = CType.assign(value, type);
    }

    @Override
    public String toString() {
        return id.getName();
    }

}
