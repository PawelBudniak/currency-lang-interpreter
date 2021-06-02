package currencies.executor;

import currencies.structures.Function;
import currencies.structures.simple_values.Variable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scope {

    public Scope(Map<String, Variable> variables, List<Function> functions) {
        this.variables = variables;
        this.functions = functions;
    }

    public Scope(List<Function> functions) {
        this.functions = functions;
    }

    private Map<String, Variable> variables = new HashMap<>();
    private List<Function> functions;

    public Variable getVariable(String id){
        return variables.get(id);
    }

    public static Scope empty(){
        return new Scope(new ArrayList<Function>());
    }

    public Variable newVariable(Variable var){
        return variables.put(var.getName(), var);
    }


}
