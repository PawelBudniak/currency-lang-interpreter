package currencies.structures.statements;

import currencies.ExecutionException;
import currencies.executor.Scope;
import currencies.reader.CharPosition;
import currencies.structures.expressions.RValue;
import currencies.types.CCurrency;
import currencies.types.CNumber;
import currencies.types.CType;

import java.util.Currency;

public class ExchangeDeclaration implements Statement {

    String from;
    String to;
    RValue value;
    CharPosition position;

    public ExchangeDeclaration(String from, String to, RValue value, CharPosition position) {
        this.from = from;
        this.to = to;
        this.value = value;
        this.position = position;
    }

    @Override
    public void execute(Scope scope){
        CType result = value.getValue(scope);
        if (!(result instanceof CNumber)){
            throw new ExecutionException("Exchange rate must be a number", position);
        }

        CCurrency.setExchangeRate(from, to, (CNumber)result);

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
