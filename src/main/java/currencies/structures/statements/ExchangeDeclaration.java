package currencies.structures.statements;

import currencies.executor.Scope;
import currencies.structures.expressions.RValue;

public class ExchangeDeclaration implements Statement {

    String from;
    String to;
    RValue value;

    public ExchangeDeclaration(String from, String to, RValue value) {
        this.from = from;
        this.to = to;
        this.value = value;
    }

    @Override
    public void execute(Scope scope){
        
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public RValue getValue() {
        return value;
    }

    @Override
    public String toString(){
        return "exchange from " + from.toString().toLowerCase() + " to " + to.toString().toLowerCase() + " " +  value + ";";

    }

}
