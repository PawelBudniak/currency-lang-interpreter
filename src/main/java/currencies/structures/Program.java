package currencies.structures;

import java.util.ArrayList;
import java.util.List;

public class Program {

    private List<Function> functions;

    public Program(List<Function> functions) {
        this.functions = functions;
    }

    public List<Function> getFunctions() {
        return functions;
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
