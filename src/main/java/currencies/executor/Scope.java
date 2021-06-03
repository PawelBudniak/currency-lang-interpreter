package currencies.executor;

import currencies.structures.Function;
import currencies.structures.simple_values.Variable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scope {

    public Scope(Map<String, Variable> variables, Map<String, Function> functions) {
        this.variables = variables;
        this.functions = functions;
    }

    public Scope newVariableSet(Map<String, Variable> newVariables){
        return new Scope(newVariables, functions);
    }

    public Scope(Map<String, Function> functions) {
        this.functions = functions;
    }

    private Scope() {}

    private Map<String, Variable> variables = new HashMap<>();
    private Map<String, Function> functions = new HashMap<>();

    public Variable getVariable(String id){
        return variables.get(id);
    }
    public Function getFunction(String id) { return functions.get(id); }


    public static Scope empty(){
        return new Scope();
    }

    public Variable newVariable(Variable var){
        return variables.put(var.getName(), var);
    }
    public Function newFunction(Function fun){
        return functions.put(fun.getName(), fun);
    }




}
