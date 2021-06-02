package currencies.structures.simple_values;

import currencies.executor.Scope;
import currencies.structures.expressions.RValue;
import currencies.structures.statements.Statement;
import currencies.types.CType;

import java.util.List;

public class FunctionCall extends RValue implements Statement {

    String functionName;
    List<RValue> arguments;
    private boolean asStatement = false;


    public boolean asStatement() {
        return asStatement;
    }

    public void setAsStatement(boolean asStatement) {
        this.asStatement = asStatement;
    }


    public FunctionCall(String functionName, List<RValue> arguments) {
        this.functionName = functionName;
        this.arguments = arguments;
    }

    @Override
    public void execute(Scope scope){

    }

    @Override
    public CType getValue() {
        return null;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder(functionName + "(");

        for (int i = 0; i < arguments.size(); ++i){
            str.append(arguments.get(i));
            if (i != arguments.size() - 1)
                str.append(", ");
        }
        str.append(")");
        if (asStatement)
            str.append(";");
        return str.toString();
    }
}
