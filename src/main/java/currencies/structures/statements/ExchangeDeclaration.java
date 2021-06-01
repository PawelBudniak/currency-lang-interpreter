package currencies.structures.statements;

import currencies.Currency;
import currencies.structures.expressions.RValue;

public class ExchangeDeclaration implements Statement {

    Currency.Type from;
    Currency.Type to;
    RValue value;

    public ExchangeDeclaration(Currency.Type from, Currency.Type to, RValue value) {
        this.from = from;
        this.to = to;
        this.value = value;
    }

    public Currency.Type getFrom() {
        return from;
    }

    public Currency.Type getTo() {
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
