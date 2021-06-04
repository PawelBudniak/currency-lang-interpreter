package currencies.structures.statements;

import currencies.ExecutionException;
import currencies.InterpreterException;
import currencies.executor.Scope;
import currencies.lexer.Token;
import currencies.structures.expressions.RValue;
import currencies.structures.simple_values.Identifier;
import currencies.structures.simple_values.Variable;
import currencies.types.CType;


public class Assignment implements Statement{
    private Token type;
    private Identifier varId;
    private RValue value;

    public Assignment(Token type, Identifier varId, RValue value) {
        this.type = type;
        //TODO: trzymac stringa a nie var w klasie
        this.varId = varId;
        this.value = value;
    }

    @Override
    public String toString() {
        if (type != null){
            return type.valueStr() + " " + varId + " = " + value +";";
        }
        return varId + " = " + value + ";";
    }

    @Override
    public void execute(Scope scope){

        CType assigningValue = value.getValue(scope);
        Variable variable;

        //TODO: implicit cast na bool?

        try {

            // declaration and initialization, e.g int x = 3;
            if (type != null) {


                variable = new Variable(varId, CType.typeOf(type.valueStr()));
                variable.setValue(assigningValue);
                scope.newVariable(variable);
            }

            // simple assignment, e.g x = 3;
            else {
                variable = scope.getVariable(varId.getName());
                if (variable == null)
                    throw new ExecutionException("Trying to assign to undeclared variable", varId.getPosition());
                variable.setValue(assigningValue);

            }
        } catch (InterpreterException e){
            if (e.getPosition() == null)
                e.setPosition(varId.getPosition());
            throw e;
        }

        assert scope.getVariable(varId.getName()) == variable;
    }



    public Token getType() {
        return type;
    }

    public Identifier getVarId() {
        return varId;
    }

    public RValue getValue() {
        return value;
    }
}
