package currencies.structures.simple_values;

import currencies.execution.ExecutionException;
import currencies.execution.Scope;
import currencies.structures.Function;
import currencies.structures.TypeAndId;
import currencies.structures.expressions.RValue;
import currencies.structures.statements.Statement;
import currencies.types.CType;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class FunctionCall extends RValue implements Statement {

    Identifier functionId;
    List<RValue> arguments;
    private boolean asStatement = false;
    private CType returnValue;


    public boolean asStatement() {
        return asStatement;
    }

    public void setAsStatement(boolean asStatement) {
        this.asStatement = asStatement;
    }


    public FunctionCall(Identifier functionId, List<RValue> arguments) {
        this.functionId = functionId;
        this.arguments = arguments;
    }

    @Override
    public void execute(Scope scope){

        // built-in print function
        if (functionId.getName().equals("print")){
            for (RValue rValue: arguments){
                System.out.print(rValue.getValue(scope));
            }
            System.out.print("\n");
            return;
        }

        // user defined function
        Function function = scope.getFunction(functionId.getName());
        Map<String, Variable> args = matchArgLists(function, scope);



        function.call(scope.enterNewFunCallScope(args));
        returnValue = function.getReturnedValue();
    }

    Map<String, Variable> matchArgLists(Function function, Scope scope){

        int expectedSize = function.getArgDefList().size();
        if (arguments.size() != expectedSize)
            throw new ExecutionException("Invalid number of arguments for function " + functionId + " expected: " +  expectedSize, functionId.getPosition());


        Map<String, Variable> matchedArgs = new HashMap<>();
        Iterator<RValue> provided = arguments.iterator();
        Iterator<TypeAndId> expected = function.getArgDefList().iterator();
        while (provided.hasNext()){
            CType val = provided.next().getValue(scope);
            TypeAndId expectedVar = expected.next();

            Variable var = new Variable(expectedVar.getId(), CType.typeOf(expectedVar.getTypeToken().getType()), val);
            matchedArgs.put(expectedVar.getId().getName(), var);
        }

        return matchedArgs;
    }


    /** Executes the function and returns the returned value */
    @Override
    public CType getValue(Scope scope) {
        execute(scope);
        return returnValue;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder(functionId + "(");

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
