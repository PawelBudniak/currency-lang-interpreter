package currencies.execution;

import currencies.structures.Function;
import currencies.structures.simple_values.Variable;

import java.util.*;

public class Scope {


    private Map<String, Function> functions;
    private Deque<Map<String, Variable>> subScopes;
    private boolean representsFunCallArgs = false;

    public void enterNewLocalScope(){
        // thanks to this check we can treat funcall arguments as standard local variables (so they can't be redefined)
        if (representsFunCallArgs)
            representsFunCallArgs = false;
        else
            subScopes.addLast(new HashMap<>());
    }

    public void leaveLocalScope(){
        subScopes.pollLast();
    }

    private Map<String, Variable> currentLocalScope(){
        return subScopes.peekLast();
    }

    public Variable getVariable(String id){

        Iterator<Map<String, Variable>> iterator = subScopes.descendingIterator();

        // iterate over scopes starting from the "most local"
        // this allows for shadowing variables from outer blocks
        while(iterator.hasNext()) {
            Variable found = iterator.next().get(id);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    public void newVariable(Variable newVar){
        if (currentLocalScope().get(newVar.getName()) != null)
            throw new ExecutionException("Trying to redefine variable " + newVar.getName(), null);

        currentLocalScope().put(newVar.getName(), newVar);
    }

    public Scope enterNewFunCallScope(Map<String, Variable> args){
        Scope newScope =  new Scope(args, functions);
        newScope.representsFunCallArgs = true;
        return newScope;
    }

    public Scope(Map<String, Variable> variables, Map<String, Function> functions) {
        subScopes = new ArrayDeque<>();
        subScopes.addLast(variables);
        this.functions = functions;
    }

    public Scope(){
        subScopes = new ArrayDeque<>();
        subScopes.addLast(new HashMap<>());
        functions = new HashMap<>();
    }


    public Function newFunction(Function fun){
        return functions.put(fun.getId(), fun);
    }

    public static Scope empty(){
        return new Scope();
    }

    public Function getFunction(String id) { return functions.get(id); }
}
