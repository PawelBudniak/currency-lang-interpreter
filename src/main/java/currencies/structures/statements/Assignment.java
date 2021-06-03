package currencies.structures.statements;

import currencies.ExecutionException;
import currencies.executor.Scope;
import currencies.lexer.Token;
import currencies.structures.expressions.RValue;
import currencies.structures.simple_values.Variable;
import currencies.types.CType;


public class Assignment implements Statement{
    private Token type;
    private Variable var;
    private RValue value;

    public Assignment(Token type, Variable var, RValue value) {
        this.type = type;
        //TODO: trzymac stringa a nie var w klasie
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

    @Override
    public void execute(Scope scope){

        CType assigningValue = value.getValue(scope);
        Variable variable;

        //TODO: implicit cast na bool?

        // declaration and initialization, e.g int x = 3;
        if (type != null){
            if (scope.getVariable(var.getName()) != null)
                throw new ExecutionException("Trying to redefine variable: " + var.getName(), null);

            variable = new Variable(var.getName(), CType.typeOf(type.valueStr()));
            variable.setValue(assigningValue);
            scope.newVariable(variable);
        }

        // simple assignment, e.g x = 3;
        else {
            variable = scope.getVariable(var.getName());
            if (variable == null)
                throw new ExecutionException("Trying to assign to undeclared variable", null);
            variable.setValue(assigningValue);

        }

        assert scope.getVariable(var.getName()) == variable;
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
