package currencies.executor;

import currencies.ExecutionException;
import currencies.structures.Function;
import currencies.structures.simple_values.Variable;

import java.util.*;

public class Scope {


    private Map<String, Function> functions;
    private Deque<Map<String, Variable>> subScopes;
    private boolean representsFunCallArgs = false;

    public void enterNewLocalScope(){
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

//    void fun(bool x){
//        if (true){
//            if (true){
//                fun(!x);
//            }
//        }
//        return false;

 //   }
    public Scope newFunCallScope(Map<String, Variable> args){
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



//
//
//    private Map<String, Variable> variables = new HashMap<>();
//    private Map<String, Function> functions = new HashMap<>();
//
//
//
//    public Scope(Map<String, Variable> variables, Map<String, Function> functions) {
//        this.variables = variables;
//        this.functions = functions;
//    }
//
//
//
//    public Scope newVariableSet(Map<String, Variable> newVariables){
//        return new Scope(newVariables, functions);
//    }
//
//    public Scope(Map<String, Function> functions) {
//        this.functions = functions;
//    }
//
//    private Scope() {}
//
//    public Variable getVariable(String id){ return variables.get(id); }
//    public Function getFunction(String id) { return functions.get(id); }
//
//
//    public static Scope empty(){
//        return new Scope();
//    }
//
//    public Variable newVariable(Variable var){
//        return variables.put(var.getName(), var);
//    }





}
