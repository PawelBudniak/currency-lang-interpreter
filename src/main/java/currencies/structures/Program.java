package currencies.structures;

import currencies.execution.Scope;

import java.util.List;

public class Program {

    private List<Function> functions;

    public Program(List<Function> functions) {
        this.functions = functions;
    }

    public List<Function> getFunctions() {
        return functions;
    }

    public void execute(){
        Scope scope = Scope.empty();

        for (Function function: functions){
            function.define(scope);
        }

        Function main = scope.getFunction("main");
        main.call(scope);
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        for (Function fun: functions){
            str.append(fun);
        }
        return str.toString();
    }
}
