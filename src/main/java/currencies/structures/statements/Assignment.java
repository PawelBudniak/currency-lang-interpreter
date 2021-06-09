package currencies.structures.statements;

import currencies.execution.ExecutionException;
import currencies.error.InterpreterException;
import currencies.execution.Scope;
import currencies.lexer.Token;
import currencies.structures.expressions.RValue;
import currencies.structures.simple_values.Identifier;
import currencies.structures.simple_values.Variable;
import currencies.types.CType;


public class Assignment implements Statement{
    private Token typeToken;
    private Identifier varId;
    private RValue value;

    public Assignment(Token typeToken, Identifier varId, RValue value) {
        this.typeToken = typeToken;
        this.varId = varId;
        this.value = value;
    }

    @Override
    public String toString() {
        if (typeToken != null){
            return typeToken.valueStr() + " " + varId + " = " + value +";";
        }
        return varId + " = " + value + ";";
    }

    @Override
    public void execute(Scope scope){

        CType assigningValue = value.getValue(scope);
        Variable variable;

        try {

            // declaration and initialization, e.g int x = 3;
            if (typeToken != null) {


                variable = new Variable(varId, CType.typeOf(typeToken.getType()));
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



    public Token getTypeToken() {
        return typeToken;
    }

    public Identifier getVarId() {
        return varId;
    }

    public RValue getValue() {
        return value;
    }
}
